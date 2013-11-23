package org.xidget.parser.oo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base implementation of IState. 
 */
public abstract class State implements IState
{
  /* (non-Javadoc)
   * @see org.xidget.parser.IState#configure(org.xidget.parser.Parser)
   */
  public void configure( Parser parser)
  {
  }

  /* (non-Javadoc)
   * @see org.xidget.parser.IState#addEnterListener(org.xidget.parser.IListener)
   */
  public final void addEnterListener( IListener listener)
  {
    if ( onEnter == null) onEnter = new ArrayList<IListener>( 2);
    onEnter.add( listener);
  }
  
  /* (non-Javadoc)
   * @see org.xidget.parser.IState#addExitListener(org.xidget.parser.IListener)
   */
  public final void addExitListener( IListener listener)
  {
    if ( onExit == null) onExit = new ArrayList<IListener>( 2);
    onExit.add( listener);
  }
  
  /* (non-Javadoc)
   * @see org.xidget.parser.IState#addEventListener(org.xidget.parser.IListener)
   */
  public final void addEventListener( IListener listener)
  {
    if ( onEvent == null) onEvent = new ArrayList<IListener>( 2);
    onEvent.add( listener);
  }
  
  /* (non-Javadoc)
   * @see org.xidget.parser.IState#onEnter(org.xidget.parser.Parser, char, org.xidget.parser.IState)
   */
  public void onEnter( Parser parser, char event, IState previous) throws IOException
  {
    if ( onEnter != null)
      for( IListener listener: onEnter)
        listener.onEnter( parser, event, this, previous);
  }

  /* (non-Javadoc)
   * @see org.xidget.parser.IState#onExit(org.xidget.parser.Parser, char, org.xidget.parser.IState)
   */
  public void onExit( Parser parser, char event, IState next) throws IOException
  {
    if ( onExit != null)
      for( IListener listener: onExit)
        listener.onExit( parser, event, next, this);
  }

  /* (non-Javadoc)
   * @see org.xidget.parser.IState#process(org.xidget.parser.Parser, char)
   */
  public final void process( Parser parser, char event) throws IOException
  {
    this.parser = parser;
    processImpl( parser, event);
    
    if ( onEvent != null)
      for( IListener listener: onEvent)
        listener.onEvent( parser, event, parser.getNext(), this);
  }
  
  /**
   * Convenience method for calling method of same name on parser.
   * @param state The state transition.
   */
  public final void setNext( IState state) throws IOException
  {
    parser.setNext( state);
  }
  
  /**
   * Convenience method for calling method of same name on parser.
   * @param state The state transition.
   */
  public final void pushNext( IState state) throws IOException
  {
    parser.pushNext( state);
  }
  
  /**
   * Convenience method for calling method of same name on parser.
   */
  public final void popNext() throws IOException
  {
    parser.popNext();
  }
  
  /**
   * Convenience method for calling method of same name on parser.
   */
  public final void rewind()
  {
    parser.rewind();
  }
  
  /**
   * Convenience method for calling method of same name on parser.
   */
  public final void syntaxError() throws IOException
  {
    parser.syntaxError();
  }
  
  /**
   * Process the specified event.
   * @param parser The parser.
   * @param event The event.
   */
  protected abstract void processImpl( Parser parser, char event) throws IOException;

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return getClass().getSimpleName();
  }
  
  private Parser parser;
  private List<IListener> onEnter;
  private List<IListener> onExit;
  private List<IListener> onEvent;
}
