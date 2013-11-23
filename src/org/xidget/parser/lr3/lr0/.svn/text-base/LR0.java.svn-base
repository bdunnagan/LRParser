package org.xidget.parser.lr3.lr0;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xidget.parser.lr3.DFA;
import org.xidget.parser.lr3.Grammar;

/**
 * An LR0 table generator.
 */
public class LR0
{
  /**
   * Compile the specified grammar and return a DFA.
   * @param grammar The grammar.
   * @return Returns the DFA.
   */
  public DFA compile( Grammar grammar)
  {
    grammar.augment();
    
    List<LR0ItemSet> itemSets = createItemSets( grammar);
    for( int i=0; i < itemSets.size(); i++)
    {
      LR0ItemSet itemSet = itemSets.get( i);
      System.out.printf( "ItemSet %d\n", i);
      System.out.print( itemSet);
    }
    
    return null;
  }

  /**
   * Create the ItemSets for the specified grammar.
   * @param grammar The grammar.
   * @return Returns the ItemSets.
   */
  private List<LR0ItemSet> createItemSets( Grammar grammar)
  {
    List<LR0ItemSet> itemSets = new ArrayList<LR0ItemSet>();
    Map<LR0Item, LR0ItemSet> map = new HashMap<LR0Item, LR0ItemSet>();
    
    LR0ItemSet itemSet = new LR0ItemSet();
    itemSet.kernel.add( new LR0Item( grammar.rules().get( 0)));
    
    List<LR0ItemSet> stack = new ArrayList<LR0ItemSet>();
    stack.add( itemSet);
    itemSets.add( itemSet);
    while( stack.size() > 0)
    {
      itemSet = stack.remove( 0);
      itemSet.closure( grammar);
      map.put( itemSet.kernel.get( 0), itemSet);
      
      for( LR0ItemSet subset: itemSet.subsets())
      {
        if ( !map.containsKey( subset.kernel.get( 0)))
        {
          stack.add( subset);
          itemSets.add( subset);
        }
      }
    }
    
    return itemSets;
  }
}
