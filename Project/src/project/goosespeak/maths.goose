function void main() {
	int r;
	int b = 3;
	int a = 5;
	while(b>1){
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
	int r;
	while(b > 1) {
		r = add(r,a);
        decrement(b);
	}
	return r;
}