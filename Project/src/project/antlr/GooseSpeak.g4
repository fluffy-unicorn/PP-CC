grammar GooseSpeak;

import LexerGooseSpeak;

/* Program */
program: (globalStat | func | COMMENT | concurrentDecl)+ EOF;

/* Global variable declaration statements */
globalStat : type ID SEMI             #globalDeclStat
		   | type ID ASSIGN expr SEMI #globalDeclAssStat
		   ;
		   
/* Function declaration */
func : FUNCTION (BASICTYPE | VOID) ID LBRACE argumentDecl? RBRACE stat;

/* Declaration of function variables */
argumentDecl : type ID (COMMA type ID)*;

concurrentDecl : LOCK ID SEMI #lockDecl
			   | SHAREDINT ID ASSIGN expr SEMI #sharedDecl
			   | THREAD ID LBRACE RBRACE stat #threadDecl
			   ;

/* Statements */
stat : LPAR stat* RPAR 							 #blockStat
	 | PRINT expr SEMI 						     #printStat
	 | READ ID SEMI 							 #readStat
	 | type ID SEMI                              #declStat
	 | type ID ASSIGN expr SEMI                  #declAssStat
	 | ID ASSIGN expr SEMI                       #assignStat
	 | ID array+ ASSIGN expr SEMI 			     #arrayElemAssStat
	 | IF expr stat (ELSE stat)? 				 #ifStat
	 | WHILE expr stat 							 #whileStat
	 | BREAK SEMI 								 #breakStat
	 | CONTINUE SEMI 							 #continueStat
	 | ID LBRACE arguments RBRACE SEMI 			 #functionCall
	 | ID LBRACE RBRACE SEMI 					 #procedureCall
	 | RETURN expr SEMI 						 #returnExpr
	 | RETURN SEMI 								 #returnVoid
	 | COMMENT 									 #comment
	 | THREAD ID ASSIGN FORK ID LBRACE RBRACE SEMI #forkStat
	 | JOIN ID SEMI								 #joinStat
	 | ACQUIRE ID SEMI							 #acquireStat
	 | RELEASE ID SEMI							 #releaseStat
	 ;
//TODO rename
array : LBRACKET expr RBRACKET;
/* Function call arguments list */
arguments : expr (COMMA expr)*;

/* Different types */
type : BASICTYPE                  #basicType
	 | PTRTYPE 					  #pointerType
	 | type LBRACKET NUM? RBRACKET #arrayType
	 ;
	 		  
/* Expressions */
expr : prfOp expr        		    #prfExpr  
	 | <assoc=right> expr powerOp expr #powerExpr
     | expr multOp expr  		    #multExpr
     | expr plusOp expr  		    #plusExpr
     | expr compOp expr  		    #compExpr
     | expr bitOp expr				#bitExpr
     | expr boolOp expr  		    #boolExpr
     | LPAR (expr (COMMA expr)*)? RPAR #arrayExpr
     | ID array+ 				    #arrayElemExpr
     | ID LBRACE arguments RBRACE   #functionExpr
	 | ID LBRACE RBRACE 		    #procedureExpr
     | TRUE              		    #trueExpr
     | FALSE             		    #falseExpr
     | CHAR_LIT 				    #charExpr
     | STRING_LIT 				    #stringExpr
     | NUM               		    #numExpr
     | ID                		    #idExpr
     | LBRACE expr RBRACE		    #braceExpr
     ;

/* Operators */
prfOp  : BW_NOT | LOG_NOT | LEN | STR;
powerOp : POWER;
multOp : MULT | DIV | MOD;
plusOp : PLUS | MINUS;
bitOp : BW_OR | BW_AND | BW_XOR | BW_LSHFT | BW_RSHFT;
boolOp : LOG_AND | LOG_OR;
compOp : LOG_LE | LOG_LT | LOG_GE | LOG_GT | LOG_EQ | LOG_NE;