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
STRING: '"' ('\\\\' | '\\"' | ~('\r' | '\n' | '"'))* '"';
IDENTIFIER: [a-zA-Z][a-zA-Z0-9_]*;
WHITESPACE: [ \t\r\n]+ -> skip;

fragment SCI_NOTATION: ('E' | 'e') [+-]? [0-9]+;


// ***************** parser rules:

program: (stmt)* EOF;

stmt: LITERAL_VAR IDENTIFIER ASSIGN expr
    | LITERAL_OUT expr
    | LITERAL_OUT STRING;

expr: additive_expr;

additive_expr: multiplicative_expr ((PLUS | MINUS) multiplicative_expr)*;

multiplicative_expr: power_expr ((STAR | DIVIDE) power_expr)*;

power_expr: unary_expr (POWER unary_expr)*;

unary_expr: (PLUS | MINUS)? atom;

atom: LPAREN expr RPAREN 
     | sequence
     | number
     | map_operator
     | reduce_operator
     | IDENTIFIER;

map_operator: LITERAL_MAP LPAREN expr COMMA map_lambda RPAREN;
              
map_lambda: IDENTIFIER ARROW expr;

reduce_operator: LITERAL_REDUCE LPAREN expr COMMA reduce_lambda RPAREN;
              
reduce_lambda: IDENTIFIER IDENTIFIER ARROW expr;       

number: (PLUS | MINUS)? (INTEGER_NUMBER | DOUBLE_NUMBER);

sequence: LCURLY expr COMMA expr RCURLY;
