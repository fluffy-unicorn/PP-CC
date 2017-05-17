lexer grammar CC3Vocab;

POWER : '^';
PLUS : '+';
EQUALS : '=';
LPAR   : '(';
RPAR   : ')';

NUM : [0-9]+;
BOOL : 'true' | 'false';
STR : [a-z]+ ;

WS : [ \t\n\r] -> skip;