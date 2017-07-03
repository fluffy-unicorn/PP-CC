function void main() {
	bool cont = true;
	int i = 0;
	while(cont) {
		i = i + 1;
		if(i > 10)
			cont = false;
		print i;
		print cont;
	}
}
