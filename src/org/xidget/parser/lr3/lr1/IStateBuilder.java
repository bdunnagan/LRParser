package org.xidget.parser.lr3.lr1;

import java.util.List;
import org.xidget.parser.lr3.Grammar;

/**
 * An interface for building the representation of an item-set state.  This interface is provided
 * to decouple the language implementation of the parser from compilation of the grammar. 
 */
public interface IStateBuilder
{
  /**
   * Create a new parser state.
   * @param grammar The grammar.
   * @param itemSet The LR1 item set representing the state.
   * @param tOps The terminal state operations.
   * @param ntOps The non-terminal state operations.
   */
  public void createState( Grammar grammar, LR1ItemSet itemSet, List<LR1Event> tOps, List<LR1Event> ntOps);
  
  /**
   * Create a new parser state.
   * @param grammar The grammar.
   * @param itemSet The LR1 item set representing the state.
   * @param tOps One group of terminal state operations that conflict.
   */
  public void handleTerminalConflicts( Grammar grammar, LR1ItemSet itemSet, List<LR1Event> tOps);
  
  /**
   * Create a new parser state.
   * @param grammar The grammar.
   * @param itemSet The LR1 item set representing the state.
   * @param ntOps One group of non-terminal state operations that conflict.
   */
  public void handleNonTerminalConflicts( Grammar grammar, LR1ItemSet itemSet, List<LR1Event> ntOps);
}
