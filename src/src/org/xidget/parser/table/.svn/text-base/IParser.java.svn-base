package org.xidget.parser.table;

import java.io.IOException;
import java.io.Reader;

/**
 * An interface for super-efficient parsers.
 */
public interface IParser
{
  /**
   * Specify the names of the states.
   * @param names The names in order.
   */
  public void setNames( String[] names);

  /**
   * Add a state transition.
   * @param state The current state (value greater than 0).
   * @param next The next state (value greater than 0).
   * @param first The first event.
   * @param last The last event.
   */
  public void setNext( int state, int next, char first, char last);

  /**
   * Add a push state transition.
   * @param state The current state (value greater than 0).
   * @param next The next state (value greater than 0).
   * @param first The first event.
   * @param last The last event.
   */
  public void pushNext( int state, int next, char first, char last);

  /**
   * Add a pop state transition.
   * @param state The current state (value greater than 0).
   * @param first The first event.
   * @param last The last event.
   */
  public void popNext( int state, char first, char last);

  /**
   * Add a pop state transition.
   * @param state The current state (value greater than 0).
   * @param count The number of states to pop.
   * @param first The first event.
   * @param last The last event.
   */
  public void popNext( int state, int count, char first, char last);

  /**
   * Add a state transition.
   * @param state The current state (value greater than 0).
   * @param next The next state (value greater than 0).
   * @param event The event.
   */
  public void setNext( int state, int next, char event);

  /**
   * Add a push state transition.
   * @param state The current state (value greater than 0).
   * @param next The next state (value greater than 0).
   * @param event The event.
   */
  public void pushNext( int state, int next, char event);

  /**
   * Add a pop state transition.
   * @param state The current state (value greater than 0).
   * @param event The event.
   */
  public void popNext( int state, char event);

  /**
   * Add a pop state transition.
   * @param state The current state (value greater than 0).
   * @param count The number of states to pop.
   * @param event The event.
   */
  public void popNext( int state, int count, char event);

  /**
   * Parse the specified stream.
   * @param reader The stream.
   */
  public void parse( Reader reader) throws IOException;

  /**
   * Parse the specified buffer.
   * @param buffer The buffer.
   * @param offset The offset into the buffer.
   * @param length The number of characters to parse.
   */
  public void parse( char[] buffer, int offset, int length) throws IOException;

  /**
   * Parse the specified buffer.
   * @param buffer The buffer.
   * @param offset The offset into the buffer.
   * @param length The number of characters to parse.
   */
  public void parseDebug( char[] buffer, int offset, int length) throws IOException;
}