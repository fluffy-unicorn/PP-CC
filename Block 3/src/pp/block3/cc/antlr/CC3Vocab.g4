lexer grammar CC3Vocab;

POWER : '^';
PLUS : '+';
EQUALS : '=';
LPAR   : '(';
RPAR   : ')';

NUM : [0-9]+;
BOOL : 'true' | 'false';
STR : '"' ~(' '|'\t'|'\n'|'\r') '"';

WS : [ \t\n\r] -> skip;