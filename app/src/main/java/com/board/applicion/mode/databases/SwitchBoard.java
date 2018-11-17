package com.board.applicion.mode.databases;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

/**
 * 压板表：SWITCH_BOARD
 * (version 1.0 data 2018-11-10)
 * ID	        INT	            压板ID
 * NAME	    VARCHAR(64)	    压板名称
 * DESC	    VARCHAR(512)	压板描述
 * SUB_ID	    INT	            变电站ID
 * MCR_ID	    INT	            主控室ID
 * CABINET_ID	INT	            屏柜ID
 * ROW	        INT	            行号
 * COL	        INT	            列号
 * CREATOR	    VARCHAR(64)	    创建人
 * UPDATE_TIME	TIME	        创建/修改时间
 * STATUS	    INT	            状态
 */
@Entity
public class SwitchBoard {

    @Id
    public long id = 0;
    public String name;
    public String desc;
    public long subId;
    public long mcrId;
    public long cabinetId;
    public int row;
    public int col;
    public String creator;
    public long updateTime;
    public int status;
    public ToOne<Substation> substationToOne;
    public ToOne<MainControlRoom> mainControlRoomToOne;
    public ToOne<Cabinet> cabinetToOne;

    public SwitchBoard() {
    }

    public SwitchBoard(long id, String name, String desc, long subId, long mcrId, long cabinetId
            , int row, int col, String creator, long updateTime, int status) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.subId = subId;
        this.mcrId = mcrId;
        this.cabinetId = cabinetId;
        this.row = row;
        this.col = col;
        this.creator = creator;
        this.updateTime = updateTime;
        this.status = status;
    }
}
