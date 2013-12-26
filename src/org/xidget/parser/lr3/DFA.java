package org.xidget.parser.lr3;

import java.util.Arrays;
import org.xidget.parser.lr3.State.StackOp;

/**
 * Implementation of a deterministic finite automaton.
 */
public final class DFA
{
  public DFA( State start)
  {
    this.start = start;
    reset();
  }
  
  public DFA( DFA dfa, State state)
  {
    this.start = state;
    
    sindex = dfa.sindex;
    
    sstack = new State[ dfa.sstack.length];
    System.arraycopy( dfa.sstack, 0, sstack, 0, sindex);
    
    pstack = new int[ dfa.pstack.length];
    System.arraycopy( dfa.pstack, 0, pstack, 0, sindex);
    
    sstack[ sindex] = state;
  }
  
  /**
   * Reset the DFA to the start state.
   */
  public void reset()
  {
    sstack = new State[ 128];
    pstack = new int[ 128];
    sindex = 0;
    
    sstack[ 0] = start;
    pstack[ 0] = 0;
  }
  
  /**
   * Parse the specified characters from the buffer.
   * @param parse The parser.
   * @param buffer The buffer containing the characters.
   * @param start The offset of the first character to parse.
   * @param length The number of characters to parse.
   * @param removed The number of characters removed from the buffer since the last call.
   * @return Returns the offset into the buffer of the first handle that should be preserved.
   *         If an error occurs then -1 is returned.
   *         If parsing is complete then -2 is returned.
   */
  public int parse( Parser parser, char[] buffer, int start, int length, int removed)
  {
    while( branchDFA != null)
    {
      int result = branchDFA.parse( parser, buffer, start, length, removed);
      if ( result == -2) return -2;
      if ( result == -1)
      {
        if ( ++branchIndex == branches.length) return -1;
        branchDFA = new DFA( this, branches[ branchIndex]);
      }
      else
      {
        return result;
      }
    }
    
    int consumed = 0;
    int last = start + length;
    for( int offset = start; offset < last; )
    {
      int symbol = buffer[ offset];
      
      State state = sstack[ sindex];
      StackOp[] ops = state.stackOps;
      
//      System.out.printf( "[%c] ", buffer[ offset]);
//      printStack();
      
      if ( ops == null) 
      {
        branches = state.splits;
        branchIndex = 0;
        branchDFA = new DFA( this, branches[ branchIndex]);
        while( branchDFA != null)
        {
          int result = branchDFA.parse( parser, buffer, offset, last - offset, removed);
          if ( result == -2) return -2;
          if ( result == -1)
          {
            if ( ++branchIndex == branches.length) return -1;
            branchDFA = new DFA( this, branches[ branchIndex]);
          }
          else
          {
            return result;
          }
        }
      }
      
      StackOp op = ops[ 0];
      if ( symbol < op.low || symbol > op.high)
      {
        boolean found = false;
        for( int i=1; i<ops.length; i++)
        {
          op = ops[ i];
          if ( symbol >= op.low && symbol <= op.high)
          {
            ops[ i] = ops[ i-1];
            ops[ i-1] = op;
            found = true;
            break;
          }
        }
        
        if ( !found)
        {
          parser.onError( this, offset, sstack, sindex);
          return -1;
        }
      }
      
      Rule reduce = op.reduce;
      if ( reduce != null)
      {
        sindex -= reduce.length;

        if ( reduce.handler != null)
        {
          // pstack contains the absolute position
          int position = pstack[ sindex] - removed;
          reduce.handler.onProduction( reduce, buffer, position, offset - position);
          consumed = offset;
        }
        
        state = sstack[ sindex].gotos[ reduce.symbol];
        sstack[ ++sindex] = state;
      }
      else
      {
        offset++;
        
        if ( ++sindex == sstack.length)
        {
          sstack = Arrays.copyOf( sstack, sstack.length * 2);
          pstack = Arrays.copyOf( pstack, pstack.length * 2);
        }

        if ( (sstack[ sindex] = op.next) == null) return -2;
      }
      
      // put absolute position in pstack
      pstack[ sindex] = offset + removed;
    }
    
    return consumed;
  }
  
  /**
   * Print the current stack.
   */
  private final void printStack()
  {
    System.out.printf( "%s ", this);
    for( int i=0; i<=sindex; i++) 
      System.out.printf( "(S%d, %d) ", sstack[ i].index, pstack[ i]); 
    System.out.println();
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return String.format( "DFA-%X", start.hashCode());
  }

  private State start;
  private State[] sstack;
  private int[] pstack;
  private int sindex;
  private State[] branches;
  private int branchIndex;
  private DFA branchDFA;
}
