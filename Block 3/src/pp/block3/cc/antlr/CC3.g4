grammar CC3;

import CC3Vocab;

t returns [ Type type ]
	: t0=t POWER t1=t	#power
	| t0=t PLUS t1=t    #plus
	| t0=t EQUALS t1=t  #equals
	| LPAR t0=t RPAR    #parentheses
	| NUM				#num
	| BOOL				#bool
	| STR				#string
	;