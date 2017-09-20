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

stmt: var_stmt
    | out_stmt
    | print_stmt;

var_stmt: LITERAL_VAR IDENTIFIER ASSIGN additive_expr;

out_stmt: LITERAL_OUT additive_expr;

print_stmt: LITERAL_PRINT STRING;

additive_expr: multiplicative_expr ((PLUS | MINUS) multiplicative_expr)*;

multiplicative_expr: power_expr ((STAR | DIVIDE) power_expr)*;

power_expr: unary_expr (POWER unary_expr)*;

unary_expr: (PLUS | MINUS)? atom;

atom: LPAREN additive_expr RPAREN 
     | sequence
     | number
     | map_operator
     | reduce_operator
     | IDENTIFIER;

map_operator: LITERAL_MAP LPAREN additive_expr COMMA map_lambda RPAREN;
              
map_lambda: IDENTIFIER ARROW additive_expr;

reduce_operator: LITERAL_REDUCE LPAREN additive_expr COMMA additive_expr COMMA reduce_lambda RPAREN;
              
reduce_lambda: IDENTIFIER IDENTIFIER ARROW additive_expr;       

number: INTEGER_NUMBER | DOUBLE_NUMBER;

sequence: LCURLY additive_expr COMMA additive_expr RCURLY;
