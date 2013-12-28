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
	g = new Grammar();
	
	definePath( handler);
	defineExpression( handler);
	defineFunction( handler);
	definePrimitives();
  }

  /**
   * Define rules for expressions.
   */
  private void defineExpression( IHandler handler)
  {
	// Expr
	g.rule( "Expr", "PrimaryExpr");
//	g.rule( "Expr", "Expr", "space", "or", "space", "Expr");
//	g.rule( "Expr", "");
//	g.rule( "Expr", "");
//	g.rule( "Expr", "");
//	g.rule( "Expr", "");
//	g.rule( "Expr", "");  
	
	// ExplicitExpr
	g.rule( "ExplicitExpr", "(", "Expr", ")");
	
	// PrimaryExpr
	g.rule( "PrimaryExpr", "$", "QName");
	g.rule( "PrimaryExpr", "single-quoted");
	g.rule( "PrimaryExpr", "double-quoted");
	g.rule( "PrimaryExpr", "number");
//	g.rule( "PrimaryExpr", "FunctionCall");
  }
  
  /**
   * Define rules for functions.
   */
  private void defineFunction( IHandler handler)
  {
	g.rule( "FunctionCall");
  }
  
  /**
   * Define rules for paths.
   */
  private void definePath( IHandler handler)
  {
	// Path
	g.rule( "Path", "ChildStep", "RelativePath");
	g.rule( "Path", "DescendantStep", "RelativePath");
	g.rule( "Path", "RelativePath");
	
	// Relative Path
	g.rule( "RelativePath", "Step");
	g.rule( "RelativePath", "Step", "PredicateList");
	g.rule( "RelativePath", "RelativePath", "ChildStep");
	g.rule( "RelativePath", "RelativePath", "DescendantStep");
	
	// Predicates
	g.rule( "PredicateList", "Predicate");
	g.rule( "PredicateList", "PredicateList", "Predicate");
	g.rule( "Predicate", "#5B", "Expr", "#5D");
	
	// types of steps
	g.rule( "ChildStep", "/", "Step");
	g.rule( "DescendantStep", "/", "/", "Step");
	
	// Step
	g.rule( handler, "Step", "AxisSpecifier", "NodeTest");
	g.rule( handler, "Step", "AxisSpecifier", "NodeTest", "PredicateList");
	g.rule( handler, "Step", "NCName");
	g.rule( handler, "Step", "NCName", "PredicateList");
	g.rule( handler, "Step", ".");
	g.rule( handler, "Step", ".", ".");
	
	// Node Test
	g.rule( "NodeTest", "QName");
	g.rule( "NodeTest", "NCName", ":", "*");
	
	// NodeType omitted (comment|text|processing-instruction|node)
	g.rule( "NodeTest", "NCName", "(", "double-quoted", ")");
	g.rule( "NodeTest", "NCName", "(", "single-quoted", ")");
		
	// Name Test
	g.rule( "NameTest", "NCName", ":", "*");
	g.rule( "NameTest", "QName");
	
	// Axis Specifier
	g.rule( "AxisSpecifier", "AxisName", ":", ":");
	g.rule( "AxisSpecifier", "@");
	
	// axis name
	g.rule( "AxisName", "ancestor".toCharArray());
	g.rule( "AxisName", "ancestor-or-self".toCharArray());
	g.rule( "AxisName", "attribute".toCharArray());
	g.rule( "AxisName", "child".toCharArray());
	g.rule( "AxisName", "descendant".toCharArray());
	g.rule( "AxisName", "descendant-or-self".toCharArray());
	g.rule( "AxisName", "following".toCharArray());
	g.rule( "AxisName", "following-sibling".toCharArray());
	g.rule( "AxisName", "namespace".toCharArray());
	g.rule( "AxisName", "parent".toCharArray());
	g.rule( "AxisName", "preceding".toCharArray());
	g.rule( "AxisName", "preceding-sibling".toCharArray());
	g.rule( "AxisName", "self".toCharArray());
  }
  
  /**
   * Define rules for primitives.
   */
  private void definePrimitives()
  {
	// QName
	g.rule( "QName", "NCName", ":", "NCName");
	g.rule( "QName", "NCName");
	
	// NCName
	g.rule( "NCName", "letter", "nmchars");
	g.rule( "NCName", "_", "nmchars");
	g.rule( "nmchars", "nmchar");
	g.rule( "nmchars", "nmchars", "nmchar");
	g.rule( "nmchar", "letter");
	g.rule( "nmchar", "digit");
	g.rule( "nmchar", ".");
	g.rule( "nmchar", "-");
	g.rule( "nmchar", "_");
			
	// number
	g.rule( "number", "digit");
	g.rule( "number", "digit", ".", "number");
	g.rule( "number", ".", "number");
	
	// letters and digits
	g.rule( "letter", "[a-z]");
	g.rule( "letter", "[A-Z]");
	g.rule( "digit", "[0-9]");

	// double quoted literal
	g.rule( "double-quoted", "#22", "double-quote-chars", "#22");
	g.rule( "double-quote-chars", "double-quote-chars", "non-double-quote");
	g.rule( "double-quote-chars", "non-double-quote");
	g.rule( "non-double-quote", "[#01-#21]");
	g.rule( "non-double-quote", "[#23-#FFFF]");
		
	// single quoted literal
	g.rule( "single-quoted", "#27", "single-quote-chars", "#27");
	g.rule( "single-quote-chars", "single-quote-chars", "non-single-quote");
	g.rule( "single-quote-chars", "non-single-quote");
	g.rule( "non-single-quote", "[#01-#26]");
	g.rule( "non-single-quote", "[#28-#FFFF]");
	
	// whitespace
    g.rule( "space", "[#01-#20]", "space").setPriority( -1000);
    g.rule( "space", "[#01-#20]").setPriority( -1001);
    g.rule( "space?");
    g.rule( "space?", "space");    
  }
  
  private Grammar g;
  
  public static void main( String[] args) throws Exception
  {
    LR1.log.setLevel( Log.all);
	  
    IHandler handler = new IHandler() {
      public void onProduction( Parser parser, Rule rule, char[] buffer, int start, int length)
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
    Parser parser = lr.compile( xpath.g);
    
    System.out.println( xpath.g);
    
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
