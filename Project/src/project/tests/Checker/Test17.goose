# Incorrect redefinition of arrays
int[] as;
function void main() {
    as = {1,2,3,4};
    as = {5,4,3,2,1};
    print as[0];
}
