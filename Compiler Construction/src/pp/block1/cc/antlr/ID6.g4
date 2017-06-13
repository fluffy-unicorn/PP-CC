lexer grammar ID6;

@header{package pp.block1.cc.antlr;}

ID : ALPHA ALPHANUM ALPHANUM ALPHANUM ALPHANUM ALPHANUM;
fragment ALPHA : 'A'..'Z' | 'a'..'z';
fragment NUM : '0'..'9';
fragment ALPHANUM : ALPHA | NUM;