package pp.block2.cc.antlr;

import java.math.BigInteger;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import pp.block2.cc.ParseException;
import pp.block2.cc.SymbolFactory;
import pp.block2.cc.antlr.ArithmeticParser.AdditionExprContext;
import pp.block2.cc.antlr.ArithmeticParser.BracketExprContext;
import pp.block2.cc.antlr.ArithmeticParser.FieldExprContext;
import pp.block2.cc.antlr.ArithmeticParser.MainContext;
import pp.block2.cc.antlr.ArithmeticParser.MultiplicationExprContext;
import pp.block2.cc.antlr.ArithmeticParser.NegationExprContext;
import pp.block2.cc.antlr.ArithmeticParser.PowerExprContext;
import pp.block2.cc.antlr.ArithmeticParser.SubtractionExprContext;
import pp.block2.cc.ll.Sentence;

public class Calculator extends ArithmeticBaseListener {
	/**
	 * Factory needed to create terminals of the {@link Sentence} grammar. See
	 * {@link pp.block2.cc.ll.SentenceParser} for example usage.
	 */
	private final SymbolFactory fact;
	private boolean error = false;
	private ParseTreeProperty<BigInteger> values = new ParseTreeProperty<BigInteger>();

	public Calculator() {
		this.fact = new SymbolFactory(ArithmeticLexer.class);
	}

	public BigInteger compute(Lexer lexer) {
		TokenStream tokens = new CommonTokenStream(lexer);
		ArithmeticParser parser = new ArithmeticParser(tokens);
		ParseTree tree = parser.main();
		new ParseTreeWalker().walk(this, tree);
		return values.get(tree);
	}

	@Override
	public void exitMain(MainContext ctx) {
		values.put(ctx, values.get(ctx.getChild(0)));
	}

	@Override
	public void exitBracketExpr(BracketExprContext ctx) {
		values.put(ctx, values.get(ctx.getChild(1)));
	}
	
	@Override
	public void exitPowerExpr(PowerExprContext ctx) {
		BigInteger left = values.get(ctx.getChild(0));
		BigInteger right = values.get(ctx.getChild(2));
		BigInteger result = left.pow(right.intValue());
		values.put(ctx, result);
	}

	@Override
	public void exitNegationExpr(NegationExprContext ctx) {
		BigInteger value = values.get(ctx.getChild(1));
		BigInteger result = value.negate();
		values.put(ctx, result);
	}

	@Override
	public void exitMultiplicationExpr(MultiplicationExprContext ctx) {
		BigInteger left = values.get(ctx.getChild(0));
		BigInteger right = values.get(ctx.getChild(2));
		BigInteger result = left.multiply(right);
		values.put(ctx, result);
	}

	@Override
	public void exitSubtractionExpr(SubtractionExprContext ctx) {
		BigInteger left = values.get(ctx.getChild(0));
		BigInteger right = values.get(ctx.getChild(2));
		BigInteger result = left.subtract(right);
		values.put(ctx, result);
	}

	@Override
	public void exitAdditionExpr(AdditionExprContext ctx) {
		BigInteger left = values.get(ctx.getChild(0));
		BigInteger right = values.get(ctx.getChild(2));
		BigInteger result = left.add(right);
		values.put(ctx, result);
	}

	@Override
	public void exitFieldExpr(FieldExprContext ctx) {
		values.put(ctx, new BigInteger(ctx.getText()));
	}
}
