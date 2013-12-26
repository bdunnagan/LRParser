package org.xidget.parser.lr3;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
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
    int removed = 0;
    
    while( true)
    {
      read = reader.read( buffer, preserve, buffer.length - preserve);
      if ( read < 0)
      {
        buffer[ preserve] = Grammar.terminusChar;
        read = 1;
      }

      int consumed = dfa.parse( this, buffer, 0, read + preserve, removed);
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
        removed += consumed;
      }
    }
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
    System.out.printf( "%s Parsing error at offset %d: \n", dfa, index);

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
