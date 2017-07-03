lock l;
shared int amount = 0;

thread add() {
	acquire l;
	print 'S';
	amount = amount + 100;
	print amount;
	print 'E';
	release l;
}

thread sub() {
	bool written = false;
	while(!written) {
		acquire l;
		print 'S';
		if(amount - 50 < 0) {
			print amount;
		}
		else {
			amount = amount - 50;
			print amount;
			written = true;
		}
		print 'E';	
		release l;
	}
}

function void main() {
	thread a = fork sub();
	#longComputation();
	thread b = fork add();
	join a;
	join b;
	
}

# function void longComputation() {
# 	int i = 0;
# 	while(i < 10)
#		i = i + 1;
# }




