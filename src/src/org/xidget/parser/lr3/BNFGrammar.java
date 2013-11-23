package org.xidget.parser.lr3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;

import org.xidget.parser.lr3.Rule.IHandler;
import org.xidget.parser.lr3.lr1.LR1;

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
    addRule( "Rule+", "Rule");
    addRule( "Rule+", "Rule+", "Rule");
    
    addRule( handler, "Rule", "Name", "S*", ":", "=", "S*", "Decl", "S*", ";", "S*");
    addRule( handler, "Rule", "Name", "S*", ":", "=", "S*", "Decl", "S*", "XGS", "S*", ";", "S*");
    
    addRule( handler, "Name", "W+");
    
    addRule( "Decl", "Reference");
    addRule( "Decl", "Symbols");
    addRule( "Decl", "Group");
    addRule( "Decl", "Choice");
    addRule( "Decl", "Decl", "S+", "Reference");
    addRule( "Decl", "Decl", "S+", "Symbols");
    addRule( "Decl", "Decl", "S+", "Group");
    
    addRule( handler, "Reference", "W+", "Quantifier");
    addRule( handler, "Symbols", "Q", "NQ*", "Q", "Quantifier");
    addRule( handler, "Symbols", "#5B", "CharSet", "#5D", "Quantifier");
    addRule( handler, "Group", "(", "S*", "Decl", "S*", ")", "Quantifier");
    addRule( handler, "Choice", "Decl", "S*", "|", "S*", "Decl");
    
    addRule( "CharSet", "#5C");
    addRule( "CharSet", "[#21-#5A]");
    addRule( "CharSet", "[#5E-#FF]");
    addRule( "CharSet", "CharSet", "#5C");
    addRule( "CharSet", "CharSet", "[#21-#5A]");
    addRule( "CharSet", "CharSet", "[#5E-#FF]");
    
    addRule( handler, "XGS", "/", "*", "Comment+", "*", "/");
    addRule( "Comment+", "[#01-#29]");
    addRule( "Comment+", "[#2B-#FF]");
    addRule( "Comment+", "Comment+", "[#01-#29]");
    addRule( "Comment+", "Comment+", "[#2B-#FF]");
    
    addRule( "Quantifier");
    addRule( handler, "Quantifier", "?");
    addRule( handler, "Quantifier", "*");
    addRule( handler, "Quantifier", "+");
  }
  
  private void buildTokens()
  {
    addRule( "W", "[a-z]");
    addRule( "W", "[A-Z]");
    addRule( "W", "[0-9]");
    
    addRule( "W+", "[a-z]");
    addRule( "W+", "[A-Z]");
    addRule( "W+", "[0-9]");
    addRule( "W+", "W+", "[a-z]");
    addRule( "W+", "W+", "[A-Z]");
    addRule( "W+", "W+", "[0-9]");
    
    addRule( "S*");
    addRule( "S*", "S*", "[#01-#20]");
    
    addRule( "S+", "[#01-#20]");
    addRule( "S+", "S+", "[#01-#20]");
    
    addRule( "Q", "#22");
    
    addRule( "NQ*");
    addRule( "NQ*", "NQ*", "[#01-#21]");
    addRule( "NQ*", "NQ*", "[#23-#FF]");
  }
  
  private IHandler handler = new IHandler() {
    public void onProduction( Rule rule, char[] buffer, int start, int length)
    {
//      String string = new String( buffer, start, length);
//      System.out.printf( "%s: %s\n", rule.name(), string);
//      System.out.println( string);
//      System.out.printf( "Ç%sÈ\n", rule);
//      if ( start >= 0 && length >= 0)
//      {
//        System.out.printf( "%s\n", string);
//        char[] indent = new char[ start]; Arrays.fill( indent, ' ');
//        char[] underline = new char[ length]; Arrays.fill( underline, '^');
//        System.out.printf( "%s%s\n", new String( indent), new String( underline));
//      }
    }
  };
    
  public static void main( String[] args) throws Exception
  {
    BNFGrammar grammar = new BNFGrammar();
    
    LR1 lr = new LR1();
    Parser parser = lr.compile( grammar);
    
    System.out.println( "\nParse...\n");
    
//    BufferedReader reader = new BufferedReader( new InputStreamReader( System.in));
//    while( true)
//    {
//      System.out.print( "> ");
//      String line = reader.readLine();
//      if ( line.equals( " ")) break;
//      parser.parse( new StringReader( line));
//    }
    
    File file = new File( "XPath.txt");
    System.out.println( file.getAbsolutePath());
    
    BufferedReader reader = new BufferedReader( new FileReader( file));
    StringBuilder sb = new StringBuilder();
    while( reader.ready())
    {
      sb.append( reader.readLine());
      sb.append( ";\n");
    }
    
    String s = sb.toString();
    //System.out.println( s);
    
    long t0 = System.nanoTime();
    int n = 1;
    for( int i=0; i<n; i++)
    {
      StringReader input = new StringReader( s);
      parser.parse( input);
    }
    
    float speed = (System.nanoTime() - t0) / (float)n / sb.length();
    System.out.printf( "time = %f ms/mb\n", speed);
    
    DFA.searches /= n;
    DFA.loops /= n;
    
    System.out.printf( "loops         = %,12d\n", DFA.loops);
    System.out.printf( "reduces       = %,12d %4.1f\n", DFA.reduces, (float)DFA.reduces / DFA.loops);
    System.out.printf( "search(shift) = %,12d %4.1f\n", DFA.searches, (float)DFA.searches / DFA.loops);
  }
}
