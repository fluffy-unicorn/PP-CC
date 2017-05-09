package pp.block2.cc.antlr;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import pp.block2.cc.AST;
import pp.block2.cc.NonTerm;
import pp.block2.cc.ParseException;
import pp.block2.cc.Parser;
import pp.block2.cc.SymbolFactory;
import pp.block2.cc.antlr.SentenceParser.ModifierContext;
import pp.block2.cc.antlr.SentenceParser.ObjectContext;
import pp.block2.cc.antlr.SentenceParser.SentenceContext;
import pp.block2.cc.antlr.SentenceParser.SubjectContext;
import pp.block2.cc.ll.Sentence;

public class SentenceConverter extends SentenceBaseListener implements Parser {
	/**
	 * Factory needed to create terminals of the {@link Sentence} grammar. See
	 * {@link pp.block2.cc.ll.SentenceParser} for example usage.
	 */
	private final SymbolFactory fact;
	private boolean error = false;
	private ParseTreeProperty<AST> nodes =new ParseTreeProperty<AST>();

	public SentenceConverter() {
		this.fact = new SymbolFactory(Sentence.class);
	}

	@Override
	public AST parse(Lexer lexer) throws ParseException {
		TokenStream tokens = new CommonTokenStream(lexer);
		SentenceParser parser = new SentenceParser(tokens);
		ParseTree tree = parser.sentence();
		new ParseTreeWalker().walk(this, tree);
		if(error)
			throw new ParseException("");
		return this.nodes.get(tree);
	}

	@Override
	public void exitSentence(SentenceContext ctx) {
		AST ast = new AST(new NonTerm("Sentence"));
		for(ParseTree pt : ctx.children) {
			ast.addChild(nodes.get(pt));
		}
		nodes.put(ctx, ast);
	}

	@Override
	public void exitSubject(SubjectContext ctx) {
		AST ast = new AST(new NonTerm("Subject"));
		for(ParseTree pt : ctx.children) {
			ast.addChild(nodes.get(pt));
		}
		nodes.put(ctx, ast);
	}

	@Override
	public void exitModifier(ModifierContext ctx) {
		AST ast = new AST(new NonTerm("Modifier"));
		for(ParseTree pt : ctx.children) {
			ast.addChild(nodes.get(pt));
		}
		nodes.put(ctx, ast);
	}

	@Override
	public void exitObject(ObjectContext ctx) {
		AST ast = new AST(new NonTerm("Object"));
		for(ParseTree pt : ctx.children) {
			ast.addChild(nodes.get(pt));
		}
		nodes.put(ctx, ast);
	}

	@Override
	public void visitTerminal(TerminalNode node) {
		AST ast = new AST(fact.getTerminal(node.getSymbol().getType()), node.getSymbol());
		nodes.put(node, ast);
	}

	@Override
	public void visitErrorNode(ErrorNode node) {
		error = true;
	}
	

	
	
}
