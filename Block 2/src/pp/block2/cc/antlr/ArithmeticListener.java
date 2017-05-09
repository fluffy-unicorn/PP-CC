// Generated from Arithmetic.g4 by ANTLR 4.7
package pp.block2.cc.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ArithmeticParser}.
 */
public interface ArithmeticListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ArithmeticParser#main}.
	 * @param ctx the parse tree
	 */
	void enterMain(ArithmeticParser.MainContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArithmeticParser#main}.
	 * @param ctx the parse tree
	 */
	void exitMain(ArithmeticParser.MainContext ctx);
	/**
	 * Enter a parse tree produced by the {@code negationExpr}
	 * labeled alternative in {@link ArithmeticParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNegationExpr(ArithmeticParser.NegationExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code negationExpr}
	 * labeled alternative in {@link ArithmeticParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNegationExpr(ArithmeticParser.NegationExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code powerExpr}
	 * labeled alternative in {@link ArithmeticParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterPowerExpr(ArithmeticParser.PowerExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code powerExpr}
	 * labeled alternative in {@link ArithmeticParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitPowerExpr(ArithmeticParser.PowerExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bracketExpr}
	 * labeled alternative in {@link ArithmeticParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterBracketExpr(ArithmeticParser.BracketExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bracketExpr}
	 * labeled alternative in {@link ArithmeticParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitBracketExpr(ArithmeticParser.BracketExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code additionExpr}
	 * labeled alternative in {@link ArithmeticParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAdditionExpr(ArithmeticParser.AdditionExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code additionExpr}
	 * labeled alternative in {@link ArithmeticParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAdditionExpr(ArithmeticParser.AdditionExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code multiplicationExpr}
	 * labeled alternative in {@link ArithmeticParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicationExpr(ArithmeticParser.MultiplicationExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code multiplicationExpr}
	 * labeled alternative in {@link ArithmeticParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicationExpr(ArithmeticParser.MultiplicationExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldExpr}
	 * labeled alternative in {@link ArithmeticParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterFieldExpr(ArithmeticParser.FieldExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldExpr}
	 * labeled alternative in {@link ArithmeticParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitFieldExpr(ArithmeticParser.FieldExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code subtractionExpr}
	 * labeled alternative in {@link ArithmeticParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterSubtractionExpr(ArithmeticParser.SubtractionExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code subtractionExpr}
	 * labeled alternative in {@link ArithmeticParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitSubtractionExpr(ArithmeticParser.SubtractionExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArithmeticParser#field}.
	 * @param ctx the parse tree
	 */
	void enterField(ArithmeticParser.FieldContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArithmeticParser#field}.
	 * @param ctx the parse tree
	 */
	void exitField(ArithmeticParser.FieldContext ctx);
}