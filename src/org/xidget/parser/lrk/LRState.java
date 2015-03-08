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
    this.loci = new ArrayList<Locus>(); 
    this.loci.add( locus);
    this.actions = new ArrayList<Action>( 5);
  }
  
  public void addLocus( Locus locus)
  {
    loci.add( locus);
  }
  
  public List<Locus> getLoci()
  {
    return loci;
  }
  
  public void setTerminals( List<Locus> terminals)
  {
    this.terminals = terminals;
  }
  
  public List<Locus> getTerminals()
  {
    return terminals;
  }

  public void addTransition( Locus terminal, LRState state)
  {
    System.out.printf( "  %-15s %s\n", terminal, (state != null)? state.getLoci(): "ACCEPT");
  }
  
  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    
    sb.append( "LRState at ");
    sb.append( loci); sb.append( '\n');
    
    for( Action action: actions)
    {
      sb.append( action); sb.append( '\n');
    }
    
    return sb.toString();
  }

  private List<Locus> loci;
  private List<Action> actions;
  private List<Locus> terminals;
}
