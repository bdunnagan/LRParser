package org.xidget.parser.lr3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xidget.parser.lr3.State.Shift;

/**
 * Implementation of a Deterministic Finite Automaton (DFA) that executes multiple stacks in parallel.
 * This type of DFA does not emit productions.  It is used to find a non-ambiguous path.
 */
public final class ParallelDFA
{
  public ParallelDFA( State[][] sstacks, int[][] pstacks, int sindex)
  {
    this.id = DFA.counter++;
    this.sstacks = sstacks;
    this.pstacks = pstacks;
    this.sindex = sindex;
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
    int consumed = 0;
    int last = start + length;
    for( int offset = start; offset < last; )
    {
      int symbol = buffer[ offset];
      
      for
      State state = sstack[ sindex];
      Shift[] shifts = state.shifts;
      
      //System.out.printf( "[%c] ", buffer[ offset]);
      //printStack();
      
      if ( shifts == null) return split( parser, state, buffer, offset, length - offset);
      
      searches++;
      Shift shift = shifts[ 0];
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

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return String.format( "PDFA-%s", id);
  }

  private int id;
  private State[][] sstacks;
  private int[][] pstacks;
  private int sindex;
  private int line;
  private int column;
}
