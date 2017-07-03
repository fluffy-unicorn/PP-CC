# Correct use of multiple functions with the same name (arguments must differ)
function int add(int a, int b) {
    return a + b;
}
function int add(int a, int b, int* c) {
    c = a + b;
    return a + b;
}
function void main() {
    int  a = 10;
    int  b = 7;
    int* c;
    print add(a,b);
    print add(a,b,c);
    print c;
}
