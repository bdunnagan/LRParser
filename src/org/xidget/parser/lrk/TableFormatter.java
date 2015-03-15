package org.xidget.parser.lrk;

import java.util.ArrayList;
import java.util.List;

public class TableFormatter
{
  public enum Align { left, center, right}
  
  public TableFormatter()
  {
    table = new ArrayList<List<String>>();
    columns = new ArrayList<Column>();
    borders = false;
  }
  
  public void setColumnAlign( int column, Align align)
  {
    for( int i=columns.size(); i<=column; i++)
      columns.add( new Column());
    columns.get( column).align = align;
  }
  
  public void setColumnWidth( int column, int width)
  {
    for( int i=columns.size(); i<=column; i++)
      columns.add( new Column());
    columns.get( column).width = width;
  }
  
  public void add( int row, int column, Object object)
  {
    for( int i=table.size(); i<=row; i++)
      table.add( new ArrayList<String>());
    
    List<String> cells = table.get( row);
    for( int i=cells.size(); i<=column; i++)
      cells.add( "");
    for( int i=columns.size(); i<=column; i++)
      columns.add( new Column());
    
    String str = object.toString();
    cells.set( column, str);

    if ( columns.get( column).width < str.length())
      columns.get( column).width = str.length();
  }

  private static void align( Align align, int width, char pad, String str, StringBuilder sb)
  {
    switch( align)
    {
      case left:   leftAlign( width, str, pad, sb); break;
      case center: centerAlign( width, str, pad, sb); break;
      case right:  rightAlign( width, str, pad, sb); break;
    }
  }

  private static void leftAlign( int width, String str, char pad, StringBuilder sb)
  {
    sb.append( str);
    width -= str.length();
    for( int i=0; i<width; i++) sb.append( pad);
  }
  
  private static void centerAlign( int width, String str, char pad, StringBuilder sb)
  {
    int left = (width - str.length()) >> 1;
    for( int i=0; i<left; i++) sb.append( pad);
    sb.append( str);
    int right = width - str.length() - left;
    for( int i=0; i<right; i++) sb.append( pad);
  }
  
  private static void rightAlign( int width, String str, char pad, StringBuilder sb)
  {
    width -= str.length();
    for( int i=0; i<width; i++) sb.append( pad);
    sb.append( str);
  }

  private void border( StringBuilder sb)
  {
    sb.append( "+-");
    for( int col=0; col<columns.size(); col++)
    {
      if ( col > 0) sb.append( "-+-");
      Column column = columns.get( col);
      align( column.align, column.width, '-', "", sb); 
    }
    sb.append( "-+\n");
  }
  
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    if ( borders) border( sb);
    for( int row=0; row<table.size(); row++)
    {
      List<String> cells = table.get( row);
      if ( borders) sb.append( "| ");
      
      for( int col=0; col<columns.size(); col++)
      {
        if ( col > 0) sb.append( borders? " | ": "  ");
        Column column = columns.get( col);
        String cell = (col < cells.size())? cells.get( col): "";
        align( column.align, column.width, ' ', cell, sb); 
      }
      if ( borders) sb.append( " |");
      sb.append( '\n');
    }
    if ( borders) border( sb);
    return sb.toString();
  }

  private static class Column
  {
    public Column()
    {
      width = 0;
      align = Align.left;
    }
    
    public int width;
    public Align align;
  }
  
  private List<Column> columns;
  private List<List<String>> table;
  private boolean borders;
}
