package io.pivotal.poc.gemfire.sumfunc.test;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.gemstone.gemfire.distributed.ServerLauncher;
import io.pivotal.poc.gemfire.sumfunc.SumFunction;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by cq on 14/4/16.
 */

public class FunctionTest {
    @Test
    public void testFunction(){

        ServerLauncher serverLauncher  = new ServerLauncher.Builder()
                .setMemberName("server1")
                .set("cache-xml-file", "server-cache.xml")
                .set("log-level", "info")
                .build();

        System.out.println("Attempting to start cache server");

        serverLauncher.start();

        System.out.println("Cache server successfully started");

        Cache cache = new CacheFactory().create();


        SumFunction sumFunc = new SumFunction();
        FunctionService.registerFunction(sumFunc);

        Assert.assertTrue(FunctionService.isRegistered("sum"));


        Region region = cache.getRegion("testRegion");

        region.put("1",10.3);
        region.put("2",10.6);

        Set<String> keys = new HashSet<String>();
        keys.add("1");

        List results = (List) FunctionService.onRegion(region).withArgs(new String[]{""}).withFilter(keys).execute(sumFunc).getResult();
        Assert.assertEquals(10.3,(Double)results.get(0),1);

        results = (List) FunctionService.onRegion(region).withArgs(new String[]{""}).withFilter(new HashSet<String>()).execute(sumFunc).getResult();
        Assert.assertEquals(20.9,(Double)results.get(0),1);

        region.put("3",11.3);
        region.put("4",12.6);

        results = (List) FunctionService.onRegion(region).withArgs(new String[]{""}).withFilter(new HashSet<String>()).execute(sumFunc).getResult();
        Assert.assertEquals(44.8,(Double)results.get(0),1);

    }
}
