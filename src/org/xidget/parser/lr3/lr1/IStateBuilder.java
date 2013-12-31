package org.xidget.parser.lr3.lr1;

import java.util.List;
import org.xidget.parser.lr3.Grammar;

/**
 * An interface for building the representation of an item-set state.  This interface is provided
 * to decouple the language implementation of the parser from compilation of the grammar. 
 */
public interface IStateBuilder
{
  public void createState( Grammar grammar, LR1ItemSet itemSet, List<LR1Event> tOps, List<LR1Event> ntOps);
  
  public void handleTerminalConflicts( Grammar grammar, LR1ItemSet itemSet, List<LR1Event> events);
  
  public void handleNonTerminalConflicts( Grammar grammar, LR1ItemSet itemSet, List<LR1Event> events);
}
