package org.xidget.parser.oo;

public class Listener implements IListener
{
  /* (non-Javadoc)
   * @see org.xidget.parser.IListener#onEnter(org.xidget.parser.Parser, char, 
   * org.xidget.parser.IState, org.xidget.parser.IState)
   */
  public void onEnter( Parser parser, char event, IState entered, IState exited)
  {
  }

  /* (non-Javadoc)
   * @see org.xidget.parser.IListener#onExit(org.xidget.parser.Parser, char, 
   * org.xidget.parser.IState, org.xidget.parser.IState)
   */
  public void onExit( Parser parser, char event, IState entered, IState exited)
  {
  }
  
  /* (non-Javadoc)
   * @see org.xidget.parser.IListener#onEvent(org.xidget.parser.Parser, char, 
   * org.xidget.parser.IState, org.xidget.parser.IState)
   */
  public void onEvent( Parser parser, char event, IState state, IState next)
  {
  }
}
