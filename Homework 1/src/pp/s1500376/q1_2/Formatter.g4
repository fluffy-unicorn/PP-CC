lexer grammar Formatter;

@header{package pp.s1500376.q1_2;}

GENERAL   : '%' ARG_IDX? GFLAGS* WIDTH? PRECISION? GCONV;
CHARACTER : '%' ARG_IDX? CFLAGS* WIDTH? PRECISION? CCONV;
INTEGRAL  : '%' ARG_IDX? NFLAGS* WIDTH? PRECISION? ICONV;
FLOATING  : '%' ARG_IDX? NFLAGS* WIDTH? PRECISION? FCONV;

fragment ARG_IDX : [1-9][0-9]* '$';
fragment CFLAGS : '-';
fragment GFLAGS : CFLAGS | '#';
fragment NFLAGS : GFLAGS | '+' | ' ' | '0' | ',' | '(';
fragment WIDTH : [1-9][0-9]*;
fragment PRECISION : '.' [0-9]+;
fragment GCONV : 'b' | 'B' | 'h' | 'H' | 's' | 'S';
fragment CCONV : 'c' | 'C';
fragment ICONV : 'd' | 'o' | 'x' | 'X';
fragment FCONV : 'e' | 'E' | 'f' | 'g' | 'G' | 'a' | 'A';

