package com.nobigsoftware.dfalex;

import static com.nobigsoftware.dfalex.Pattern.*;

import com.nobigsoftware.dfalex.CharRange;
import com.nobigsoftware.dfalex.Pattern;

public enum JavaToken {
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Ported from JFlex 1.6.1 Java Example                                    *
     * Copyright (C) 1998-2015  Gerwin Klein <lsf@jflex.de>                    *
     * All rights reserved.                                                    *
     *                                                                         *
     * License: BSD                                                            *
     *                                                                         *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    
    /* keywords */
    ABSTRACT("abstract"),
    BOOLEAN("boolean"),
    BREAK("break"),
    BYTE("byte"),
    CASE("case"),
    CATCH("catch"),
    CHAR("char"),
    CLASS("class"),
    CONST("const"),
    CONTINUE("continue"),
    DO("do"),
    DOUBLE("double"),
    ELSE("else"),
    EXTENDS("extends"),
    FINAL("final"),
    FINALLY("finally"),
    FLOAT("float"),
    FOR("for"),
    DEFAULT("default"),
    IMPLEMENTS("implements"),
    IMPORT("import"),
    INSTANCEOF("instanceof"),
    INT("int"),
    INTERFACE("interface"),
    LONG("long"),
    NATIVE("native"),
    NEW("new"),
    GOTO("goto"),
    IF("if"),
    PUBLIC("public"),
    SHORT("short"),
    SUPER("super"),
    SWITCH("switch"),
    SYNCHRONIZED("synchronized"),
    PACKAGE("package"),
    PRIVATE("private"),
    PROTECTED("protected"),
    TRANSIENT("transient"),
    RETURN("return"),
    VOID("void"),
    STATIC("static"),
    WHILE("while"),
    THIS("this"),
    THROW("throw"),
    THROWS("throws"),
    TRY("try"),
    VOLATILE("volatile"),
    STRICTFP("strictfp"),
    
    /* literals */
    NULL("null"),
    TRUE("true"),
    FALSE("false"),
    
    /* separators */
    LPAREN("("),
    RPAREN(")"),
    LBRACE("{"),
    RBRACE("}"),
    LBRACK("["),
    RBRACK("]"),
    SEMICOLON(";"),
    COMMA(","),
    DOT("."),
    
    /* operators */
    EQ("="),
    GT(">"),
    LT("<"),
    NOT("!"),
    COMP("~"),
    QUESTION("?"),
    COLON(":"),
    EQEQ("=="),
    LTEQ("<="),
    GTEQ(">="),
    NOTEQ("!="),
    ANDAND("&&"),
    OROR("||"),
    PLUSPLUS("++"),
    MINUSMINUS("--"),
    PLUS("+"),
    MINUS("-"),
    MULT("*"),
    DIV("/"),
    AND("&"),
    OR("|"),
    XOR("^"),
    MOD("%"),
    LSHIFT("<<"),
    RSHIFT(">>"),
    URSHIFT(">>>"),
    PLUSEQ("+="),
    MINUSEQ("-="),
    MULTEQ("*="),
    DIVEQ("/="),
    ANDEQ("&="),
    OREQ("|="),
    XOREQ("^="),
    MODEQ("%="),
    LSHIFTEQ("<<="),
    RSHIFTEQ(">>="),
    URSHIFTEQ(">>>="),
    
    STRING_LITERAL(
        match("\"")
        .thenMaybeRepeat(anyOf(
            CharRange.builder().addChars("\r\n\"\\").invert().build(), //literal string char
            SubPatterns.STRING_ESCAPE
        ))
        .then("\"")
    ),
    
    CHARACTER_LITERAL(
        match("\'")
        .then(anyOf(
            CharRange.builder().addChars("\r\n\'\\").invert().build(), //literal char
            SubPatterns.STRING_ESCAPE
        ))
        .then("\'")
    ),
    
    INTEGER_LITERAL(regex("[+\\-]?(0|[1-9][0-9]*|0[0-7]+|0[xX][0-9a-fA-F]+)")),
        
    LONG_LITERAL(
        INTEGER_LITERAL.m_pattern.then(anyCharIn("lL"))
    ),
            
    DOUBLE_LITERAL(
        maybe(anyCharIn("+-")).then(anyOf(
             SubPatterns.DIGITS_WITH_DECIMAL.thenMaybe(SubPatterns.EXPONENT),
             repeat(CharRange.DIGITS).then(SubPatterns.EXPONENT)
        ))
        .thenMaybe(anyCharIn("dD"))
    ),
    FLOAT_LITERAL(
        anyCharIn("+-").then(anyOf(
             SubPatterns.DIGITS_WITH_DECIMAL.thenMaybe(SubPatterns.EXPONENT),
             repeat(CharRange.DIGITS).then(SubPatterns.EXPONENT)
        ))
        .then(anyCharIn("fF"))
    );

    final Pattern m_pattern;

    /**
     * Create a new Tokens.
     * @param pattern
     */
    private JavaToken(Pattern pattern)
    {
        m_pattern = pattern;
    }
    
    private JavaToken(String exact)
    {
        m_pattern = match(exact);
    }
    
    private static class SubPatterns
    {
        static final Matchable STRING_ESCAPE = match("\\").then(anyOf( //escapes
            anyCharIn("btnfr\"\'\\"), //single char escapes
            regex("[0-3]?[0-7]?[0-7]") //octal escape
        ));
                    static final Pattern DIGITS_WITH_DECIMAL = anyOf(
            repeat(CharRange.DIGITS).then(".").thenMaybeRepeat(CharRange.DIGITS),
            match(".").thenRepeat(CharRange.DIGITS)
        );
        static final Pattern EXPONENT = anyCharIn("eE").then(anyCharIn("+-")).thenRepeat(CharRange.DIGITS);
    }
}
