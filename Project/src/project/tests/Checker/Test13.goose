# Incorrect use of function arguments
function int add(int a, int b) {
    return a + b;
}

function void main() {
    print add('c','d');
}
