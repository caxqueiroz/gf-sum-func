package io.pivotal.poc.gemfire.sumfunc;


import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.*;
import com.gemstone.gemfire.cache.partition.PartitionRegionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * Created by cq on 14/4/16.
 */

public class SumFunction extends FunctionAdapter{

    private Logger log = LoggerFactory.getLogger(SumFunction.class);

    public boolean hasResult() {
        return true;
    }

    public void execute(FunctionContext functionContext) {

        if(! (functionContext instanceof RegionFunctionContext)) throw new FunctionException("This is a data aware function, and has to be called using FunctionService.onRegion.");

        ResultSender<Object> resultSender = functionContext.getResultSender();

        RegionFunctionContext context = (RegionFunctionContext)functionContext;

        String[] fieldNames = (String[])functionContext.getArguments();
        String fieldName = "";

        if(fieldNames!=null && fieldNames.length ==1)
            fieldName = fieldNames[0];

        Region region = PartitionRegionHelper.getLocalDataForContext(context);

        Set keys = context.getFilter();

        double sum = 0.0;

        if(keys == null || keys.size()==0){
            keys = region.keySet();
        }

        for(Object key: keys){

            if(fieldName!=null && !fieldName.equals("")){
                try {
                    Object object = region.get(key);
                    Field field = object.getClass().getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Double value = field.getDouble(object);
                    sum = sum + value;
                } catch (Exception e) {

                    log.error(e.getMessage());
                }
            }else{
                if(region.get(key) instanceof String){
                    Double value =  Double.valueOf((String)region.get(key));
                    sum = sum + value;
                }else{
                    Double value =  (Double) region.get(key);
                    sum = sum + value;
                }

            }

        }
        resultSender.lastResult(sum);

    }

    public String getId() {
        return "sum";
    }

    public boolean optimizeForWrite() {
        return false;
    }

    public boolean isHA() {
        return false;
    }
}
