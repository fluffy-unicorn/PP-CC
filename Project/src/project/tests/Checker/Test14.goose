# Incorrect use of return type
function int add(int a, int b) {
    return a + b;
}

function void main() {
    char c = add(1,5);
}
