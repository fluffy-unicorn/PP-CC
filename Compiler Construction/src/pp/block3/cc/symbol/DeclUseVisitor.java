// Generated from DeclUse.g4 by ANTLR 4.7
package pp.block3.cc.symbol;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link DeclUseParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface DeclUseVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link DeclUseParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(DeclUseParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link DeclUseParser#series}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSeries(DeclUseParser.SeriesContext ctx);
	/**
	 * Visit a parse tree produced by {@link DeclUseParser#unit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnit(DeclUseParser.UnitContext ctx);
	/**
	 * Visit a parse tree produced by {@link DeclUseParser#decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecl(DeclUseParser.DeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link DeclUseParser#use}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUse(DeclUseParser.UseContext ctx);
}