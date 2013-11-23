package org.xidget.parser.oo.xml;

import java.io.IOException;

import org.xidget.parser.oo.IState;
import org.xidget.parser.oo.Parser;
import org.xidget.parser.oo.State;

public class Entity extends State
{
  /* (non-Javadoc)
   * @see org.xidget.parser.State#onEnter(org.xidget.parser.Parser, char, org.xidget.parser.IState)
   */
  @Override
  public void onEnter( Parser parser, char event, IState previous) throws IOException 
  {
    super.onEnter( parser, event, previous);
    name.setLength( 0);
  }

  /* (non-Javadoc)
   * @see org.xidget.parser.State#processImpl(org.xidget.parser.Parser, char)
   */
  @Override
  protected void processImpl( Parser parser, char event) throws IOException
  {
    if ( Character.isLetter( event)) { name.append( event); return;}
    if ( event == ';') { popNext(); return;}
    syntaxError();
  }
  
  private StringBuilder name = new StringBuilder();
}
