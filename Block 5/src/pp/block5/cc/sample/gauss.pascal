Program gauss;

Var n, i, sum : Integer;

Begin
	In("n? ", n);
	While i <> n Do
	Begin
		i := i + 1;
		sum := sum + i
	End;
	Out("Sum: ", sum)
End.