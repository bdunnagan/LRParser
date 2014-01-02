package org.xidget.parser.lr3.lr1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xidget.parser.lr3.Grammar;
import org.xidget.parser.lr3.Parser;
import org.xidget.parser.lr3.State;
import org.xidget.parser.lr3.State.StackOp;
import org.xidget.parser.lr3.lr1.LR1Event.Type;
import org.xmodel.log.Log;

public class DefaultStateBuilder implements IStateBuilder
{
  public DefaultStateBuilder()
  {
    states = new HashMap<LR1ItemSet, State>();
    itemSets = new HashMap<State, LR1ItemSet>();
    branches = new HashSet<LR1Event>();
  }
  
  /**
   * @return Returns the generated parser.
   */
  public Parser getParser()
  {
    return new Parser( this, start);
  }
  
  /**
   * Get the item set for the specified state.
   * @param state The state.
   * @return Returns the item set.
   */
  public LR1ItemSet getItemSet( State state)
  {
    return itemSets.get( state);
  }
  
  /* (non-Javadoc)
   * @see org.xidget.parser.lr3.lr1.IStateBuilder#createState(org.xidget.parser.lr3.Grammar, org.xidget.parser.lr3.lr1.LR1ItemSet, java.util.List, java.util.List)
   */
  @Override
  public void createState( Grammar grammar, LR1ItemSet itemSet, List<LR1Event> tOps, List<LR1Event> ntOps)
  {
    State state = getCreateState( itemSet);
    if ( state.stackOps == null) createSymbolTable( grammar, state, tOps, ntOps);
    
    if ( branches.size() > 0)
    {
      state.branches = new State[ branches.size()];
      state.stackOps = null;
      
      List<LR1Event> btOps = new ArrayList<LR1Event>( tOps);
      btOps.removeAll( branches);
      btOps.add( branches);
      
      for( State branch: state.branches)
      {
      }
    }
    else
    {
      createStackOps( grammar, state, tOps, ntOps);
    }
    
    
    // terminals
    for( int i=0; i<tOps.size(); i++)
    {
      LR1Event tOp = tOps.get( i);
      StackOp stackOp = state.stackOps[ i];
      
      switch( tOp.type)
      {
        case tshift: stackOp.next = getCreateState( tOp.itemSet); break;
        case reduce: stackOp.reduce = tOp.item.rule; break;
        case accept: stackOp.next = null; break;
        case ntshift: throw new IllegalStateException();
      }
      
      stackOp.next = getCreateState( tOp.itemSet);
      if ( branched.containsKey( tOp)) branched.put( tOp, i);
    }
    
    // non-terminals
    state.gotos = new State[ grammar.rules().size()];
    for( int i=0; i<ntOps.size(); i++)
    {
      LR1Event ntOp = ntOps.get( i);
      State target = getCreateState( ntOp.itemSet);
      if ( target == null) throw new IllegalStateException();
      state.gotos[ ntOp.symbols[ 0]] = target;
    }
    
    // branches
    if ( branched.size() > 0)
    {
      state.branches = new State[ branched.size()];
      int i=0;
      for( Map.Entry<LR1Event, Integer> entry: branched.entrySet())
      {
        State newState = copy( state);
        newState.stackOps[ entry.getValue()] = null;
        state.branches[ i++] = newState;
      }
      branched.clear();
      
      state.stackOps = null;
    }
  }
  
  private void createStackOps( Grammar grammar, State state, List<LR1Event> tOps, List<LR1Event> ntOps)
  {
    // terminals
    for( int i=0; i<tOps.size(); i++)
    {
      LR1Event tOp = tOps.get( i);
      StackOp stackOp = state.stackOps[ i];
      
      switch( tOp.type)
      {
        case tshift: stackOp.next = getCreateState( tOp.itemSet); break;
        case reduce: stackOp.reduce = tOp.item.rule; break;
        case accept: stackOp.next = null; break;
        case ntshift: throw new IllegalStateException();
      }
      
      stackOp.next = getCreateState( tOp.itemSet);
    }
    
    // non-terminals
    state.gotos = new State[ grammar.rules().size()];
    for( int i=0; i<ntOps.size(); i++)
    {
      LR1Event ntOp = ntOps.get( i);
      State target = getCreateState( ntOp.itemSet);
      if ( target == null) throw new IllegalStateException();
      state.gotos[ ntOp.symbols[ 0]] = target;
    }    
  }
  
  private State getCreateState( LR1ItemSet itemSet)
  {
    State state = states.get( itemSet);
    if ( state == null)
    {
      state = new State();
      state.index = ++counter;
      states.put( itemSet, state);
      itemSets.put( state, itemSet);
      
      if ( start == null) start = state;
    }
    return state;
  }
  
  /* (non-Javadoc)
   * @see org.xidget.parser.lr3.lr1.IStateBuilder#handleTerminalConflicts(org.xidget.parser.lr3.Grammar, org.xidget.parser.lr3.lr1.LR1ItemSet, java.util.List)
   */
  @Override
  public void handleTerminalConflicts( Grammar grammar, LR1ItemSet itemSet, List<LR1Event> tOps)
  {
    //
    // Rule 1: If there is at least one shift, then ignore reductions.
    // (Type.reduce follows Type.tshift)
    //
    if ( tOps.get( 0).type == Type.tshift)
    {
      resolveTerminalShiftConflicts( grammar, itemSet, tOps);
      return;
    }
    
    //
    // Rule 2: Resolve reduction conflicts by choosing the rule that comes first in the grammar.
    //
    if ( tOps.get( 0).type == Type.reduce)
    {
      resolveTerminalReduceConflicts( grammar, itemSet, tOps);
      return;
    }
  }
  
  /**
   * Resolve terminal shift conflicts (and ignore reductions).
   * @param grammar The grammar.
   * @param itemSet The item set.
   * @param tOps The terminal operations.
   */
  private void resolveTerminalShiftConflicts( Grammar grammar, LR1ItemSet itemSet, List<LR1Event> tOps)
  {
    for( LR1Event tOp: tOps)
      branches.add( tOp);
  }

  /**
   * Resolve terminal reduce conflicts.
   * @param grammar The grammar.
   * @param itemSet The item set.
   * @param tOps The terminal operations.
   */
  private void resolveTerminalReduceConflicts( Grammar grammar, LR1ItemSet itemSet, List<LR1Event> tOps)
  {
    int minRuleIndex = Integer.MAX_VALUE;
    int minOpIndex = 0;
    for( int i=0; i<tOps.size(); i++)
    {
      LR1Event tOp = tOps.get( i);
      int index = grammar.rules().indexOf( tOp.item.rule);
      if ( index < minRuleIndex) 
      {
        minRuleIndex = index;
        minOpIndex = i;
      }
    }
    
    tOps.subList( 0, minOpIndex).clear();
    tOps.subList( minOpIndex + 1, tOps.size()).clear();
  }

  /* (non-Javadoc)
   * @see org.xidget.parser.lr3.lr1.IStateBuilder#handleNonTerminalConflicts(org.xidget.parser.lr3.Grammar, org.xidget.parser.lr3.lr1.LR1ItemSet, java.util.List)
   */
  @Override
  public void handleNonTerminalConflicts( Grammar grammar, LR1ItemSet itemSet, List<LR1Event> ntOps)
  {
    StringBuilder sb = new StringBuilder();
    for( LR1Event ntOp: ntOps)
    {
      sb.append( ntOp.item);
      sb.append( '\n');
    }
    log.warnf( "The following rules conflict:\n%s", sb);
  }
  
  /**
   * Create the symbol table for the specified state.
   * @param grammar The grammar.
   * @param state The state.
   * @param tOps The state changes triggered by terminals.
   * @param ntOps The state changes triggered by non-terminals.
   */
  private void createSymbolTable( Grammar grammar, State state, List<LR1Event> tOps, List<LR1Event> ntOps)
  {
    state.stackOps = new StackOp[ tOps.size()];
    for( int i=0; i<state.stackOps.length; i++)
    {
      StackOp stackOp = new StackOp();
      state.stackOps[ i] = stackOp;
      
      int[] symbol = tOps.get( i).symbols;
      if ( symbol.length == 2)
      {
        stackOp.low = symbol[ 0];
        stackOp.high = symbol[ 1];
      }
      else if ( symbol.length == 1)
      {
        stackOp.low = symbol[ 0];
        stackOp.high = symbol[ 0];
      }
    }
  }
  
  /**
   * Create a copy of the specified state.
   * @param state The state.
   * @return Returns the copy.
   */
  private final State copy( State state)
  {
    State copy = new State();
    copy.index = counter++;
    copy.stackOps = Arrays.copyOf( state.stackOps, state.stackOps.length);
    copy.gotos = Arrays.copyOf( state.gotos, state.gotos.length);
    return copy;
  }    
  
  private static Log log = Log.getLog( DefaultStateBuilder.class);
  
  private Map<LR1ItemSet, State> states;
  private Map<State, LR1ItemSet> itemSets;
  private Set<LR1Event> branches;
  private int counter;
  private State start;
}
