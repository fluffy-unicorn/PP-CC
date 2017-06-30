char X = 'X';
char O = 'O';
function void main() { 
	char[3][3] game = {"123","456","789"};
	int* turn;
	int* winner;
	string name1;
	string name2;
	int* v;
	while(!hasWinner(game, winner)) {
		printGame(game);
		print turn(turn);
		print "Choose your spot";
		read v;
		while(!(move(game, turn, v))) {
			print "Invalid spot, try again";
			read v;
		}
	}
	printGame(game);
	print "Player " + str winner + " has won the game!";
}
function void printGame(char[3][3] game) {
	print str game[0][0] + " | " + str game[0][1] + " | " + str game[0][2];
	print "---------";
	print str game[1][0] + " | " + str game[1][1] + " | " + str game[1][2];
	print "---------";
	print str game[2][0] + " | " + str game[2][1] + " | " + str game[2][2];
}
function bool move(char[3][3] game, int* turn, int* v) {
	int row = v/3; #integer division
	int col = v%3;
	if(game[row][col] == X || game[row][col] == O)
		return false;
	else {
		game[row][col] = toPlayer(v);
		next(v);
		return true;
	}
}
function string turn(int* turn) {
	return "Player " + str turn + "'s turn";
}

function bool hasWinner(char[3][3] game, int* winner) {
	return rowsWinner(game, winner) || colsWinner(game, winner) || diagWinner(game, winner);
}

function bool rowsWinner(char[3][3] game, int* winner) {
	int i = 0;
	while (i < 3) {
		if(game[i][0] == game[i][1] && game[i][0] == game[i][2]) {
			winner = toPlayer(game[i][0]);
			return true;
		}
	}
	return false;
}

function bool colsWinner(char[3][3] game, int* winner) {
	int i = 0;
	while (i < 3) {
		if(game[0][i] == game[1][i] && game[0][i] == game[1][i]) {
			winner = toPlayer(game[0][i]);
			return true;
		}
	}
	return false;
}

function bool diagWinner(char[3][3] game, int* winner) {
	if(game[0][0] == game[1][1] && game[0][0] == game[2][2]) {
		winner = toPlayer(game[0][0]);
		return true;
	}
	if(game[0][2] == game[1][1] && game[0][2] == game[2][0]) {
		winner = toPlayer(game[0][2]);
		return true;
	}
	return false;
}
function int toPlayer(char c) {
	if(c == 'X')
		return 0;
	if(c == 'O')
		return 1;
	else
		return 2;
}

function char toPlayer(int i) {
	if(i == 0)
		return 'X';
	if(i == 1)
		return 'O';
	else
		return '.';
}

function void next(int* v) {
	if(v == 0)
		v = 1;
	else
		v = 0;
}


