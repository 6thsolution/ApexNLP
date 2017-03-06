package com.nobigsoftware.dfalex;


/**
 * A refinement of the {@link Appendable} interface that doesn't throw exceptions
 */
public interface SafeAppendable extends Appendable
{
    @Override
    SafeAppendable append(char c);

    @Override
    SafeAppendable append(CharSequence csq, int start, int end);

    @Override
    SafeAppendable append(CharSequence csq);
}
