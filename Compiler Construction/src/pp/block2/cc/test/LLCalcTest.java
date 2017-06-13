package pp.block2.cc.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import pp.block2.cc.NonTerm;
import pp.block2.cc.Symbol;
import pp.block2.cc.Term;
import pp.block2.cc.ll.CC2;
import pp.block2.cc.ll.Grammar;
import pp.block2.cc.ll.Grammars;
import pp.block2.cc.ll.If;
import pp.block2.cc.ll.LLCalc;
import pp.block2.cc.ll.MyLLCalc;
import pp.block2.cc.ll.Rule;
import pp.block2.cc.ll.Sentence;

public class LLCalcTest {
	// Sentence Grammar //
	Grammar sentenceG = Grammars.makeSentence();
	// Define the non-terminals
	NonTerm subj = sentenceG.getNonterminal("Subject");
	NonTerm obj = sentenceG.getNonterminal("Object");
	NonTerm sent = sentenceG.getNonterminal("Sentence");
	NonTerm mod = sentenceG.getNonterminal("Modifier");
	// Define the terminals
	Term adj = sentenceG.getTerminal(Sentence.ADJECTIVE);
	Term noun = sentenceG.getTerminal(Sentence.NOUN);
	Term verb = sentenceG.getTerminal(Sentence.VERB);
	Term end = sentenceG.getTerminal(Sentence.ENDMARK);
	// Now add the last rule, causing the grammar to fail
	Grammar sentenceXG = Grammars.makeSentence();
	{
		sentenceXG.addRule(mod, mod, mod);
	}
	LLCalc sentenceXLL = createCalc(sentenceXG);

	// If Grammar //
	Grammar ifG = Grammars.makeIf();
	NonTerm stat = ifG.getNonterminal("Stat");
	NonTerm elsepart = ifG.getNonterminal("ElsePart");
	Term ifT = ifG.getTerminal(If.IF);
	Term thenT = ifG.getTerminal(If.THEN);
	Term condT = ifG.getTerminal(If.COND);
	Term elseT = ifG.getTerminal(If.ELSE);
	Term assignT = ifG.getTerminal(If.ASSIGN);
	LLCalc ifLL = createCalc(ifG);

	// CC2 Grammar //
	Grammar cc2G = Grammars.makeCC2();
	NonTerm L = cc2G.getNonterminal("L");
	NonTerm R = cc2G.getNonterminal("R");
	NonTerm Rp= cc2G.getNonterminal("Rp");
	NonTerm Q = cc2G.getNonterminal("Q");
	NonTerm Qp= cc2G.getNonterminal("Qp");
	Term a = cc2G.getTerminal(CC2.A);
	Term b = cc2G.getTerminal(CC2.B);
	Term c = cc2G.getTerminal(CC2.C);
	LLCalc cc2LL = createCalc(cc2G);
	
	/** Tests the LL-calculator for the Sentence grammar. */
	@Test
	public void testSentenceOrigLL1() {
		// Without the last (recursive) rule, the grammar is LL-1
		assertTrue(createCalc(sentenceG).isLL1());
	}

	@Test
	public void testSentenceXFirst() {
		Map<Symbol, Set<Term>> first = sentenceXLL.getFirst();
		assertEquals(set(adj, noun), first.get(sent));
		assertEquals(set(adj, noun), first.get(subj));
		assertEquals(set(adj, noun), first.get(obj));
		assertEquals(set(adj), first.get(mod));
	}

	@Test
	public void testSentenceXFollow() {
		// FOLLOW sets
		Map<NonTerm, Set<Term>> follow = sentenceXLL.getFollow();
		assertEquals(set(Symbol.EOF), follow.get(sent));
		assertEquals(set(verb), follow.get(subj));
		assertEquals(set(end), follow.get(obj));
		assertEquals(set(noun, adj), follow.get(mod));
	}

	@Test
	public void testSentenceXFirstPlus() {
		// Test per rule
		Map<Rule, Set<Term>> firstp = sentenceXLL.getFirstp();
		List<Rule> subjRules = sentenceXG.getRules(subj);
		assertEquals(set(noun), firstp.get(subjRules.get(0)));
		assertEquals(set(adj), firstp.get(subjRules.get(1)));
	}

	@Test
	public void testSentenceXLL1() {
		assertFalse(sentenceXLL.isLL1());
	}

	@Test
	public void testIfFirst() {
		Map<Symbol, Set<Term>> first = ifLL.getFirst();
		assertEquals(set(assignT,ifT), first.get(stat));
		assertEquals(set(elseT, Symbol.EMPTY), first.get(elsepart));
	}

	@Test
	public void testIfFollow() {
		Map<NonTerm, Set<Term>> follow = ifLL.getFollow();
		assertEquals(set(Symbol.EOF, elseT), follow.get(stat));
		assertEquals(set(Symbol.EOF, elseT), follow.get(elsepart));
	}

	@Test
	public void testIfFirstPlus() {
		Map<Rule, Set<Term>> firstp = ifLL.getFirstp();
		List<Rule> statRules = ifG.getRules(stat);
		List<Rule> elseRules = ifG.getRules(elsepart);
		assertEquals(set(assignT), firstp.get(statRules.get(0)));
		assertEquals(set(ifT), firstp.get(statRules.get(1)));
		assertEquals(set(elseT), firstp.get(elseRules.get(0)));
		assertEquals(set(Symbol.EOF, elseT, Symbol.EMPTY), firstp.get(elseRules.get(1)));
	}

	@Test
	public void testIfLL1() {
		assertFalse(ifLL.isLL1());
	}

	@Test
	public void testCCFirst() {
		Map<Symbol, Set<Term>> first = cc2LL.getFirst();
		assertEquals(set(a,c,b), first.get(L));
		assertEquals(set(a,c), first.get(R));
		assertEquals(set(b, Symbol.EMPTY), first.get(Rp));
		assertEquals(set(b), first.get(Q));
		assertEquals(set(b,c), first.get(Qp));
		
	}
	
	@Test
	public void testCCFollow () {
		Map<NonTerm, Set<Term>> follow = cc2LL.getFollow();
		assertEquals(set(Symbol.EOF), follow.get(L));
		assertEquals(set(a), follow.get(R));
		assertEquals(set(a), follow.get(Rp));
		assertEquals(set(a), follow.get(Q));
		assertEquals(set(a), follow.get(Qp));
		}
	
	@Test
	public void testCCFirstPlus() {
		Map<Rule, Set<Term>> firstp = cc2LL.getFirstp();
		List<Rule> LRules = cc2G.getRules(L);
		List<Rule> RRules = cc2G.getRules(R);
		List<Rule> RpRules = cc2G.getRules(Rp);
		List<Rule> QRules = cc2G.getRules(Q);
		List<Rule> QpRules = cc2G.getRules(Qp);
		assertEquals(set(a,c), firstp.get(LRules.get(0)));
		assertEquals(set(b), firstp.get(LRules.get(1)));
		assertEquals(set(a), firstp.get(RRules.get(0)));
		assertEquals(set(c), firstp.get(RRules.get(1)));
		assertEquals(set(b), firstp.get(RpRules.get(0)));
		assertEquals(set(Symbol.EMPTY, a), firstp.get(RpRules.get(1)));
		assertEquals(set(b), firstp.get(QRules.get(0)));
		assertEquals(set(b), firstp.get(QpRules.get(0)));
		assertEquals(set(c), firstp.get(QpRules.get(1)));
		
	}
	
	@Test
	public void testCCLL1() {
		assertTrue(cc2LL.isLL1());
	}
	/** Creates an LL1-calculator for a given grammar. */
	private LLCalc createCalc(Grammar g) {
		return new MyLLCalc(g);
	}

	@SuppressWarnings("unchecked")
	private <T> Set<T> set(T... elements) {
		return new HashSet<>(Arrays.asList(elements));
	}
}
