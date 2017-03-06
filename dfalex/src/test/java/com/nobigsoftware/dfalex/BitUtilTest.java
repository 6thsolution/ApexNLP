package com.nobigsoftware.dfalex;

import org.junit.Assert;
import org.junit.Test;

public class BitUtilTest extends TestBase
{
    @Test
    public void testBitIndex() throws Exception
    {
        Assert.assertEquals(-1, BitUtils.lowBitIndex(0));
        for (int i=0;i<32;++i)
        {
            Assert.assertEquals(i, BitUtils.lowBitIndex(1<<i));
            Assert.assertEquals(i, BitUtils.lowBitIndex(5<<i));
            Assert.assertEquals(i, BitUtils.lowBitIndex(-1<<i));
        }
    }
}
