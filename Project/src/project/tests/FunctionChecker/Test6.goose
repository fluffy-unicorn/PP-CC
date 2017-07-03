# (Incorrect) threads and functions with the same name
function void myThread() {
    print 18;
}
thread myThread() {
    print 22;
}
function void main() {
    print 54;
}
