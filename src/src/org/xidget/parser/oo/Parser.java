package org.xidget.parser.oo;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * An InputStream state machine parser.
 */
public class Parser
{
  public Parser()
  {
    factory = new Factory();
    stack = new Stack<IState>();
    buffer = new char[ 4096];
  }
  
  /**
   * Set the initial state of the parser.
   * @param clss The state class.
   */
  public final void setInitialState( Class<? extends IState> clss)
  {
    initial = getState( clss);
  }
  
  /**
   * Returns the state of the specified type.
   * @param clss The state class.
   * @return Returns the state of the specified type.
   */
  public final <T extends IState> T getState( Class<T> clss)
  {
    return factory.getState( this, clss);
  }
    
  /**
   * Add a listener.
   * @param listener The listener.
   */
  public final void addEnterListener( IListener listener)
  {
    if ( onEnter == null) onEnter = new ArrayList<IListener>( 2);
    onEnter.add( listener);
  }
  
  /**
   * Add a listener.
   * @param listener The listener.
   */
  public final void addExitListener( IListener listener)
  {
    if ( onExit == null) onExit = new ArrayList<IListener>( 2);
    onExit.add( listener);
  }
  
  /**
   * Add a listener.
   * @param listener The listener.
   */
  public final void addEventListener( IListener listener)
  {
    if ( onEvent == null) onEvent = new ArrayList<IListener>( 2);
    onEvent.add( listener);
  }
  
  /**
   * Add a listener to the specified state.
   * @param The state class.
   * @param listener The listener.
   */
  public final void addEnterListener( Class<? extends IState> clss, IListener listener)
  {
    getState( clss).addEnterListener( listener);
  }
  
  /**
   * Add a listener to the specified state.
   * @param The state class.
   * @param listener The listener.
   */
  public final void addExitListener( Class<? extends IState> clss, IListener listener)
  {
    getState( clss).addExitListener( listener);
  }
  
  /**
   * Add a listener to the specified state.
   * @param The state class.
   * @param listener The listener.
   */
  public final void addEventListener( Class<? extends IState> clss, IListener listener)
  {
    getState( clss).addEventListener( listener);
  }
  
  /**
   * Returns the current line of the parser.
   * @return Returns the current line of the parser.
   */
  public final int getLine()
  {
    return line;
  }
  
  /**
   * Returns the current column of the parser.
   * @return Returns the current column of the parser.
   */
  public final int getColumn()
  {
    return column;
  }
  
  /**
   * Returns the current absolute position of the parser.
   * @return Returns the current absolute position of the parser.
   */
  public final int getPosition()
  {
    return position;
  }
  
  /**
   * Read one character from the stream.
   * @return Returns -1 or the character that was read.
   */
  public final int read() throws IOException
  {
    if ( rewind)
    {
      rewind = false;
      return event;
    }
    
    if ( bufIndex == bufLength)
    {
      bufLength = reader.read( buffer);
      if ( bufLength < 0)
      {
        bufLength = 0;
        return event = -1;
      }
      bufIndex = 0;
    }
    
    event = buffer[ bufIndex++];
    return event;
  }
  
  /**
   * Rewind the parser one character.
   */
  public final void rewind()
  {
    rewind = true;
  }
  
  /**
   * Called by implementations of IState to tell the parser to perform a state transition.
   * @param state The next state.
   * @param push True if the current state should be pushed on the stack.
   */
  public final void setNext( IState state) throws IOException
  {
    char c = (char)event;
    
    // store next state
    next = state;
    
    // exit current state
    current.onExit( this, c, state);
    
    // notify exit
    if ( onExit != null)
      for( IListener listener: onExit)
        listener.onExit( this, c, state, current);

    if ( state != null)
    {
      // enter next state
      state.onEnter( this, c, current);
      
      // notify enter
      if ( onEnter != null)
        for( IListener listener: onEnter)
          listener.onEnter( this, c, state, current);
    }
  }
  
  /**
   * Called by implementations of IState to tell the parser to perform a state transition.
   * The current state is pushed on the stack before the state transition.
   * @param state The next state.
   */
  public final void pushNext( IState state) throws IOException
  {
    char c = (char)event;
    
    // store next state
    next = state;
    
    // push current state on stack
    stack.push( current);
    
    // enter next state
    state.onEnter( this, c, current);
    
    // notify enter
    if ( onEnter != null)
      for( IListener listener: onEnter)
        listener.onEnter( this, c, state, current);
  }
  
  /**
   * Called by implementations of IState to tell the parser to pop the next state off the stack.
   * @return Returns null or the state.
   */
  public final void popNext() throws IOException
  {
    char c = (char)event;

    // pop next state off stack
    next = stack.empty()? null: stack.pop();
    
    // exit current state
    current.onExit( this, c, next);
    
    // notify exit
    if ( onExit != null)
      for( IListener listener: onExit)
        listener.onExit( this, c, next, current);
  }
  
  /**
   * Report a syntax error.
   */
  public final void syntaxError() throws IOException
  {
    throw new SyntaxException( this, current, (char)event);
  }
  
  /**
   * Returns the next state.
   * @return Returns the next state.
   */
  public final IState getNext()
  {
    return next;
  }
  
  /**
   * Parser the specified stream.
   * @param reader The reader.
   */
  public final void parse( Reader reader) throws IOException
  {
    this.current = initial;
    this.reader = reader;
    this.line = 1;
    this.column = 1;
    this.position = 1;
    
    while( current != null)
    {
      event = read();
      if ( event < 0) break;

      // track position
      position++; column++;
      if ( event == '\n' || event == '\r') { line++; column = 0;}
      
      // process event
      char c = (char)event;
      current.process( this, c);
      
      // notify event
      if ( onEvent != null)
        for( IListener listener: onEvent)
          listener.onEvent( this, c, current, next);
      
      // update current state
      current = next;
    }
  }

  private IState initial;
  private IState current;
  private IState next;
  private Factory factory;
  private Reader reader;
  private Stack<IState> stack;
  private int event;
  private boolean rewind;
  private int line;
  private int column;
  private int position;
  
  private char[] buffer;
  private int bufIndex;
  private int bufLength;
  
  private List<IListener> onEnter;
  private List<IListener> onExit;
  private List<IListener> onEvent;
}
