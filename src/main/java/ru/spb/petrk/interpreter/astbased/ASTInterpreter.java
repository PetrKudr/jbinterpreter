/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.astbased;

import ru.spb.petrk.interpreter.astbased.model.impl.IntSequenceValue;
import ru.spb.petrk.interpreter.astbased.model.impl.StringValueImpl;
import ru.spb.petrk.interpreter.astbased.model.impl.MapSequenceValue;
import ru.spb.petrk.interpreter.astbased.model.impl.IntValueImpl;
import ru.spb.petrk.interpreter.astbased.model.impl.FloatingValueImpl;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import ru.spb.petrk.ast.AST;
import ru.spb.petrk.ast.ASTUtils;
import ru.spb.petrk.ast.ASTUtils.ParserError;
import static ru.spb.petrk.ast.ASTUtils.position;
import ru.spb.petrk.ast.ASTVisitor;
import ru.spb.petrk.ast.BinaryOperator;
import ru.spb.petrk.ast.FloatingLiteral;
import ru.spb.petrk.ast.IntegerLiteral;
import ru.spb.petrk.ast.LambdaExpr;
import ru.spb.petrk.ast.MapOperator;
import ru.spb.petrk.ast.OutStmt;
import ru.spb.petrk.ast.PrintStmt;
import ru.spb.petrk.ast.ProgramStmt;
import ru.spb.petrk.ast.ReduceOperator;
import ru.spb.petrk.ast.RefExpr;
import ru.spb.petrk.ast.SequenceExpr;
import ru.spb.petrk.ast.Stmt;
import ru.spb.petrk.ast.StringLiteral;
import ru.spb.petrk.ast.UnaryOperator;
import ru.spb.petrk.ast.VarDeclStmt;
import ru.spb.petrk.interpreter.InterpreterError;
import ru.spb.petrk.interpreter.Interpreter;
import ru.spb.petrk.interpreter.InterpreterListener;
import ru.spb.petrk.interpreter.astbased.model.FloatingValue;
import ru.spb.petrk.interpreter.astbased.model.IntValue;
import ru.spb.petrk.interpreter.astbased.model.NumberValue;
import ru.spb.petrk.interpreter.astbased.model.SequenceValue;
import ru.spb.petrk.interpreter.astbased.model.StringValue;
import ru.spb.petrk.interpreter.astbased.model.Value;
import ru.spb.petrk.interpreter.astbased.model.VoidValue;
import ru.spb.petrk.interpreter.astbased.model.impl.VoidValueImpl;

/**
 *
 * @author petrk
 */
public final class ASTInterpreter implements Interpreter {
    
    @Override
    public boolean interpret(String input, final PrintStream out, final PrintStream err) {
        return interpret(input, new InterpreterListener() {
            @Override
            public void onOut(String msg) {
                out.print(msg);
            }

            @Override
            public void onError(InterpreterError error) {
                err.println(error.toString());
            }
        });
    }
    
    @Override
    public boolean interpret(String input, InterpreterListener listener) {
        List<ASTUtils.ParserError> parseOrLexErrors = new ArrayList<>();
        ProgramStmt program = ASTUtils.parse(input, parseOrLexErrors);
        if (program == null) {
            parseOrLexErrors.stream()
                    .map(parseError -> toInterpretError(parseError))
                    .forEach(interpretError -> listener.onError(interpretError));
            return false;
        }
        try {
            interpret(program, new HashMap<>(), listener);
            return true;
        } catch (ASTInterruptedInterpreterException ex) {
            // Just return
            return false;
        } catch (ASTInterpreterException ex) {
            assert ex.getError() != null;
            listener.onError(ex.getError());
            return false;
        }
    }
    
    /**
     * Interprets code with the given symbol table.
     * 
     * Doesn't prints any output at all.
     * 
     * @param code
     * @param symTable
     * 
     * @throws ASTInterpreterException
     * @throws ASTInterruptedInterpreterException
     * 
     * @return value of the code (void value if code doesn't return value)
     */
    public Value interpret(AST code, Map<String, Value> symTable) {
        return interpret(code, symTable, null);
    }
    
    /**
     * Interprets code with the given symbol table.
     * 
     * Note, that listener will not receive error events, 
     * because the first error triggers exception.
     * 
     * @param code
     * @param symTable
     * @param listener
     * 
     * @throws ASTInterpreterException
     * @throws ASTInterruptedInterpreterException
     * 
     * @return value of the code (void value if code doesn't return value)
     */
    public Value interpret(AST code, Map<String, Value> symTable, InterpreterListener listener) {
        return new InterpretVisitor(listener, symTable).eval(code);
    }
    
    private static InterpreterError toInterpretError(ParserError parseError) {
        return new InterpreterError(
                parseError.message, 
                parseError.offendingStartOffset,
                parseError.offendingStartLine, 
                parseError.offendingStartColumn, 
                parseError.offendingLength
        );
    }
    
    private static final class InterpretVisitor implements ASTVisitor<Value> {
        
        private final InterpreterListener listener;
        
        private final Map<String, Value> symTab;

        public InterpretVisitor(InterpreterListener listener, Map<String, Value> symTab) {
            this.listener = listener;
            this.symTab = symTab;
        }

        @Override
        public Value visit(AST ast) {
            if (Thread.interrupted()) {
                throw new ASTInterruptedInterpreterException();
            }
            return ASTVisitor.super.visit(ast);
        }

        public Value eval(AST expr) {
            return visit(expr);
        }
        
        public <T extends Value> T eval(Class<T> cls, AST expr) {
            Value val = eval(expr);
            if (!cls.isAssignableFrom(val.getClass())) {
                InterpreterError error = new InterpreterError(
                        "mismatched types: " 
                            + "expected \"" + getValueType(cls) + "\"," 
                            + " but found \"" + getValueType(val.getClass()) + "\"", 
                        expr.getStart().getOffset(), 
                        expr.getStart().getLine(),
                        expr.getStart().getColumn(), 
                        expr.getStop().getOffset() - expr.getStart().getOffset() + 1
                );
                throw new ASTInterpreterException(error);
            }
            return (T) val;
        }

        @Override
        public Value visitUnaryOperator(UnaryOperator op) {
            NumberValue val = eval(NumberValue.class, op.getExpr());
            if (op.isMinus()) {
                if (isIntValue(val)) {
                    return new IntValueImpl(-((IntValue) val).value());
                } else {
                    assert isFloatingValue(val) : "Unexpected value type " + val;
                    return new FloatingValueImpl(-((FloatingValue) val).value());
                }
            }
            return val;
        }

        @Override
        public Value visitBinaryOperator(BinaryOperator op) {
            NumberValue lhs = eval(NumberValue.class, op.getLHS());
            NumberValue rhs = eval(NumberValue.class, op.getRHS());
            double left = lhs.asDouble();
            double right = rhs.asDouble();
            double res;
            switch (op.getOperation()) {
                case PLUS:
                    res = left + right;
                    break;
                case MINUS:
                    res = left - right;
                    break;
                case MULTIPLY:
                    res = left * right;
                    break;
                case DIVIDE:
                    res = left / right;
                    break;
                case POWER:
                    res = Math.pow(left, right);
                    break;
                default:
                    throw new AssertionError("Unexpected op kind: " + op.getOperation());
            }
            if (isIntValue(lhs) && isIntValue(rhs)) {
                return new IntValueImpl(Double.valueOf(res).intValue());
            }
            return new FloatingValueImpl(res);
        }

        @Override
        public Value visitMapOperator(MapOperator op) {
            SequenceValue seq = eval(SequenceValue.class, op.getSequence());
            return new MapSequenceValue(seq, op.getLambda());
        }

        @Override
        public Value visitReduceOperator(ReduceOperator op) {
            SequenceValue seq = eval(SequenceValue.class, op.getSequence());
            Value neutral = eval(op.getNeutralValue());
            Value reduced = seq.stream().parallel().reduce(neutral, (left, right)-> {
                Map<String, Value> lambdaSymTab = new HashMap(2);
                lambdaSymTab.put(op.getLambda().getParams().get(0), left);
                lambdaSymTab.put(op.getLambda().getParams().get(1), right);
                return new ASTInterpreter().interpret(
                        op.getLambda().getBody(), 
                        lambdaSymTab
                );
            });
            return reduced;
        }

        @Override
        public Value visitIntegerLiteral(IntegerLiteral literal) {
            return new IntValueImpl(literal.getValue());
        }

        @Override
        public Value visitFloatingLiteral(FloatingLiteral literal) {
            return new FloatingValueImpl(literal.getValue());
        }

        @Override
        public Value visitStringLiteral(StringLiteral literal) {
            return new StringValueImpl(literal.getString());
        }

        @Override
        public Value visitSequenceExpr(SequenceExpr expr) {
            IntValue left = eval(IntValue.class, expr.getLHS());
            IntValue right = eval(IntValue.class, expr.getRHS());
            if (left.value() > right.value()) {
                return VoidValueImpl.INSTANCE; // not defined
            }
            return new IntSequenceValue(left, right);
        }

        @Override
        public Value visitRefExpr(RefExpr expr) {
            Value val = symTab.get(expr.getName());
            if (val == null) {
                InterpreterError error = new InterpreterError(
                        "unresolved variable: \"" + expr.getName() + "\"", 
                        expr.getStart().getOffset(), 
                        expr.getStart().getLine(),
                        expr.getStart().getColumn(), 
                        expr.getStop().getOffset() - expr.getStart().getOffset() + 1
                );
                throw new ASTInterpreterException(error);
            }
            return val;
        }

        @Override
        public Value visitLambdaExpr(LambdaExpr expr) {
            assert false : "Why interpreting lambda declaration?";
            return VoidValueImpl.INSTANCE;
        }

        @Override
        public Value visitVarDeclStmt(VarDeclStmt stmt) {
            if (symTab.containsKey(stmt.getName())) {
                InterpreterError error = new InterpreterError(
                        "redeclaration of variable " + "\"" + stmt.getName() + "\"", 
                        stmt.getStart().getOffset(), 
                        stmt.getStart().getLine(),
                        stmt.getStart().getColumn(), 
                        stmt.getStop().getOffset() - stmt.getStart().getOffset() + 1
                );
                throw new ASTInterpreterException(error);
            }
            Value varValue = eval(stmt.getInitializer());
            symTab.put(stmt.getName(), varValue);
            return VoidValueImpl.INSTANCE;
        }

        @Override
        public Value visitOutStmt(OutStmt stmt) {
            if (listener != null) {
                listener.onOut(String.valueOf(eval(stmt.getExpression())));
            }
            return VoidValueImpl.INSTANCE;
        }

        @Override
        public Value visitPrintStmt(PrintStmt stmt) {
            if (listener != null) {
                String literal = stmt.getMessage().getString();
                assert literal.length() > 1; // "" = 2
                listener.onOut(literal.substring(1, literal.length() - 1));
            }
            return VoidValueImpl.INSTANCE;
        }

        @Override
        public Value visitProgramStmt(ProgramStmt stmt) {
            for (Stmt child : stmt.getStatements()) {
                visit(child);
            }
            return VoidValueImpl.INSTANCE;
        }
        
        private static boolean isIntValue(Value val) {
            return val instanceof IntValue;
        }

        private static boolean isFloatingValue(Value val) {
            return val instanceof FloatingValue;
        }
        
        private static String getValueType(Class<? extends Value> cls) {
            if (IntValue.class.isAssignableFrom(cls)) {
                return "Integer";
            } else if (FloatingValue.class.isAssignableFrom(cls)) {
                return "Float";
            } else if (StringValue.class.isAssignableFrom(cls)) {
                return "String";
            } else if (SequenceValue.class.isAssignableFrom(cls)) {
                return "Sequence";
            } else if (NumberValue.class.isAssignableFrom(cls)) {
                return "Number";
            } else if (VoidValue.class.isAssignableFrom(cls)) {
                return "Void";
            }
            return "Unexpected value type!";
        }
    }
}
