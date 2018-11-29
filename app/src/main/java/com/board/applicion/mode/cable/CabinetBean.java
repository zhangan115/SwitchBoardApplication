package com.board.applicion.mode.cable;

public class CabinetBean {

    private long id;
    private long mcrId;
    private String name;
    private int status;
    private long subId;
    private int type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMcrId() {
        return mcrId;
    }

    public void setMcrId(long mcrId) {
        this.mcrId = mcrId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getSubId() {
        return subId;
    }

    public void setSubId(long subId) {
        this.subId = subId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
