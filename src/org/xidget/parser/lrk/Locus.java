package org.xidget.parser.lrk;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Locus
{
  public Locus( Rule rule, int pos)
  {
    this.rule = rule;
    this.pos = pos;
  }

  public List<Locus> next()
  {
    Symbol symbol = getSymbol();
    if ( symbol == null)
    {
      // Since we are at the end of this rule, find all loci in the grammar
      // immediately following where this rule is used. The lookahead from
      // these loci will decide which reduction is chosen, and how many states
      // to pop off the stack.
      return rule.getGrammar().usage( rule.getSymbol());
    }
    else if ( symbol.isTerminal())
    {
      return Collections.singletonList( new Locus( rule, pos+1));
    }
    else
    {
      List<Locus> loci = new ArrayList<Locus>();
      for( Rule match: rule.getGrammar().lookup( symbol))
      {
        if ( match != rule)
          loci.add( new Locus( match, 0));
      }
      return loci;
    }
  }
  
  /**
   * @return Returns a list of all terminals seen from this locus.
   */
  public Set<Symbol> terminals()
  {
    Set<Symbol> symbols = new HashSet<Symbol>();
    
    Deque<Locus> stack = new ArrayDeque<Locus>();
    stack.push( this);
    while( !stack.isEmpty())
    {
      Locus locus = stack.pop();
      Symbol symbol = locus.getSymbol();
      if ( symbol == null || symbol.isEmpty())
      {
        for( Locus next: locus.next())
          stack.push( next);
      }
      else if ( symbol.isTerminal())
      {
        symbols.add( symbol);
      }
      else
      {
        for( Rule match: rule.getGrammar().lookup( symbol))
        {
          if ( match != rule)
          {
            stack.push( new Locus( match, 0));
          }
        }
      }
    }
    
    return symbols;
  }
  
  public Rule getRule()
  {
    return rule;
  }
  
  public int getPosition()
  {
    return pos;
  }
  
  public boolean isLast()
  {
    return (pos+1) == rule.size();
  }
  
  public Symbol getSymbol()
  {
    return (pos < rule.size())? rule.get( pos): null;
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

  private Rule rule;
  private int pos;
  
  public static void main( String[] args) throws Exception
  {
    Grammar grammar = new Grammar();
    
    // 1. S := A
    // 2. A := x B z
    // 3. B := y
    // 4. B := y y
    
    Symbol sS = new Symbol( "S");
    Symbol sA = new Symbol( "A");
    Symbol sB = new Symbol( "B");
    Symbol w = new Symbol( "w", 'w');
    Symbol x = new Symbol( "x", 'x');
    Symbol y = new Symbol( "y", 'y');
    Symbol z = new Symbol( "z", 'z');

    Rule r1 = new Rule(); 
    Rule r2 = new Rule(); 
    Rule r3 = new Rule(); 
    Rule r4 = new Rule();
    
    r1.add( sA);
    r2.add( x); r2.add( sB); r2.add( z);
    r3.add( y);
    r4.add( w);

    grammar.addRule( sS, r1);
    grammar.addRule( sA, r2);
    grammar.addRule( sB, r3);
    grammar.addRule( sB, r4);
    
    Deque<Locus> stack = new ArrayDeque<Locus>();
    stack.push( new Locus( grammar.getStart(), 0));
    while( !stack.isEmpty())
    {
      Locus locus = stack.pop();
      
      System.out.printf( "%-30s ", locus);
      for( Symbol symbol: locus.terminals())
        System.out.printf( "%s ", symbol);
      System.out.println();
      
      List<Locus> nextLoci = locus.next();
      if ( nextLoci != null)
      {
        for( Locus nextLocus: nextLoci)
          stack.push( nextLocus);
      }
    }
  }
}
