package project.compiler;

/** GooseSpeak data type kind. */
public enum TypeKind {
	/** Function void type */
	Void,
	/** Integer type */
	Int,
	/** Boolean type */
	Bool,
	/** Character type */
	Char,
	/** Array type */
	Array,
	/** String type */
	String,
	/** Pointer to a integer */
	IntPtr,
	/** Pointer to a boolean */
	BoolPtr,
	/** Pointer to a character */
	CharPtr,
	/** Concurrent lock */
	Lock,
	/** Thread */
	Thread;
}
