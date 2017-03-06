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
 * Implementation of a Dfa State.
 * <P>
 * This can either be a "placeholder" state that delegates to another DFA state, or
 * a DFA state in final form.  As the last step in DFA construction, 
 */
abstract class DfaStateImpl<MATCH> extends DfaState<MATCH>
{
	/**
	 * Replace any internal placeholder references with references to
	 * their delegates.
	 * <P>
	 * Every reference to a state X is replaces with x.resolvePlaceholder();
	 */
	abstract void fixPlaceholderReferences();
	
	/**
	 * If this is a placeholder that delegates to another state,
	 * return that other state.  Otherwise return this.
	 * <P>
	 * This method will follow a chain of placeholders to the end
	 * 
	 * @return the final delegate of this state
	 */
	abstract DfaStateImpl<MATCH> resolvePlaceholder();
}
