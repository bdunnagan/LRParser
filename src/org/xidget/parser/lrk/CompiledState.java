package org.xidget.parser.lrk;

import java.util.ArrayList;
import java.util.List;

public class CompiledState
{
  public CompiledState()
  {
    transitions = new ArrayList<Transition>();
  }

  public void addTransition( SymbolString string, CompiledState state, Rule production)
  {
    // if terminals in string are matched then,
    //   1. consume consecutive terminals only
    //   2. end on first reduction
    
    Transition transition = new Transition();
    transition.string = string;
    transition.state = state;
    transition.production = production;
    transitions.add( transition);
  }
  
  public static class Transition
  {
    public SymbolString string;
    public CompiledState state;
    public Rule production;
  }

  private List<Transition> transitions;
}
