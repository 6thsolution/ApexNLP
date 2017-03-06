package com.sixthsolution.apex.nlp.util;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class Pair<F, S> {
    public final F first;
    public final S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "[" + first + "," + second + "]";
    }
}
