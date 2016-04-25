package io.pivotal.poc.gemfire.sumfunc.test;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.gemstone.gemfire.distributed.ServerLauncher;
import io.pivotal.poc.gemfire.sumfunc.SumFunction;
import io.pivotal.poc.gemfire.sumfunc.SumResultFunction;
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


        SumResultFunction sumFunc = new SumResultFunction();
        SumFunction sumFunction = new SumFunction();
        FunctionService.registerFunction(sumFunc);
        FunctionService.registerFunction(sumFunction);


        Assert.assertTrue(FunctionService.isRegistered("sum"));
        Assert.assertTrue(FunctionService.isRegistered("local-sum"));


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


        Dummy dummy0 = new Dummy();
        dummy0.setId("key00");
        dummy0.setDvalue(13.2);
        dummy0.setValue(20);

        Dummy dummy1 = new Dummy();
        dummy1.setId("key01");
        dummy1.setDvalue(25.4);
        dummy1.setValue(2);

        Dummy dummy2 = new Dummy();
        dummy2.setId("key02");
        dummy2.setDvalue(1.6);
        dummy2.setValue(6);

        Region dummyRegion = cache.getRegion("dummyRegion");

        dummyRegion.put(dummy0.getId(),dummy0);
        dummyRegion.put(dummy1.getId(),dummy1);
        dummyRegion.put(dummy2.getId(),dummy2);

        results = (List) FunctionService.onRegion(dummyRegion).withArgs(new String[]{"value"}).withFilter(new HashSet<String>()).execute(sumFunc).getResult();
        Assert.assertEquals(28.0,(Double)results.get(0),1);



    }
}
