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
 * A functional interface that can accept transitions
 * <P>
 * This is used with {@link DfaState#enumerateTransitions(DfaTransitionConsumer)}
 */
public interface DfaTransitionConsumer<MATCHRESULT>
{
    /**
     * Accept a DFA transition
     * <P>
     * This call indicates that the current state has a transition to target on
     * every character with code point &gt;=firstChar and &lt;=lastChar
     * @param firstChar First character that triggers this transition
     * @param lastChar Last character that triggers this transition
     * @param target Target state of this transition
     */
    void acceptTransition(char firstChar, char lastChar, DfaState<MATCHRESULT> target);
}
