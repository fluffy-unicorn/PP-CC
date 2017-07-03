int* i = 5;
char* c = 'n';
bool* b = false;
function void main() {
	print i;
	print c;
	print b;
	change(i);
	change(c);
	change(b);
	print i;
	print c;
	print b;
}

function void change(int* i) {
	i = 7;
}

function void change(char* c) {
	c = 'p';
}

function void change(bool*  b) {
	b = true;
}


