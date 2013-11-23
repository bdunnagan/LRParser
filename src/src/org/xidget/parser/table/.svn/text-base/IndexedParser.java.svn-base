package org.xidget.parser.table;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import org.xmodel.IModelObject;
import org.xmodel.xml.XmlIO;

/**
 * A configurable ascii character stream parser that does not use an object-oriented state machine
 * design and thereby avoids the overhead of a virtual function call for every character. This
 * implementation performs an index lookup of the next state.
 */
@SuppressWarnings("unused")
public class IndexedParser implements IParser
{
  public IndexedParser()
  {
    table = new int[ 10][ 1];
    buffer = new char[ 1 << 16];
    stack = new int[ 1024];
  }
  
  /* (non-Javadoc)
   * @see org.xidget.parser.table.IParser#setNames(java.lang.String[])
   */
  public void setNames( String[] names)
  {
    this.names = names;
    if ( names.length < 16)
    {
      push = 0x10;
      pop = 0x20;
      maxState = 0xF;
    }
    else if ( names.length < 256)
    {
      push = 0x100;
      pop = 0x200;
      maxState = 0xFF;
    }
    else
    {
      push = 0x1000000;
      pop = 0x2000000;
      maxState = 0xFFFFFF;
    }
    
    notPush = ~push;
    notPop = ~pop;
  }
  
  /* (non-Javadoc)
   * @see org.xidget.parser.table.IParser#popNext(int, char, char)
   */
  
  public void popNext( int state, char first, char last)
  {
    for( char i=first; i<=last; i++)
      popNext( state, i);
  }

  /* (non-Javadoc)
   * @see org.xidget.parser.table.IParser#popNext(int, int, char, char)
   */
  
  public void popNext( int state, int count, char first, char last)
  {
    for( char i=first; i<=last; i++)
      popNext( state, count, i);
  }

  /* (non-Javadoc)
   * @see org.xidget.parser.table.IParser#pushNext(int, int, char, char)
   */
  
  public void pushNext( int state, int next, char first, char last)
  {
    for( char i=first; i<=last; i++)
      pushNext( state, next, i);
  }

  /* (non-Javadoc)
   * @see org.xidget.parser.table.IParser#setNext(int, int, char, char)
   */
  
  public void setNext( int state, int next, char first, char last)
  {
    for( char i=first; i<=last; i++)
      setNext( state, next, i);
  }

  /* (non-Javadoc)
   * @see org.xidget.parser.table.IParser#setNext(int, int, char)
   */
  public void setNext( int state, int next, char event)
  {
    if ( table.length <= state) resizeTable( state);
    if ( table[ state].length <= event) resizeJump( state, event);
    if ( table[ state][ event] == 0) table[ state][ event] = next;
  }
  
  /* (non-Javadoc)
   * @see org.xidget.parser.table.IParser#pushNext(int, int, char)
   */
  public void pushNext( int state, int next, char event)
  {
    if ( table.length <= state) resizeTable( state);
    if ( table[ state].length <= event) resizeJump( state, event);
    next |= push;
    if ( table[ state][ event] == 0) table[ state][ event] = next;
  }

  /* (non-Javadoc)
   * @see org.xidget.parser.table.IParser#popNext(int, char)
   */
  public void popNext( int state, char event)
  {
    popNext( state, 1, event);
  }
  
  /* (non-Javadoc)
   * @see org.xidget.parser.table.IParser#popNext(int, int, char)
   */
  public void popNext( int state, int count, char event)
  {
    if ( table.length <= state) resizeTable( state);
    if ( table[ state].length <= event) resizeJump( state, event);
    if ( table[ state][ event] == 0) table[ state][ event] = pop | count;
  }
  
  /**
   * Resize the table array to accomodate at least the specified number of states.
   * @param min The minimum number of states.
   */
  private void resizeTable( int min)
  {
    int size = (int)(min + 1);
    int[][] array = new int[ size][ 128];
    System.arraycopy( table, 0, array, 0, table.length);
    table = array;
  }
  
  /**
   * Resize the table array to accomodate at least the specified number of states.
   * @param min The minimum number of states.
   */
  private void resizeJump( int state, int min)
  {
    int size = (int)(min + 1);
    int[] array = new int[ size];
    System.arraycopy( table[ state], 0, array, 0, table[ state].length);
    table[ state] = array;
  }
  
  /* (non-Javadoc)
   * @see org.xidget.parser.table.IParser#parse(java.io.Reader)
   */
  public final void parse( Reader reader) throws IOException
  {
    int start = 0;
    int index = 0;
    
    bufIndex = 0;
    bufSize = 0;
    state = 1;
    jump = table[ state];
    
    while( true)
    {
      if ( bufIndex == bufSize)
      {
        bufIndex = 0;
        bufSize = reader.read( buffer, 0, maxBuffer);
        if ( bufSize < 0) { bufSize = 0; return;}
      }
      
      int next = jump[ buffer[ bufIndex++]];
      if ( (next & push) != 0 || next != state)
      {
        if ( next > maxState)
        {
          if ( (next & push) != 0) { next &= notPush; stack[ depth++] = state;}
          else if ( (next & pop) != 0) { next &= notPop; depth -= next; next = stack[ depth];}
        }
        else if ( next <= 0)
        {
          break;
        }
        
        state = next;
        jump = table[ state];
        
        start = index;
      }
      
      index++;
    }
  }

  /* (non-Javadoc)
   * @see org.xidget.parser.table.IParser#parse(char[], int, int)
   */
  public final void parse( char[] buffer, int offset, int length) throws IOException
  {
    int start = offset;
    int end = offset + length;
    
    state = 1;
    jump = table[ state];
    
    while( offset < end)
    {
      int next = jump[ buffer[ offset++]];
      if ( (next & push) != 0 || next != state)
      {
        if ( next > maxState)
        {
          if ( (next & push) != 0) { next &= notPush; stack[ depth++] = state;}
          else if ( (next & pop) != 0) { next &= notPop; depth -= next; next = stack[ depth];}
        }
        else if ( next <= 0)
        {
          break;
        }
        
        state = next;
        jump = table[ state];
        
        start = offset;
      }
    }
  }

  /* (non-Javadoc)
   * @see org.xidget.parser.table.IParser#parseDebug(char[], int, int)
   */
  public final void parseDebug( char[] buffer, int offset, int length) throws IOException
  {
    int start = offset;
    int end = offset + length;
    
    state = 1;
    jump = table[ state];
    
    while( offset < end)
    {
      char c = buffer[ offset++];
      int next = jump[ c];
      if ( (next & push) != 0 || next != state)
      {
        if ( next > maxState)
        {
          if ( (next & push) != 0) { next &= notPush; stack[ depth++] = state;}
          else if ( (next & pop) != 0) { next &= notPop; depth -= next; next = stack[ depth];}
        }
        else if ( next <= 0)
        {
          break;
        }
  
        System.out.printf( "[%s] (%s -> %s) %d (", 
            buffer[ offset - 1], 
            names[ state], 
            names[ next], 
            depth); 
        
        for( int i=start; i<offset; i++) System.out.print( buffer[ i]);
        System.out.print( ")\n");
        
        state = next;
        jump = table[ state];
        
        start = offset;
      }
    }
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  
  public String toString()
  {
    StringBuilder sb = new StringBuilder();

    sb.append( "switch( next)\n");
    sb.append( "{\n");
    for( int i=0; i<names.length; i++)
    {
      if ( i > 0) sb.append( "\n");
      sb.append( String.format( "  // %s\n", names[ i]));
      sb.append( String.format( "  case %d: \n", i));
      sb.append( "    break;\n");
    }
    sb.append( "}\n");
    
    sb.append( "private final int[][] table = {\n");
    for( int i=0; i<table.length; i++)
    {
      if ( i > 0) sb.append( ",\n  ");
      sb.append( "{ ");
      for( int j=0; j<table[ i].length; j++)
      {
        if ( j > 0) sb.append( ", ");
        //if ( (j % 32) == 7) sb.append( "\n  ");
        sb.append( String.format( "%d", table[ i][ j]));
      }
      sb.append( "}");
    }
    sb.append( "};\n");
    
    return sb.toString();
  }

  private int maxBuffer = 8192;
  private int push = 0x10000000;
  private int pop = 0x20000000;
  private int maxState = 0x00FFFFFF;
  private int notPush = ~push;
  private int notPop = ~pop;
  
  private int[][] table;
  private int[] jump;
  private int state;
  private int[] stack;
  private int depth;

  private char[] buffer;
  private int bufIndex;
  private int bufSize;

  private String[] names;
  
  public static void main( String[] args) throws Exception
  {
    XmlIO xmlIO = new XmlIO();
    IModelObject root = xmlIO.read( new FileInputStream( "xml-parser.xml"));
    
    IParser parser = new IndexedParser();
    Config config = new Config( parser);
    config.configure( root);
    
    System.out.println( parser);
    
    char[] buffer = new char[ 5225000];
    FileReader reader = new FileReader( "bible.xml");
    int size = reader.read( buffer);
    
    StringReader memory = new StringReader( new String( buffer));
    
    long t0 = System.nanoTime();
    int runs = 1;
    for( int i=0; i<runs; i++)
    {
      parser.parse( buffer, 0, buffer.length);
      memory.reset();
    }
    
    long t1 = System.nanoTime();
    double e = (t1 - t0) / 1e6;
    double n = (double)size * runs / 1e3;
    
    System.out.printf( "%3.3f ms, %3.3f kb/ms\n", e / runs, n / e); 
  }
}
