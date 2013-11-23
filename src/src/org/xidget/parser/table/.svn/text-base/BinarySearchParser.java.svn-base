package org.xidget.parser.table;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.xmodel.IModelObject;
import org.xmodel.xml.XmlIO;

/**
 * A configurable ascii character stream parser that does not use an object-oriented state machine
 * design and thereby avoids the overhead of a virtual function call for every character. This
 * implementation performs a binary search against the event to find the next state.
 */
@SuppressWarnings("unused")
public class BinarySearchParser implements IParser
{
  public BinarySearchParser()
  {
    config = new ArrayList<List<Hop>>( 10);
    buffer = new char[ 1 << 16];
    stack = new int[ 1024];
  }
  
  /* (non-Javadoc)
   * @see org.xidget.parser.table.IParser#setNames(java.lang.String[])
   */
  public void setNames( String[] names)
  {
    this.names = names;
  }
  
  /* (non-Javadoc)
   * @see org.xidget.parser.table.IParser#popNext(int, char, char)
   */
  
  public void popNext( int state, char first, char last)
  {
    setNext( state, -1, first, last, -1);
  }

  /* (non-Javadoc)
   * @see org.xidget.parser.table.IParser#popNext(int, int, char, char)
   */
  
  public void popNext( int state, int count, char first, char last)
  {
    setNext( state, -1, first, last, -count);
  }

  /* (non-Javadoc)
   * @see org.xidget.parser.table.IParser#pushNext(int, int, char, char)
   */
  
  public void pushNext( int state, int next, char first, char last)
  {
    setNext( state, next, first, last, 1);
  }

  /* (non-Javadoc)
   * @see org.xidget.parser.table.IParser#setNext(int, int, char, char)
   */
  
  public void setNext( int state, int next, char first, char last)
  {
    setNext( state, next, first, last, 0);
  }

  /* (non-Javadoc)
   * @see org.xidget.parser.table.IParser#setNext(int, int, char)
   */
  public void setNext( int state, int next, char event)
  {
    setNext( state, next, event, event, 0);
  }
  
  /* (non-Javadoc)
   * @see org.xidget.parser.table.IParser#pushNext(int, int, char)
   */
  public void pushNext( int state, int next, char event)
  {
    setNext( state, next, event, event, 1);
  }

  /* (non-Javadoc)
   * @see org.xidget.parser.table.IParser#popNext(int, char)
   */
  public void popNext( int state, char event)
  {
    setNext( state, -1, event, event, -1);
  }
  
  /* (non-Javadoc)
   * @see org.xidget.parser.table.IParser#popNext(int, int, char)
   */
  public void popNext( int state, int count, char event)
  {
    setNext( state, -1, event, event, -count);
  }
  
  private void setNext( int state, int next, char first, char last, int stack)
  {
    for( int i=config.size(); i<=state; i++) config.add( new ArrayList<Hop>( 1));
    
    List<Hop> list = config.get( state);
    if ( list.size() == 0)
    {
      Hop hop = new Hop( first, last, next, stack);
      list.add( hop);
      return;
    }
    
    // binary search
    int h = list.size() >> 1;
    int k = h;
    while( k >= 0 && k < list.size())
    {
      Hop hop = list.get( k);
      if ( last < hop.first)
      {
        if ( h == 0) { k = 0; break;}
        h >>= 1;
        k -= h;
      }
      else if ( first > hop.last)
      {
        if ( h == 0) { k = list.size(); break;}
        h >>= 1;
        k += h;
      }
      else
      {
        if ( first < hop.first) hop.first = first;
        if ( last > hop.last) hop.last = last;
        break;
      }
    }
      
    Hop hop = new Hop( first, last, next, stack);
    list.add( k, hop);
  }
  
  /**
   * Convert the configuration into the compact table array.
   */
  private void createTable()
  {
    if ( table != null) return;
    table = new Hop[ config.size()][];
    for( int i=0; i<table.length; i++)
    {
      table[ i] = new Hop[ config.get( i).size()];
      for( int j=0; j<table[ i].length; j++)
      {
        table[ i][ j] = config.get( i).get( j);
      }
    }
  }
  
  /* (non-Javadoc)
   * @see org.xidget.parser.table.IParser#parse(java.io.Reader)
   */
  public final void parse( Reader reader) throws IOException
  {
    createTable();
    
    int start = 0;
    int index = 0;
    
    bufIndex = 0;
    bufSize = 0;
    state = 1;
    jump = table[ state];
    
 outer:
    while( true)
    {
      if ( bufIndex == bufSize)
      {
        bufIndex = 0;
        bufSize = reader.read( buffer, 0, maxBuffer);
        if ( bufSize < 0) { bufSize = 0; return;}
      }
      
      char event = buffer[ bufIndex++];
      
      // binary search
      int k = 0;
      int h = 1;
      if ( jump.length < 2)
      {
        h = jump.length >> 1;
        k = h;
        while( true)
        {
          Hop hop = jump[ k];
          if ( event < hop.first)
          {
            if ( h == 0) break outer;
            h >>= 1;
            k -= h;
          }
          else if ( event > hop.last)
          {
            if ( h == 0) break outer;
            h >>= 1;
            k += h;
          }
          else
          {
            break;
          }
        }
      }
      
      Hop hop = jump[ k];
      int next = hop.next;
      int stkop = hop.stack;
      if ( stkop != 0 || next != state)
      {
        if ( stkop > 0) { stack[ depth++] = state;}
        else if ( stkop < 0) { depth += stkop; next = stack[ depth];}
        
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
    createTable();

    int start = offset;
    int end = offset + length;
    
    state = 1;
    jump = table[ state];
    
outer:    
    while( offset < end)
    {
      char event = buffer[ offset++];
      
      int h = jump.length >> 1;
      int k = h;
      while( true)
      {
        Hop hop = jump[ k];
        if ( event < hop.first)
        {
          if ( h == 0) break outer;
          h >>= 1;
          k -= h;
        }
        else if ( event > hop.last)
        {
          if ( h == 0) break outer;
          h >>= 1;
          k += h;
        }
        else
        {
          break;
        }
      }      
      
      Hop hop = jump[ k];
      int next = hop.next;
      int stkop = hop.stack;
      if ( stkop != 0 || next != state)
      {
        if ( stkop > 0) { stack[ depth++] = state;}
        else if ( stkop < 0) { depth += stkop; next = stack[ depth];}
        
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
    createTable();

    int start = offset;
    int end = offset + length;
    
    state = 1;
    jump = table[ state];

outer:
    while( offset < end)
    {
      char event = buffer[ offset++];
      
      int h = jump.length >> 1;
      int k = h;
      while( true)
      {
        Hop hop = jump[ k];
        if ( event < hop.first)
        {
          if ( h == 0) break outer;
          h >>= 1;
          k -= h;
        }
        else if ( event > hop.last)
        {
          if ( h == 0) break outer;
          h >>= 1;
          k += h;
        }
        else
        {
          break;
        }
      }
      
      Hop hop = jump[ k];
      int next = hop.next;
      int stkop = hop.stack;
      if ( stkop != 0 || next != state)
      {
        if ( stkop > 0) { stack[ depth++] = state;}
        else if ( stkop < 0) { depth += stkop; next = stack[ depth];}
        
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

  final static class Hop
  {
    Hop( char first, char last, int next, int stack)
    {
      this.first = first;
      this.last = last;
      this.next = next;
      this.stack = stack;
    }
    
    char first;
    char last;
    int next;
    int stack;
  }
  
  private final static int maxBuffer = 8192;
  
  private List<List<Hop>> config;
  private Hop[][] table;
  private Hop[] jump;
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
    
    IParser parser = new BinarySearchParser();
    Config config = new Config( parser);
    config.configure( root);
    
    char[] buffer = new char[ 500000];
    FileReader reader = new FileReader( "KingLear.xml");
    int size = reader.read( buffer);
    
    StringReader memory = new StringReader( new String( buffer));
    
    long t0 = System.nanoTime();
    int runs = 100;
    for( int i=0; i<runs; i++)
    {
      parser.parseDebug( buffer, 0, buffer.length);
      memory.reset();
    }
    
    long t1 = System.nanoTime();
    double e = (t1 - t0) / 1e6;
    double n = (double)size * runs / 1e3;
    
    System.out.printf( "%3.3f ms, %3.3f kb/ms\n", e / runs, n / e); 
  }
}
