package org.xidget.parser.lrk;

import java.util.ArrayList;
import java.util.List;

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
  }

  public Locus getParent()
  {
    return parent;
  }
  
  public List<Locus> getAncestors()
  {
    List<Locus> ancestors = new ArrayList<Locus>();
    Locus locus = parent;
    while( locus != null)
    {
      ancestors.add( locus);
      locus = locus.getParent();
    }
    return ancestors;
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
    return (parent != null)? parent.nextInRule(): null; 
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
  
  @Override
  public int hashCode()
  {
    return (rule.hashCode() & 0xFFFFFF00) ^ pos;
  }

  @Override
  public boolean equals( Object object)
  {
    Locus other = (Locus)object;
    return other.getRule() == rule && other.getPosition() == pos;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append( rule.getSymbol().getName());
    sb.append( " :=");
    for( int i=0; i<=rule.size(); i++)
    {
      if ( i == pos) sb.append( '•');
      else sb.append( ' ');
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
}
