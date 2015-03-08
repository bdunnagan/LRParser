package org.xidget.parser.lrk;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LR0Map
{
  public LR0Map()
  {
    map = new LinkedHashMap<LR0Locus, LRState>();
    flyweight = new LR0Locus( null);
  }
  
  public void put( Locus locus, LRState state)
  {
    map.put( new LR0Locus( locus), state);
    
    Locus parent = locus.getParent();
    while( parent != null)
    {
      if ( parent.getSymbol().equals( locus.getRule().getSymbol()))
        map.put( new LR0Locus( parent.nextInRule()), state);
      locus = parent;
      parent = parent.getParent();
    }
  }
  
  public LRState get( Locus locus)
  {
    flyweight.locus = locus;
    return map.get( flyweight);
  }
  
  public List<LRState> getStates()
  {
    return new ArrayList<LRState>( map.values());
  }
  
  private Map<LR0Locus, LRState> map;
  private LR0Locus flyweight;
  
  private static class LR0Locus
  {
    public LR0Locus( Locus locus)
    {
      this.locus = locus;
    }
    
    @Override
    public int hashCode()
    {
      return (locus.getRule().getSymbol().hashCode() & 0xFFFFFF00) ^ locus.getPosition();
    }

    @Override
    public boolean equals( Object object)
    {
      Locus other = ((LR0Locus)object).locus;
      return other.getRule().getSymbol().equals( locus.getRule().getSymbol()) && other.getPosition() == locus.getPosition();
    }

    @Override
    public String toString()
    {
      return locus.toString();
    }

    public Locus locus;
  }
}
