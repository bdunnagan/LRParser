package org.xidget.parser.lrk;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LRState
{
  public LRState( Locus locus)
  {
    this.locus = locus;
    expectMap = new LinkedHashMap<List<Locus>, LRState>();
    pushMap = new LinkedHashMap<SymbolString, LRState>();
    popMap = new LinkedHashMap<SymbolString, Integer>();
    resumeMap = new LinkedHashMap<SymbolString, LRState>();
  }

  public void addExpect( SymbolString la, LRState next)
  {
    expectMap.put( la, next);
  }
  
  public void addPush( SymbolString la, LRState next)
  {
    pushMap.put( la, next);
  }
  
  public void addPop( SymbolString la, int depth)
  {
    popMap.put( la, depth);
  }
  
  public void addResume( SymbolString la, LRState next)
  {
    resumeMap.put( la, next);
  }

  private Locus locus;
  private Map<SymbolString, LRState> expectMap;
  private Map<SymbolString, LRState> pushMap;
  private Map<SymbolString, Integer> popMap;
  private Map<SymbolString, LRState> resumeMap;
}
