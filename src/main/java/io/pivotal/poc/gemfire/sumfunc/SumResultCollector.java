package io.pivotal.poc.gemfire.sumfunc;

import com.gemstone.gemfire.cache.execute.FunctionException;
import com.gemstone.gemfire.cache.execute.ResultCollector;
import com.gemstone.gemfire.distributed.DistributedMember;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.summingDouble;

/**
 * Created by cq on 14/4/16.
 */
public class SumResultCollector implements ResultCollector {

    private final List<Double> results = new ArrayList<>();

    private CountDownLatch latch = new CountDownLatch(1);

    @Override
    public Object getResult() throws FunctionException {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new FunctionException("Interrupted waiting for results", e);
        }
        return results.stream().collect(summingDouble(d-> d.doubleValue()));
    }

    @Override
    public Object getResult(long l, TimeUnit timeUnit) throws FunctionException, InterruptedException {
        latch.await(l, timeUnit);
        return results.stream().collect(summingDouble(d-> d.doubleValue()));
    }

    @Override
    public void addResult(DistributedMember distributedMember, Object o) {
        results.add((Double)o);
    }

    @Override
    public void endResults() {
        latch.countDown();
    }

    @Override
    public void clearResults() {
        results.clear();
        latch = new CountDownLatch(1);
    }


}
