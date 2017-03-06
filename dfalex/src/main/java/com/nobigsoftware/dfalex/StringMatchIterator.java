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

import java.util.Iterator;

/**
 * An {@link Iterator} that provides access to the pattern matches in a string
 * <P>
 * {@link StringSearcher#searchString(String)} produces these.
 */
public interface StringMatchIterator<MATCHRESULT> extends Iterator<MATCHRESULT>
{
    /**
     * Get the position of the start of the last match in the string.
     * 
     * @return the index of the first character in the last match
     * @throws IllegalStateException unless called after a valid call to {{@link #next()}
     */
    int matchStartPosition();
    
    /**
     * Get the position of the end of the last match in the string.
     * 
     * @return the index after the last character in the last match
     * @throws IllegalStateException unless called after a valid call to {{@link #next()}
     */
    int matchEndPosition();
    
    /**
     * Get the string value of the last match
     * <P>
     * Note that a new string is allocated by the first call to this method for each match.
     * 
     * @return the source portion of the source string corresponding to the last match
     * @throws IllegalStateException unless called after a valid call to {{@link #next()}
     */
    String matchValue();
    
    /**
     * Get the result of the last match.
     * @return the MATCHRESULT returned by the last call to {@link #next()}
     * @throws IllegalStateException unless called after a valid call to {{@link #next()}
     */
    MATCHRESULT matchResult();
    
    /**
     * rewind (or jump forward) to a given position in the source string
     * <P>
     * The next match returned will be the one (if any) that starts at a position &gt;= pos
     * <P>
     * IMPORTANT:  If this method returns true, you must call {@link #next()} to get the result
     * of the next match.  Until then calls to the the match accessor methods will continue to
     * return information from the previous call to {@link #next()}.
     *  
     * @param pos  new position in the source string to search from
     * @return  true if there is a match after the given position.  The same value will be returned from {{@link #hasNext()}
     */
    boolean reposition(int pos);
}

