# Correct acquire and release on locks
lock l;
shared int sh = 50;

thread a() {
    acquire l;
    print 10;
    print sh;
    print 20;
    release l;
}

function void main() {
    thread a = fork a();
    join a;
}
