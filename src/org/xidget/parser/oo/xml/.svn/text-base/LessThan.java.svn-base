package org.xidget.parser.oo.xml;

import java.io.IOException;

import org.xidget.parser.oo.IState;
import org.xidget.parser.oo.Parser;
import org.xidget.parser.oo.State;

public class LessThan extends State
{
  /* (non-Javadoc)
   * @see org.xidget.parser.State#configure(org.xidget.parser.Parser)
   */
  @Override
  public void configure( Parser parser)
  {
    pi = parser.getState( PI.class);
    dtd = parser.getState( DTD.class);
    element = parser.getState( Element.class);
    endTag = parser.getState( EndTag.class);
  }

  /* (non-Javadoc)
   * @see org.xidget.parser.State#processImpl(org.xidget.parser.Parser, char)
   */
  @Override
  protected void processImpl( Parser parser, char event) throws IOException
  {
    switch( event)
    {
      case '?': setNext( pi); return;
      case '!': setNext( dtd); return;
      case '/': setNext( endTag); return;
    }
    
    // xml name start character
    if ( Util.isNameStart( event)) { rewind(); setNext( element); return;}
    
    syntaxError();
  }

  private IState pi;
  private IState dtd;
  private IState element;
  private IState endTag;
}
