package org.xidget.parser.lrk;

import java.util.List;

public class Locus
{
  public Locus( Rule rule, int pos)
  {
    this.rule = rule;
    this.pos = pos;
  }

  /**
   * Returns a previous locus within the same rule.
   * @param locus The starting locus.
   * @return Returns null or the new locus.
   */
  public Locus prevInRule()
  {
    return (pos > 0)? new Locus( rule, pos-1): null;
  }
  
  /**
   * Returns a next locus within the same rule.
   * @param locus The starting locus.
   * @return Returns null or the new locus.
   */
  public Locus nextInRule()
  {
    return (pos < rule.size())? new Locus( rule, pos+1): null;
  }
  
  public List<Locus> nextInGrammar()
  {
    return TraversalAlgo.nextInGrammar( this);
  }
  
  public List<Locus> nextKernelsInGrammar()
  {
    return TraversalAlgo.nextKernelsInGrammar( this);
  }
  
  public List<List<Locus>> lookahead( int k)
  {
    return TraversalAlgo.findTerminalsInGrammar( this, k);
  }
  
  public Rule getRule()
  {
    return rule;
  }
  
  public int getPosition()
  {
    return pos;
  }
  
  public boolean isEnd()
  {
    return pos == rule.size();
  }
  
  public Symbol getSymbol()
  {
    return (pos < rule.size())? rule.get( pos): null;
  }
  
  public boolean isTerminal()
  {
    return rule.getGrammar().isTerminal( getSymbol());
  }
  
  public boolean isEmpty()
  {
    Symbol symbol = getSymbol();
    return symbol != null && symbol.isEmpty();
  }

  public boolean isStreamEnd()
  {
    Symbol symbol = getSymbol();
    return symbol != null && symbol.isStreamEnd();
  }

  @Override
  public int hashCode()
  {
    return rule.hashCode() << 7 + pos;
  }

  @Override
  public boolean equals( Object object)
  {
    Locus locus = (Locus)object;
    return locus.getRule() == rule && locus.getPosition() == pos;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append( rule.getSymbol().getName());
    sb.append( " := ");
    for( int i=0; i<=rule.size(); i++)
    {
      if ( i > 0) sb.append( ' ');
      if ( i == pos) sb.append( '•');
      if ( i < rule.size()) sb.append( rule.get( i));
    }
    return sb.toString();
  }
  
  public static String toString( String indent, List<Locus> trace)
  {
    StringBuilder sb = new StringBuilder();
    for( Locus locus: trace)
    {
      sb.append( indent);
      sb.append( locus);
      sb.append( '\n');
    }
    return sb.toString();
  }

  private Rule rule;
  private int pos;
  
  public static void main( String[] args) throws Exception
  {
    Grammar grammar = new Grammar();
    
    grammar.addTestRule( "S := A");
    grammar.addTestRule( "A := x B p");
    grammar.addTestRule( "A := x B q");
    grammar.addTestRule( "B := y");
    grammar.addTestRule( "B := ø C");
    grammar.addTestRule( "C := D");
    grammar.addTestRule( "D := w w w");
    // 1. S := A
    // 2. A := x B q
    // 3. A := x B p
    // 4. B := y
    // 5. B := ø C
    // 6. C := D
    // 7. D := w w w
    
//    Symbol sS = new Symbol( "S");
//    Symbol sA = new Symbol( "A");
//    Symbol sB = new Symbol( "B");
//    Symbol sC = new Symbol( "C");
//    Symbol sD = new Symbol( "D");
//    Symbol w = new Symbol( "w", 'w');
//    Symbol x = new Symbol( "x", 'x');
//    Symbol y = new Symbol( "y", 'y');
//    Symbol q = new Symbol( "q", 'q');
//    Symbol p = new Symbol( "p", 'p');
//
//    Rule r1 = new Rule(); 
//    Rule r2 = new Rule(); 
//    Rule r3 = new Rule(); 
//    Rule r4 = new Rule();
//    Rule r5 = new Rule();
//    Rule r6 = new Rule();
//    Rule r7 = new Rule();
//    
//    r1.add( sA); r1.add( Symbol.end);
//    r2.add( x); r2.add( sB); r2.add( p);
//    r3.add( x); r3.add( sB); r3.add( q);
//    r4.add( y);
//    r5.add( Symbol.empty); r5.add( sC);
//    r6.add( sD);
//    r7.add( w); r7.add( w); r7.add( w);
//
//    grammar.addRule( sS, r1);
//    grammar.addRule( sA, r2);
//    grammar.addRule( sA, r3);
//    grammar.addRule( sB, r4);
//    grammar.addRule( sB, r5);
//    grammar.addRule( sC, r6);
//    grammar.addRule( sD, r7);
    
    System.out.println( TraversalAlgo.dumpPaths( "", new Locus( grammar.getStart(), 0)));
    
//    LRk lrk = new LRk( grammar, 1);
//    List<LRState> states = lrk.compile();
//    for( LRState state: states)
//      System.out.printf( "%s\n", state);
  }
}
