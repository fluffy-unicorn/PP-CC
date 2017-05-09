package pp.block2.cc.ll;

import java.util.List;

import static pp.block2.cc.ll.CC2.A;
import static pp.block2.cc.ll.CC2.B;
import static pp.block2.cc.ll.CC2.C;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

import pp.block2.cc.AST;
import pp.block2.cc.NonTerm;
import pp.block2.cc.ParseException;
import pp.block2.cc.Parser;
import pp.block2.cc.SymbolFactory;

public class CC2Parser implements Parser {

	private final SymbolFactory fact;
	private List<? extends Token> tokens;
	private int index;

	private static final NonTerm L = new NonTerm("L");
	private static final NonTerm R = new NonTerm("R");
	private static final NonTerm Rp = new NonTerm("Rp");
	private static final NonTerm Q = new NonTerm("Q");
	private static final NonTerm Qp = new NonTerm("Qp");

	public CC2Parser() {
		this.fact = new SymbolFactory(CC2.class);
	}

	@Override
	public AST parse(Lexer lexer) throws ParseException {
		this.tokens = lexer.getAllTokens();
		this.index = 0;
		return parseL();
	}

	private AST parseL() throws ParseException {
		AST result = new AST(L);
		Token next = peek();
		switch (next.getType()) {
		case A:
		case C:
			result.addChild(parseR());
			result.addChild(parseToken(A));
			break;
		case B:
			result.addChild(parseQ());
			result.addChild(parseToken(A));
			break;
		default:
			throw unparsable(L);
		}
		return result;
	}

	private AST parseR() throws ParseException {
		AST result = new AST(R);
		Token next = peek();
		switch (next.getType()) {
		case A:
			result.addChild(parseToken(A));
			result.addChild(parseToken(B));
			result.addChild(parseToken(A));
			result.addChild(parseRp());
			break;
		case C:
			result.addChild(parseToken(C));
			result.addChild(parseToken(A));
			result.addChild(parseToken(B));
			result.addChild(parseToken(A));
			result.addChild(parseRp());
			break;
		default:
			throw unparsable(R);

		}
		return result;
	}

	private AST parseRp() throws ParseException {
		AST result = new AST(Rp);
		Token next = peek();
		switch (next.getType()) {
		case B:
			result.addChild(parseToken(B));
			result.addChild(parseToken(C));
			result.addChild(parseRp());
			break;
		case A:
			break;
		default:
			throw unparsable(Rp);
		}
		return result;
	}

	private AST parseQ() throws ParseException {
		AST result = new AST(Q);
		result.addChild(parseToken(B));
		result.addChild(parseQp());
		return result;
	}

	private AST parseQp() throws ParseException {
		AST result = new AST(Qp);
		Token next = peek();
		switch (next.getType()) {
		case B:
			result.addChild(parseToken(B));
			result.addChild(parseToken(C));
			break;
		case C:
			result.addChild(parseToken(C));
			break;
		default:
			unparsable(Qp);
		}
		return result;
	}

	private ParseException unparsable(NonTerm nt) {
		try {
			Token next = peek();
			return new ParseException(String.format("Line %d:%d - could not parse '%s' at token '%s'", next.getLine(),
					next.getCharPositionInLine(), nt.getName(), this.fact.get(next.getType())));
		} catch (ParseException e) {
			return e;
		}
	}

	/** Creates an AST based on the expected token type. */
	private AST parseToken(int tokenType) throws ParseException {
		Token next = next();
		if (next.getType() != tokenType) {
			throw new ParseException(String.format("Line %d:%d - expected token '%s' but found '%s'", next.getLine(),
					next.getCharPositionInLine(), this.fact.get(tokenType), this.fact.get(next.getType())));
		}
		return new AST(this.fact.getTerminal(tokenType), next);
	}

	/** Returns the next token, without moving the token index. */
	private Token peek() throws ParseException {
		if (this.index >= this.tokens.size()) {
			throw new ParseException("Reading beyond end of input");
		}
		return this.tokens.get(this.index);
	}

	/** Returns the next token and moves up the token index. */
	private Token next() throws ParseException {
		Token result = peek();
		this.index++;
		return result;
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("Usage: [text]+");
		} else {
			for (String text : args) {
				CharStream stream = CharStreams.fromString(text);
				Lexer lexer = new CC2(stream);
				try {
					System.out.printf("Parse tree: %n%s%n",
							new CC2Parser().parse(lexer));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
