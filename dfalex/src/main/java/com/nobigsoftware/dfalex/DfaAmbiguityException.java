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

import java.util.ArrayList;
import java.util.List;

/**
 * Exception thrown by default when patterns for multiple results match the same string in a DFA,
 * and no way has been provided to combine result
 */
public class DfaAmbiguityException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	private final List<Object> m_results;
	
	/**
	 * Create a new AmbiguityException.
	 * @param results the multiple results for patters that match the same string
	 */
	public DfaAmbiguityException(Iterable<?> results)
	{
	    this(new _Initializer(null, results));
	}

	/**
	 * Create a new AmbiguityException.
	 * 
	 * @param message	The exception detail message
     * @param results the multiple results for patters that match the same string
	 */
	public DfaAmbiguityException(String message, Iterable<?> results)
	{
        this(new _Initializer(message, results));
	}

	private DfaAmbiguityException(_Initializer inivals)
	{
	    super(inivals.m_message);
	    m_results = inivals.m_results;
	}
	
	/**
	 * Get the set of results that can match the same string
	 * 
	 * @return set of conflicting results
	 */
	public List<Object> getResults()
	{
		return m_results;
	}

	
	private static class _Initializer
	{
	    String m_message;
	    List<Object> m_results;
	    
	    _Initializer(String message, Iterable<?> results)
	    {
	        m_results = new ArrayList<Object>();
	        for (Object obj : results)
	        {
	            m_results.add(obj);
	        }
	        if (message == null)
	        {
	            StringBuilder sb = new StringBuilder();
	            sb.append("The same string can match multiple patterns for: ");
	            String sep="";
	            for (Object result : results)
	            {
	                sb.append(sep).append(result.toString());
	                sep=", ";
	            }
	            message = sb.toString();
	        }
	        m_message = message;
	    }
	}
}
