package org.ga4gh.starterkit.wes.utils;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test
public class DrsUrlResolverTest {

    @DataProvider(name = "cases")
    public Object[][] getData() {
        return new Object[][] {
            {
                "someStringParam123",
                null
            },
            {
                123,
                null
            }
        };
    }

    @Test(dataProvider = "cases")
    public void testDrsUrlResolver(Object object, String expResult) {
        String result = DrsUrlResolver.resolveAccessPathOrUrl(object, false);
        Assert.assertEquals(result, expResult);
    }
}
