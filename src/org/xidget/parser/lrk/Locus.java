package org.xidget.parser.lrk;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Locus
{
  public Locus( Rule rule)
  {
    this( null, rule, 0);
  }
  
  public Locus( Locus parent, Rule rule, int pos)
  {
    this.parent = parent;
    this.rule = rule;
    this.pos = pos;
    this.visited = (parent != null)? new HashSet<Rule>( parent.visited): new HashSet<Rule>();
  }

  public Locus getParent()
  {
    return parent;
  }
  
  public int getDepth()
  {
    int depth = 0;
    while( parent != null)
    {
      depth++;
      parent = parent.getParent();
    }
    return depth;
  }
  
  public Locus previousInGrammar()
  {
    Locus previous = prevInRule();
    if ( previous != null) return previous;
    return (parent != null)? parent: null;
  }
  
  public Locus nextInGrammar()
  {
    Locus next = nextInRule();
    if ( next != null) return next;
    if ( parent != null)
    {
      parent.visited.addAll( visited);
      return parent.nextInRule();
    }
    return null;
  }
  
  /**
   * Returns a previous locus within the same rule.
   * @param locus The starting locus.
   * @return Returns null or the new locus.
   */
  public Locus prevInRule()
  {
    return (pos > 0)? new Locus( parent, rule, pos-1): null;
  }
  
  /**
   * Returns a next locus within the same rule.
   * @param locus The starting locus.
   * @return Returns null or the new locus.
   */
  public Locus nextInRule()
  {
    return (pos < rule.size())? new Locus( parent, rule, pos+1): null;
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
  
  public boolean visit( Rule rule)
  {
    return visited.add( rule);
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

  private Locus parent;
  private Rule rule;
  private int pos;
  private Set<Rule> visited;
}
