package org.xidget.parser.lrk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Locus
{
  public Locus( Locus parent, Rule rule, int pos)
  {
    this.parent = parent;
    this.rule = rule;
    this.pos = pos;
  }
  
  public Locus nextOver()
  {
    if ( (pos+1) < rule.size())
    {
      return new Locus( parent, rule, pos+1);
    }
    else if ( parent != null)
    {
      return parent.nextOver();
    }
    else
    {
      return null;
    }
  }
  
  public List<Locus> nextInto()
  {
    Symbol symbol = getSymbol();
    if ( symbol.isTerminal())
    {
      if ( (pos+1) < rule.size())
      {
        return Collections.singletonList( new Locus( parent, rule, pos+1));
      }
      else if ( parent != null)
      {
        return parent.nextInto();
      }
      else
      {
        return null;
      }
    }
    else
    {
      List<Locus> nextLoci = new ArrayList<Locus>();
      for( Rule followRule: rule.getGrammar().lookup( symbol))
      {
        if ( followRule != rule)
          nextLoci.add( new Locus( this, followRule, 0));
      }
      return nextLoci;
    }
  }
  
  public Locus getRoot()
  {
    if ( parent == null) return this;
    return parent.getRoot();
  }
  
  public int getDepth()
  {
    int depth = 0;
    Locus locus = parent;
    while( locus != null)
    {
      locus = locus.getParent();
      depth++;
    }
    return depth;
  }
  
  public Locus getParent()
  {
    return parent;
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
    return rule.get( pos);
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append( rule.getName());
    sb.append( " := ");
    for( int i=0; i<rule.size(); i++)
    {
      if ( i > 0) sb.append( ' ');
      if ( i == pos) sb.append( '•');
      sb.append( rule.get( i));
    }
    return sb.toString();
  }

  private Locus parent;
  private Rule rule;
  private int pos;
}
