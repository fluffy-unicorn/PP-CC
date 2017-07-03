# Correct usage of arrays

int[] as;
function void main() {
    as = {1,2,3,4};
    printArray(as);
}

function void printArray(int[] as) {
    int i = 0;
    while (i < len as) {
        print as[i];
        i = i + 1;
    }
}
