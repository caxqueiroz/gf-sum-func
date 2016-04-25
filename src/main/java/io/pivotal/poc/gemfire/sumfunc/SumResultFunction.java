package io.pivotal.poc.gemfire.sumfunc;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.summingDouble;

/**
 * Created by cq on 14/4/16.
 */
public class SumResultFunction extends FunctionAdapter {

    public void execute(FunctionContext functionContext) {
        RegionFunctionContext context = (RegionFunctionContext)functionContext;

        String fieldName = (String)functionContext.getArguments();

        if(fieldName==null)
            fieldName = "";

        Region region = context.getDataSet();

        Set keys = context.getFilter();
        if(keys==null || keys.size()==0)
            keys = new HashSet<>();

        Execution execution = FunctionService.onRegion(region)
                .withFilter(keys)
                .withArgs(fieldName);

        List<Double> result = (List) execution.execute("temp-sum").getResult();

        Double finalResult = result.stream().collect(summingDouble(d-> d.doubleValue()));
        ResultSender<Object> resultSender = functionContext.getResultSender();
        resultSender.lastResult(finalResult);

    }

    public String getId() {
        return "sum";
    }
}
