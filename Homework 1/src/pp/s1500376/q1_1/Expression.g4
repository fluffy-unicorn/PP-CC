grammar Expression;

expression : term;
term : factor (PLUS factor)+
     | value;
factor : value (TIMES value)*;
value: NUM | LPAR expression RPAR;

NUM: NUMCHAR* DOT NUMCHAR;
fragment PLUS: '+';
fragment TIMES: '*';
fragment LPAR: '(';
fragment RPAR: ')';
fragment NUMCHAR: [0..9];
fragment DOT: '.';

WS: [' \t\r\n']+ -> skip;
	   