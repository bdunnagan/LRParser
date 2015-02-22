package org.xidget.parser.lrk;

public class Locus
{
  public Locus( Locus parent, Rule rule, int pos)
  {
    this.parent = parent;
    this.rule = rule;
    this.pos = pos;
  }
  
  public void lookahead( Lookahead la, int k)
  {
    rule.lookahead( la, pos, k);
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
  
  public Symbol getSymbol()
  {
    return rule.get( pos);
  }
  
  public boolean isRuleEnd()
  {
    return pos == rule.size();
  }
  
  private Locus parent;
  private Rule rule;
  private int pos;
}
