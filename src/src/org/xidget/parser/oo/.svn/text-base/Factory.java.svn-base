package org.xidget.parser.oo;

import java.util.HashMap;
import java.util.Map;

/**
 * A factory for creating states for a parser.
 */
public class Factory
{
  public Factory()
  {
    states = new HashMap<Class<? extends IState>, IState>();
  }

  /**
   * Returns the singleton state of the specified class.
   * @param parser The parser.
   * @param clss The state class.
   * @return Returns the singleton state of the specified class.
   */
  @SuppressWarnings("unchecked")
  public <T extends IState> T getState( Parser parser, Class<T> clss)
  {
    IState state = states.get( clss);
    if ( state == null)
    {
      try
      {
        state = clss.newInstance();
        states.put( clss, state);
        state.configure( parser);
      }
      catch( Exception e)
      {
        e.printStackTrace( System.err);
        return null;
      }
    }
    return (T)state;
  }
  
  private Map<Class<? extends IState>, IState> states;
}
