package org.xidget.parser.lr3;

public class XPathGrammar extends Grammar
{
  public XPathGrammar()
  {
    final String COMMA = ",";
    
    addRule( "Expr", "ExprSingle");
    addRule( "Expr", "Expr", "S*", COMMA, "S*", "ExprSingle");
    
    addRule( "ExprSingle", "ForExpr");
    addRule( "ExprSingle", "QuantifiedExpr");
    addRule( "ExprSingle", "IfExpr");
    addRule( "ExprSingle", "OrExpr");
    
    addRule( "ForExpr", "SimpleForClause", "S+", "return", "S+", "ExprSingle");
    addRule( "SimpleForClause", "for", "S+", "$", "VarName", "S+", "in", "S+", "ExprSingle");
    addRule( "SimpleForClause", "SimpleForClause", "S*", COMMA, "S*", "$", "VarName", "S+", "in", "S+", "ExprSingle");

    
    addRule( "S", "[#01-#20]");
    addRule( "S*");
    addRule( "S*", "S*", "S");
    addRule( "S+", "S*", "S");
  }
}
