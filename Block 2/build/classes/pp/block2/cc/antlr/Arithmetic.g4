grammar Arithmetic;

@header{package pp.block2.cc.antlr;}

main : expr;
expr : '(' expr ')' #bracketExpr
	 | <assoc=right> expr '^' expr #powerExpr
	 | '-' expr #negationExpr
	 | expr '*' expr #multiplicationExpr
	 | expr '-' expr #subtractionExpr
	 | expr '+' expr #additionExpr
	 | field #fieldExpr;
field : NUM;
NUM : [0-9]+;
// ignore whitespace
WS : [ \t\n\r] -> skip;

// everything else is a typo
TYPO : [a-zA-Z]+;

