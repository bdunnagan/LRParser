package org.xidget.parser.lr3;

/**
 * An exception that is thrown during parser generation.
 */
public class BuildException extends RuntimeException
{
  private static final long serialVersionUID = -4691054371184633610L;

  public BuildException( String message)
  {
    super( message);
  }
}
