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
# Expected output:
# At least from S -> E all the same sprockells
# Sprockell 1 says S
# Sprockell 1 says 0
# Sprockell 1 says E
# Sprockell 2 says S
# Sprockell 2 says 100
# Sprockell 2 says E
# Sprockell 1 says S
# Sprockell 1 says 50
# Sprockell 1 says E



