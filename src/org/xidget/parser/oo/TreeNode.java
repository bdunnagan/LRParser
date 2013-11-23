package org.xidget.parser.oo;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @par A state that is part of a tree of states that recognize keywords. Each state
 * in the tree maps events to states where the map is sorted by event so that it
 * binary-searched.
 * @par This state pops the stack when it does not match an event, so the tree should
 * be entered with a pushNext call. This means that the next state associated with a
 * TreeNode must eventually lead to a popNext call.
 */
public class TreeNode extends State
{
  public TreeNode()
  {
    this( '\0');
  }
  
  /**
   * Create a child node.
   * @param c The key character.
   */
  public TreeNode( char c)
  {
    this.keyChar = c;
    children = new ArrayList<TreeNode>( 4);
    events = new char[ 4];
  }
  
  /**
   * Add a keyword to this tree node. 
   * @par Usually this method is only called on the root of the tree.
   * @param keyword The keyword.
   * @param state The keyword match exit state.
   */
  public void addKeyword( String keyword, State state)
  {
    TreeNode node = makeTree( keyword);
    node.next = state;
  }

  /**
   * Traverse and create nodes in the tree for the specified keyword.
   * @param keyword The keyword.
   * @return Returns the last node.
   */
  private TreeNode makeTree( String keyword)
  {
    TreeNode node = this;
    int index = 0;
    for( ; index < keyword.length(); index++)
    {
      TreeNode child = node.findChild( keyword.charAt( index));
      if ( child == null) break;
      node = child;
    }
    
    // create remaining nodes
    for( ; index < keyword.length(); index++)
    {
      TreeNode child = new TreeNode( keyword.charAt( index));
      node.addChild( child);
      node = child;
    }
    
    return node;
  }
  
  /**
   * Add a child to this node.
   * @param child The child.
   */
  private void addChild( TreeNode child)
  {
    if ( events.length <= children.size())
    {
      char[] array = new char[ (int)(children.size() * 1.5)];
      System.arraycopy( events, 0, array, 0, events.length);
      events = array;
    }
    
    int index = -Arrays.binarySearch( events, 0, children.size(), child.keyChar) - 1;
    for( int i = children.size(); i >= index; i--) events[ i+1] = events[ i];
    
    events[ index] = child.keyChar;
    if ( index == children.size()) children.add( child); else children.add( index, child);
  }
  
  /* (non-Javadoc)
   * @see org.xidget.parser.State#processImpl(org.xidget.parser.Parser, char)
   */
  public void processImpl( Parser parser, char event) throws IOException
  {
    TreeNode child = findChild( event);
    if ( child == null) { popNext(); return;}
    if ( child.next != null) setNext( child.next); else setNext( child);
  }
  
  /**
   * Find the child that matches the specified character.
   * @param c The character.
   * @return Returns null or the matching child.
   */
  private final TreeNode findChild( char c)
  {
    int index = Arrays.binarySearch( events, 0, children.size(), c);
    if ( index >= 0) return children.get( index);
    return null;
  }

  /* (non-Javadoc)
   * @see org.xidget.parser.State#toString()
   */
  @Override
  public String toString()
  {
    if ( keyChar == 0) return super.toString();
    return String.format( "TreeNode[%s]", keyChar);
  }

  private char keyChar;
  private State next;
  private char[] events;
  private List<TreeNode> children;
  
  public static void main( String[] args) throws Exception
  {
    Parser parser = new Parser();
    parser.setInitialState( TreeNode.class);
    
    TreeNode root = parser.getState( TreeNode.class);
    root.addKeyword( "a ", null);
    root.addKeyword( "abc ", null);
    root.addKeyword( "ab ", null);
    root.addKeyword( "bc ", null);
    root.addKeyword( "b ", null);

    parser.addEnterListener( new Listener() {
      public void onEnter( Parser parser, char event, IState entered, IState exited)
      {
        System.out.printf( "[%s] %s -> %s\n", event, exited, entered);
      }
    });
    
    parser.parse( new StringReader( "abc "));
    parser.parse( new StringReader( "bk "));
  }
}
