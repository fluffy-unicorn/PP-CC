# Correct fork and join
thread myThread() {
    print 10;
}

function void main() {
    thread a = fork myThread();
    thread b = fork myThread();
    join a;
    join b;
}
