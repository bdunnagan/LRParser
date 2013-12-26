package org.xidget.parser.lr3;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Arrays;
import org.xidget.parser.lr3.Rule.IHandler;
import org.xidget.parser.lr3.lr1.LR1;
import org.xmodel.log.Log;

public class XPath
{
  public XPath( IHandler handler)
  {	
	buildTokenGrammar( handler);
  }

  /**
   * Build the lexical grammar.
   * @param handler The event handler.
   */
  private void buildLexicalGrammar( IHandler handler)
  {
	
  }
  
  /**
   * Build the tokenizer.
   * @param handler The token handler.
   */
  private void buildTokenGrammar( IHandler handler)
  {
	Grammar g = new Grammar();

	// tokens
	g.rule( "tokens", "token", "tokens");
	g.rule( "tokens", "token");
	
	// token
	g.rule( handler, "token", "+");
	g.rule( handler, "token", "-");
	g.rule( handler, "token", "=");
	g.rule( handler, "token", "<");
	g.rule( handler, "token", ">");
	g.rule( handler, "token", "!");
	g.rule( handler, "token", "@");
	g.rule( handler, "token", "(");
	g.rule( handler, "token", ")");
	g.rule( handler, "token", '[');
	g.rule( handler, "token", ']');
	g.rule( handler, "token", "\"");
	g.rule( handler, "token", "\'");
	g.rule( handler, "token", "$");
	g.rule( handler, "token", "/");
	g.rule( handler, "token", ":");
	g.rule( handler, "token", ";");
	g.rule( handler, "token", "word");
	g.rule( handler, "token", "space");
		
	// letters and digits
	g.rule( "word", "wchar", "word").setPriority( 2);
	g.rule( "word", "wchar").setPriority( -1);
	g.rule( "wchar", "_");
	g.rule( "wchar", "[a-z]");
	g.rule( "wchar", "[A-Z]");
	g.rule( "wchar", "[0-9]");

	// whitespace
    g.rule( "space", "[#01-#20]", "space").setPriority( -100);
    g.rule( "space", "[#01-#20]").setPriority( -101);
    
    this.tokenizer = g;
  }
  
  private Grammar tokenizer;
  
  public static void main( String[] args) throws Exception
  {
    //LR1.log.setLevel( Log.all);
	  
    IHandler handler = new IHandler() {
      public void onProduction( Rule rule, char[] buffer, int start, int length)
      {
        String string = new String( buffer, 0, buffer.length);
        System.out.printf( "%d: %s\n", rule.symbol(), rule);
        if ( start >= 0 && length >= 0)
        {
          System.out.printf( "%s\n", string);
          char[] indent = new char[ start]; Arrays.fill( indent, ' ');
          char[] underline = new char[ length]; Arrays.fill( underline, '^');
          System.out.printf( "%s%s\n\n", new String( indent), new String( underline));
        }
      }
    };
    
    
    XPath xpath = new XPath( handler);
        
    LR1 lr = new LR1();
    Parser parser = lr.compile( xpath.tokenizer);
    
    //System.out.println( xpath.tokenizer);
    
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
