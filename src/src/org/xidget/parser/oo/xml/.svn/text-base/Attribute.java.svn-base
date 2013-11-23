package org.xidget.parser.oo.xml;

import java.io.IOException;

import org.xidget.parser.oo.IState;
import org.xidget.parser.oo.Parser;
import org.xidget.parser.oo.State;

public class Attribute extends State
{
  /* (non-Javadoc)
   * @see org.xidget.parser.State#configure(org.xidget.parser.Parser)
   */
  @Override
  public void configure( Parser parser)
  {
    name = parser.getState( Name.class);
    value = parser.getState( AttributeValue.class);
  }

  /* (non-Javadoc)
   * @see org.xidget.parser.State#onEnter(org.xidget.parser.Parser, char, org.xidget.parser.IState)
   */
  @Override
  public void onEnter( Parser parser, char event, IState previous) throws IOException 
  {
    super.onEnter( parser, event, previous);
    haveName = false;
  }

  /* (non-Javadoc)
   * @see org.xidget.parser.State#processImpl(org.xidget.parser.Parser, char)
   */
  @Override
  protected void processImpl( Parser parser, char event) throws IOException
  {
    if ( !haveName && Util.isNameStart( event)) { pushNext( name); haveName = true; return;}
    if ( event == '>') { rewind(); popNext(); return;}
    if ( event == '=') { pushNext( value); return;}
    if ( Character.isWhitespace( event)) return;
     
    syntaxError();
  }
  
  private IState name;
  private IState value;
  private boolean haveName;
}
