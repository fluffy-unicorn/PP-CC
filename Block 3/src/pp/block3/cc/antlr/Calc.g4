grammar Calc;

import CalcVocab;

expr : MINUS expr	   # unaryMinus
	 | expr TIMES expr # times
     | expr PLUS expr  # plus
     | LPAR expr RPAR  # par
     | NUMBER          # number
     ;
