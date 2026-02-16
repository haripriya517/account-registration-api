package com.alexa.account.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum AccountType {
    SAVINGS,
    CURRENT,
    INVESTMENT;

    @JsonCreator
    public static AccountType from(String v) {
        if (v == null) return null;
        return AccountType.valueOf(v.trim().toUpperCase());
    }
}

