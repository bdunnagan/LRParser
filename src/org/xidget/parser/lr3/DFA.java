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
    System.arraycopy( dfa.sstack, 0, sstack, 0, sindex+1);
    
    pstack = new int[ dfa.pstack.length];
    System.arraycopy( dfa.pstack, 0, pstack, 0, sindex+1);
    
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
   * @return Returns the current state.
   */
  public State getState()
  {
  	if ( branchDFA != null) return branchDFA.getState();
	  return sstack[ sindex];
  }
    
  /**
   * Parse the specified characters from the buffer.
   * @param parse The parser.
   * @param buffer The buffer containing the characters.
   * @param start The offset of the first character to parse.
   * @param length The number of characters to parse.
   * @return Returns the offset into the buffer of the first handle that should be preserved.
   *         If an error occurs then -1 is returned.
   *         If parsing is complete then -2 is returned.
   */
  public int parse( Parser parser, char[] buffer, int start, int length)
  {
    while( branchDFA != null)
    {
      int offset = branchDFA.parse( parser, buffer, start, length);
      if ( offset == -1)
      {
        if ( ++branchIndex == branches.length) return -1;
        branchDFA = new DFA( this, branches[ branchIndex]);
      }
      else
      {
        pstack = branchDFA.pstack;
        sstack = branchDFA.sstack;
        sindex = branchDFA.sindex;
        return offset;
      }
    }
    
    int offset = start;
    int last = start + length;
    while( offset < last)
    {
      int symbol = buffer[ offset];
      
      State state = sstack[ sindex];
      StackOp[] ops = state.stackOps;
      
      System.out.printf( "[%c] ", buffer[ offset]);
      parser.dumpState();
      
      if ( ops == null) return branch( state.branches, parser, buffer, offset, last - offset);
      
      StackOp op = ops[ 0];
      if ( symbol < op.low || symbol > op.high)
      {
        int i=0;
        while( ++i < ops.length)
        {
          op = ops[ i];
          if ( symbol >= op.low && symbol <= op.high)
          {
            // mem writes may be expensive here
            ops[ i] = ops[ i-1];
            ops[ i-1] = op;
            break;
          }
        }
        if ( i == ops.length)
        {
          parser.onError( this, offset, sstack, sindex);
          return -1;
        }
      }
      
      // reduce
      Rule reduce = op.reduce;
      if ( reduce != null)
      {
        sindex -= reduce.length;
        
        if ( reduce.handler != null)
        {
          // pstack contains the absolute position
          int first = pstack[ sindex];
          reduce.handler.onProduction( parser, reduce, buffer, first, offset - first);
        }
        
        state = sstack[ sindex].gotos[ reduce.symbol];
        sstack[ ++sindex] = state;
      }
      
      // shift
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
      pstack[ sindex] = offset;
    }
    
    return offset;
  }
  
  /**
   * Handle a branch point.
   * @param branches The branches.
   * @param parser The parser.
   * @param buffer The buffer.
   * @param start The starting offset into the buffer.
   * @param length The length of the buffer.
   * @param removed The number of characters removed from the buffer.
   * @return Returns the offset of the next character to parse, -1 if more characters are need, -2 on error.
   */
  private int branch( State[] branches, Parser parser, char[] buffer, int start, int length)
  {
    this.branches = branches;
    branchIndex = 0;
    branchDFA = new DFA( this, branches[ branchIndex]);
    while( branchDFA != null)
    {
      int offset = branchDFA.parse( parser, buffer, start, length);
      if ( offset == -1)
      {
        if ( ++branchIndex == branches.length) return -1;
        branchDFA = new DFA( this, branches[ branchIndex]);
      }
      else
      {
        pstack = branchDFA.pstack;
        sstack = branchDFA.sstack;
        sindex = branchDFA.sindex;
        return offset;
      }
    }	
    return -1;
  }
  
//  /**
//   * Print the current stack.
//   */
//  private final void printStack()
//  {
//    System.out.printf( "%s ", this);
//    for( int i=0; i<=sindex; i++) 
//      System.out.printf( "(S%d, %d) ", sstack[ i].index, pstack[ i]); 
//    System.out.println();
//  }
  
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
