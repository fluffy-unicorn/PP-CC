package project.compiler;

/** GooseSpeak data type. */
abstract public class Type {

	/** Singleton for the function type void */
	public static final Type Void = new Void();
	/** Singleton for the type boolean */
	public static final Type Bool = new Bool();
	/** Singleton for the type integer */
	public static final Type Int = new Int();
	/** Singleton for the type character */
	public static final Type Char = new Char();
	/** Placeholder for undeclared string variable */
	public static final Type String = new Array(-1, Char);
	/** Singleton for the type integer pointer */
	public static final Type IntPtr = new Pointer(TypeKind.Int);
	/** Singleton for the type character pointer */
	public static final Type CharPtr = new Pointer(TypeKind.Char);
	/** Singleton for the type boolean pointer */
	public static final Type BoolPtr = new Pointer(TypeKind.Bool);
	/** Singleton for the type lock */
	public static final Type Lock = new Lock();
	/** Singleton for the type shared int */
	public static final Type SharedInt = new Int(true);
	/** Singleton for the type thread */
	public static final Type Thread = new Thread();

	private final TypeKind kind;

	/** Constructor for subclasses. */
	protected Type(TypeKind kind) {
		this.kind = kind;
	}

	/** Returns the kind of this type. */
	public TypeKind getKind() {
		return this.kind;
	}

	/** returns the size (in bytes) of a value of this type. */
	abstract public int size();

	static public class Void extends Type {
		private Void() {
			super(TypeKind.Void);
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public String toString() {
			return "void";
		}
	}

	static public class Bool extends Type {
		private Bool() {
			super(TypeKind.Bool);
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public String toString() {
			return "boolean";
		}

		@Override
		public boolean equals(Object other) {
			if (other == null)
				return false;
			if (this == other)
				return true;
			if (other instanceof Type) {
				Type otherType = (Type) other;
				return otherType.getKind() == TypeKind.Bool || otherType.getKind() == TypeKind.BoolPtr;
			}
			return false;
		}
	}

	static public class Int extends Type {
		private boolean shared;
		private Int() {
			super(TypeKind.Int);
			this.shared = false;
		}

		private Int(boolean shared) {
			super(TypeKind.Int);
			this.shared = shared;
		}
		@Override
		public int size() {
			return 1;
		}

		@Override
		public String toString() {
			return (isShared() ? "shared " : "") + "integer";
		}

		@Override
		public boolean equals(Object other) {
			if (other == null)
				return false;
			if (this == other)
				return true;
			if (other instanceof Type) {
				Type otherType = (Type) other;
				return otherType.getKind() == TypeKind.Int|| otherType.getKind() == TypeKind.IntPtr;
			}
			return false;
		}

		public boolean isShared() {
			return shared;
		}
	}

	static public class Char extends Type {
		public Char() {
			super(TypeKind.Char);
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public String toString() {
			return "character";
		}

		@Override
		public boolean equals(Object other) {
			if (other == null)
				return false;
			if (this == other)
				return true;
			if (other instanceof Type) {
				Type otherType = (Type) other;
				return otherType.getKind() == TypeKind.Char|| otherType.getKind() == TypeKind.CharPtr;
			}
			return false;
		}
	}

	static public class Lock extends Type {
		protected Lock() {
			super(TypeKind.Lock);
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return "lock";
		}
	}

	static public class Thread extends Type {

		protected Thread() {
			super(TypeKind.Thread);
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "thread";
		}
	}

	static public class Pointer extends Type {

		private final TypeKind elemType;

		/**
		 * Create a new pointer of type {@code kind}.
		 * 
		 * @param kind
		 *            The {@link TypeKind} of the pointer.
		 * @throws IllegalArgumentException
		 *             when kind is not one of {@link TypeKind#Int},
		 *             {@link TypeKind#Bool} or {@link TypeKind#Char}.
		 */
		protected Pointer(TypeKind kind) {
			super(getPointerTypeKind(kind));
			elemType = kind;
		}

		private static TypeKind getPointerTypeKind(TypeKind kind) {
			switch (kind) {
			case Int:
				return TypeKind.IntPtr;
			case Bool:
				return TypeKind.BoolPtr;
			case Char:
				return TypeKind.CharPtr;
			default:
				throw new IllegalArgumentException(kind + " cannot be a pointer type.");
			}
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public String toString() {
			return "Pointer to " + this.elemType;
		}

		public TypeKind getType() {
			return this.elemType;
		}

		@Override
		public boolean equals(Object other) {
			if (other == null)
				return false;
			if (this == other)
				return true;
			if (other instanceof Type) {
				Type otherType = (Type) other;
				if (this.getKind() == otherType.getKind())
					return true;
				if (this.getType() == otherType.getKind())
					return true;
				if (other instanceof Pointer && ((Pointer) other).getType() == this.getType())
					return true;
			}
			return false;
		}
	}

	static public class Array extends Type {
		private int size;
		private final Type elemType;

		public Array(int size, Type elemType) {
			super(elemType.equals(Type.Char) ? TypeKind.String : TypeKind.Array);
			this.size = size;
			this.elemType = elemType;
		}

		/** Returns the element bound of this array type. */
		public Type getElemType() {
			return this.elemType;
		}

		@Override
		public int size() {
			return size;
		}
		
		public void setSize(int size) {
			this.size = size;
		}

		@Override
		public String toString() {
			String type = elemType instanceof Array ? "(" + elemType + ")" : elemType.toString();
			return "Array[" + size + "] of " + type;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + this.elemType.hashCode();
			result = prime * result + this.size;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null)
				return false;
			if (this == obj)
				return true;
			if (!(obj instanceof Array))
				return false;
			Array other = (Array) obj;
			if (!this.elemType.equals(other.elemType))
				return false;
			if (this.size == -1)
				return true;
			if (other.size == -1)
				return true;
			if (this.size != other.size)
				return false;
			return true;
		}

	}
}
