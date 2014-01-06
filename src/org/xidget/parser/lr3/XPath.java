package org.xidget.parser.lr3;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Arrays;
import org.xidget.parser.lr3.Rule.IHandler;
import org.xidget.parser.lr3.lr1.DefaultStateBuilder;
import org.xidget.parser.lr3.lr1.LR1;
import org.xmodel.log.ConsoleSink;
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
//    g.rule( "RelativePath", "Step", "PredicateList");
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
    g.rule( handler, "Step", "QName");
    g.rule( handler, "Step", "QName", "PredicateList");
    g.rule( handler, "Step", ".");
    g.rule( handler, "Step", "'..'");

    // Node Test
    g.rule( handler, "NodeTest", "QName");
    g.rule( handler, "NodeTest", "NCName", ":", "*");
    g.rule( handler, "NodeTest", "*");
    g.rule( handler, "NodeTest", "NodeType", "(", ")");
    g.rule( handler, "NodeTest", "NodeType", "(", "double-quoted", ")");
    g.rule( handler, "NodeTest", "NodeType", "(", "single-quoted", ")");

    // NodeType
    g.rule( handler, "NodeType", "'comment'");
    g.rule( handler, "NodeType", "'text'");
    g.rule( handler, "NodeType", "'processing-instruction'");
    g.rule( handler, "NodeType", "'node'");

    // Name Test
    g.rule( handler, "NameTest", "NCName", ":", "*");
    g.rule( handler, "NameTest", "QName");

    // Axis Specifier
    g.rule( handler, "AxisSpecifier", "AxisName", ":", ":");
    g.rule( handler, "AxisSpecifier", "@");

    // axis name
    g.rule( handler, "AxisName", "'ancestor'");
    g.rule( handler, "AxisName", "'ancestor-or-self'");
    g.rule( handler, "AxisName", "'attribute'");
    g.rule( handler, "AxisName", "'child'");
    g.rule( handler, "AxisName", "'descendant'");
    g.rule( handler, "AxisName", "'descendant-or-self'");
    g.rule( handler, "AxisName", "'following'");
    g.rule( handler, "AxisName", "'following-sibling'");
    g.rule( handler, "AxisName", "'namespace'");
    g.rule( handler, "AxisName", "'parent'");
    g.rule( handler, "AxisName", "'preceding'");
    g.rule( handler, "AxisName", "'preceding-sibling'");
    g.rule( handler, "AxisName", "'self'");
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
    g.rule( "NCName", "NCNameStartChar");
    g.rule( "NCName", "NCName", "NameChar");

    // Names
    g.rule( "NameStartChar", ":");
    g.rule( "NameStartChar", "NCNameStartChar");
    g.rule( "NCNameStartChar", "_");
    g.rule( "NCNameStartChar", "letter");
    //	g.rule( "NCNameStartChar", "[#C0-#D6]");
    //	g.rule( "NCNameStartChar", "[#D8-#F6]");
    //	g.rule( "NCNameStartChar", "[#F8-#2FF]");
    //	g.rule( "NCNameStartChar", "[#370-#37D]");
    //	g.rule( "NCNameStartChar", "[#37F-#1FFF]");
    //	g.rule( "NCNameStartChar", "[#200C-#200D]");
    //	g.rule( "NCNameStartChar", "[#2070-#218F]");
    //	g.rule( "NCNameStartChar", "[#2C00-#2FEF]");
    //	g.rule( "NCNameStartChar", "[#3001-#D7FF]");
    //	g.rule( "NCNameStartChar", "[#F900-#FDCF]");
    //	g.rule( "NCNameStartChar", "[#FDF0-#FFFD]");

    g.rule( "NameChar", "NCNameStartChar");
    g.rule( "NameChar", "-");
    g.rule( "NameChar", ".");
    g.rule( "NameChar", "digit");
    //	g.rule( "NameChar", "[#0300-#036F]");
    //	g.rule( "NameChar", "[#203F-#2040]");

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
    Log.setDefaultSink( new ConsoleSink());
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
        System.out.println();
      }
    };


    XPath xpath = new XPath( handler);

    LR1 lr = new LR1();
    DefaultStateBuilder builder = new DefaultStateBuilder();
    lr.compile( xpath.g, builder);
    Parser parser = builder.getParser();

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
