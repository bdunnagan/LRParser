package org.xidget.parser.lrk;

import java.util.LinkedHashMap;
import java.util.Map;

public class CompiledState
{
  public CompiledState( Locus locus)
  {
    this.locus = locus;
    expectMap = new LinkedHashMap<SymbolString, CompiledState>();
    pushMap = new LinkedHashMap<SymbolString, CompiledState>();
    popMap = new LinkedHashMap<SymbolString, Integer>();
    resumeMap = new LinkedHashMap<SymbolString, CompiledState>();
  }

  public void addExpect( SymbolString la, CompiledState next)
  {
    expectMap.put( la, next);
  }
  
  public void addPush( SymbolString la, CompiledState next)
  {
    pushMap.put( la, next);
  }
  
  public void addPop( SymbolString la, int depth)
  {
    popMap.put( la, depth);
  }
  
  public void addResume( SymbolString la, CompiledState next)
  {
    resumeMap.put( la, next);
  }

  private Locus locus;
  private Map<SymbolString, CompiledState> expectMap;
  private Map<SymbolString, CompiledState> pushMap;
  private Map<SymbolString, Integer> popMap;
  private Map<SymbolString, CompiledState> resumeMap;
}
