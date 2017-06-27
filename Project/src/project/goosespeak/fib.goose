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
    return; # TODO maybe fix that voids don't need return?
}


