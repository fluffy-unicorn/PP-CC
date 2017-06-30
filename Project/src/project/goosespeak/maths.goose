function void main() {
	int r = 0;
	int b = 3;
	int a = 5;
	while(b>0){
		r=add(r,a);
		b=decrement(b);
	}
	print r;
	print mult(5,3);
}

function int decrement(int a) {
    return a - 1;
}

function int add(int a, int b) {
	return a + b;
}

function int mult(int a, int b) {
	int r=0;
	while(b > 0) {
		r = add(r,a);
        b=decrement(b);
	}
	return r;
}
# Expected output:
# Sprockell 0 says 15
# Sprockell 0 says 15

