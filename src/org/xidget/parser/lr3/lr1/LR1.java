package org.xidget.parser.lr3.lr1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.xidget.parser.lr3.Grammar;
import org.xidget.parser.lr3.Parser;
import org.xidget.parser.lr3.Rule;
import org.xidget.parser.lr3.State;
import org.xidget.parser.lr3.State.StackOp;
import org.xmodel.log.Log;

/**
 * An LR1 table generator.
 */
public class LR1
{
  public LR1()
  {
    counter = 0;
  }
  
  /**
   * Compile the specified grammar and return a DFA.
   * @param grammar The grammar.
   * @return Returns the DFA.
   */
  public Parser compile( Grammar grammar)
  {
    grammar.augment();
    
    conflicts = 0;
    
    itemSets = createItemSets( grammar);
    State start = createStates( grammar, itemSets);
    grammar.freeGraph();

    log.infof( "\nFound %d conflicts in %d states.", conflicts, itemSets.size());
    
    return new Parser( this, start);
  }

  /**
   * @return Returns the generated item sets.
   */
  public Collection<LR1ItemSet> itemSets()
  {
    return itemSets;
  }
  
  /**
   * Returns the LR1ItemSet associated with the specified state.
   * @param state The state.
   * @return Returns the LR1ItemSet associated with the specified state.
   */
  public LR1ItemSet itemSet( State state)
  {
    return itemSetMap.get( state);
  }
  
  /**
   * Create the ItemSets for the specified grammar.
   * @param grammar The grammar.
   * @return Returns the ItemSets.
   */
  private Collection<LR1ItemSet> createItemSets( Grammar grammar)
  {
    Map<LR1ItemSet, LR1ItemSet> itemSets = new LinkedHashMap<LR1ItemSet, LR1ItemSet>();
    
    LR1ItemSet itemSet = new LR1ItemSet();
    itemSet.kernel.add( new LR1Item( grammar.rules().get( 0), Grammar.terminus));
    itemSet.closure( grammar);
    itemSets.put( itemSet, itemSet);
    
    List<LR1ItemSet> stack = new ArrayList<LR1ItemSet>();
    stack.add( itemSet);
    while( stack.size() > 0)
    {
      itemSet = stack.remove( 0);

      //
      // Compute the kernel of the successors of the LR1ItemSet, but only incur the cost
      // of closure if the LR1ItemSet has not already been created.  LR1ItemSets are unique
      // by kernel.
      //
      itemSet.successors( grammar);
      for( LR1ItemSet successor: itemSet.successors.values())
      {
        LR1ItemSet existing = itemSets.get( successor);
        if ( existing != null)
        {
          LR1Item firstItem = existing.kernel.iterator().next();
          String leadSymbol = firstItem.rule.rhs().get( firstItem.dot - 1);
          itemSet.successors.put( leadSymbol, existing);
        }
        else
        {
          successor.closure( grammar);
          itemSets.put( successor, successor);
          stack.add( successor);
        }
      }
    }
    
    return itemSets.values();
  }

  /**
   * Create states for the specified LR1ItemSets.
   * @param grammar The grammar.
   * @param itemSets The item sets.
   * @return Returns the first state.
   */
  private State createStates( Grammar grammar, Collection<LR1ItemSet> itemSets)
  {
    itemSetMap = new HashMap<State, LR1ItemSet>();
    
    for( LR1ItemSet itemSet: itemSets) 
    {
      log.debugf( "\n%s", itemSet);
      itemSet.state = new State();
      itemSet.state.index = counter++;
      itemSetMap.put( itemSet.state, itemSet);
    }
    
    for( LR1ItemSet itemSet: itemSets)
    {
      Set<String> ts = new HashSet<String>();
      Set<String> nts = new HashSet<String>();
      
      List<Action> tshifts = new ArrayList<Action>();
      List<Action> ntshifts = new ArrayList<Action>();
      
      for( LR1Item item: itemSet.items)
      {
        if ( item.complete())
        {
          if ( item.rule == grammar.rules().get( 0))
          {
            if ( item.laList.contains( Grammar.terminus))
            {
              int[] terminal = new int[] { Grammar.terminusChar};
              tshifts.add( new Action( Action.Type.accept, terminal, item));
            }
          }
          else
          {
            for( String la: item.laList)
            {
              int[] terminal = grammar.toTerminal( la);
              tshifts.add( new Action( Action.Type.reduce, terminal, item));
            }
          }
        }
        else
        {
          if ( !grammar.isTerminal( item.symbol()))
          {
            nts.add( item.symbol());
          }
          else
          {
            Set<String> first = item.first( grammar);
            LR1ItemSet successor = itemSet.successors.get( item.symbol());
            if ( successor != null)
            {
              for( String symbol: first)
              {
                if ( ts.add( symbol))
                {
                  int[] terminal = grammar.toTerminal( symbol);
                  tshifts.add( new Action( Action.Type.tshift, terminal, successor));
                }
              }
            }
          }
        }
      }
      
      for( String symbol: nts)
      {
        LR1ItemSet successor = itemSet.successors.get( symbol);
        if ( successor != null)
        {
          List<Rule> rules = grammar.lhs( symbol);
          for( Rule rule: rules)
          {
            Action action = new Action( Action.Type.ntshift, new int[] { rule.symbol()}, successor);
            ntshifts.add( action);
          }
        }
      }
      
      buildActionTable( grammar, itemSet.state, tshifts, ntshifts);
    }
    
    return itemSets.iterator().next().state;
  }

  /**
   * Populate the action table of the specified state.
   * @param state The state.
   * @param tshifts The state changes triggered by terminals.
   * @param ntshifts The state changes triggered by non-terminals.
   */
  private void buildActionTable( Grammar grammar, State state, List<Action> tshifts, List<Action> ntshifts)
  {
    Collections.sort( tshifts);
    
    createSymbolTable( grammar, state, tshifts, ntshifts);
    
    state.gotos = new State[ grammar.rules().size()];
    
    log.debugf( "\nState: %d -----------------------\n%s", state.index, itemSet( state));
    
    for( int i=0; i<tshifts.size(); i++)
    {
      Action action = tshifts.get( i);
      if ( action.omit) continue;
      
      switch( action.type)
      {
        case tshift: state.stackOps[ i].next = action.itemSet.state; break;
        case reduce: state.stackOps[ i].reduce = action.item.rule; break;
        case accept: state.stackOps[ i].next = null; break;
      }
      
      state.stackOps[ i].next = (action.itemSet != null)? action.itemSet.state: null;
      log.debugf( "    %s", action);
    }
    
    for( int i=0; i<ntshifts.size(); i++)
    {
      Action action = ntshifts.get( i);
      if ( action.omit) continue;
      
      state.gotos[ action.symbols[ 0]] = action.itemSet.state;
      log.debugf( "    %s", action);
    }
    
    resolveConflicts( grammar, state, tshifts, ntshifts);
  }
  
  /**
   * Resolve any conflicts found in the specified state by splitting it for GLR processing.
   * This is the only place where conflicts are identified because the sparse symbol table 
   * can contain overlapping symbol ranges or duplicate symbols.
   * @param grammar The grammar.
   * @param state The state.
   * @param tshifts The state changes triggered by terminals.
   * @param ntshifts The state changes triggered by non-terminals.
   */
  private void resolveConflicts( Grammar grammar, State state, List<Action> tshifts, List<Action> ntshifts)
  {
    List<State> splits = new ArrayList<State>();

    Collections.sort( tshifts);
    Collections.sort( ntshifts);
    
    State currSplit = null;
    State prevSplit = null;
    for( int i=1, k=1; i<tshifts.size(); i++, k++)
    {
      prevSplit = currSplit;
      currSplit = null;
      
      Action prev = tshifts.get( i-1);
      Action curr = tshifts.get( i);
      
      if ( isOverlapping( prev.symbols, curr.symbols))
      {
        boolean conflict =
          (curr.type == Action.Type.tshift && prev.type == Action.Type.reduce) || 
          (curr.type == Action.Type.reduce && prev.type == Action.Type.tshift) ||
          (curr.type == Action.Type.reduce && prev.type == Action.Type.reduce) ||
          (curr.type == Action.Type.tshift && prev.type == Action.Type.tshift);

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
      }
    }
    
    for( int i=1; i<ntshifts.size(); i++)
    {
      prevSplit = currSplit;
      currSplit = null;
      
      Action prev = ntshifts.get( i-1);
      Action curr = ntshifts.get( i);
      
      if ( curr.symbols[ 0] <= prev.symbols[ 0])
      {
        if ( curr.type == Action.Type.ntshift && prev.type == Action.Type.ntshift && curr.itemSet.equals( prev.itemSet))
        {
          log.warnf( "\nConflict in state %d:\n    %s\n    %s", state.index, prev, curr);
          conflicts++;
        }
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
  
  /**
   * Delete the stack operation with the specified index from the state.
   * @param state The state.
   * @param index The index.
   */
  private void deleteStackOp( State state, int index)
  {
    StackOp[] oldOps = state.stackOps;
    StackOp[] newOps = new StackOp[ oldOps.length - 1];
    System.arraycopy( oldOps, 0, newOps, 0, index);
    System.arraycopy( oldOps, index+1, newOps, index, newOps.length - index);
    state.stackOps = newOps;
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
   * Returns true if the specified terminal ranges overlap.
   * @param t0 The first terminal range.
   * @param t1 The second terminal range.
   * @return Returns true if the specified terminal ranges overlap.
   */
  private boolean isOverlapping( int[] t0, int[] t1)
  {
    if ( t0.length == 1)
    {
      return t0[ 0] >= t1[ 0];  
    }
    else
    {
      return t0[ 1] >= t1[ 0];  
    }
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
   * Create the symbol table for the specified state.
   * @param grammar The grammar.
   * @param state The state.
   * @param tshifts The state changes triggered by terminals.
   * @param ntshifts The state changes triggered by non-terminals.
   */
  private void createSymbolTable( Grammar grammar, State state, List<Action> tshifts, List<Action> ntshifts)
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
    itemSetMap.put( copy, itemSetMap.get( state));
    return copy;
  }
  
  public final static class Action implements Comparable<Action>
  {
    public Action( Type type, int[] symbols, LR1ItemSet itemSet)
    {
      this.type = type;
      this.symbols = symbols;
      this.itemSet = itemSet;
    }
    
    public Action( Type type, int[] symbols, LR1Item item)
    {
      this.type = type;
      this.symbols = symbols;
      this.item = item;
    }

    /**
     * @return Returns the priority of the 
     */
    public long getPriority()
    {
      if ( item != null) return item.rule.getPriority();
      
      long maxPriority = Long.MIN_VALUE;
      for( LR1Item item: itemSet.items)
      {
        if ( item.rule.getPriority() > maxPriority)
          maxPriority = item.rule.getPriority();
      }
      
      return maxPriority;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo( Action action)
    {
      // sort by terminal or start of terminal range
      return symbols[ 0] - action.symbols[ 0];
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
      String string = "";
      
      if ( symbols.length == 1)
      {
        if ( type == Action.Type.ntshift)
        {
          string = String.format( "%d", symbols[ 0]);
        }
        else
        {
          if ( symbols[ 0] == Grammar.epsilonChar) string = Grammar.epsilon;
          else if ( symbols[ 0] > 32) string = String.format( "[%c]", (char)symbols[ 0]);
          else string = String.format( "#%02X", symbols[ 0]);
        }
      }
      else
      {
        if ( type == Action.Type.ntshift)
        {
          string = String.format( "[%d,%d]", symbols[ 0], symbols[ 1]);
        }
        else
        {
          string = ( symbols[ 0] > 32)? 
              String.format( "[%c-%c]", (char)symbols[ 0], (char)symbols[ 1]): 
              String.format( "[%02X-%02X]", symbols[ 0], symbols[ 1]);
        }
      }
      
      switch( type)
      { 
        case tshift:  return String.format( "Shift  %s S%d %s", string, itemSet.state.index, itemSet.items.iterator().next());
        case reduce:  return String.format( "Reduce %s %s", string, item);
        case accept:  return String.format( "Accept %s", string);
        case ntshift: return String.format( "Goto   %s S%d %s", string, itemSet.state.index, itemSet.items.iterator().next());
      }
      
      return null;
    }

    public enum Type { tshift, ntshift, reduce, accept};

    public Type type;
    public int[] symbols;
    public LR1ItemSet itemSet;
    public LR1Item item;
    public boolean omit;
  }
  
  private final static StackOp nullShift = new StackOp();
  public final static Log log = Log.getLog( LR1.class);
  
  private Collection<LR1ItemSet> itemSets;
  private Map<State, LR1ItemSet> itemSetMap;
  private int counter;
  private int conflicts;
}
