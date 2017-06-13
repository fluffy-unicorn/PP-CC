package pp.block2.cc.ll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pp.block2.cc.NonTerm;
import pp.block2.cc.Symbol;
import pp.block2.cc.Term;

public class MyLLCalc implements LLCalc {
	private Grammar myGrammar;
	private Map<Symbol, Set<Term>> _FIRST = null;
	private Map<NonTerm, Set<Term>> _FOLLOW = null;
	private Map<Rule, Set<Term>> _FIRSTp = null;

	public MyLLCalc(Grammar g) {
		myGrammar = g;
		_FIRST = calculateFirst();
		_FOLLOW = calculateFollow();
		_FIRSTp = calculateFirstp();
	}

	private Map<Symbol, Set<Term>> calculateFirst() {
		Map<Symbol, Set<Term>> FIRST = new HashMap<Symbol, Set<Term>>();
		for (Symbol s : myGrammar.getNonterminals()) {
			FIRST.put(s, new HashSet<Term>());
		}
		for (Term t : myGrammar.getTerminals()) {
			Set<Term> tSet = new HashSet<Term>();
			tSet.add(t);
			FIRST.put(t, tSet);
		}
		boolean unstable = true;
		while (unstable) {
			boolean firstChanged = false;
			for (Rule p : myGrammar.getRules()) {
				List<Symbol> BETA = copy(p.getRHS());
				int k = BETA.size();
				int i = 0;
				Set<Term> rhs = new HashSet<Term>();
				if (!BETA.isEmpty()) {
					rhs = copy(FIRST.get(BETA.get(0)));
					for (; i <= k - 1; i++) {
						if (!FIRST.get(BETA.get(i)).contains(Symbol.EMPTY)) {
							break;
						} else {
							Set<Term> copy = copy(FIRST.get(BETA.get(i + 1)));
							copy.remove(Symbol.EMPTY);
							rhs.addAll(copy);
						}
					}
				} else {
					rhs.add(Symbol.EMPTY);
				}
				if (i == k && !BETA.isEmpty() && FIRST.get(
						BETA.get(k)).contains(Symbol.EMPTY)) {
					rhs.add(Symbol.EMPTY);
				}
				boolean addAllResult = FIRST.get(p.getLHS()).addAll(rhs);
				if (!firstChanged) {
					firstChanged = addAllResult;
				}
			}
			unstable = firstChanged;
		}
		return FIRST;
	}

	private Map<NonTerm, Set<Term>> calculateFollow() {
		Map<NonTerm, Set<Term>> FOLLOW = new HashMap<NonTerm, Set<Term>>();
		for (NonTerm s : myGrammar.getNonterminals()) {
			FOLLOW.put(s, new HashSet<Term>());
		}
		Set<Term> startSet = new HashSet<Term>();
		startSet.add(Symbol.EOF);
		FOLLOW.put(myGrammar.getStart(), startSet);
		boolean unstable = true;
		while (unstable) {
			boolean followChanged = false;
			for (Rule p : myGrammar.getRules()) {
				Set<Term> TRAILER = copy(FOLLOW.get(p.getLHS()));
				List<Symbol> beta = copy(p.getRHS());
				int k = beta.size();
				for (int i = (k - 1); i >= 0; i--) {
					if (myGrammar.getNonterminals().contains(beta.get(i))) {
						boolean addAllResult = FOLLOW.get(beta.get(i)).addAll(TRAILER);
						if (!followChanged) {
							followChanged = addAllResult;
						}
						if (_FIRST.get(beta.get(i)).contains(Symbol.EMPTY)) {
							Set<Term> copy = copy(_FIRST.get(beta.get(i)));
							copy.remove(Symbol.EMPTY);
							TRAILER.addAll(copy);
						} else {
							TRAILER = copy(_FIRST.get(beta.get(i)));
						}
					} else {
						TRAILER = copy(_FIRST.get(beta.get(i)));
					}
				}
			}
			unstable = followChanged;
		}
		return FOLLOW;
	}

	@Override
	public Map<Symbol, Set<Term>> getFirst() {
		return _FIRST;
	}

	@Override
	public Map<NonTerm, Set<Term>> getFollow() {
		return _FOLLOW;
	}

	public Set<Term> first(List<Symbol> list) {
		if (list.isEmpty() || (list.size() == 1 && list.contains(Symbol.EMPTY))) {
			Set<Term> result = new HashSet<Term>();
			result.add(Symbol.EMPTY);
			return result;
		} else if (!_FIRST.get(list.get(0)).contains(Symbol.EMPTY)) {
			return copy(_FIRST.get(list.get(0)));
		} else {
			Set<Term> copy = copy(_FIRST.get(list.get(0)));
			copy.remove(Symbol.EMPTY);
			Set<Term> result = new HashSet<Term>();
			List<Symbol> betaPrime = copy(list);
			betaPrime.remove(list.get(0));
			result.addAll(copy);
			result.addAll(first(betaPrime));
			return result;
		}
	}

	@Override
	public Map<Rule, Set<Term>> getFirstp() {
		return _FIRSTp;
	}

	public Map<Rule, Set<Term>> calculateFirstp() {
		Map<Rule, Set<Term>> FIRSTp = new HashMap<Rule, Set<Term>>();
		for (Rule p : myGrammar.getRules()) {
			Set<Term> firstBeta = first(p.getRHS());
			if (!firstBeta.contains(Symbol.EMPTY)) {
				FIRSTp.put(p, firstBeta);
			} else {
				Set<Term> result = copy(firstBeta);
				result.addAll(_FOLLOW.get(p.getLHS()));
				FIRSTp.put(p, result);
			}
		}
		return FIRSTp;
	}

	@Override
	public boolean isLL1() {
		for (NonTerm s : myGrammar.getNonterminals()) {
			List<Rule> rules = myGrammar.getRules(s);
			if (rules.size() >= 2) {
				Set<Term> union = copy(_FIRSTp.get(rules.get(0)));
				for (int i = 1; i < rules.size(); i++) {
					int oldSize = union.size();
					Set<Term> firstp = _FIRSTp.get(rules.get(i));
					union.addAll(_FIRSTp.get(rules.get(i)));
					if (oldSize + firstp.size() != union.size()) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public <T> Set<T> copy(Set<T> list) {
		return new HashSet<T>(list);
	}

	public <T> List<T> copy(List<T> list) {
		return new ArrayList<T>(list);
		
	}
}
