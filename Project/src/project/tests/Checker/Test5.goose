# Correct while-loops and if-else statements
function void main() {
    int[] input = {9,10,11};
    int i = -1;
    while(true) {
        i = i + 1;
        if(input[i] > 10) 
            break;
        else
            if(input[i] < 10)
                continue;
        print input[i];
    }
}
