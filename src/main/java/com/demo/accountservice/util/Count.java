package com.demo.accountservice.util;

import java.util.ArrayList;
import java.util.List;

public class Count {

    private int sum;
    private List<Integer> values;

    public Count(int sum) {
        this.sum = sum;
        this.values = new ArrayList<>();
    }

    public Count(Count count) {
        this.sum = count.getSum();
        this.values = new ArrayList<>(count.getValues());
    }

    public Count(int sum, List<Integer> values) {
        this.sum = sum;
        this.values = new ArrayList<>(values);
    }

    public int getSum() {
        return sum;
    }

    public List<Integer> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return "Count{" +
                "sum=" + sum +
                ", values=" + values +
                '}';
    }
}
