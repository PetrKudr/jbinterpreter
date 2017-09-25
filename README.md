Interpreter for the following language:

* `expr ::= expr op expr | atom | + atom | - atom`
* `atom ::= (expr) | identifier | { expr, expr } | number | map(expr, identifier -> expr) | reduce(expr, expr, identifier identifier -> expr)`
* `op ::= + | - | * | / | ^`
* `stmt ::= var identifier = expr | out expr | print "string"`
* `program ::= stmt | program stmt`

**Interpreter requirements**
* [x] Interpreter should report parsing errors and mismatched types errors (adding number to a sequence, applying map/reduce to a number, etc).
* [x] Support of calculations on long sequences (millions of elements)
* [x] Map/reduce should be executed in parallel using rational execution strategy

**UI requirements**
* [x] Program should be interpreted on the fly
* [x] Time-consuming calculations should not block the UI and should be cancellable
* [x] Errors from the interpreter should be shown in the editor
