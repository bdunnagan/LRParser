package org.xidget.parser.lr3;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

import org.xidget.parser.lr3.Rule.IHandler;
import org.xidget.parser.lr3.lr1.DefaultStateBuilder;
import org.xidget.parser.lr3.lr1.LR1ItemSet;

public class Parser implements IHandler
{
  /**
   * Create a Parser with the specified table generator and start state.
   * @param builder The parser generator. 
   * @param start The start state.
   */
  public Parser( DefaultStateBuilder builder, State start)
  {
    this( builder, start, 1024);
  }

  /**
   * Create a Parser with the specified table generator, start state and buffer size.
   * @param start The start state.
   * @param bufferSize The initial buffer size.
   */
  public Parser( DefaultStateBuilder builder, State start, int bufferSize)
  {
    this.start = start;
    this.builder = builder;
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

    int offset = 0;
    while( true)
    {
      int read = reader.read( buffer, offset, buffer.length - offset);
      if ( read < 0)
      {
        buffer[ offset] = Grammar.terminusChar;
        read = 1;
      }

      offset = dfa.parse( this, buffer, offset, read);
      if ( offset == -1) return false;
      if ( offset == -2) return true;

      if ( offset == buffer.length)
      {
        buffer = Arrays.copyOf( buffer, buffer.length * 2);
      }
    }
  }

  /**
   * Show the current state of the parser.
   */
  public void dumpState()
  {
    System.out.println( "---------------------------------------------");
    System.out.println( builder.getItemSet( dfa.getState()));
    System.out.println( "---------------------------------------------");
  }

  /**
   * Returns the item-set for the specified state.
   * @param state The state.
   * @return Returns the item-set for the specified state.
   */
  public LR1ItemSet itemSet( State state)
  {
    return builder.getItemSet( state);
  }

  /* (non-Javadoc)
   * @see org.xidget.parser.lr3.Rule.IHandler#onProduction(org.xidget.parser.lr3.Parser, org.xidget.parser.lr3.Rule, char[], int, int)
   */
  @Override
  public void onProduction( Parser parser, Rule rule, char[] buffer, int start, int length) 
  {
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
    dumpState();

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

  private DefaultStateBuilder builder;
  private State start;
  private DFA dfa;
  private char[] buffer;
}
