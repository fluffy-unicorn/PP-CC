/**
 * 
 */
package pp.block2.cc.ll;

import pp.block2.cc.NonTerm;
import pp.block2.cc.SymbolFactory;
import pp.block2.cc.Term;

/**
 * Class containing some example grammars.
 * @author Arend Rensink
 *
 */
public class Grammars {
	/** Returns a grammar for simple English sentences. */
	public static Grammar makeSentence() {
		// Define the non-terminals
		NonTerm sent = new NonTerm("Sentence");
		NonTerm subj = new NonTerm("Subject");
		NonTerm obj = new NonTerm("Object");
		NonTerm mod = new NonTerm("Modifier");
		// Define the terminals, using the Sentence.g4 lexer grammar
		// Make sure you take the token constantss from the right class!
		SymbolFactory fact = new SymbolFactory(Sentence.class);
		Term noun = fact.getTerminal(Sentence.NOUN);
		Term verb = fact.getTerminal(Sentence.VERB);
		Term adj = fact.getTerminal(Sentence.ADJECTIVE);
		Term end = fact.getTerminal(Sentence.ENDMARK);
		// Build the context free grammar
		Grammar g = new Grammar(sent);
		g.addRule(sent, subj, verb, obj, end);
		g.addRule(subj, noun);
		g.addRule(subj, mod, subj);
		g.addRule(obj, noun);
		g.addRule(obj, mod, obj);
		g.addRule(mod, adj);
		return g;
	}
	
	public static Grammar makeIf() {
		NonTerm stat = new NonTerm("Stat");
		NonTerm elsepart = new NonTerm("ElsePart");
		SymbolFactory fact = new SymbolFactory(If.class);
		Term _if = fact.getTerminal(If.IF);
		Term _then = fact.getTerminal(If.THEN);
		Term _else = fact.getTerminal(If.ELSE);
		Term _assign = fact.getTerminal(If.ASSIGN);
		Grammar g = new Grammar(stat);
		g.addRule(stat, _assign);
		g.addRule(stat, _if, _assign, _then, stat, elsepart);
		g.addRule(elsepart, _else, stat);
		g.addRule(elsepart);
		return g;
	}
	
	public static Grammar makeCC2() {
		NonTerm L = new NonTerm("L");
		NonTerm R = new NonTerm("R");
		NonTerm Rp= new NonTerm("Rp");
		NonTerm Q = new NonTerm("Q");
		NonTerm Qp= new NonTerm("Qp");
		SymbolFactory fact = new SymbolFactory(CC2.class);
		Term a = fact.getTerminal(CC2.A);
		Term b = fact.getTerminal(CC2.B);
		Term c = fact.getTerminal(CC2.C);
		Grammar g = new Grammar(L);
		g.addRule(L, R, a);
		g.addRule(L, Q, a);
		g.addRule(R, a, b, a, Rp);
		g.addRule(R, c, a, b, a, Rp);
		g.addRule(Rp, b, c, Rp);
		g.addRule(Rp);
		g.addRule(Q, b, Qp);
		g.addRule(Qp, b, c);
		g.addRule(Qp, c);
		return g;
	}
}
