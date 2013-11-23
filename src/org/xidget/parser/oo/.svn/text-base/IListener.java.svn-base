package org.xidget.parser.oo;

/**
 * An interface for receiving notifications about state transitions.
 */
public interface IListener
{
  /**
   * Called when a state transition occurs.
   * @param parser The parser.
   * @param event The event that triggered the transition.
   * @param entered The state that was entered.
   * @param exited The state that was exited.
   */
  public void onEnter( Parser parser, char event, IState entered, IState exited);
  
  /**
   * Called when a state transition occurs.
   * @param parser The parser.
   * @param event The event that triggered the transition.
   * @param entered The state that was entered.
   * @param exited The state that was exited.
   */
  public void onExit( Parser parser, char event, IState entered, IState exited);
  
  /**
   * Called when a state transition occurs.
   * @param parser The parser.
   * @param event The event that triggered the transition.
   * @param state The state that received the event.
   * @param next Null or the next state.
   */
  public void onEvent( Parser parser, char event, IState state, IState next);
}
