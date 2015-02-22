package org.xidget.parser.lrk;

import java.util.List;

public class Grammar
{
  public Rule getStartRule()
  {
    return startRule;
  }
  
  public List<Rule> lookup( Symbol symbol)
  {
  }
  
  private Rule startRule;
}
