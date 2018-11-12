package com.library.chart;

import java.util.List;

/**
 * Created by pingan on 2017/7/2.
 */

public class ChartData {
    private String userName;
    private List<Value> valueList;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<Value> getValueList() {
        return valueList;
    }

    public void setValueList(List<Value> valueList) {
        this.valueList = valueList;
    }

    public static class Value {

        public Value() {
        }

        public Value(int value) {
            this.value = value;
        }

        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
