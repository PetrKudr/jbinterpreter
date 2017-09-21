grammar JetBrainsLanguage;

options {
  language = Java;
}

@lexer::header {
package ru.spb.petrk.antlr4;
}

@parser::header {
package ru.spb.petrk.antlr4;
}


// ***************** lexer rules:

ASSIGN: '=';
PLUS: '+';
MINUS: '-';
STAR: '*';
DIVIDE: '/';
POWER: '^';
COMMA: ',';
ARROW: '->';
LPAREN: '(';
RPAREN: ')';
LCURLY: '{';
RCURLY: '}';
LITERAL_MAP: 'map';
LITERAL_REDUCE: 'reduce';
LITERAL_VAR: 'var';
LITERAL_OUT: 'out';
LITERAL_PRINT: 'print';

DOUBLE_NUMBER: [0-9]+ ('.' [0-9]+ (SCI_NOTATION)? | SCI_NOTATION);
INTEGER_NUMBER: [0-9]+;
STRING: '"' .*? '"';
IDENTIFIER: [a-zA-Z][a-zA-Z0-9_]*;
WHITESPACE: [ \t\r\n]+ -> skip;

fragment SCI_NOTATION: ('E' | 'e') [+-]? [0-9]+;


// ***************** parser rules:

program: (stmt)* EOF;

stmt: varStmt
    | outStmt
    | printStmt;

varStmt: LITERAL_VAR IDENTIFIER ASSIGN additiveExpr;

outStmt: LITERAL_OUT additiveExpr;

printStmt: LITERAL_PRINT STRING;

additiveExpr: multiplicativeExpr ((PLUS | MINUS) multiplicativeExpr)*;

multiplicativeExpr: powerExpr ((STAR | DIVIDE) powerExpr)*;

powerExpr: unaryExpr (POWER unaryExpr)*;

unaryExpr: (PLUS | MINUS)? atom;

atom: LPAREN additiveExpr RPAREN 
     | sequence
     | number
     | mapOperator
     | reduceOperator
     | IDENTIFIER;

mapOperator: LITERAL_MAP LPAREN additiveExpr COMMA mapLambda RPAREN;
              
mapLambda: IDENTIFIER ARROW additiveExpr;

reduceOperator: LITERAL_REDUCE LPAREN additiveExpr COMMA additiveExpr COMMA reduceLambda RPAREN;
              
reduceLambda: IDENTIFIER IDENTIFIER ARROW additiveExpr;       

number: INTEGER_NUMBER | DOUBLE_NUMBER;

sequence: LCURLY additiveExpr COMMA additiveExpr RCURLY;
