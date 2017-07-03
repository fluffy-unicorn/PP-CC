# Incorrect multiple functions with same name (return types differ)
function bool isEven(int a) {
    return a%2==0;
}
function int isEven(int a) {
    return a%2;
}
function void main() { 
    print isEven(10);
    print isEven(10);
}
