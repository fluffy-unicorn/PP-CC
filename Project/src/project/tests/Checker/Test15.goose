# Correct use of pointers in function calls
function void add(int a, int b, int* result) {
    result = a + b;
}

function void main() {
    int* r;
    add(10,2,r);
    print r;
}
