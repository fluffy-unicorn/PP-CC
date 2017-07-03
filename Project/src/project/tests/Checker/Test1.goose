# Redefining variables in a different scope
function void main() {
    int i = 2;
    if(i == 0) {
        int i = 8;
        print i;
    } else {
        int i = 7;
        print i;
    }
}

