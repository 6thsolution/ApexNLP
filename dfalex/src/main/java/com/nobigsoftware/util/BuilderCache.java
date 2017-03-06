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

import java.io.Serializable;

/**
 * Implementations of this interface can cache serializable objects that
 * can be used to bypass expensive building operations by providing
 * pre-built objects
 */
public interface BuilderCache
{
    /**
     * Get a cached item.
     * 
     * @param key  The key used to identify the item.  The key uniquely identifies all
     *  of the source information that will go into building the item if this call fails
     *  to retrieve a cached version.  Typically this will be a cryptographic hash of
     *  the serialized form of that information.
     * 
     * @return  the item that was previously cached under the key, or null if no such item
     *  can be retrieved.
     */
    Serializable getCachedItem(String key);
    
    /**
     * This method may be called when an item is built, providing an opportunity to
     * cache it.
     * 
     * @param key   The key that will be used to identify the item in future calls to {@link #getCachedItem(String)}.
     *      Only letters, digits, and underscores are valid in keys, and key length is limited to 32 characters.
     *      The behaviour of this method for invalid keys is undefined.
     *      <P>
     *      Keys that differ only by case may or may not be considered equal by this class.
     * @param item  The item to cache, if desired
     */
    void maybeCacheItem(String key, Serializable item);


}
