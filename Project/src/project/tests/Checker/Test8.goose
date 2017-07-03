# Incorrect fork
function void myThread() {
    print 10;
}

function void main() {
    thread a = fork myThread();
    join a;
}
