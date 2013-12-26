package org.xidget.parser.lr3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.Arrays;
import org.xidget.parser.lr3.Rule.IHandler;
import org.xidget.parser.lr3.lr1.LR1;
import org.xmodel.log.ConsoleSink;
import org.xmodel.log.Log;

public class BNFGrammar extends Grammar
{
  public BNFGrammar()
  {
    buildTokens();
    buildGrammar();
    setStart( "Rule+");
  }

  private void buildGrammar()
  {
    rule( "Rule+", "Rule");
    rule( "Rule+", "Rule+", "Rule");
    
    rule( handler, "Rule", "Name", "S*", ":", "=", "S*", "Decl", "S*", ";", "S*");
    rule( handler, "Rule", "Name", "S*", ":", "=", "S*", "Decl", "S*", "XGS", "S*", ";", "S*");
    
    rule( handler, "Name", "W+");
    
    rule( "Decl", "Reference");
    rule( "Decl", "Symbols");
    rule( "Decl", "Group");
    rule( "Decl", "Choice");
    rule( "Decl", "Decl", "S+", "Reference");
    rule( "Decl", "Decl", "S+", "Symbols");
    rule( "Decl", "Decl", "S+", "Group");
    
    rule( handler, "Reference", "W+", "Quantifier");
    rule( handler, "Symbols", "Q", "NQ*", "Q", "Quantifier");
    rule( handler, "Symbols", "#5B", "CharSet", "#5D", "Quantifier");
    rule( handler, "Group", "(", "S*", "Decl", "S*", ")", "Quantifier");
    rule( handler, "Choice", "Decl", "S*", "|", "S*", "Decl").setPriority( 1);
    
    rule( "CharSet", "#5C");
    rule( "CharSet", "[#21-#5A]");
    rule( "CharSet", "[#5E-#FF]");
    rule( "CharSet", "CharSet", "#5C");
    rule( "CharSet", "CharSet", "[#21-#5A]");
    rule( "CharSet", "CharSet", "[#5E-#FF]");
    
    rule( handler, "XGS", "/", "*", "Comment+", "*", "/");
    rule( "Comment+", "[#01-#29]");
    rule( "Comment+", "[#2B-#FF]");
    rule( "Comment+", "Comment+", "[#01-#29]");
    rule( "Comment+", "Comment+", "[#2B-#FF]");
    
    rule( "Quantifier");
    rule( handler, "Quantifier", "?");
    rule( handler, "Quantifier", "*");
    rule( handler, "Quantifier", "+");
  }
  
  private void buildTokens()
  {
    rule( "W", "[a-z]");
    rule( "W", "[A-Z]");
    rule( "W", "[0-9]");
    
    rule( "W+", "[a-z]");
    rule( "W+", "[A-Z]");
    rule( "W+", "[0-9]");
    rule( "W+", "W+", "[a-z]");
    rule( "W+", "W+", "[A-Z]");
    rule( "W+", "W+", "[0-9]");
    
    rule( "S*");
    rule( "S*", "S*", "[#01-#20]");
    rule( "S+", "S*", "[#01-#20]");
    
    rule( "Q", "#22");
    
    rule( "NQ*");
    rule( "NQ*", "NQ*", "[#01-#21]");
    rule( "NQ*", "NQ*", "[#23-#FF]");
  }
  
  private IHandler handler = new IHandler() {
    public void onProduction( Rule rule, char[] buffer, int start, int length)
    {
      System.out.println( rule.name());
//      String string = new String( buffer, start, length);
//      System.out.printf( "%s: %s\n", rule.name(), string);
//      System.out.println( string);
//      System.out.printf( "�%s�\n", rule);
//      if ( start >= 0 && length >= 0)
//      {
//        System.out.printf( "%s\n", string);
//        char[] indent = new char[ start]; Arrays.fill( indent, ' ');
//        char[] underline = new char[ length]; Arrays.fill( underline, '^');
//        System.out.printf( "%s%s\n", new String( indent), new String( underline));
//      }
    }
  };
}
