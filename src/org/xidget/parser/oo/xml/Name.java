package org.xidget.parser.oo.xml;

import java.io.IOException;

import org.xidget.parser.oo.IState;
import org.xidget.parser.oo.Parser;
import org.xidget.parser.oo.State;

public class Name extends State
{
  public Name()
  {
    this.name = new StringBuilder();
  }
  
  /* (non-Javadoc)
   * @see org.xidget.parser.State#onEnter(org.xidget.parser.Parser, char, org.xidget.parser.IState)
   */
  @Override
  public void onEnter( Parser parser, char event, IState previous) throws IOException
  {
    name.setLength( 0);
    name.append( event);
  }

  /* (non-Javadoc)
   * @see org.xidget.parser.State#processImpl(org.xidget.parser.Parser, char)
   */
  @Override
  protected void processImpl( Parser parser, char event) throws IOException
  {
    if ( Util.isName( event)) { name.append( event); return;}
    if ( event == '=') { rewind(); popNext(); return;}
    if ( event == '>') { rewind(); popNext(); return;}
    if ( Character.isWhitespace( event)) { popNext(); return;}
    syntaxError();
  }
  
  private StringBuilder name;
}
