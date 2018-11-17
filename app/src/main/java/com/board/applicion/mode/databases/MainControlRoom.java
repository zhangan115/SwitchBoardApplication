package com.board.applicion.mode.databases;


import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;
/**
 * 主控室表 MAIN_CONTROL_ROOM
 * (version 1.0 data 2018-11-10)
 *  ID	        INT	            主控室ID
 *  NAME	    VARCHAR(64)	    主控室名称
 *  SUB_ID	    INT	            变电站ID
 *  DESC	    VARCHAR(512)	主控室描述
 *  CREATOR	    VARCHAR(64)	    创建人
 *  UPDATE_TIME	TIME	        创建/修改时间
 *  STATUS	    INT	            状态
 */
@Entity
public class MainControlRoom {
    @Id
    public long id;
    public String name;
    public long subId;
    public String desc;
    public String create;
    public long updateTime;
    public int status;
    public ToOne<Substation> substationToOne;
    public ToMany<Cabinet> cabinetToMany;

    public MainControlRoom(long id, String name, long subId, String desc, String create, long updateTime, int status) {
        this.id = id;
        this.name = name;
        this.subId = subId;
        this.desc = desc;
        this.create = create;
        this.updateTime = updateTime;
        this.status = status;
    }

    public MainControlRoom() {
    }
}
