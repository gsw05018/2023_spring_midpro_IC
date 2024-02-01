package com.sbspro.midproject.base.exception;

import com.sbspro.midproject.base.rsData.RsData;


public class NeedHistoryBackException extends RuntimeException {
    public NeedHistoryBackException(RsData rs) {
        this(rs.getMsg());
    }

    public NeedHistoryBackException(String msg) {
        super(msg);
    }
}