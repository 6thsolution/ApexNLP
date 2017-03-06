package com.nobigsoftware.dfalex;

/**
 * For search and replace operations, a functional interface that is called to select replacement text for
 * matches, based on the MATCHRESULT.
 * <P>
 * This is called by a {@link StringSearcher#findAndReplace(String, ReplacementSelector)} to replace instances
 * of patterns found in a string.
 */
public interface ReplacementSelector<MATCHRESULT>
{
    /**
     * This will be called for each instance of each pattern found
     * 
     * @param dest  The replacement text for the matching substring should be written here
     * @param mr    The MATCHRESULT produced by the match
     * @param src   The string being searched, or a part of the stream being searched that contains the current match
     * @param startPos  the start index of the current match in src
     * @param endPos    the end index of the current match in src
     * @return if this is &gt;0, then it is the position in the source string at which to continue processing after
     *      replacement.  If you set this &lt;= startPos, a runtime exception will be thrown to
     *      abort the infinite loop that would result.  Almost always return 0.
     */
    int apply(SafeAppendable dest, MATCHRESULT mr, CharSequence src, int startPos, int endPos);
}