package org.xidget.parser.lr3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xidget.parser.lr3.State.Shift;

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
    int consumed = 0;
    int last = start + length;
    for( int offset = start; offset < last; loops++)
    {
      int symbol = buffer[ offset];
      
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

  /**
   * Handle a splitting state.
   * @param parser The parser.
   * @param state The splitting state.
   * @param buffer The buffer.
   * @param start The current offset into the buffer.
   * @param length The length of the buffer.
   * @return Returns the 
   */
  private DFA split( Parser parser, State state, char[] buffer, int start, int length)
  {
    List<DFA> paths = new ArrayList<DFA>( state.splits.length);
    for( int i=1; i<paths.size(); i++) paths.add( new DFA( this, state.splits[ i]));
    
    paths.add( 0, this);
    sstack[ sindex] = state.splits[ 0];

    return parser.tryParse( paths, buffer, start, length);
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
  @SuppressWarnings("unused")
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
}
