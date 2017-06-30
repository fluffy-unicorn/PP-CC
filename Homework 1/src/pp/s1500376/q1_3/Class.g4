grammar Class;

@header{package pp.s1500376.q1_3;}

c : p CLASS ID x y;
p : PUBLIC | ;
x : EXTENDS ID | ;
y : IMPLEMENTS i ID;
i : ID COMMA i | ;

CLASS : 'class';
PUBLIC : 'public';
EXTENDS : 'extends';
IMPLEMENTS : 'implements';
COMMA : ',';
ID : [a-z]+;
WS : [ \t\n\r] -> skip;
