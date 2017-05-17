grammar CC3Attr;

import CC3Vocab;

t returns [ Type type ]
	: t0=t POWER t1=t
	{ $type = ($t0.type == Type.NUM && $t1.type == Type.NUM) ? 
			  Type.NUM : 
			  (($t0.type == Type.STR && $t1.type == Type.NUM) ? Type.STR : Type.ERR);
	}
	| t0=t PLUS t1=t
	{ $type = ($t0.type == $t1.type) ? $t0.type : Type.ERR; }
	| t0=t EQUALS t1=t
	{ $type = ($t0.type == $t1.type) ? Type.BOOL : Type.ERR; }
	| LPAR t0=t RPAR
	{ $type = $t0.type; }
	| NUM
	{ $type = Type.NUM; }
	| BOOL
	{ $type = Type.BOOL; }
	| STR
	{ $type = Type.STR; }
	;