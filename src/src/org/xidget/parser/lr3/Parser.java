package org.xidget.parser.lr3;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.xidget.parser.lr3.lr1.LR1;

public class Parser
{
  /**
   * Create a Parser with the specified table generator and start state.
   * @param lr The table generator (for error reporting).
   * @param start The start state.
   */
  public Parser( LR1 lr, State start)
  {
    this( lr, start, 1024);
  }
  
  /**
   * Create a Parser with the specified table generator, start state and buffer size.
   * @param lr The table generator (for error reporting).
   * @param start The start state.
   * @param bufferSize The initial buffer size.
   */
  public Parser( LR1 lr, State start, int bufferSize)
  {
    this.lr = lr;
    this.start = start;
    buffer = new char[ bufferSize];
  }

  /**
   * Parse the specified stream.
   * @param reader The stream.
   * @return Returns true if the parse was successful.
   */
  public boolean parse( Reader reader) throws IOException
  {
    dfa = new DFA( start);
    
    int preserve = 0;
    int read = 0;
    
    while( true)
    {
      read = reader.read( buffer, preserve, buffer.length - preserve);
      if ( read < 0)
      {
        buffer[ preserve] = Grammar.terminusChar;
        read = 1;
      }

      int consumed = dfa.parse( this, buffer, preserve, read);
      if ( consumed == -1) return false;
      if ( consumed == -2) return true;
      
      preserve += read - consumed;
      if ( preserve == buffer.length) 
      {
        buffer = Arrays.copyOf( buffer, buffer.length * 2);
      }
      else
      {
        System.arraycopy( buffer, consumed, buffer, 0, preserve);
      }
    }
  }
  
  /**
   * Try to parse each character in the buffer with each of the specified paths. 
   * Remove paths which produce an error.
   * @param paths The paths.
   * @param buffer The buffer.
   * @param start The starting offset in the buffer.
   * @param length The length of the buffer.
   * @return Returns the DFA instances that parsed successfully.
   */
  protected List<DFA> tryParse( List<DFA> paths, char[] buffer, int start, int length)
  {
    List<DFA> completed = null;
    
    int last = start + length;
    for( ; start < last; start++)
    {
      for( int i=0; i<paths.size(); i++)
      {
        DFA path = paths.get( i);
        int result = path.parse( this, buffer, start, 1);
        if ( result < 0)
        {
          paths.remove( i--);
          if ( result == -2) 
          {
            if ( completed == null) completed = new ArrayList<DFA>( 1);
            completed.add( path);
          }
        }
      }
    }
    
    return (completed != null)? completed: Collections.<DFA>emptyList();
  }
  
  /**
   * Called when a DFA errors.
   * @param dfa The DFA that errored.
   * @param index The stream index.
   * @param stack The current DFA stack.
   * @param sindex The current stack index.
   */
  protected void onError( DFA dfa, int index, State[] stack, int sindex)
  {
    System.out.printf( "%s Parsing error at line %d, column %d: \n", dfa, dfa.line(), dfa.column());

//    int s = index;
//    for( ; s >= 0 && buffer[ s] != '\n'; s--);
//    
//    int e = index;
//    for( ; e < buffer.length && buffer[ e] != '\n'; e++);
//    
//    s++;
//    System.out.printf( "%s\n", new String( buffer, s, e-s));
//    char[] indent = new char[ index - s]; Arrays.fill( indent, ' ');
//    System.out.printf( "%s^\n", new String( indent));
//    
//    System.out.printf( "Stack: \n");
//    for( int i=sindex; i>=0; i--)
//    {
//      List<LR1Item> items = new ArrayList<LR1Item>( lr.itemSet( stack[ i]).kernel);
//      System.out.printf( "%4d. %s\n", i, items.get( 0));
//      for( int j=1; j<items.size(); j++)
//      {
//        System.out.printf( "      %s\n", items.get( j));
//      }
//      System.out.println();
//    }
  }
    
  private LR1 lr;
  private State start;
  private DFA dfa;
  private char[] buffer;
}
