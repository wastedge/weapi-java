package com.wastedge.api;

public enum EntityDataType {
    BYTES,
    STRING,
    DATE,
    DATE_TIME,
    DATE_TIME_TZ,
    DECIMAL {
        @Override
        public boolean isNumeric() {
            return true;
        }
    },
    LONG{
        @Override
        public boolean isNumeric() {
            return true;
        }
    },
    INT{
        @Override
        public boolean isNumeric() {
            return true;
        }
    },
    BOOL;

    public boolean isNumeric() {
        return false;
    }
}
