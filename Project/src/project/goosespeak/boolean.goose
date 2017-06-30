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

# Expected output:
# Sprockell 0 says 1
# Sprockell 0 says True
# Sprockell 0 says 2
# Sprockell 0 says True
# Sprockell 0 says 3
# Sprockell 0 says True
# Sprockell 0 says 4
# Sprockell 0 says True
# Sprockell 0 says 5
# Sprockell 0 says True
# Sprockell 0 says 6
# Sprockell 0 says True
# Sprockell 0 says 7
# Sprockell 0 says True
# Sprockell 0 says 8
# Sprockell 0 says True
# Sprockell 0 says 9
# Sprockell 0 says True
# Sprockell 0 says 10
# Sprockell 0 says True
# Sprockell 0 says 11
# Sprockell 0 says False

