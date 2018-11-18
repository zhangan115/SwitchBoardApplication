package com.board.applicion.mode.databases;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

/**
 * 屏柜压板位置核查详细结果：SB_POS_CK_RST_DETAIL
 * (version 1.0 data 2018-11-10)
 * SUB_ID	    INT	            变电站ID
 * MCR_ID	    INT	            主控室ID
 * CABINET_ID	INT	            屏柜ID
 * SB_ID	    INT	            压板ID
 * CHECK_TIME	TIME	        核查时间
 * NAME	    VARCHAR(64)	    压板名称
 * DESC	    VARCHAR(512)    压板描述
 * ROW	        INT	            行号
 * COL	        INT	            列号
 * POS_MATCH	INT	            压板实际位置与模板位置进行比较。
 * CHECKER	    VARCHAR(64)	    核查人
 * STATUS	    INT	            状态
 */
@Entity
public class SbPosCjRstDetail {
    @Id
    public long id;
    public long subId;
    public long mcrId;
    public long cabinetId;
    public long sbId;
    public long checkTime;
    public String name;
    public String desc;
    public int row;
    public int col;
    public int posMatch;
    public String checker;
    public long updateTime;
    public int status;

    public ToOne<CabinetSbPosCkRst> cabinetSbPosCkRstToOne;

    public SbPosCjRstDetail() {
    }

    public SbPosCjRstDetail(long id, long subId, long mcrId, long cabinetId, long sbId
            , long checkTime, String name, String desc, int row, int col, int posMatch
            , String checker, long updateTime, int status) {
        this.id = id;
        this.subId = subId;
        this.mcrId = mcrId;
        this.cabinetId = cabinetId;
        this.sbId = sbId;
        this.checkTime = checkTime;
        this.name = name;
        this.desc = desc;
        this.row = row;
        this.col = col;
        this.posMatch = posMatch;
        this.checker = checker;
        this.updateTime = updateTime;
        this.status = status;
    }

}
