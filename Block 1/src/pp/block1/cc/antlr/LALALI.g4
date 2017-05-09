lexer grammar LALALI;

@header{package pp.block1.cc.antlr;}

LA : 'L' 'a'+ | 'L' 'a'+ SPACE+;
LALA : LA LA;
LALALALI : LA LA LA LI;
fragment LI : 'L' 'i' | 'L' 'i' SPACE+;
fragment SPACE : ' ';