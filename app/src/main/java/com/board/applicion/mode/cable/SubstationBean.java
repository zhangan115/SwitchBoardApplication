package com.board.applicion.mode.cable;

import java.util.ArrayList;

/**
 * 变电站
 */
public class SubstationBean {
    private String address;
    private String jxtFilename;
    private String runDate;
    private long sectionId;
    private long substationId;
    private String substationName;
    private int substationTypeCode;
    private String voltageRank;
    private ArrayList<ControlRoomBean> controlRoomList;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getJxtFilename() {
        return jxtFilename;
    }

    public void setJxtFilename(String jxtFilename) {
        this.jxtFilename = jxtFilename;
    }

    public String getRunDate() {
        return runDate;
    }

    public void setRunDate(String runDate) {
        this.runDate = runDate;
    }

    public long getSectionId() {
        return sectionId;
    }

    public void setSectionId(long sectionId) {
        this.sectionId = sectionId;
    }

    public long getSubstationId() {
        return substationId;
    }

    public void setSubstationId(long substationId) {
        this.substationId = substationId;
    }

    public String getSubstationName() {
        return substationName;
    }

    public void setSubstationName(String substationName) {
        this.substationName = substationName;
    }

    public int getSubstationTypeCode() {
        return substationTypeCode;
    }

    public void setSubstationTypeCode(int substationTypeCode) {
        this.substationTypeCode = substationTypeCode;
    }

    public String getVoltageRank() {
        return voltageRank;
    }

    public void setVoltageRank(String voltageRank) {
        this.voltageRank = voltageRank;
    }

    public ArrayList<ControlRoomBean> getControlRoomList() {
        return controlRoomList;
    }

    public void setControlRoomList(ArrayList<ControlRoomBean> controlRoomList) {
        this.controlRoomList = controlRoomList;
    }
}
