package com.board.applicion.mode.databases;

import android.support.annotation.IntDef;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

/**
 * 屏柜压板位置核查结果：CABINET_SB_POS_CK_RST
 * (version 1.0 data 2018-11-10)
 * SUB_ID	        INT	            变电站ID
 * MCR_ID	        INT	            主控室ID
 * CABINET_ID	    INT	            屏柜ID
 * CHECK_TIME	    TIME    	    核查时间
 * POS_IMAGE	    VARCHAR(512)	压板实际位置图片。
 * CHECK_VALUE	    INT	            核查值
 * CHECKER	        VARCHAR(64)	    核查人
 * STATUS	        INT	            状态
 */
@Entity
public class CabinetSbPosCkRst {

    @Id
    public long id;
    public long subId;
    public long mcrId;
    public long cabinetId;
    public long checkTime;
    public String posImage;
    public String checkValue;
    public String checker;
    public long updateTime;
    public int status;
    public ToMany<SbPosCjRstDetail> sbPosCjRstDetailToMany;
    public ToOne<Cabinet> cabinetToOne;
    public ToOne<MainControlRoom> mainControlRoomToOne;
    public ToOne<Substation> substationToOne;

    public CabinetSbPosCkRst(long id, long subId, long mcrId, long cabinetId, long checkTime
            , String posImage, String checkValue, String checker, long updateTime, int status) {
        this.id = id;
        this.subId = subId;
        this.mcrId = mcrId;
        this.cabinetId = cabinetId;
        this.checkTime = checkTime;
        this.posImage = posImage;
        this.checkValue = checkValue;
        this.checker = checker;
        this.updateTime = updateTime;
        this.status = status;
    }

    public CabinetSbPosCkRst() {
    }

}
