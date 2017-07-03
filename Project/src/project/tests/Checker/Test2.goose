# Type checker on expressions
shared int a = 3;
int* b = 2;

function void main() {
    int* i = 7;
    int  j = 5;
    int  k = i + j;
    print (a + b) * k;
}
