# Correct use of function call and return type
function int add(int a, int b) {
    return a + b;
}

function void printInt(int p) {
    print p;
}

function void main() {
    printInt(add(10,12));
}
