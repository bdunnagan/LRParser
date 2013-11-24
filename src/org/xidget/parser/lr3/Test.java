package org.xidget.parser.lr3;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Arrays;
import org.xidget.parser.lr3.Rule.IHandler;
import org.xidget.parser.lr3.lr1.LR1;
import org.xmodel.log.Log;

public class Test
{
  /**
   * Parse a simple CFG-style grammar for test purposes.
   * @param cfg The cfg grammar.
   * @return Returns the parsed grammar.
   */
  public Grammar parse( String cfg)
  {
    Grammar grammar = new Grammar();

    String[] lines = cfg.split( "[;\n]");
    for( int i=0; i<lines.length; i++)
    {
      String[] parts = lines[ i].split( "\\s*:=\\s*");
      
      Rule rule = new Rule( parts[ 0]);
      if ( parts.length == 2)
      {
        String[] list = parts[ 1].split( "\\s++");
        for( String rhs: list) 
        {
          rule.add( rhs);
        }
      }
      
      grammar.addRule( rule);
    }
    
    return grammar;
  }
  
  public static void main( String[] args) throws Exception
  {
    LR1.log.setLevel( Log.all);
    
    String cfg =
  		"A := A + A;" +
  		"A := A * A;" +
  		"A := 1;";
    
    Test test = new Test();
    Grammar grammar = test.parse( cfg);
    
    IHandler handler = new IHandler() {
      public void onProduction( Rule rule, char[] buffer, int start, int length)
      {
        String string = new String( buffer, 0, buffer.length);
        System.out.printf( "|%s|\n", rule);
        if ( start >= 0 && length >= 0)
        {
          System.out.printf( "%s\n", string);
          char[] indent = new char[ start]; Arrays.fill( indent, ' ');
          char[] underline = new char[ length]; Arrays.fill( underline, '^');
          System.out.printf( "%s%s\n", new String( indent), new String( underline));
        }
      }
    };
    
    grammar.rules().get( 2).setPriority( 1);
    
    for( Rule rule: grammar.rules())
      rule.handler = handler;
    
    LR1 lr = new LR1();
    Parser parser = lr.compile( grammar);
    
    System.out.println( grammar);
    
    BufferedReader reader = new BufferedReader( new InputStreamReader( System.in));
    while( true)
    {
      System.out.print( "> ");
      String line = reader.readLine();
      if ( line.equals( " ")) break;
      parser.parse( new StringReader( line));
    }
  }
}
