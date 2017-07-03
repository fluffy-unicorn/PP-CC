int[3] a = {1,2,3};
string s = "ABC";
function void main() {
	int[5] f = {1,2,3,4,5};
	print len s;
	printArray(a);
	doubleArray(a);
	printArray(a);
	printArray(f);
}

function void doubleArray(int[3] i) {
	i[0] = i[0] * i[0];
	i[1] = i[1] * i[1];
	i[2] = i[2] * i[2];
}

function void printArray(int[3] i) {
	print i[0];
	print i[1];
	print i[2];
} 

function void printArray(int[5] a) {
	int i = 0;
	while (i < 5) {
		print a[i];
		i = i + 1;
	}
}
