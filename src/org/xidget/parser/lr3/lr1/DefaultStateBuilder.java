package org.xidget.parser.lr3.lr1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xidget.parser.lr3.Grammar;
import org.xidget.parser.lr3.State;
import org.xidget.parser.lr3.State.StackOp;
import org.xidget.parser.lr3.lr1.LR1Event.Type;
import org.xmodel.log.Log;

public class DefaultStateBuilder implements IStateBuilder
{
  public DefaultStateBuilder()
  {
    states = new HashMap<LR1ItemSet, State>();
  }
  
  /* (non-Javadoc)
   * @see org.xidget.parser.lr3.lr1.IStateBuilder#createState(org.xidget.parser.lr3.Grammar, org.xidget.parser.lr3.lr1.LR1ItemSet, java.util.List, java.util.List)
   */
  @Override
  public void createState( Grammar grammar, LR1ItemSet itemSet, List<LR1Event> tshifts, List<LR1Event> ntshifts)
  {
    State state = new State();
    state.index = ++counter;
    state.itemSet = itemSet;
    states.put( itemSet, state);
    
    createSymbolTable( grammar, state, tshifts, ntshifts);
    
    // terminals
    for( int i=0; i<tshifts.size(); i++)
    {
      LR1Event action = tshifts.get( i);
      
      switch( action.type)
      {
        case tshift: state.stackOps[ i].next = states.get( action.itemSet); break;
        case reduce: state.stackOps[ i].reduce = action.item.rule; break;
        case accept: state.stackOps[ i].next = null; break;
        case ntshift: throw new IllegalStateException();
      }
      
      state.stackOps[ i].next = states.get( action.itemSet);
    }

    // non-terminals
    state.gotos = new State[ grammar.rules().size()];
    
    for( int i=0; i<ntshifts.size(); i++)
    {
      LR1Event action = ntshifts.get( i);
      state.gotos[ action.symbols[ 0]] = states.get( action.itemSet);
    }
  }
  
  /* (non-Javadoc)
   * @see org.xidget.parser.lr3.lr1.IStateBuilder#resolveTerminalConflicts(org.xidget.parser.lr3.Grammar, org.xidget.parser.lr3.lr1.LR1ItemSet, java.util.List)
   */
  @Override
  public void resolveTerminalConflicts( Grammar grammar, LR1ItemSet itemSet, List<LR1Event> tOps)
  {
    List<State> splits = new ArrayList<State>();
    
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
    // Rule 2:
    //
    
    
    boolean conflict =
      (curr.type == LR1Event.Type.tshift && prev.type == LR1Event.Type.reduce) || 
      (curr.type == LR1Event.Type.reduce && prev.type == LR1Event.Type.tshift) ||
      (curr.type == LR1Event.Type.tshift && prev.type == LR1Event.Type.tshift);

    if ( conflict)
    {
      long prevPriority = prev.getPriority();
      long currPriority = curr.getPriority();
      if ( prevPriority < currPriority)
      {
        log.debugf( "\nConflict in state %d:\n    %s\n    %s", state.index, prev, curr);
        log.debug( "Conflict resolved: second rule(s) have higher priority");
        deleteStackOp( state, k-1); k--;
      }
      else if ( prevPriority > currPriority)
      {
        log.debugf( "\nConflict in state %d:\n    %s\n    %s", state.index, prev, curr);
        log.debug( "Conflict resolved: first rule(s) have higher priority");
        deleteStackOp( state, k); k--;
      }
      else
      {
        log.warnf( "\nConflict in state %d:\n    %s\n    %s", state.index, prev, curr);
        log.warn( "Conflict resolved by splitting state");
        splitState( state, k, prevSplit, splits);
        currSplit = splits.get( splits.size() - 1);
        conflicts++;
      }
    }
    else if ( curr.type == LR1Event.Type.reduce && prev.type == LR1Event.Type.reduce)
    {
      if ( prev.symbols.length > curr.symbols.length)
      {
        StackOp[] ops = new StackOp[ state.stackOps.length - 1];
        System.arraycopy( state.stackOps, 0, ops, 0, i);
        System.arraycopy( state.stackOps, i+1, ops, i, ops.length - i);    
        state.stackOps = ops;
        tshifts.remove( i--);
      }
      else
      {
        tshifts.remove( --i);
        StackOp[] ops = new StackOp[ state.stackOps.length - 1];
        System.arraycopy( state.stackOps, 0, ops, 0, i);
        System.arraycopy( state.stackOps, i+1, ops, i, ops.length - i);    
        state.stackOps = ops;
      }
    }
    
    if ( splits.size() > 0)
    {
      for( State split: splits) 
      {
        log.debugf( "Created new state %d to resolve conflict.", split.index);
        removeNulls( split);
        
        for( StackOp shift: split.stackOps)
        {
          if ( shift == nullShift)
            throw new IllegalStateException();
        }
      }

      state.splits = splits.toArray( new State[ 0]);
      state.stackOps = null;
      state.gotos = null;
    }
  }

  /* (non-Javadoc)
   * @see org.xidget.parser.lr3.lr1.IStateBuilder#resolveNonTerminalConflicts(org.xidget.parser.lr3.Grammar, org.xidget.parser.lr3.lr1.LR1ItemSet, java.util.List)
   */
  @Override
  public void resolveNonTerminalConflicts( Grammar grammar, LR1ItemSet itemSet, List<LR1Event> events)
  {
    for( int i=1; i<ntshifts.size(); i++)
    {
      prevSplit = currSplit;
      currSplit = null;
      
      LR1Event prev = ntshifts.get( i-1);
      LR1Event curr = ntshifts.get( i);
      
      if ( curr.symbols[ 0] <= prev.symbols[ 0])
      {
        if ( curr.type == LR1Event.Type.ntshift && prev.type == LR1Event.Type.ntshift && curr.itemSet.equals( prev.itemSet))
        {
          log.warnf( "\nConflict in state %d:\n    %s\n    %s", state.index, prev, curr);
          conflicts++;
        }
      }
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
  }

  /**
   * Create the symbol table for the specified state.
   * @param grammar The grammar.
   * @param state The state.
   * @param tshifts The state changes triggered by terminals.
   * @param ntshifts The state changes triggered by non-terminals.
   */
  private void createSymbolTable( Grammar grammar, State state, List<LR1Event> tshifts, List<LR1Event> ntshifts)
  {
    state.stackOps = new StackOp[ tshifts.size()];
    for( int i=0; i<state.stackOps.length; i++)
    {
      StackOp shift = new StackOp();
      state.stackOps[ i] = shift;
      
      int[] symbol = tshifts.get( i).symbols;
      if ( symbol.length == 2)
      {
        shift.low = symbol[ 0];
        shift.high = symbol[ 1];
      }
      else if ( symbol.length == 1)
      {
        shift.low = symbol[ 0];
        shift.high = symbol[ 0];
      }
    }
  }
  
  /**
   * Remove null shifts introduced into split state.
   * @param state A split state.
   */
  private void removeNulls( State state)
  {
    StackOp[] shifts = new StackOp[ state.stackOps.length - 1];
    for( int i=0, j=0; j < shifts.length; i++)
    {
      if ( state.stackOps[ i] != nullShift)
        shifts[ j++] = state.stackOps[ i];
    }
    state.stackOps = shifts;
  }
    
  /**
   * Split the specified state at the specified overlapping symbol.
   * @param state The state.
   * @param i The index of the overlapping symbol.
   * @param split Null or the previous split.
   * @param result The list where the new states will be added.
   */
  private void splitState( State state, int i, State split, List<State> result)
  {
    if ( split == null)
    {
      State v1 = copy( state);
      v1.stackOps[ i] = nullShift;
      result.add( v1);
    }
    
    State v2 = copy( state);
    v2.stackOps[ i-1] = nullShift;
    result.add( v2);
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
    copy.itemSet = state.itemSet;
    return copy;
  }  
  
  private static Log log = Log.getLog( DefaultStateBuilder.class);
  private final static StackOp nullShift = new StackOp();
  
  private Map<LR1ItemSet, State> states;
  private int counter;
  private int conflicts;
}
