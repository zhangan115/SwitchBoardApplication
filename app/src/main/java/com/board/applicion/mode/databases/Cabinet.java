package com.board.applicion.mode.databases;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

/**
 * 屏柜表：CABINET
 * (version 1.0 data 2018-11-10)
 * ID	        INT	        屏柜ID
 * NAME	    VARCHAR(64)	屏柜名称
 * SUB_ID	    INT	        变电站ID
 * MCR_ID	    INT	        主控室ID
 * ROW_NUM	    INT	        行数
 * COL_NUM	    INT	        列数
 * CREATOR	    VARCHAR(64)	创建人
 * UPDATE_TIME	TIME	    创建/修改时间
 * STATUS	    INT	        状态
 */
@Entity
public class Cabinet {
    @Id
    public long id;
    public String name;
    public long subId;
    public long mcrId;
    public int rowNum;
    public int colNum;
    public String create;
    public long updateTime;
    public int status;
    public ToOne<Substation> substationToOne;
    public ToOne<MainControlRoom> mainControlRoomToOne;
    public ToMany<CabinetSbPosTemplate> cabinetSbPosTemplateToMany;

    public Cabinet(long id, String name, long subId, long mcrId, int rowNum, int colNum, String create, long updateTime, int status) {
        this.id = id;
        this.name = name;
        this.subId = subId;
        this.mcrId = mcrId;
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.create = create;
        this.updateTime = updateTime;
        this.status = status;
    }

    public Cabinet() {
    }



}
