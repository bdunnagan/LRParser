package org.xidget.parser.lr3.lr1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xidget.parser.lr3.Grammar;
import org.xidget.parser.lr3.Rule;
import org.xmodel.log.Log;

/**
 * An LR1 table generator.
 */
public class LR1
{
  /**
   * Compile the specified grammar and return a DFA.
   * @param grammar The grammar.
   * @param builder The parser builder.
   * @return Returns the DFA.
   */
  public void compile( Grammar grammar, IStateBuilder builder)
  {
    grammar.augment();
    
    conflicts = 0;
    
    itemSets = createItemSets( grammar);
    createStates( grammar, itemSets, builder);
    grammar.freeGraph();

    log.infof( "Found %d conflicts in %d states.", conflicts, itemSets.size());
  }

  /**
   * @return Returns the generated item sets.
   */
  public Collection<LR1ItemSet> itemSets()
  {
    return itemSets;
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
   */
  private void createStates( Grammar grammar, Collection<LR1ItemSet> itemSets, IStateBuilder builder)
  {
    for( LR1ItemSet itemSet: itemSets)
    {
      Set<String> tSet = new HashSet<String>();
      Set<String> ntSet = new HashSet<String>();
      
      List<LR1Event> tOps = new ArrayList<LR1Event>();
      List<LR1Event> ntOps = new ArrayList<LR1Event>();
      
      for( LR1Item item: itemSet.items)
      {
        if ( item.complete())
        {
          if ( item.rule == grammar.rules().get( 0))
          {
            if ( item.laList.contains( Grammar.terminus))
            {
              int[] terminal = new int[] { Grammar.terminusChar};
              tOps.add( new LR1Event( LR1Event.Type.accept, terminal, item));
            }
          }
          else
          {
            for( String la: item.laList)
            {
              int[] terminal = grammar.toTerminal( la);
              tOps.add( new LR1Event( LR1Event.Type.reduce, terminal, item));
            }
          }
        }
        else
        {
          if ( !grammar.isTerminal( item.symbol()))
          {
            ntSet.add( item.symbol());
          }
          else
          {
            Set<String> first = item.first( grammar);
            LR1ItemSet successor = itemSet.successors.get( item.symbol());
            if ( successor != null)
            {
              for( String symbol: first)
              {
                if ( tSet.add( symbol))
                {
                  int[] terminal = grammar.toTerminal( symbol);
                  tOps.add( new LR1Event( LR1Event.Type.tshift, terminal, successor));
                }
              }
            }
          }
        }
      }
      
      for( String symbol: ntSet)
      {
        LR1ItemSet successor = itemSet.successors.get( symbol);
        if ( successor != null)
        {
          List<Rule> rules = grammar.lhs( symbol);
          for( Rule rule: rules)
          {
            LR1Event action = new LR1Event( LR1Event.Type.ntshift, new int[] { rule.symbol()}, successor);
            ntOps.add( action);
          }
        }
      }
      
      Collections.sort( tOps);
      Collections.sort( ntOps);
      
      handleConflicts( grammar, itemSet, tOps, ntOps, builder);
      builder.createState( grammar, itemSet, tOps, ntOps);
    }    
  }
  
  /**
   * Handle any conflicts found in the specified state.
   * @param grammar The grammar.
   * @param itemSet The item set.
   * @param tOps The state changes triggered by terminals.
   * @param ntOps The state changes triggered by non-terminals.
   * @param builder The state builder implementation.
   */
  private void handleConflicts( Grammar grammar, LR1ItemSet itemSet, List<LR1Event> tOps, List<LR1Event> ntOps, IStateBuilder builder)
  {
    for( int i=0; i<tOps.size(); )
    {
      int count = findOverlappingExtent( tOps, i);
      if ( count > 1)
      {
        List<LR1Event> tConflicts = tOps.subList( i, i + count);
        logTerminalConflicts( grammar, itemSet, tConflicts);
        builder.handleTerminalConflicts( grammar, itemSet, tConflicts);
        conflicts++;
      }
      i += count;
    }
    
    for( int i=0; i<ntOps.size(); )
    {
      int count = findOverlappingExtent( ntOps, i);
      if ( count > 1)
      {
        builder.handleNonTerminalConflicts( grammar, itemSet, ntOps.subList( i, i + count));
        conflicts++;
      }
      i += count;    
    }
  }
    
  /**
   * Find the number of ops with symbols that are equal to, or overlap the range of, the op with the specified index.
   * @param ops The list of ops.
   * @param index The index of the first op to check.
   * @return Returns the number of ops (always >= 1).
   */
  private int findOverlappingExtent( List<LR1Event> ops, int index)
  {
    LR1Event op = ops.get( index);
    for( int i=index+1; i<ops.size(); i++)
    {
      int[] symbols = ops.get( i).symbols;
      if ( symbols[ 0] > op.symbols[ op.symbols.length - 1])
        return i - index;
    }
    return 1;
  }
  
  /**
   * Format and log the specified terminal conflicts.
   * @param grammar The grammar.
   * @param tConflicts The list of conflicting terminal operations.
   */
  private void logTerminalConflicts( Grammar grammar, LR1ItemSet itemSet, List<LR1Event> tConflicts)
  {
    StringBuilder sb = new StringBuilder();
    sb.append( "Rule: ");
    sb.append( itemSet.items.iterator().next()); 
    sb.append( '\n');
    
    for( LR1Event tOp: tConflicts)
    {
      String symbol = "";
      char c0 = (char)tOp.symbols[ 0];
      if ( tOp.symbols.length == 1)
      {
        symbol = (c0 == '\'')? "[']": String.format( "'%c'       ", c0);
      }
      else
      {
        char c1 = (char)tOp.symbols[ 1];
        symbol = String.format( "[%c-%c]     ", c0, c1);
      }
      
      if ( tOp.item != null)
      {
        sb.append( symbol);
        sb.append( tOp.item); 
        sb.append( '\n');
      }
      else
      {
        for( LR1Item item: findMatchingItems( grammar, tOp.itemSet, tOp.symbols[ 0]))
        {
          sb.append( symbol);
          sb.append( item); 
          sb.append( '\n');
        }
      }
    }
    
    log.debugf( "Terminal conflicts:\n%s", sb);
  }
  
  /**
   * Find all items in the specified set with a dot before the specified symbol.
   * @param grammar The grammar.
   * @param itemSet The item set.
   * @param symbol The symbol.
   * @return Returns the list of matching items.
   */
  private List<LR1Item> findMatchingItems( Grammar grammar, LR1ItemSet itemSet, int symbol)
  {
    List<LR1Item> items = new ArrayList<LR1Item>();
    for( LR1Item item: itemSet.items)
    {
      if ( !grammar.isTerminal( item.symbol())) continue;
      
      for( String la: item.laList)
      {
        int[] symbols = grammar.toTerminal( la);
        if ( symbols.length == 1)
        {
          if ( symbol == symbols[ 0])
            items.add( item);
        }
        else
        {
          if ( symbols[ 0] <= symbol && symbol <= symbols[ 1])
            items.add( item);
        }
      }
    }
    return items;
  }
  
  public final static Log log = Log.getLog( LR1.class);
  
  private Collection<LR1ItemSet> itemSets;
  private int conflicts;
}
