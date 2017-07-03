# Calling myFunction(8) does not make sense
function void myFunction(int* i) {
    i = 5;
}
function void main() {
    myFunction(8);
}
