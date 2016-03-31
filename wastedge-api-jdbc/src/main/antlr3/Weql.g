grammar Weql;

options {
    backtrack = true;
    memoize = true;
}

@header {
    package com.wastedge.api.jdbc.weql;

    import com.wastedge.api.jdbc.weql.syntax.*;
}

@lexer::header {
    package com.wastedge.api.jdbc.weql;

    import com.wastedge.api.jdbc.weql.syntax.*;
}

////////////////////////////////////////////////////////////////////////////////
// PARSER                                                                     //
////////////////////////////////////////////////////////////////////////////////

//
// Identifiers
//

identifier returns [IdentifierSyntax value]
@init {
    Token start = input.LT(1);
}
    :
        i=IDENTIFIER
        { value = new IdentifierSyntax(parseIdentifier(i.getText()), span(start, input.LT(-1))); }
    ;

qualified_identifier returns [NameSyntax value]
@init {
    Token start = input.LT(1);
}
    :
        i=identifier
        { value = i; }
        (
            DOT i=identifier
            { value = new QualifiedNameSyntax(value, i, span(start, input.LT(-1))); }
        )*
    ;

entity_identifier returns [NameSyntax value]
@init {
    Token start = input.LT(1);
}
    :
        i=identifier
        { value = i; }
        (
            SLASH i=identifier
            { value = new EntityNameSyntax(value, i, span(start, input.LT(-1))); }
        )*
    ;

//
// Literals
//

string_literal returns [LiteralSyntax value]
    : s=STRING { value = new LiteralSyntax(s.getText(), LiteralType.STRING, span(input.LT(-1))); }
    ;

number_literal returns [LiteralSyntax value]
options { k=1; }
    : r=REAL { value = new LiteralSyntax(r.getText(), LiteralType.REAL, span(input.LT(-1))); }
    ;

//
// Expressions
//

parenthesized_value_expression returns [ParenValueSyntax value]
options { k=1; }
@init {
    Token start = input.LT(1);
}
    :
        OP_PAREN_LEFT ve=value_expression OP_PAREN_RIGHT
        { value = new ParenValueSyntax(ve, span(start, input.LT(-1))); }
    ;

value_specification returns [ValueSyntax value]
options { k=1; }
    : e1=number_literal { value = e1; }
    | e2=string_literal { value = e2; }
    | e3=qualified_identifier { value = e3; }
    | e4=dynamic_parameter_specification { value = e4; }
    | NULL { value = new LiteralSyntax(null, LiteralType.NULL, span(input.LT(-1), input.LT(-1))); }
    | TRUE { value = new LiteralSyntax("TRUE", LiteralType.BOOLEAN, span(input.LT(-1), input.LT(-1))); }
    | FALSE { value = new LiteralSyntax("FALSE", LiteralType.BOOLEAN, span(input.LT(-1), input.LT(-1))); }
    ;

target_specification returns [ValueSyntax value]
options { k=1; }
    : e1=qualified_identifier { value = e1; }
    | e2=dynamic_parameter_specification { value = e2; }
    ;

// Dynamic parameter

dynamic_parameter_specification returns [DynamicParameterSyntax value]
    :
        pi=PARAM_IDENTIFIER
        { value = new DynamicParameterSyntax(pi.getText(), span(input.LT(-1))); }
    ;

// Expressions

value_expression returns [ValueSyntax value]
options { k=1; }
    : e1=common_value_expression { value = e1; }
    | e2=row_value_expression { value = e2; }
    ;

common_value_expression returns [ValueSyntax value]
options { k=1; }
    : nve=value_expression_primary { value = nve; }
    ;

boolean_value_expression returns [PredicateSyntax value]
options { k=1; }
    : loe=logical_or_expression { value = loe; }
    ;

logical_or_expression returns [PredicateSyntax value]
options { k=1; }
@init {
    Token start = input.LT(1);
}
    :
        lae=logical_and_expression
        { value = lae; }
        (
            OR
            lae=logical_and_expression
            { value = new BinaryPredicateSyntax(BinaryPredicateOperator.OR, value, lae, span(start, input.LT(-1))); }
        )*
    ;

logical_and_expression returns [PredicateSyntax value]
options { k=1; }
@init {
    Token start = input.LT(1);
}
    :
        lne=logical_not_expression
        { value = lne; }
        (
            AND
            lne=logical_not_expression
            { value = new BinaryPredicateSyntax(BinaryPredicateOperator.AND, value, lne, span(start, input.LT(-1))); }
        )*
    ;

logical_not_expression returns [PredicateSyntax value]
options { k=1; }
@init {
    Token start = input.LT(1);
    boolean hadNot = false;
}
    :
        (
            NOT
            { hadNot = true; }
        )?
        lp=logical_predicate
        {
            if (hadNot) {
                value = new UnaryPredicateSyntax(UnaryPredicateOperator.NOT, lp, span(start, input.LT(-1)));
            } else {
                value = lp;
            }
        }
    ;

logical_predicate returns [PredicateSyntax value]
options { k=1; }
    : e2=predicate { value = e2; }
    | e3=parenthesized_boolean_value_expression { value = e3; }
    ;

parenthesized_boolean_value_expression returns [PredicateSyntax value]
options { k=1; }
@init {
    Token start = input.LT(1);
}
    :
        OP_PAREN_LEFT bev=boolean_value_expression OP_PAREN_RIGHT
        { value = new ParenPredicateSyntax(bev, span(start, input.LT(-1))); }
    ;

value_expression_primary returns [ValueSyntax value]
options { k=1; }
    : e1=parenthesized_value_expression { value = e1; }
    | e2=nonparenthesized_value_expression_primary { value = e2; }
    ;

nonparenthesized_value_expression_primary returns [ValueSyntax value]
options { k=1; }
    : ( identifier OP_PAREN_LEFT )=> e2=routine_invocation { value = e2; }
    | e3=qualified_identifier { value = e3; }
    | e5=value_specification { value = e5; }
    ;

// Query expressions

row_value_expression returns [ValueSyntax value]
options { k=1; }
    : e1=nonparenthesized_value_expression_primary { value = e1; }
    ;

row_value_predicand returns [ValueSyntax value]
options { k=1; }
    : e1=row_value_constructor_predicand { value = e1; }
    | e2=row_value_expression { value = e2; }
    ;

row_value_constructor_predicand returns [ValueSyntax value]
options { k=1; }
    : e1=common_value_expression { value = e1; }
    ;

// Entry point; query with an order by clause

direct_select_statement_multiple_rows returns [QuerySyntax value]
options { k=1; }
@init {
    Token start = input.LT(1);
}
    :
        fc=from_clause
        ec=expand_clause?
        wc=where_clause?
        obc=order_by_clause?
        lc=limit_clause?
        { value = new QuerySyntax(fc, ec, wc, obc, lc, span(start, input.LT(-1))); }
        EOF
    ;

// From

from_clause returns [FromClauseSyntax value]
options { k=1; }
@init {
    Token start = input.LT(1);
}
    :
        FROM qi=entity_identifier
        { value = new FromClauseSyntax(qi, span(start, input.LT(-1))); }
    ;

// Expand

expand_clause returns [ExpandClauseSyntax value]
options { k=1; }
@init {
    Token start = input.LT(1);
}
	:
		EXPAND
		esl=expand_specification_list
		{ value = new ExpandClauseSyntax(esl, span(start, input.LT(-1))); }
	;
		
expand_specification_list returns [List<NameSyntax> value]
@init {
    value = new ArrayList<>();
}
    :
        qi=qualified_identifier
        { value.add(qi); }
        (
            COMMA qi=qualified_identifier
            { value.add(qi); }
        )*
    ;

// Where

where_clause returns [PredicateSyntax value]
    :
        WHERE sc=search_condition
        { value = sc; }
    ;

// Order by

order_by_clause returns [OrderByClauseSyntax value]
@init {
    Token start = input.LT(1);
}
    :
        ORDER BY spl=sort_specification_list
        { value = new OrderByClauseSyntax(spl, span(start, input.LT(-1))); }
    ;

sort_specification_list returns [List<OrderByElementSyntax> value]
@init {
    value = new ArrayList<>();
}
    :
        ss=sort_specification
        { value.add(ss); }
        (
            COMMA ss=sort_specification
            { value.add(ss); }
        )*
    ;

sort_specification returns [OrderByElementSyntax value]
@init {
    Token start = input.LT(1);
}
    :
        i=identifier os=ordering_specification?
        { value = new OrderByElementSyntax(i, os, span(start, input.LT(-1))); }
    ;

ordering_specification returns [OrderType value]
    : ASC { value = OrderType.ASCENDING; }
    | DESC { value = OrderType.DESCENDING; }
    ;

// Limit

limit_clause returns [LimitClauseSyntax value]
options { k=1; }
@init {
    Token start = input.LT(1);
}
    :
        LIMIT
        (
            offset=number_literal
            COMMA
        )?
        count=number_literal
        { value = new LimitClauseSyntax(offset, count, span(start, input.LT(-1))); }
    ;

// Predicates

predicate returns [PredicateSyntax value]
options { k=1; }
    : e2=comparison_predicate { value = e2; }
    | e4=in_predicate { value = e4; }
    | e5=like_predicate { value = e5; }
    ;

// Comparison predicate

comparison_predicate returns [PredicateSyntax value]
options { k=1; }
@init {
    Token start = input.LT(1);
}
    :
        rvpl=row_value_predicand co=comp_op rvpr=row_value_predicand
        { value = new BinaryComparisonSyntax(co, rvpl, rvpr, span(start, input.LT(-1))); }
    ;

comp_op returns [BinaryComparisonOperator value]
    : OP_EQ { value = BinaryComparisonOperator.EQUALS; }
    | OP_NE { value = BinaryComparisonOperator.NOT_EQUALS; }
    | OP_LT { value = BinaryComparisonOperator.LESS_THAN; }
    | OP_GT { value = BinaryComparisonOperator.GREATER_THAN; }
    | OP_LE { value = BinaryComparisonOperator.LESS_THAN_OR_EQUALS; }
    | OP_GE { value = BinaryComparisonOperator.GREATER_THAN_OR_EQUALS; }
    ;

// In predicate

in_predicate returns [PredicateSyntax value]
@init {
    Token start = input.LT(1);
    BinaryComparisonOperator op = null;
}
    :
        rvp=row_value_predicand
        { op = BinaryComparisonOperator.IN; }
        (
            NOT
            { op = BinaryComparisonOperator.NOT_IN; }
        )?
        IN
        ipv=in_value_list
        { value = new BinaryComparisonSyntax(op, rvp, ipv, span(start, input.LT(-1))); }
    ;

in_value_list returns [ValueListSyntax value]
@init {
    Token start = input.LT(1);
    List<ValueSyntax> list = new ArrayList<>();
}
    :
        OP_PAREN_LEFT
        (
            rve=row_value_expression
            { list.add(rve); }
            (
                COMMA rve=row_value_expression
                { list.add(rve); }
            )*
        )?
        OP_PAREN_RIGHT
        { value = new ValueListSyntax(list, span(start, input.LT(-1))); }
    ;

// Like predicate

like_predicate returns [PredicateSyntax value]
@init {
    Token start = input.LT(1);
    BinaryComparisonOperator op = null;
}
    :
        rvp=row_value_predicand
        { op = BinaryComparisonOperator.LIKE; }
        (
            NOT
            { op = BinaryComparisonOperator.NOT_LIKE; }
        )?
        le=like_expression
        { value = new BinaryComparisonSyntax(op, rvp, le, span(start, input.LT(-1))); }
    ;

like_expression returns [LikeSyntax value]
options { k=1; }
@init {
    Token start = input.LT(1);
}
    :
        LIKE sve=string_literal
        { value = new LikeSyntax(sve, span(start, input.LT(-1))); }
    ;

// Search condition

search_condition returns [PredicateSyntax value]
    : e1=boolean_value_expression { value = e1; }
    ;

// Invocation

routine_invocation returns [ValueSyntax value]
options { k=1; }
@init {
    Token start = input.LT(1);
}
    :
        i=identifier sal=sql_argument_list
        { value = new InvocationSyntax(i, sal, span(start, input.LT(-1))); }
    ;

sql_argument_list returns [List<ValueSyntax> value]
@init {
    value = new ArrayList<>();
}
    :
        OP_PAREN_LEFT
        (
            sa=sql_argument
            { value.add(sa); }
            (
                COMMA sa=sql_argument
                { value.add(sa); }
            )*
        )?
        OP_PAREN_RIGHT
    ;

sql_argument returns [ValueSyntax value]
    : e1=value_expression { value = e1; }
    | e2=target_specification { value = e2; }
    ;

////////////////////////////////////////////////////////////////////////////////
// LEXER                                                                      //
////////////////////////////////////////////////////////////////////////////////

AND : 'AND' ;
ASC : 'ASC' ;
BY : 'BY' ;
DESC : 'DESC' ;
EXPAND : 'EXPAND';
FALSE : 'FALSE' ;
FROM : 'FROM' ;
IN : 'IN' ;
LIKE : 'LIKE' ;
LIMIT : 'LIMIT' ;
NOT : 'NOT' ;
NULL : 'NULL' ;
OR : 'OR' ;
ORDER : 'ORDER' ;
SELECT : 'SELECT' ;
TRUE : 'TRUE' ;
WHERE : 'WHERE' ;

OP_NE : '!=' ;
OP_GE : '>=' ;
OP_LE : '<=' ;
OP_PAREN_LEFT : '(' ;
OP_PAREN_RIGHT : ')' ;
OP_EQ : '=' ;
OP_LT : '<' ;
OP_GT : '>' ;

COMMA : ',' ;
DOT : '.' ;
SLASH : '/' ;

fragment
Digit
    : '0'..'9'
    ;

fragment
Letter
    : 'a'..'z' | 'A'..'Z'
    ;

fragment
Sign
	: '+'
	| '-'
	;

REAL
    :
    	Sign? Digit+ ( '.' Digit* )?
    |
    	Sign? '.' Digit+
    ;

STRING
    : '\'' ( '\'\'' | ~( '\'' ) )* '\''
    ;

IDENTIFIER
    :
        ( '$' | Letter | '_' )
        ( '$' | Letter | Digit | '_' )*
    ;

PARAM_IDENTIFIER
    :
        ':' ( Letter | Digit | '_' )+
    ;

Space
    :
        ( ' ' | '\r' | '\t' | '\u000C' | '\n' )
        { skip(); }
    ;
