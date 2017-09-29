/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import ru.spb.petrk.ast.AST;
import static ru.spb.petrk.ast.ASTKindUtils.*;
import ru.spb.petrk.ast.ASTUtils;
import ru.spb.petrk.ast.ASTUtils.ParserError;
import ru.spb.petrk.ast.ASTVisitor;
import ru.spb.petrk.ast.BinaryOperator;
import ru.spb.petrk.ast.FloatingLiteral;
import ru.spb.petrk.ast.IntegerLiteral;
import ru.spb.petrk.ast.LambdaExpr;
import ru.spb.petrk.ast.MapOperator;
import ru.spb.petrk.ast.OutStmt;
import ru.spb.petrk.ast.ParamExpr;
import ru.spb.petrk.ast.PrintStmt;
import ru.spb.petrk.ast.ProgramStmt;
import ru.spb.petrk.ast.ReduceOperator;
import ru.spb.petrk.ast.RefExpr;
import ru.spb.petrk.ast.SequenceExpr;
import ru.spb.petrk.ast.SequenceType;
import ru.spb.petrk.ast.Stmt;
import ru.spb.petrk.ast.StringLiteral;
import ru.spb.petrk.ast.Type;
import ru.spb.petrk.ast.UnaryOperator;
import ru.spb.petrk.ast.VarDeclStmt;
import ru.spb.petrk.interpreter.Interpreter;
import ru.spb.petrk.interpreter.InterpreterError;
import ru.spb.petrk.interpreter.InterpreterListener;
import ru.spb.petrk.interpreter.evalbased.model.EvalKindUtils;
import static ru.spb.petrk.interpreter.evalbased.model.EvalKindUtils.*;
import ru.spb.petrk.interpreter.evalbased.model.EvalUtils;
import static ru.spb.petrk.interpreter.evalbased.model.EvalUtils.getEvalTypeName;
import ru.spb.petrk.interpreter.evalbased.model.Evaluator;
import ru.spb.petrk.interpreter.evalbased.model.FloatEvaluator;
import ru.spb.petrk.interpreter.evalbased.model.FloatSequenceEvaluator;
import ru.spb.petrk.interpreter.evalbased.model.IntEvaluator;
import ru.spb.petrk.interpreter.evalbased.model.IntSequenceEvaluator;
import ru.spb.petrk.interpreter.evalbased.model.NumberEvaluator;
import ru.spb.petrk.interpreter.evalbased.model.SequenceEvaluator;
import ru.spb.petrk.interpreter.evalbased.model.SequenceSequenceEvaluator;
import ru.spb.petrk.interpreter.evalbased.model.impl.ConstFloatEvaluatorImpl;
import ru.spb.petrk.interpreter.evalbased.model.impl.ConstIntEvaluatorImpl;
import ru.spb.petrk.interpreter.evalbased.model.impl.FloatEvaluatorImpl;
import ru.spb.petrk.interpreter.evalbased.model.impl.IntEvaluatorImpl;
import ru.spb.petrk.interpreter.evalbased.model.impl.IntSequenceEvaluatorImpl;
import ru.spb.petrk.interpreter.evalbased.model.impl.MappedFloatSequenceEvaluatorImpl;
import ru.spb.petrk.interpreter.evalbased.model.impl.MappedIntSequenceEvaluatorImpl;
import ru.spb.petrk.interpreter.evalbased.model.impl.MappedSequenceSequenceEvaluatorImpl;
import ru.spb.petrk.interpreter.evalbased.model.impl.StringEvaluatorImpl;
import ru.spb.petrk.interpreter.evalbased.model.impl.SymTabFloatSequenceEvaluatorImpl;
import ru.spb.petrk.interpreter.evalbased.model.impl.SymTabIntSequenceEvaluatorImpl;
import ru.spb.petrk.interpreter.evalbased.model.impl.SymTabSequenceSequenceEvaluatorImpl;
import ru.spb.petrk.interpreter.evalbased.model.impl.VoidEvaluatorImpl;

/**
 *
 * @author petrk
 */
public final class EvalInterpreter implements Interpreter {
    
    @Override
    public boolean interpret(String input, final PrintStream out, final PrintStream err, AtomicBoolean canceller) {
        return interpret(input, new InterpreterListener() {
            @Override
            public void onOut(String msg) {
                out.print(msg);
            }

            @Override
            public void onError(InterpreterError error) {
                err.println(error.toString());
            }
        }, canceller);
    }
    
    @Override
    public boolean interpret(String input, InterpreterListener listener, AtomicBoolean canceller) {
        List<ASTUtils.ParserError> parseOrLexErrors = new ArrayList<>();
        ProgramStmt program = ASTUtils.parse(input, parseOrLexErrors);
        if (program == null) {
            parseOrLexErrors.stream()
                    .map(parseError -> toInterpretError(parseError))
                    .forEach(interpretError -> listener.onError(interpretError));
            return false;
        }
        try {
            interpret(program, new SymTabImpl(), msg -> listener.onOut(msg), canceller);
            return true;
        } catch (EvalInterruptedInterpreterException ex) {
            // Just return
            return false;
        } catch (EvalInterpreterException ex) {
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
     * @param canceller
     * 
     * @throws EvalInterpreterException
     * @throws EvalInterruptedInterpreterException
     * 
     * @return value of the code (void value if code doesn't return value)
     */
    public Evaluator interpret(AST code, SymTab symTable, AtomicBoolean canceller) {
        return interpret(code, symTable, null, canceller);
    }
    
    /**
     * Interprets code with the given symbol table.
     * 
     * Note, that listener will not receive error events, 
     * because the first error triggers exception.
     * 
     * @param code
     * @param symTab
     * @param listener
     * @param canceller
     * 
     * @throws EvalInterpreterException
     * @throws EvalInterruptedInterpreterException
     * 
     * @return value of the code (void value if code doesn't return value)
     */
    public Evaluator interpret(AST code, SymTab symTab, EvalInterpreterListener listener, AtomicBoolean canceller) {
        return new EvaluatorsBuilder(listener, symTab, canceller).visit(code);
    }
    
    private static InterpreterError toInterpretError(ParserError parseError) {
        return new EvalInterpreterError(
                parseError.message, 
                parseError.offendingStartOffset,
                parseError.offendingStartLine, 
                parseError.offendingStartColumn, 
                parseError.offendingLength
        );
    }
    
    private static final class EvaluatorsBuilder implements ASTVisitor<Evaluator> {
        
        private static final boolean ENABLE_IMPLICIT_WIDENING = false;
        
        private final EvalInterpreterListener listener;
        
        private final SymTab symTab;
        
        private final AtomicBoolean canceller;

        public EvaluatorsBuilder(EvalInterpreterListener listener, SymTab symTab, AtomicBoolean canceller) {
            this.listener = listener;
            this.symTab = symTab;
            this.canceller = canceller;
        }

        @Override
        public Evaluator visit(AST ast) {
            if (canceller.get()) {
                throw new EvalInterruptedInterpreterException();
            }
            return ASTVisitor.super.visit(ast);
        }
        
        public <T extends Evaluator> T visit(Class<T> cls, AST expr) {
            Evaluator val = visit(expr);
            if (!cls.isAssignableFrom(val.getClass())) {
                if (ENABLE_IMPLICIT_WIDENING) {
                    if (EvalKindUtils.isNumberEval(val)) {
                        if (IntEvaluator.class == cls) {
                            return (T) ((NumberEvaluator) val).asInt();
                        } else if (FloatEvaluator.class == cls) {
                            return (T) ((NumberEvaluator) val).asFloat();
                        }
                    }
                }
                throw new EvalInterpreterException(reportMismatchedTypes(cls, val, expr));
            }
            return (T) val;
        }

        @Override
        public Evaluator visitUnaryOperator(UnaryOperator op) {
            NumberEvaluator val = visit(NumberEvaluator.class, op.getExpr());
            if (op.isMinus()) {
                if (isConstIntEval(val)) {
                    return new ConstIntEvaluatorImpl(-((IntEvaluator) val).value(null));
                } else if (isConstFloatEval(val)) {
                    return new ConstFloatEvaluatorImpl(-((FloatEvaluator) val).value(null));
                } else if (isIntEval(val)) {
                    return new IntEvaluatorImpl(canceller, (st) -> -((IntEvaluator) val).value(st));
                } else {
                    assert isFloatEval(val) : "Unexpected value type " + val;
                    return new FloatEvaluatorImpl(canceller, (st) -> -((FloatEvaluator) val).value(st));
                }
            }
            return val;
        }

        @Override
        public Evaluator visitBinaryOperator(BinaryOperator op) {
            NumberEvaluator lhs = visit(NumberEvaluator.class, op.getLHS());
            NumberEvaluator rhs = visit(NumberEvaluator.class, op.getRHS());
            FloatEvaluator left = lhs.asFloat();
            FloatEvaluator right = rhs.asFloat();
            FloatEvaluator res;
            switch (op.getOperation()) {
                case PLUS:
                    if (isConstFloatEval(left) && isConstFloatEval(right)) {
                        res = new ConstFloatEvaluatorImpl(left.value(null) + right.value(null));
                    } else {
                        res = new FloatEvaluatorImpl(canceller, (st) -> left.value(st) + right.value(st));
                    }
                    break;
                case MINUS:
                    if (isConstFloatEval(left) && isConstFloatEval(right)) {
                        res = new ConstFloatEvaluatorImpl(left.value(null) - right.value(null));
                    } else {
                        res = new FloatEvaluatorImpl(canceller, (st) -> left.value(st) - right.value(st));
                    }
                    break;
                case MULTIPLY:
                    if (isConstFloatEval(left) && isConstFloatEval(right)) {
                        res = new ConstFloatEvaluatorImpl(left.value(null) * right.value(null));
                    } else {
                        res = new FloatEvaluatorImpl(canceller, (st) -> left.value(st) * right.value(st));
                    }
                    break;
                case DIVIDE:
                    if (isConstFloatEval(left) && isConstFloatEval(right)) {
                        res = new ConstFloatEvaluatorImpl(left.value(null) / right.value(null));
                    } else {
                        res = new FloatEvaluatorImpl(canceller, (st) -> left.value(st) / right.value(st));
                    }
                    break;
                case POWER:
                    if (isConstFloatEval(left) && isConstFloatEval(right)) {
                        res = new ConstFloatEvaluatorImpl(Math.pow(left.value(null), right.value(null)));
                    } else {
                        res = new FloatEvaluatorImpl(canceller, (st) -> Math.pow(left.value(st), right.value(st)));
                    }
                    break;
                default:
                    throw new AssertionError("Unexpected op kind: " + op.getOperation());
            }
            if (isIntEval(lhs) && isIntEval(rhs)) {
                return res.asInt();
            }
            return res;
        }

        @Override
        public Evaluator visitMapOperator(MapOperator op) {
            final LambdaExpr lambda = op.getLambda();
            Evaluator mapFun = visit(op.getLambda().getBody());
            SequenceEvaluator input = visit(SequenceEvaluator.class, op.getSequence());
            if (isIntEval(mapFun)) {
                IntEvaluator intMapFun = (IntEvaluator) mapFun;
                return mapToInt(lambda, input, intMapFun);
            } else if (isFloatEval(mapFun)) {
                FloatEvaluator floatMapFun = (FloatEvaluator) mapFun;
                return mapToFloat(lambda, input, floatMapFun);
            } else {
                SequenceEvaluator seqMapFun = (SequenceEvaluator) mapFun;
                return mapToSequence(lambda, input, seqMapFun);
            }
        }
        
        private IntSequenceEvaluator mapToInt(
                LambdaExpr lambda, 
                SequenceEvaluator input, 
                IntEvaluator intMapFun
        ) {
            if (isIntSequenceEval(input)) {
                // Case 1.1: {int} map({integers}, int -> int)
                final IntSequenceEvaluator intInput = (IntSequenceEvaluator) input;
                return new MappedIntSequenceEvaluatorImpl<>(canceller, intInput, (in, st) -> in.stream(st).map((val) -> {
                    SymTab lambdaSymTab = SymTab.of(lambda, new ConstIntEvaluatorImpl(val));
                    return intMapFun.value(lambdaSymTab);
                }));
            } else if (isFloatSequenceEval(input)) {
                // Case 1.2: {int} map({doubles}, double -> int)
                final FloatSequenceEvaluator floatInput = (FloatSequenceEvaluator) input;
                return new MappedIntSequenceEvaluatorImpl<>(canceller, floatInput, (in, st) -> in.stream(st).mapToInt((val) -> {
                    SymTab lambdaSymTab = SymTab.of(lambda, new ConstFloatEvaluatorImpl(val));
                    return intMapFun.value(lambdaSymTab);
                }));
            } else {
                // Case 1.3: {int} map({{...}, {...}}, {...} -> int)
                SequenceSequenceEvaluator seqInput = (SequenceSequenceEvaluator) input;
                return new MappedIntSequenceEvaluatorImpl<>(canceller, seqInput, (in, st) -> in.stream(st).mapToInt((val) -> {
                    SymTab lambdaSymTab = SymTab.of(lambda, val);
                    return intMapFun.value(lambdaSymTab);
                }));
            }
        }
        
        private FloatSequenceEvaluator mapToFloat(
                LambdaExpr lambda, 
                SequenceEvaluator input, 
                FloatEvaluator floatMapFun
        ) {
            if (isIntSequenceEval(input)) {
                // Case 2.1: {double} map({integers}, int -> double)
                final IntSequenceEvaluator intInput = (IntSequenceEvaluator) input;
                return new MappedFloatSequenceEvaluatorImpl<>(canceller, intInput, (in, st) -> in.stream(st).mapToDouble((val) -> {
                    SymTab lambdaSymTab = SymTab.of(lambda, new ConstIntEvaluatorImpl(val));
                    return floatMapFun.value(lambdaSymTab);
                }));
            } else if (isFloatSequenceEval(input)) {
                // Case 2.2: {double} map({doubles}, double -> double)
                final FloatSequenceEvaluator floatInput = (FloatSequenceEvaluator) input;
                return new MappedFloatSequenceEvaluatorImpl<>(canceller, floatInput, (in, st) -> in.stream(st).map((val) -> {
                    SymTab lambdaSymTab = SymTab.of(lambda, new ConstFloatEvaluatorImpl(val));
                    return floatMapFun.value(lambdaSymTab);
                }));
            } else {
                // Case 2.3: {double} map({{...}, {...}}, {...} -> double)
                SequenceSequenceEvaluator seqInput = (SequenceSequenceEvaluator) input;
                return new MappedFloatSequenceEvaluatorImpl<>(canceller, seqInput, (in, st) -> in.stream(st).mapToDouble((val) -> {
                    SymTab lambdaSymTab = SymTab.of(lambda, val);
                    return floatMapFun.value(lambdaSymTab);
                }));
            }
        }
        
        private SequenceSequenceEvaluator mapToSequence(
                LambdaExpr lambda, 
                SequenceEvaluator input, 
                SequenceEvaluator seqMapFun
        ) {
            if (isIntSequenceEval(input)) {
                // Case 3.1: {{...},...,{...}} map({integers}, int -> {...})
                final IntSequenceEvaluator intInput = (IntSequenceEvaluator) input;
                return new MappedSequenceSequenceEvaluatorImpl<>(canceller, intInput, (in, st) -> in.stream(st).mapToObj((val) -> {
                    SymTab lambdaSymTab = SymTab.of(lambda, new ConstIntEvaluatorImpl(val));
                    return seqMapFun.bind(lambdaSymTab);
                }));
            } else if (isFloatSequenceEval(input)) {
                // Case 3.2: {{...},...,{...}} map({doubles}, double -> {...})
                final FloatSequenceEvaluator floatInput = (FloatSequenceEvaluator) input;
                return new MappedSequenceSequenceEvaluatorImpl<>(canceller, floatInput, (in, st) -> in.stream(st).mapToObj((val) -> {
                    SymTab lambdaSymTab = SymTab.of(lambda, new ConstFloatEvaluatorImpl(val));
                    return seqMapFun.bind(lambdaSymTab);
                }));
            } else {
                // Case 3.3: {{...},...,{...}} map({{...}, {...}}, {...} -> {...})
                SequenceSequenceEvaluator seqInput = (SequenceSequenceEvaluator) input;
                return new MappedSequenceSequenceEvaluatorImpl<>(canceller, seqInput, (in, st) -> in.stream(st).map((val) -> {
                    SymTab lambdaSymTab = SymTab.of(lambda, val);
                    return seqMapFun.bind(lambdaSymTab);
                }));
            }
        }

        @Override
        public Evaluator visitReduceOperator(ReduceOperator op) {
            final LambdaExpr lambda = op.getLambda();
            SequenceEvaluator input = visit(SequenceEvaluator.class, op.getSequence());
            Evaluator reduce = visit(lambda.getBody());
            if (isIntEval(reduce)) {
                IntEvaluator intReduce = (IntEvaluator) reduce;
                NumberEvaluator neutral = visit(NumberEvaluator.class, op.getNeutralValue());
                return reduceToInt(lambda, input, neutral, intReduce);
            } else if (isFloatEval(reduce)) {
                FloatEvaluator floatReduce = (FloatEvaluator) reduce;
                NumberEvaluator neutral = visit(NumberEvaluator.class, op.getNeutralValue());
                return reduceToFloat(lambda, input, neutral, floatReduce);
            } else if (isSequenceEval(reduce)) {
                SequenceEvaluator seqReduce = (SequenceEvaluator) reduce;
                SequenceEvaluator neutral = visit(SequenceEvaluator.class, op.getNeutralValue());
                return reduceToSequence(lambda, input, neutral, seqReduce);
            }
            throw new AssertionError("Unexpected evaluator: " + EvalUtils.getEvalTypeName(reduce));
        }
        
        private IntEvaluator reduceToInt(
                LambdaExpr lambda,
                SequenceEvaluator input,
                NumberEvaluator neutral,
                IntEvaluator intReduce
        ) {
            if (isIntSequenceEval(input)) {
                // Case 1.1: int reduce({integers}, neutral, int int -> int)
                IntSequenceEvaluator intInput = (IntSequenceEvaluator) input;
                return new IntEvaluatorImpl(canceller, (st) -> 
                        intInput.stream(st).parallel().reduce(
                                neutral.asInt().value(st), 
                                (l, r) -> {
                                    SymTab reduceSymTab = SymTab.of(
                                        lambda, 
                                        new ConstIntEvaluatorImpl(l),
                                        new ConstIntEvaluatorImpl(r)
                                    );
                                    return intReduce.value(reduceSymTab);
                                }
                        )
                );
            } else if (isFloatSequenceEval(input)) {
                // Case 1.2: int reduce({doubles}, neutral, double double -> int)
                FloatSequenceEvaluator floatInput = (FloatSequenceEvaluator) input;
                return new IntEvaluatorImpl(canceller, (st) -> 
                        Double.valueOf(floatInput.stream(st).parallel().reduce(
                                neutral.asFloat().value(st), 
                                (l, r) -> {
                                    SymTab reduceSymTab = SymTab.of(
                                        lambda, 
                                        new ConstFloatEvaluatorImpl(l),
                                        new ConstFloatEvaluatorImpl(r)
                                    );
                                    return intReduce.value(reduceSymTab);
                                }
                        )).intValue()
                );
            } else {
                throw new AssertionError("Unexpected input sequence: " + EvalUtils.getEvalTypeName(input));
            }
        }
        
        private FloatEvaluator reduceToFloat(
                LambdaExpr lambda,
                SequenceEvaluator input,
                NumberEvaluator neutral,
                FloatEvaluator floatReduce
        ) {
            if (isIntSequenceEval(input)) {
                // Case 2.1: double reduce({integers}, neutral, int int -> double)
                IntSequenceEvaluator intInput = (IntSequenceEvaluator) input;
                return new FloatEvaluatorImpl(canceller, (st) -> 
                        intInput.asFloatSequence().stream(st).parallel().reduce(
                                neutral.asFloat().value(st), 
                                (l, r) -> {
                                    SymTab reduceSymTab = SymTab.of(
                                            lambda, 
                                            new ConstFloatEvaluatorImpl(l),
                                            new ConstFloatEvaluatorImpl(r)
                                    );
                                    return floatReduce.value(reduceSymTab);
                                }
                        )
                );
            } else if (isFloatSequenceEval(input)) {
                // Case 2.2: double reduce({doubles}, neutral, double double -> double)
                FloatSequenceEvaluator floatInput = (FloatSequenceEvaluator) input;
                return new FloatEvaluatorImpl(canceller, (st) -> 
                        floatInput.stream(st).parallel().reduce(
                                neutral.asFloat().value(st), 
                                (l, r) -> {
                                    SymTab reduceSymTab = SymTab.of(
                                            lambda, 
                                            new ConstFloatEvaluatorImpl(l),
                                            new ConstFloatEvaluatorImpl(r)
                                    );
                                    return floatReduce.value(reduceSymTab);
                                }
                        )
                );
            } else {
                throw new AssertionError("Unexpected input sequence: " + EvalUtils.getEvalTypeName(input));
            }
        }
        
        private SequenceEvaluator reduceToSequence(
                LambdaExpr lambda,
                SequenceEvaluator input, 
                SequenceEvaluator neutral, 
                SequenceEvaluator reduce
        ) {
            if (isSequenceSequenceEval(input)) {
                SequenceSequenceEvaluator seqInput = (SequenceSequenceEvaluator) input;
                if (EvalKindUtils.isIntSequenceEval(reduce)) {
                    return new MappedIntSequenceEvaluatorImpl<>(canceller, seqInput, (in, st) ->
                            ((IntSequenceEvaluator) in.stream(st).parallel().reduce(neutral, (l, r) -> {
                                SymTab symTab = SymTab.of(lambda, l, r);
                                return reduce.bind(symTab);
                            })).stream(st)
                    );
                } else if (EvalKindUtils.isFloatSequenceEval(reduce)) {
                    return new MappedFloatSequenceEvaluatorImpl<>(canceller, seqInput, (in, st) ->
                            ((FloatSequenceEvaluator) in.stream(st).parallel().reduce(neutral, (l, r) -> {
                                SymTab symTab = SymTab.of(lambda, l, r);
                                return reduce.bind(symTab);
                            })).stream(st)
                    );
                } else if (EvalKindUtils.isSequenceSequenceEval(reduce)) {
                    return new MappedSequenceSequenceEvaluatorImpl<>(canceller, seqInput, (in, st) ->
                            ((SequenceSequenceEvaluator) in.stream(st).parallel().reduce(neutral, (l, r) -> {
                                SymTab symTab = SymTab.of(lambda, l, r);
                                return reduce.bind(symTab);
                            })).stream(st)
                    );
                } else {
                    throw new AssertionError("Unexpected type of output sequence: " + getEvalTypeName(reduce));
                }
            } else {
                throw new AssertionError("Unexpected input sequence: " + EvalUtils.getEvalTypeName(input));
            }
        }

        @Override
        public Evaluator visitIntegerLiteral(IntegerLiteral literal) {
            return new ConstIntEvaluatorImpl(literal.getValue());
        }

        @Override
        public Evaluator visitFloatingLiteral(FloatingLiteral literal) {
            return new ConstFloatEvaluatorImpl(literal.getValue());
        }

        @Override
        public Evaluator visitStringLiteral(StringLiteral literal) {
            return new StringEvaluatorImpl(literal.getString());
        }

        @Override
        public Evaluator visitSequenceExpr(SequenceExpr expr) {
            IntEvaluator left = visit(IntEvaluator.class, expr.getLHS());
            IntEvaluator right = visit(IntEvaluator.class, expr.getRHS());
            return new IntSequenceEvaluatorImpl(canceller, left, right);
        }

        @Override
        public Evaluator visitRefExpr(RefExpr expr) {
            if (isIntegerType(expr.getType())) {
                return new IntEvaluatorImpl(canceller, (st) -> 
                        st.getEval(NumberEvaluator.class, expr).asInt().value(st)
                );
            } else if (isFloatingType(expr.getType())) {
                return new FloatEvaluatorImpl(canceller, (st) -> 
                        st.getEval(NumberEvaluator.class, expr).asFloat().value(st)
                );
            } else if (isSequenceType(expr.getType())) {
                SequenceType seqType = (SequenceType) expr.getType();
                Type elemType = seqType.getElementType();
                if (isIntegerType(elemType)) {
                    return new SymTabIntSequenceEvaluatorImpl(canceller, (st) -> 
                            st.getEval(IntSequenceEvaluator.class, expr)
                    );
                } else if (isFloatingType(elemType)) {
                    return new SymTabFloatSequenceEvaluatorImpl(canceller, (st) -> 
                            st.getEval(FloatSequenceEvaluator.class, expr)
                    );
                } else {
                    return new SymTabSequenceSequenceEvaluatorImpl(canceller, (st) -> 
                            st.getEval(SequenceSequenceEvaluator.class, expr)
                    );
                }
            }
            throw new AssertionError("Unexpected type: " + ASTUtils.getTypeName(expr.getType()));
        }

        @Override
        public Evaluator visitLambdaExpr(LambdaExpr expr) {
            assert false : "Why interpreting lambda declaration?";
            return VoidEvaluatorImpl.INSTANCE;
        }

        @Override
        public Evaluator visitParamExpr(ParamExpr expr) {
            assert false : "Why interpreting lambda param declaration?";
            return VoidEvaluatorImpl.INSTANCE;
        }

        @Override
        public Evaluator visitVarDeclStmt(VarDeclStmt stmt) {
            Evaluator varEval = visit(stmt.getInitializer());
            symTab.putSym(stmt, varEval);
            return VoidEvaluatorImpl.INSTANCE;
        }

        @Override
        public Evaluator visitOutStmt(OutStmt stmt) {
            if (listener != null) {
                Evaluator eval = visit(stmt.getExpression());
                listener.onOut(eval.asString(symTab));  
            }
            return VoidEvaluatorImpl.INSTANCE;
        }

        @Override
        public Evaluator visitPrintStmt(PrintStmt stmt) {
            if (listener != null) {
                String literal = stmt.getMessage().getString();
                assert literal.length() > 1; // "" = 2
                listener.onOut(literal.substring(1, literal.length() - 1));
            }
            return VoidEvaluatorImpl.INSTANCE;
        }

        @Override
        public Evaluator visitProgramStmt(ProgramStmt stmt) {
            for (Stmt child : stmt.getStatements()) {
                visit(child);
            }
            return VoidEvaluatorImpl.INSTANCE;
        }
    }
    
    /*package*/ static InterpreterError reportMismatchedTypes(
            Class<? extends Evaluator> expected, 
            Evaluator eval,
            AST expr
    ) {
        return reportError(
                "mismatched types: " 
                        + "expected \"" + EvalUtils.getEvalTypeName(expected) + "\"," 
                        + " but found \"" + EvalUtils.getEvalTypeName(eval) + "\"", 
                expr
        );
    }
    
    /*package*/ static InterpreterError reportError(String message, AST ast) {
        return new EvalInterpreterError(
                message,
                ast.getStart().getOffset(),
                ast.getStart().getLine(),
                ast.getStart().getColumn(),
                ast.getStop().getOffset() - ast.getStart().getOffset() + 1
        );
    }
}
