// Generated from Tree.g4 by ANTLR 4.7
package pp.s1500376.q1_4;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TreeParser}.
 */
public interface TreeListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TreeParser#top}.
	 * @param ctx the parse tree
	 */
	void enterTop(TreeParser.TopContext ctx);
	/**
	 * Exit a parse tree produced by {@link TreeParser#top}.
	 * @param ctx the parse tree
	 */
	void exitTop(TreeParser.TopContext ctx);
	/**
	 * Enter a parse tree produced by the {@code num}
	 * labeled alternative in {@link TreeParser#node}.
	 * @param ctx the parse tree
	 */
	void enterNum(TreeParser.NumContext ctx);
	/**
	 * Exit a parse tree produced by the {@code num}
	 * labeled alternative in {@link TreeParser#node}.
	 * @param ctx the parse tree
	 */
	void exitNum(TreeParser.NumContext ctx);
	/**
	 * Enter a parse tree produced by the {@code left}
	 * labeled alternative in {@link TreeParser#node}.
	 * @param ctx the parse tree
	 */
	void enterLeft(TreeParser.LeftContext ctx);
	/**
	 * Exit a parse tree produced by the {@code left}
	 * labeled alternative in {@link TreeParser#node}.
	 * @param ctx the parse tree
	 */
	void exitLeft(TreeParser.LeftContext ctx);
	/**
	 * Enter a parse tree produced by the {@code right}
	 * labeled alternative in {@link TreeParser#node}.
	 * @param ctx the parse tree
	 */
	void enterRight(TreeParser.RightContext ctx);
	/**
	 * Exit a parse tree produced by the {@code right}
	 * labeled alternative in {@link TreeParser#node}.
	 * @param ctx the parse tree
	 */
	void exitRight(TreeParser.RightContext ctx);
	/**
	 * Enter a parse tree produced by the {@code full}
	 * labeled alternative in {@link TreeParser#node}.
	 * @param ctx the parse tree
	 */
	void enterFull(TreeParser.FullContext ctx);
	/**
	 * Exit a parse tree produced by the {@code full}
	 * labeled alternative in {@link TreeParser#node}.
	 * @param ctx the parse tree
	 */
	void exitFull(TreeParser.FullContext ctx);
}