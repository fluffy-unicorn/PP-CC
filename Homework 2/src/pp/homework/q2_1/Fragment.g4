grammar Fragment;

@header{package pp.homework.q2_1;}

program: stat+ EOF;

stat: type ID (ASSIGN expr)? SEMI         #decl
    | target ASSIGN expr SEMI             #assignStat
    | IF LPAR expr RPAR stat (ELSE stat)? #ifStat 
    | WHILE LPAR expr RPAR stat           #whileStat 
    | FOR LPAR type? ID ASSIGN expr SEMI
               expr SEMI
               ID ASSIGN expr RPAR stat   #forStat 
    | LCURLY stat* RCURLY                 #blockStat
    | PRINT LPAR STRING (COMMA ID)* RPAR SEMI #printStat
    | BREAK SEMI                          #breakStat
    | CONTINUE SEMI                       #contStat
    ;

target
    : ID              #idTarget
    | ID LSQ expr RSQ #arrayTarget
    ;

expr: expr DOT ID                   #fieldExpr
    | NOT expr                      #notExpr
    | expr (PLUS | MINUS) expr      #addExpr
    | expr (TIMES | DIV) expr       #multExpr
    | expr AND expr                 #andExpr
    | expr OR  expr                 #orExpr
    | expr (LT | GT | EQ | NE | LE | GE) expr #compExpr
    | LPAR expr RPAR                #parExpr
    | (NUM | TRUE | FALSE)          #constExpr
    | IN LPAR RPAR                  #inExpr
    | ID LSQ expr RSQ               #arrayExpr
    | ID                            #idExpr
    ;

type: INT | BOOL;

AND: '&&';
ASSIGN: '=';
COMMA: ',';
DIV: '/';
DOT: '.';
EQ: '==';
GE: '>=';
GT: '>';
LCURLY: '{';
LE: '<=';
LPAR: '(';
LSQ: '[';
LT: '<';
MINUS: '-';
NE: '!=';
NOT: '!';
OR: '||';
PLUS: '+';
RCURLY: '}';
RPAR: ')';
RSQ: ']';
SEMI: ';';
TIMES: '*';

IN: 'in';
PRINT: 'printf';
BOOL: 'boolean';
FOR: 'for';
INT: 'int';
WHILE: 'while';
IF: 'if';
ELSE: 'else';
TRUE: 'true';
FALSE: 'false';
BREAK: 'break';
CONTINUE: 'continue';

fragment LETTER: [a-zA-Z];
fragment DIGIT: [0-9];

ID: LETTER (LETTER | DIGIT)*;
NUM: DIGIT+;
STRING: '"' (~["\\] | '\\'.)* '"';

WS: [ \t\r\n]+ -> skip;