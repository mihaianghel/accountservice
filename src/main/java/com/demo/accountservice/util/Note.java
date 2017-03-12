package com.demo.accountservice.util;

import java.io.Serializable;

public class Note implements Serializable {

    Integer denomination;
    Integer count;

    public Integer getDenomination() {
        return denomination;
    }

    public void setDenomination(Integer denomination) {
        this.denomination = denomination;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Note() {}

    public Note(Integer denomination, Integer count) {
        this.denomination = denomination;
        this.count = count;
    }

    @Override
    public String toString() {
        return "{denomination=" + denomination + ", count=" + count + '}';
    }
}
