// Generated from SentenceCopy.g4 by ANTLR 4.7
package pp.block2.cc.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SentenceCopyParser}.
 */
public interface SentenceCopyListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SentenceCopyParser#sentence}.
	 * @param ctx the parse tree
	 */
	void enterSentence(SentenceCopyParser.SentenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link SentenceCopyParser#sentence}.
	 * @param ctx the parse tree
	 */
	void exitSentence(SentenceCopyParser.SentenceContext ctx);
	/**
	 * Enter a parse tree produced by the {@code modSubject}
	 * labeled alternative in {@link SentenceCopyParser#subject}.
	 * @param ctx the parse tree
	 */
	void enterModSubject(SentenceCopyParser.ModSubjectContext ctx);
	/**
	 * Exit a parse tree produced by the {@code modSubject}
	 * labeled alternative in {@link SentenceCopyParser#subject}.
	 * @param ctx the parse tree
	 */
	void exitModSubject(SentenceCopyParser.ModSubjectContext ctx);
	/**
	 * Enter a parse tree produced by the {@code simpleSubject}
	 * labeled alternative in {@link SentenceCopyParser#subject}.
	 * @param ctx the parse tree
	 */
	void enterSimpleSubject(SentenceCopyParser.SimpleSubjectContext ctx);
	/**
	 * Exit a parse tree produced by the {@code simpleSubject}
	 * labeled alternative in {@link SentenceCopyParser#subject}.
	 * @param ctx the parse tree
	 */
	void exitSimpleSubject(SentenceCopyParser.SimpleSubjectContext ctx);
	/**
	 * Enter a parse tree produced by {@link SentenceCopyParser#object}.
	 * @param ctx the parse tree
	 */
	void enterObject(SentenceCopyParser.ObjectContext ctx);
	/**
	 * Exit a parse tree produced by {@link SentenceCopyParser#object}.
	 * @param ctx the parse tree
	 */
	void exitObject(SentenceCopyParser.ObjectContext ctx);
	/**
	 * Enter a parse tree produced by {@link SentenceCopyParser#modifier}.
	 * @param ctx the parse tree
	 */
	void enterModifier(SentenceCopyParser.ModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link SentenceCopyParser#modifier}.
	 * @param ctx the parse tree
	 */
	void exitModifier(SentenceCopyParser.ModifierContext ctx);
}