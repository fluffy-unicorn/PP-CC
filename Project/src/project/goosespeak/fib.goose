int a = 0;
int b = 1;
 
function int add(int a, int b) {
    return a+b;
}

function void fib() {
    int e;
    read e;
    while (a<=e) {
        print a;
        a=add(a,b);
        if(b>e) 
    	    break;
        print b;
        b=add(a,b);
    }
    return;
}

function void main() {
    fib();
}

# Input: 100
# Expected output:
# Sprockell 0 asks for a number: 100
# Sprockell 0 says 0
# Sprockell 0 says 1
# Sprockell 0 says 1
# Sprockell 0 says 2
# Sprockell 0 says 3
# Sprockell 0 says 5
# Sprockell 0 says 8
# Sprockell 0 says 13
# Sprockell 0 says 21
# Sprockell 0 says 34
# Sprockell 0 says 55
# Sprockell 0 says 89


