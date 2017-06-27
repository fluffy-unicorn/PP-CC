package project.sprockell;

import project.compiler.IntegerReference;

/**
 * Interface for the different types in SPRIL.
 */
public interface SPRILType {
	
	/**
	 * Operators for the {@link Instruction#InstructionType} {@code Compute}
	 */
	public enum Op implements SPRILType {
		Add, Sub, Mul, Div, Mod, Equal, Neq, Gt, Lt, GtE, LtE, And, Or, Xor, LShift, RShift, Decr, Incr;
	}

	/**
	 * Register for the SPRIL language
	 */
	public enum Reg implements SPRILType {
		reg0, regSprId, regA, regB, regC, regD, regE, regF, regARP, regDSZ, regSP, regPC, numberIO, charIO, boolIO, stringIO;
	}
	
	/**
	 * Wrapper for comments
	 */
	public class Comment implements SPRILType {
		private String comment;
		public Comment(String comment) {
			this.comment = comment;
		}
		@Override
		public String toString() {
			return this.comment;
		}
	}

	/**
	 * Wrapper class for SPRIL jump targets
	 */
	public class Target implements SPRILType {
		/** Different types of targets */
		public enum TTypes {
			/** Absolute jump target value */
			Abs, 
			/** Relative jump, increase program counter */
			Rel,
			/** Indirect jump, set program counter to the value at the register */
			Ind;
		}

		private TTypes t;
		private Object addr;

		private Target(TTypes t, int addr) {
			if (t == TTypes.Ind) {
				throw new IllegalArgumentException("Second argument must be a register");
			}
			this.t = t;
			this.addr = addr;
		}

		private Target(TTypes t, Reg reg) {
			if (t != TTypes.Ind) {
				throw new IllegalArgumentException("Second argument must be a memory address (integer)");
			}
			this.t = t;
			this.addr = reg;
		}

		private Target(TTypes t, IntegerReference intRef) {
			this.t = t;
			this.addr = intRef;
		}
		public static Target target(TTypes t, int addr) {
			return new Target(t, addr);
		}

		public static Target target(TTypes t, Reg reg) {
			return new Target(t, reg);
		}
		public static Target target(TTypes t, IntegerReference intRef) {
			return new Target(t, intRef);
		}

		@Override
		public String toString() {
			return "(" + t + " " + SPRILType.toString(addr) + ")";
		}
	}

	public static String toString(Object o) {
		int i;
		if(o instanceof Reg) {
			return ((Reg) o).toString();
		} else if (o instanceof IntegerReference) {
			i = ((IntegerReference) o).getValue();
		} else {
			i = (int) o;
		}
		return (i < 0 ? "(" + i + ")" : "" + i);
	}
	/**
	 * Wrapper class for the SPRIL AddrImmDI type
	 */
	public class AddrImmDI implements SPRILType {
		/** Types of addressses */
		public enum ATypes {
			/** Immediate value. */
			ImmValue, 
			/** Direct address, the address is an address in (local or shared) memory */
			DirAddr, 
			/** Indirect address, the address is the content of the register */
			IndAddr;
		}

		private ATypes t;
		private Object addr;
		
		private AddrImmDI(ATypes t, int addr) {
			if (t == ATypes.IndAddr) {
				throw new IllegalArgumentException("Second argument must be a register");
			}
			this.t = t;
			this.addr = addr;
		}

		private AddrImmDI(ATypes t, Reg reg) {
			if (t != ATypes.IndAddr) {
				throw new IllegalArgumentException("Second argument must be a memory address (integer)");
			}
			this.t = t;
			this.addr = reg;
		}

		private AddrImmDI(ATypes t, IntegerReference intRef) {
			this.t = t;
			this.addr = intRef;
		}
		
		public static AddrImmDI addr(ATypes t, int addr) {
			return new AddrImmDI(t, addr);
		}

		public static AddrImmDI addr(ATypes t, Reg reg) {
			return new AddrImmDI(t, reg);
		}
		
		public static AddrImmDI addr(ATypes t, IntegerReference intRef) {
			return new AddrImmDI(t, intRef);
		}

		@Override
		public String toString() {
			return "(" + t + " " + SPRILType.toString(addr) + ")";
		}
	}
}
