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
 * BackportFuncs interface that provides the replacement values for strings in a search+replace operation
 * of patterns found in a string.
 */
public interface StringReplacement
{
    /**
     * This will be called for each instance of each pattern found
     * 
     * @param dest  The replacement text for the matching substring should be written here
     * @param src   The string being searched, or the part of the stream being searched that contains the current match
     * @param startPos  the start index of the current match in src
     * @param endPos    the end index of the current match in src
     * @return if this is &gt;0, then it is the position in the source string at which to continue processing after
     *      replacement.  If you set this &lt;= startPos, an IndexOutOfBoundsException will be thrown to
     *      abort the infinite loop that would result.  Almost always return 0.
     */
    int apply(SafeAppendable dest, CharSequence src, int startPos, int endPos);
}

