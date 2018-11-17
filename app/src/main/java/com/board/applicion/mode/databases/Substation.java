package com.board.applicion.mode.databases;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;
/**
 * 变电站表 SUBSTATION
 * (version 1.0 data 2018-11-10)
 *  ID	                    INT	            变电站ID
 *  NAME	                VARCHAR(64)	    变电站名称
 *  VOLTAGE_RANK	        VARCHAR(32)	    电压等级
 *  DESC	                VARCHAR(512)	变电站描述
 *  CREATOR	                VARCHAR(64)	    创建人
 *  UPDATE_TIME         	TIME	        创建/修改时间
 *  STATUS	                INT	状态
 */
@Entity
public class Substation {

    @Id
    public long id;
    public String name;
    public String voltageRank;
    public String desc;
    public String creator;
    public long updateTime;
    public int status;
    @Backlink(to = "substationToOne")
    public ToMany<MainControlRoom> mainControlRoomToMany;

    public Substation(long id, String name, String voltageRank, String desc, String creator, long updateTime, int status) {
        this.id = id;
        this.name = name;
        this.voltageRank = voltageRank;
        this.desc = desc;
        this.creator = creator;
        this.updateTime = updateTime;
        this.status = status;
    }

    public Substation() {
    }
}
