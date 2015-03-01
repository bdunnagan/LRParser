package org.xidget.parser.lrk;

import java.util.ArrayList;
import java.util.List;

public class LRState
{
  public enum ActionType { expect, push, pop, resume, accept}
  
  public static class Action
  {
    public Action( ActionType type, List<Locus> lookahead)
    {
      this.type = type;
      this.lookahead = lookahead;
    }
    
    @Override
    public String toString()
    {
      StringBuilder sb = new StringBuilder();
      sb.append( "  ");
      sb.append( type.name().toUpperCase());
      sb.append( ": ");
      sb.append( Locus.toString( "    ", lookahead));
      return sb.toString();
    }
    
    public ActionType type;
    public List<Locus> lookahead;
  }
  
  public LRState( Locus locus)
  {
    this.locus = locus;
    this.actions = new ArrayList<Action>( 5);
  }

  public void accept( List<Locus> lookahead)
  {
    actions.add( new Action( ActionType.accept, lookahead));
  }
  
  public void expect( List<Locus> lookahead)
  {
    actions.add( new Action( ActionType.expect, lookahead));
  }
  
  public void push( List<Locus> lookahead)
  {
    actions.add( new Action( ActionType.push, lookahead));
  }
  
  public void resume( List<Locus> lookahead)
  {
    actions.add( new Action( ActionType.resume, lookahead));
  }
  
  public void pop( List<Locus> lookahead)
  {
    actions.add( new Action( ActionType.pop, lookahead));
  }
  
  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    
    sb.append( "LRState at ");
    sb.append( locus); sb.append( '\n');
    
    for( Action action: actions)
    {
      sb.append( action); sb.append( '\n');
    }
    
    return sb.toString();
  }

  private Locus locus;
  private List<Action> actions;
}
