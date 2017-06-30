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

# Expected output:
# Sprockell 0 says 5
# Sprockell 0 says n
# Sprockell 0 says false
# Sprockell 0 says 7
# Sprockell 0 says p
# Sprockell 0 says true

