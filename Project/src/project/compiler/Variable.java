package project.compiler;

/**
 * Wrapper class for a variable.
 */
public class Variable {
	/** The offset of the variable */
	private int offset;
	/** The type of the variable */
	private Type type;
	private Object value; // TODO ?
	/** Indication whether the variable has been assigned to or not */
	private boolean initalized = false;
	/** Indication whether the variable is global or not */
	private boolean global = false;
	/**
	 * The thread id associated with this variable (only useful for
	 * {@link Type.Thread} variables).
	 */
	private int threadID;

	/** Get the {@link Type} of the {@code variable} */
	public Type getType() {
		return type;
	}

	/** Set the {@link Type} of the {@code variable} */
	public void setType(Type type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	/** Get whether the {@code variable} has been assigned to or not */
	public boolean isInitalized() {
		return initalized;
	}

	/** Set initialized to {@code true}. */
	public void setInitalized() {
		this.initalized = true;
	}

	/** Get the offset relative to the TODO ARP */
	public int getOffset() {
		return offset;
	}

	/** Set the offset */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/** Get whether the variable is global or not. */
	public boolean isGlobal() {
		return global;
	}

	/** Set global to true */
	public void setGlobal() {
		this.global = true;
	}

	/** Returns whether the variable is a pointer type or not */
	public boolean isPointer() {
		return this.getType() instanceof Type.Pointer;
	}

	/** Sets the thread id */
	public void setThreadId(int id) {
		threadID = id;
	}

	/** Gets the thread id */
	public int getThreadId() {
		return threadID;
	}

	/** Returns whether the variable is a shared integer or not */
	public boolean isSharedInt() {
		if (this.getType() instanceof Type.Int)
			return ((Type.Int) this.getType()).isShared();
		else
			return false;
	}

	/** Returns whether the variable is an array or not */
	public boolean isArray() {
		return this.getType() instanceof Type.Array;
	}

	@Override
	public String toString() {
		return (global ? "global " : "") + type + " @" + offset + (initalized ? " [initialized]" : "");
	}

}
