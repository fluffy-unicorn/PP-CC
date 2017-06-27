lock l;
shared int amount = 0;

thread add(int i) {
	acquire l;
	amount = amount + i;
	print amount;
	release l;
}

thread sub(int i) {
	while(true) {
		acquire l;
		if(amount - i < 0) {
			print amount;
		}
		else {
			amount = amount - i;
			print amount;
		}
		release l;
	}
}

function void main() {
	thread a = fork add(20);
	thread b = fork sub(10);
	join a;
	join b;
	
}