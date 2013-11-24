package org.xidget.parser.lr3;

import java.util.Arrays;
import org.xidget.parser.lr3.State.StackOp;

/**
 * Implementation of a Deterministic Finite Automaton (DFA).
 */
public final class DFA
{
  public DFA( State start)
  {
    this.id = counter++;
    this.start = start;
    reset();
  }
  
  public DFA( DFA dfa, State state)
  {
    this.id = counter++;
    this.start = state;
    
    sindex = dfa.sindex;
    
    sstack = new State[ dfa.sstack.length];
    System.arraycopy( dfa.sstack, 0, sstack, 0, sindex);
    
    pstack = new int[ dfa.pstack.length];
    System.arraycopy( dfa.pstack, 0, pstack, 0, sindex);
    
    sstack[ sindex] = state;
    
    line = dfa.line;
    column = dfa.column;
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
    
    line = 1;
    column = 0;
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
      int result = branchDFA.parse( parser, buffer, start, length);
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
    for( int offset = start; offset < last; loops++)
    {
      int symbol = buffer[ offset];
      
      State state = sstack[ sindex];
      StackOp[] shifts = state.stackOps;
      
      System.out.printf( "[%c] ", buffer[ offset]);
      printStack();
      
      if ( shifts == null) 
      {
        branches = state.splits;
        branchIndex = 0;
        branchDFA = new DFA( this, branches[ branchIndex]);
        while( branchDFA != null)
        {
          int result = branchDFA.parse( parser, buffer, offset, last - offset);
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
      
      searches++;
      StackOp shift = shifts[ 0];
      if ( symbol < shift.low || symbol > shift.high)
      {
        boolean found = false;
        for( int i=1; i<shifts.length; i++)
        {
          searches++;
          shift = shifts[ i];
          if ( symbol >= shift.low && symbol <= shift.high)
          {
            shifts[ i] = shifts[ i-1];
            shifts[ i-1] = shift;
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
      
      Rule reduce = shift.reduce;
      if ( reduce != null)
      {
        reduces++;
        
        sindex -= reduce.length;

        if ( reduce.handler != null)
        {
          int position = pstack[ sindex];
          reduce.handler.onProduction( reduce, buffer, position, offset - position);
          consumed = offset;
        }
        
        state = sstack[ sindex].gotos[ reduce.symbol];
        sstack[ ++sindex] = state;
      }
      else
      {
        if ( symbol == '\n') 
        {
          line++;
          column = -1;
        }
        
        column++;
        offset++;
        
        if ( ++sindex == sstack.length)
        {
          sstack = Arrays.copyOf( sstack, sstack.length * 2);
          pstack = Arrays.copyOf( pstack, pstack.length * 2);
        }

        if ( (sstack[ sindex] = shift.next) == null) return -2;
      }
      
      pstack[ sindex] = offset;
    }
    
    return consumed;
  }
  
  /**
   * @return Returns the current line number.
   */
  public int line()
  {
    return line;
  }
  
  /**
   * @return Returns the current offset in the current line.
   */
  public int column()
  {
    return column;
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
    return String.format( "DFA-%d", id);
  }

  public static long reduces = 0;
  public static long searches = 0;
  public static long loops = 0;
  public static int counter = 0;

  private int id;
  private State start;
  private State[] sstack;
  private int[] pstack;
  private int sindex;
  private int line;
  private int column;
  private State[] branches;
  private int branchIndex;
  private DFA branchDFA;
}
