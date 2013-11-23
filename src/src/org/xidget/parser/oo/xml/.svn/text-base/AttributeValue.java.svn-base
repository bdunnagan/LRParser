package org.xidget.parser.oo.xml;

import java.io.IOException;

import org.xidget.parser.oo.IState;
import org.xidget.parser.oo.Parser;
import org.xidget.parser.oo.QuoteDouble;
import org.xidget.parser.oo.QuoteSingle;
import org.xidget.parser.oo.State;

public class AttributeValue extends State
{
  /* (non-Javadoc)
   * @see org.xidget.parser.State#configure(org.xidget.parser.Parser)
   */
  @Override
  public void configure( Parser parser)
  {
    name = parser.getState( Name.class);
    quoteDouble = parser.getState( QuoteDouble.class);
    quoteSingle = parser.getState( QuoteSingle.class);
    entity = parser.getState( Entity.class);
  }
  
  /* (non-Javadoc)
   * @see org.xidget.parser.State#processImpl(org.xidget.parser.Parser, char)
   */
  @Override
  protected void processImpl( Parser parser, char event) throws IOException
  {
    if ( Util.isNameStart( event)) { pushNext( name); return;}
    if ( event == '\"') { pushNext( quoteDouble); return;}
    if ( event == '\'') { pushNext( quoteSingle); return;}
    if ( event == '&') { pushNext( entity); return;}
    if ( event == '>') { rewind(); popNext(); return;}
    syntaxError();
  }
  
  private IState name;
  private IState quoteDouble;
  private IState quoteSingle;
  private IState entity;
}
