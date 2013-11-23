package org.xidget.parser.oo.xml;

import java.io.IOException;

import org.xidget.parser.oo.Parser;
import org.xidget.parser.oo.State;

public class PIEnd extends State
{
  /* (non-Javadoc)
   * @see org.xidget.parser.State#processImpl(org.xidget.parser.Parser, char)
   */
  @Override
  protected void processImpl( Parser parser, char event) throws IOException
  {
    if ( event == '>') { popNext(); return;}
    if ( Character.isWhitespace( event)) return;
    syntaxError();
  }
}
