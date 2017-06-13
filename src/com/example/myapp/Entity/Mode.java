package com.example.myapp.Entity;

/**
 * Created by Administrator on 2017/5/18.
 */
public enum Mode {
    SINGLE_TUNE_CIRCULATION,
    RANDOM,
    LIST_CIRCULATION;

    public static Mode getNext(Mode mode){
        switch(mode){
            case SINGLE_TUNE_CIRCULATION:
                return RANDOM;
            case RANDOM:
                return LIST_CIRCULATION;
           default:
                return SINGLE_TUNE_CIRCULATION;
        }
    }
}
