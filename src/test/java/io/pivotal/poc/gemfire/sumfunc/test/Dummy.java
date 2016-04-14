package io.pivotal.poc.gemfire.sumfunc.test;

import java.io.Serializable;

/**
 * Created by cq on 14/4/16.
 */
public class Dummy implements Serializable{

    private String id;

    private int value;

    private double dvalue;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public double getDvalue() {
        return dvalue;
    }

    public void setDvalue(double dvalue) {
        this.dvalue = dvalue;
    }
}
