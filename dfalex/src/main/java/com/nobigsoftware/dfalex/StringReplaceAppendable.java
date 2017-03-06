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

import java.util.Arrays;


/**
 * An {@link Appendable} for string replacements that will allocate a new string buffer only when
 * the first difference is written
 */
class StringReplaceAppendable implements SafeAppendable
{
    private final String m_src;
    private char[] m_buf;
    private int m_len;
    
    /**
     * Create a new StringReplaceAppendable.
     * @param src
     */
    public StringReplaceAppendable(String src)
    {
        m_src = src;
    }

    @Override
    public SafeAppendable append(CharSequence csq)
    {
        append(csq,0,csq.length());
        return this;
    }
    
    @Override
    public SafeAppendable append(char c)
    {
        if (m_buf != null)
        {
            if (m_len >= m_buf.length)
            {
                m_buf = Arrays.copyOf(m_buf, m_buf.length*2);
            }
            m_buf[m_len++]=c;
            return this;
        }
        if (m_len < m_src.length() && m_src.charAt(m_len)==c)
        {
            ++m_len;
            return this;
        }
        _allocate(1);
        m_buf[m_len++]=c;
        return this;
    }
    
    @Override
    public SafeAppendable append(CharSequence csq, int start, int end)
    {
        if (start < 0 || end < start)
        {
            throw new IndexOutOfBoundsException();
        }
        if (m_buf == null)
        {
            if (csq == m_src && start == m_len)
            {
                if (end > m_src.length())
                {
                    throw new IndexOutOfBoundsException();
                }
                m_len = end;
                return this;
            }
            for (;; ++start, ++m_len)
            {
                if (start >= end)
                {
                    return this;
                }
                if (m_len >= m_src.length() || m_src.charAt(m_len) != csq.charAt(start))
                {
                    break;
                }
            }
            //new data - need to allocate
            _allocate(end-start);
        }
        else if (m_buf.length - m_len < (end-start))
        {
            m_buf = Arrays.copyOf(m_buf, Math.max(m_buf.length*2, m_len+(end-start)));
        }
        if (csq instanceof String)
        {
            ((String)csq).getChars(start, end, m_buf, m_len);
            m_len += (end-start);
        }
        else
        {
            while (start<end)
            {
                m_buf[m_len++] = csq.charAt(start++);
            }
        }
        return this;
    }
    
    @Override
    public String toString()
    {
        if (m_buf != null)
        {
            return String.copyValueOf(m_buf, 0, m_len);
        }
        if (m_len == m_src.length())
        {
            return m_src;
        }
        return m_src.substring(0, m_len);
    }
    
    private void _allocate(int addlen)
    {
        m_buf = new char[Math.max(m_len + addlen, m_src.length()+16)];
        m_src.getChars(0, m_len, m_buf, 0);
    }
}
