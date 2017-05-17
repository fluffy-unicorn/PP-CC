grammar CC3Attr;

import CC3Vocab;

@members {
    private int getValue(String text) {
        return Integer.parseInt(text);
    }
}

T returns [ Type type ]
	: t0=T POWER t1=T
	{ $type = $t0.type; }
	;