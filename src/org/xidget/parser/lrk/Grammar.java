package org.xidget.parser.lrk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Grammar
{
  public Grammar()
  {
    map = new HashMap<Symbol, List<Rule>>();
    order = new ArrayList<Rule>();
  }
  
  public void addRule( Symbol symbol, Rule rule)
  {
    if ( start == null)
    {
      start = new Rule();
      start.setGrammar( this);
      start.setSymbol( new Symbol( "START"));
      start.add( symbol);
    }
    
    rule.setGrammar( this);
    rule.setSymbol( symbol);
    
    List<Rule> rules = map.get( symbol);
    if ( rules == null)
    {
      rules = new ArrayList<Rule>();
      map.put( symbol, rules);
    }
    
    rules.add( rule);
    order.add( rule);
  }
  
  public Rule getStart()
  {
    return start;
  }
  
  public List<Rule> lookup( Symbol symbol)
  {
    List<Rule> rules = map.get( symbol);
    return (rules != null)? rules: Collections.<Rule>emptyList();
  }
  
  private Rule start;
  private Map<Symbol, List<Rule>> map;
  private List<Rule> order;
}
