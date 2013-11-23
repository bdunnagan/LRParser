package org.xidget.parser.oo;

import java.io.IOException;

/**
 * An interface for a state in a finite state machine that handles bytes from a stream.
 */
public interface IState
{
  /**
   * Configure this state.
   * @param parser The parser.
   */
  public void configure( Parser parser);
  
  /**
   * Add a listener.
   * @param listener The listener.
   */
  public void addEnterListener( IListener listener);
  
  /**
   * Add a listener.
   * @param listener The listener.
   */
  public void addExitListener( IListener listener);
  
  /**
   * Add a listener.
   * @param listener The listener.
   */
  public void addEventListener( IListener listener);
  
  /**
   * Called when this state is entered.
   * @param parser The parser state machine.
   * @param event The event.
   * @param prev The previous state.
   */
  public void onEnter( Parser parser, char event, IState previous) throws IOException;
  
  /**
   * Called when this state is exited.
   * @param parser The parser state machine.
   * @param event The event.
   * @param next The next state.
   */
  public void onExit( Parser parser, char event, IState next) throws IOException;
  
  /**
   * Called when this state receives an event.
   * @param parser The parser state machine.
   * @return Returns the action to be performed by the state machine.
   */
  public void process( Parser parser, char event) throws IOException;
}
