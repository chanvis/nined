package com.nined.userservice.enums;

/**
 * Supported privileges
 * 
 * @author vijay
 *
 */
public enum Privilege {

    PRE_AUTH("-1"), //
    REFRESH_AUTH("0"), //
    VIEW_VENDOR("10"), //
    ADD_VENDOR("11"), //
    EDIT_VENDOR("12"), //
    DELETE_VENDOR("13"), //
    ALL_VENDOR("14"), //
    VIEW_CARRIER("15"), //
    ADD_CARRIER("16"), //
    EDIT_CARRIER("17"), //
    DELETE_CARRIER("18"), //
    ALL_CARRIER("19"), //
    VIEW_USER("20"), //
    ADD_USER("21"), //
    EDIT_USER("22"), //
    DELETE_USER("23"), //
    ALL_USER("24");

    private String id;

    Privilege(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }
}
