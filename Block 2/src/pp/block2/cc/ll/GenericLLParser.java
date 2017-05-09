package pp.block2.cc.ll;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

import pp.block2.cc.AST;
import pp.block2.cc.NonTerm;
import pp.block2.cc.ParseException;
import pp.block2.cc.Parser;
import pp.block2.cc.Symbol;
import pp.block2.cc.SymbolFactory;
import pp.block2.cc.Term;

/** Generic table-driven LL(1)-parser. */
public class GenericLLParser implements Parser {
	/** The grammar underlying this parser instance. */
	private final Grammar g;
	/** The LL-calculator for the grammar. */
	private final LLCalc calc;
	/** Map from non-terminals to maps of rules indexed by terminal. */
	private Map<NonTerm, Map<Term, Rule>> ll1Table;
	/** Current index in the token list. */
	private int index;
	/** Token list of the currently parsed input. */
	private List<? extends Token> tokens;

	private SymbolFactory fact;

	public GenericLLParser(Grammar g) {
		this.g = g;
		this.calc = new MyLLCalc(g);
	}

	@Override
	public AST parse(Lexer lexer) throws ParseException {
		this.tokens = lexer.getAllTokens();
		this.index = 0;
		this.fact = new SymbolFactory(lexer.getClass());
		return parse(this.g.getStart());
	}
	
	/** Parses the start of the token stream according to a given
	 * symbol. If it is a terminal, that should be the first token;
	 * otherwise, it is a non-terminal that should be expanded 
	 * according to the next token in the token stream, using the
	 * FIRST+-lookup table and recursively calling {@link #parse(Rule)}
	 * @param symb the symbol according to which the token stream 
	 * should be parsed
	 * @return the sub-AST resulting from the parsing of symb;
	 * or null if the symbol expands to the empty string
	 * @throws ParseException if the symbol cannot be parsed
	 * because the token stream does not contain the expected symbols
	 */
	private AST parse(Symbol symb) throws ParseException {
		AST result;
		if(g.getTerminals().contains(symb)) {
			Term t = (Term) symb;
			result = parseToken(t.getTokenType());
		} else {
			NonTerm nt = (NonTerm) symb;
			result = parse(lookup(nt));
			
		}
		return result;
	}
	/** Parses the start of the token stream according to a given
	 * rule, recursively calling {@link #parse(Symbol)} to process
	 * the RHS.
	 * @return the sub-AST resulting from the parsing of the rule.
	 * The top node is the node for the LHS of the rule, its direct
	 * children correspond to the symbols of the rule's RHS.
	 * @throws ParseException if the symbol cannot be parsed
	 * because the token stream does not contain the expected symbols
	 */
	private AST parse(Rule rule) throws ParseException {
		AST result = new AST(rule.getLHS());
		for (Symbol s : rule.getRHS()) {
			result.addChild(parse(s));
		}
		return result;
	}

	/** Uses the lookup table to look up the rule to which
	 * a given nonterminal should be expanded.
	 * The next rule is determined by the next token, using the
	 * FIRST+-set of the nonterminal.
	 * @throws ParseException if the lookup table does not 
	 * contain a rule for the nonterminal in combination with
	 * the first token in the token stream.
	 */
	private Rule lookup(NonTerm nt) throws ParseException {
		Rule result;
		if (atEnd()) {
			result = getLL1Table().get(nt).get(Term.EOF);
			if (result == null) {
				throw new ParseException("Reading beyond end of input");
			}
		} else {
			Token nextToken = peek();
			Term term = this.g.getTerminal(nextToken.getType());
			result = getLL1Table().get(nt).get(term);
			if (result == null) {
				throw new ParseException(String.format(
						"Line %d:%d - no rule for '%s' on token '%s'",
						nextToken.getLine(), 
						nextToken.getCharPositionInLine(),
						nt.getName(), nextToken));
			}
		}
		return result;
	}

	/** Tests whether the end of input has been reached. */
	private boolean atEnd() {
		return this.index >= this.tokens.size();
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

	/** Lazily builds and then returns the lookup table. */
	private Map<NonTerm, Map<Term, Rule>> getLL1Table() {
		if (this.ll1Table == null) {
			this.ll1Table = calcLL1Table();
		}
		return this.ll1Table;
	}

	/** Constructs the {@link #ll1Table}. */
	private Map<NonTerm, Map<Term, Rule>> calcLL1Table() {
		Map<NonTerm, Map<Term, Rule>> result = new HashMap<NonTerm, Map<Term, Rule>>();
		for (NonTerm nt : g.getNonterminals()) {
			Map<Term, Rule> row = new HashMap<Term, Rule>();
			for (Rule p : g.getRules(nt)) {
				for (Term t : calc.getFirstp().get(p)) {
					row.put(t, p);
				}
			}
			result.put(nt, row);
		}
		return result;
	}
	
	private AST parseToken(int tokenType) throws ParseException {
		Token next = next();
		if (next.getType() != tokenType) {
			throw new ParseException(String.format("Line %d:%d - expected token '%s' but found '%s'", next.getLine(),
					next.getCharPositionInLine(), this.fact.get(tokenType), this.fact.get(next.getType())));
		}
		return new AST(this.fact.getTerminal(tokenType), next);
	}
	


}
