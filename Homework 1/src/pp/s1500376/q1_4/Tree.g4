grammar Tree;

@header{package pp.s1500376.q1_4;}

top : node ;

node
  : NUM #num
  | LPAR node LEFT NUM  RPAR #left
  | LPAR NUM RIGHT node RPAR #right
  | LPAR node LEFT NUM RIGHT node RPAR #full
  ; 
  
LPAR  : '(';
RPAR  : ')';
LEFT  : '<';
RIGHT : '>';

NUM : [0-9]+;

WS : [ \t\r\n]+ -> skip;
