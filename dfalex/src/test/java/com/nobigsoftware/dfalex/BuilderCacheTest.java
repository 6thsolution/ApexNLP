package com.nobigsoftware.dfalex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.nobigsoftware.util.BuilderCache;

public class BuilderCacheTest extends TestBase
{
    @Test
    public void test() throws Exception
    {
        InMemoryBuilderCache cache = new InMemoryBuilderCache();

        DfaBuilder<JavaToken> builder = new DfaBuilder<>(cache);
        _build(builder);
        Assert.assertEquals(1, cache.m_cache.size());
        Assert.assertEquals(0, cache.m_hits);
        
        builder.clear();
        _build(builder);
        Assert.assertEquals(2, cache.m_cache.size());
        Assert.assertEquals(0, cache.m_hits);

        builder = new DfaBuilder<>(cache);
        _build(builder);
        Assert.assertEquals(2, cache.m_cache.size());
        Assert.assertEquals(1, cache.m_hits);
    }
    
    private void _build(DfaBuilder<JavaToken> builder) throws Exception
    {
        for (JavaToken tok : JavaToken.values())
        {
            builder.addPattern(tok.m_pattern, tok);
        }
        EnumSet<JavaToken> lang = EnumSet.allOf(JavaToken.class);
        DfaState<?> start = builder.build(lang, null);
        _checkDfa(start, "JavaTest.out.txt", false);
    }
    
    private static class InMemoryBuilderCache implements BuilderCache
    {
        Map<String, byte[]> m_cache = new HashMap<>();
        int m_hits = 0;
        
        @Override
        public Serializable getCachedItem(String key)
        {
            Serializable ret=null;
            byte[] bytes = m_cache.get(key);
            if (bytes != null)
            {
                try
                {
                    ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(bytes));
                    ret = (Serializable)is.readObject();
                }
                catch(Exception e)
                {}
            }
            if (ret != null)
            {
                ++m_hits;
            }
            return ret;
        }

        @Override
        public void maybeCacheItem(String key, Serializable item)
        {
            try
            {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(item);
                oos.close();
                m_cache.put(key, bos.toByteArray());
            }
            catch(Exception e)
            {}
        }

    }
}
