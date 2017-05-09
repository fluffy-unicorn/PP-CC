lexer grammar PL1;

@header{package pp.block1.cc.antlr;}

//ID : QUOTE QUOTE | QUOTE NQUOTE+ QUOTE | 
//		QUOTE (QUOTE QUOTE)+ QUOTE | QUOTE (NQUOTE+ (QUOTE QUOTE)+)+ QUOTE;
//ID : QUOTE (NQUOTE* | (QUOTE QUOTE)*)* QUOTE;
ID : (QUOTE (NQUOTE)* QUOTE)+;
fragment QUOTE : '"';
fragment NQUOTE : ~'"';