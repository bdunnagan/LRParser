package org.xidget.parser.oo;

import java.io.IOException;

/**
 * Exception thrown by an instance of IState for an unhandled event.
 */
@SuppressWarnings("serial")
public class SyntaxException extends IOException
{
  public SyntaxException( Parser parser, IState state, char event)
  {
    super( String.format( "[Line: %d, Column: %d] Illegal event '%s' in state %s.", 
      parser.getLine(), parser.getColumn(), event, state));
  }
  
  public SyntaxException( Parser parser, IState state, int event)
  {
    super( String.format( "[Line: %d, Column: %d] Illegal event [%d] in state %s.", 
      parser.getLine(), parser.getColumn(), event, state));
  }
}
