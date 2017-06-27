package project.compiler;

/**
 * Helper class for storing a reference to an integer.
 * Used in code generation
 */
public class IntegerReference {

	/** Indicates whether this reference has a value assigned to itself */
	private boolean defined;
	/** The value for the reference */
	private int value;
	
	/**
	 * Create a new integer reference with a value
	 * @param value The value for the reference
	 */
	public IntegerReference(int value) {
		this.value = value;
		this.defined = true;
	}
	
	/**
	 * Create a new integer reference without a value.
	 */
	public IntegerReference() {
		this.defined = false;
	}
	
	/**
	 * @return The value
	 */
	public int getValue() {
		if (!defined)
			throw new NullPointerException("The integer reference is not defined.");
		return value;
	}



	/**
	 * @param value The value to set
	 */
	public void setValue(int value) {
		this.defined = true;
		this.value = value;
	}

	/**
	 * Increment the value of the reference
	 */
	public void increment() {
		if (!defined)
			throw new NullPointerException("The integer reference is not defined.");
		this.value++;
	}
	
	/**
	 * Decrement the value of the reference.
	 */
	public void decrement() {
		if (!defined)
			throw new NullPointerException("The integer reference is not defined.");
		this.value--;
	}

	/**
	 * Textual string representation of the value.
	 * @return A string representation of the value.
	 */
	@Override
	public String toString() {
		if (!defined)
			return "null";
		return "@" + this.value;
	}
	
	/**
	 * Equality test for two IntegerReferences.
	 * @return True when the values of the two reference are the same.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!defined)
			return false;
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IntegerReference other = (IntegerReference) obj;
		if (value != other.value)
			return false;
		return true;
	}
	
	/**
	 * Returns a {@code hashCode} for the value.
	 * @return When defined, the {@code hashCode} is identical to {@link Integer#hashCode}. 
	 * When undefined the {@code hashCode} is {@code 0}
	 */
	@Override
	public int hashCode() {
		if (!defined)
			return 0;
		return Integer.hashCode(value);
	}
}
