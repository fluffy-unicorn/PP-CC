lexer grammar LexerGooseSpeak;
/* Arithmetic */
PLUS  :'+';
MINUS :'-';
MULT  :'*';
DIV   :'/';
MOD   :'%';
POWER :'**';

/* Bitwise */
BW_OR    :'|' ;
BW_AND   :'&' ;
BW_XOR   :'^' ;
BW_LSHFT :'<<';
BW_RSHFT :'>>';
BW_NOT   :'~' ;

/* Logic */
LOG_OR  :'||';
LOG_AND :'&&';
LOG_EQ  :'==';
LOG_NE  :'<>';
LOG_LE  :'<=';
LOG_LT  :'<' ;
LOG_GE  :'>=';
LOG_GT  :'>' ;
LOG_NOT :'!' ;

/* Special characters */
SEMI     :';' ;
COMMA    :',' ;
LPAR     :'{' ;
RPAR     :'}' ;
LBRACE   :'(' ;
RBRACE   :')' ;
LBRACKET :'[' ;
RBRACKET :']' ;
ASSIGN   :'=' ;
QUOTE    :'\'';
DQUOTE   :'"' ;

/* Keywords */
IF       :'if'      ;
ELSE     :'else'    ;
WHILE    :'while'   ;
FOR      :'for'     ;
FUNCTION :'function';
PRINT    :'print'   ;
READ     :'read'    ;
BREAK    :'break'   ;
CONTINUE :'continue';
RETURN   :'return'  ;
LEN		 :'len'     ;
STR		 :'str'		;
FORK     :'fork'	;
JOIN	 :'join'	;
ACQUIRE  :'acquire'	;
RELEASE  :'release' ;
COMMENT  :'#' ~( '\n' | '\r' )* NEWLINE;

/* Types */
fragment INT    :'int'   ;
fragment BOOL   :'bool'  ;
fragment STRING :'string';
fragment CHAR   :'char'  ;
fragment PTR    :'*'     ;
// Variable type
BASICTYPE : INT | BOOL | STRING | CHAR; 
// Pointer type
PTRTYPE   : INT PTR | BOOL PTR | CHAR PTR;
// Special type void for functions without return type
VOID      : 'void'  ;
// Concurrent types
LOCK	  : 'lock'      ;
SHAREDINT : 'shared int';
THREAD	  : 'thread'    ;
/* Literals definitions */
TRUE       :'true';
FALSE      :'false';
ID         :LETTER (LETTER|DIGIT)*;
NUM        :MINUS? DIGIT (DIGIT)*;
CHAR_LIT   :QUOTE ~('\n'|'\r'|'\'')* QUOTE;
STRING_LIT :DQUOTE ~('\n'|'\r'|'"')* DQUOTE;

/* Basic fragments */
fragment NEWLINE :'\n' | '\r';
fragment LETTER  :[a-zA-Z];
fragment DIGIT   :[0-9];

/* Skip whitespace  */
WS:	[ \t\n\r] -> skip;
