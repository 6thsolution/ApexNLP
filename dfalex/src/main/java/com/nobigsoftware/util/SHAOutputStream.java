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
package com.nobigsoftware.util;

import java.io.IOException;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * An output stream that computes the SHA hash of whatever you write to it
 */
public class SHAOutputStream extends DigestOutputStream
{
    private static NullOutputStream NULL_OUTPUT_STREAM = new NullOutputStream();
    private static char[] DIGITS_36 = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray(); 
    
    public SHAOutputStream()
    {
        super(NULL_OUTPUT_STREAM, _initDigest());
    }
    
    /**
     * @return a base-32 version of the digest, consisting of 32 letters and digits
     */
    public String getBase32Digest()
    {
        StringBuilder sb = new StringBuilder();
        int bits = 0, nbits = 0;
        for (byte b : getMessageDigest().digest())
        {
            bits |= (((int)b)&255)<<nbits;
            nbits+=8;
            while(nbits >= 5)
            {
                sb.append(DIGITS_36[bits&31]);
                bits>>>=5;
                nbits-=5;
            }
        }
        return sb.toString();
    }

    private static class NullOutputStream extends OutputStream
    {
        @Override
        public void close() throws IOException
        {
        }
        @Override
        public void flush() throws IOException
        {
        }
        @Override
        public void write(byte[] arg0, int arg1, int arg2) throws IOException
        {
        }
        @Override
        public void write(byte[] arg0) throws IOException
        {
        }
        @Override
        public void write(int arg0) throws IOException
        {
        }
    }
    private static MessageDigest _initDigest()
    {
        try
        {
            return MessageDigest.getInstance("SHA-1");
        }
        catch(NoSuchAlgorithmException e)
        {
            throw new RuntimeException("JRE is broken - it's supposed to support SHA-1, but does not");
        }
    }
}
