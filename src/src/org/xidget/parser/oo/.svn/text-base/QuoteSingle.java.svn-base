package org.xidget.parser.oo;

import java.io.IOException;

import org.xidget.parser.oo.Parser;
import org.xidget.parser.oo.State;

/**
 * A state that handles text in single-quotes. The backslash character
 * will escape the character that follows it.
 */
public class QuoteSingle extends State
{
  public QuoteSingle()
  {
    text = new StringBuilder();
  }
  
  /* (non-Javadoc)
   * @see org.xidget.parser.State#processImpl(org.xidget.parser.Parser, char)
   */
  @Override
  protected void processImpl( Parser parser, char event) throws IOException
  {
    if ( !escaping && event == '\'') { popNext(); return;} 
    escaping = false; if ( event == '\\') escaping = true;
    text.append( event);
  }
  
  private StringBuilder text;
  private boolean escaping;
}
