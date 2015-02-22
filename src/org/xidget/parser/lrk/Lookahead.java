package org.xidget.parser.lrk;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Lookahead
{
  public Lookahead()
  {
    stack = new ArrayDeque<Node>();
    visited = new HashSet<Rule>();
    leaves = new LinkedHashSet<Node>();
  }
  
  public List<SymbolString> getChoices()
  {
  }
  
  public void add( Symbol terminal)
  {
    stack.peek().add( terminal);
  }
  
  public void enterRule( Rule rule)
  {
    visited.add( rule);
    
    leaves.remove( stack.peek());
    
    Node node = new Node( stack.peek());
    stack.push( node);
    
    leaves.add( node);
  }
  
  public void exitRule()
  {
    stack.pop();
  }
  
  public boolean wasVisited( Rule rule)
  {
    return visited.contains( rule);
  }
  
  private static class Node
  {
    public Node( Node parent)
    {
      this.parent = parent;
      this.terminals = new ArrayList<Symbol>();
    }
    
    public void add( Symbol terminal)
    {
      terminals.add( terminal);
    }
    
    public Node getParent()
    {
      return parent;
    }
    
    public Rule getRule()
    {
      return rule;
    }
    
    public List<Symbol> getTerminals()
    {
      return terminals;
    }
    
    private Node parent;
    private Rule rule;
    private List<Symbol> terminals;
  }

  private Deque<Node> stack;
  private Set<Rule> visited;
  private Set<Node> leaves;
}
