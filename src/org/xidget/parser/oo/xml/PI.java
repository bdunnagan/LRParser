package org.xidget.parser.oo.xml;

import java.io.IOException;

import org.xidget.parser.oo.IState;
import org.xidget.parser.oo.Parser;
import org.xidget.parser.oo.State;

public class PI extends State
{
  /* (non-Javadoc)
   * @see org.xidget.parser.State#configure(org.xidget.parser.Parser)
   */
  @Override
  public void configure( Parser parser) 
  {
    piEnd = parser.getState( PIEnd.class);
  }

  /* (non-Javadoc)
   * @see org.xidget.parser.State#onEnter(org.xidget.parser.Parser, char, org.xidget.parser.IState)
   */
  @Override
  public void onEnter( Parser parser, char event, IState previous) throws IOException 
  {
    super.onEnter(parser, event, previous);
    content.setLength( 0);
  }

  /* (non-Javadoc)
   * @see org.xidget.parser.State#processImpl(org.xidget.parser.Parser, char)
   */
  @Override
  protected void processImpl( Parser parser, char event) throws IOException
  {
    if ( event == '?') { setNext( piEnd); return;}
    content.append( event);
  }
  
  private StringBuilder content = new StringBuilder();
  private IState piEnd;
}
