package org.xidget.parser.oo.xml;

/**
 * XML name start character set.
 */
public final class Util
{
  /**
   * Returns true if the specified character is a name start character.
   * @param c The character to test.
   * @return Returns true if the specified character is a name start character.
   */
  public static boolean isNameStart( char c)
  {
    return Character.isLetter( c) || c == ':' || c == '_';
  }
  
  /**
   * Returns true if the specified character is a name start character.
   * @param c The character to test.
   * @return Returns true if the specified character is a name start character.
   */
  public static boolean isName( char c)
  {
    return Character.isLetterOrDigit( c) || c == ':' || c == '_' || c == '-';
  }
}
