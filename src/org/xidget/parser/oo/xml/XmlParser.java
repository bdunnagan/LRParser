package org.xidget.parser.oo.xml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import org.xidget.parser.oo.IState;
import org.xidget.parser.oo.Listener;
import org.xidget.parser.oo.Parser;

/**
 * A parser for xml using my home-grown parser library.
 */
public class XmlParser extends Parser
{
  public XmlParser()
  {
    setInitialState( Document.class);
    
    boolean debug = false;
    if ( debug)
    {
      addEventListener( new Listener() {
        public void onEvent(Parser parser, char event, IState state, IState next) 
        {
          System.out.printf( "[%s] %s -> %s\n", event, state, next);
        }
      });
    }
  }
  
  public static void main( String[] args) throws IOException
  {
    char[] buffer = new char[ 245849];
    
    File file = new File( "KingLear.xml");
    FileReader reader = new FileReader( file);
    reader.read( buffer);
    
    StringReader memory = new StringReader( new String( buffer));
    
    long t0 = System.nanoTime();
    
    int count = 20;
    for( int i=0; i<count; i++)
    {
      XmlParser parser = new XmlParser();
      parser.parse( memory);
      memory.reset();
    }
    
    long t1 = System.nanoTime();
    double e = ((t1-t0) / 1e6 / count);
    double n = file.length() / 1e3f;
    System.out.printf( "%3.1f ms, %3.1f KB, %3.3f KB/ms\n", e, n, (n / e));
  }
}
