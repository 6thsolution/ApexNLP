/*
 * Copyright 2015 Matthew Timmermans
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nobigsoftware.dfalex;

/**
 * A utility class containing common StringReplacements that can be used with
 * {@link SearchAndReplaceBuilder}
 */
public class StringReplacements
{
    /**
     * Replacement that leaves the matching substring unmodified
     */
    public static final StringReplacement IGNORE = (dest, src, startPos, endPos) ->
    {
        dest.append(src, startPos, endPos);
        return 0;
    };
    
    /**
     * Replacement that deletes the matching substring
     */
    public static final StringReplacement DELETE = (dest, src, startPos, endPos) ->
    {
        return 0;
    };
    
    /**
     * Replacement that converts the matching substring to upper case
     */
    public static final StringReplacement TOUPPER = (dest, src, startPos, endPos) ->
    {
        for (int i=startPos; i<endPos; ++i)
        {
            dest.append(Character.toUpperCase(src.charAt(i)));
        }
        return 0;
    };
    
    /**
     * Replacement that converts the matching substring to lower case
     */
    public static final StringReplacement TOLOWER = (dest, src, startPos, endPos) ->
    {
        for (int i=startPos; i<endPos; ++i)
        {
            dest.append(Character.toLowerCase(src.charAt(i)));
        }
        return 0;
    };
    
    /**
     * Replacement that converts the matching substring to a single space (if it does not contain any newlines) or a
     * newline (if it does contain a newline)
     */
    public static final StringReplacement SPACE_OR_NEWLINE = (dest, src, startPos, endPos) ->
    {
        for (int i=startPos; i<endPos; ++i)
        {
            if (src.charAt(i)=='\n')
            {
                dest.append('\n');
                return 0;
            }
        }
        dest.append(' ');
        return 0;
    };
    
    /**
     * Make a replacement that replaces matching substrings with a given string
     * 
     * @param str replacement string
     * @return new StringReplacement
     */
    public static final StringReplacement string(CharSequence str)
    {
        return (dest, src, startPos, endPos) ->
        {
            dest.append(str);
            return 0;
        };
    }

    /**
     * Make a replacement that surrounds matches with a given prefix and suffix, and applies the given replacer
     * to the match itself
     * 
     * @param prefix to put before matches
     * @param replacement for the match itself
     * @param suffix suffix to put after matches
     * @return new StringReplacement
     */
    public static final StringReplacement surround(CharSequence prefix, StringReplacement replacement, CharSequence suffix)
    {
        return (dest, src, startPos, endPos) ->
        {
            dest.append(prefix);
            int ret = replacement.apply(dest, src, startPos, endPos);
            dest.append(suffix);
            return ret;
        };
    }
}
