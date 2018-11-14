package com.board.applicion.mode

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Transient

/**
 * 用户表：USER
 * (version 1.0 data 2018-11-10)
 *  ID	            INT	            用户ID
 *  NAME	        VARCHAR(64)	    账号名称
 *  REAL_NAME	    VARCHAR(64)	    真实名称
 *  PASSWD	        VARCHAR(256)	密码
 *  DEPARTMENT	    VARCHAR(128)	所属部门
 *  CELL_PHONE_NUM	VARCHAR（11）	手机号码
 *  CREATOR	        VARCHAR(64)	    创建人
 *  UPDATE_TIME	    TIME	        创建/修改时间
 *  STATUS	        INT	            状态
 */
@Entity
 class User(@Id var id: Long = 0
                , var name: String
                , var realName: String
                , var passWd: String
                , var department: String?
                , var cellPhoneNum: String?
                , var creator: String
                , var updateTime: Long
                , var status: Int) {
    @Transient
    var toDelete: Boolean = false
}

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
data class Substation(@Id var id: Long = 0
                      , var name: String
                      , var voltageRank: String
                      , var desc: String?
                      , var creator: String
                      , var updateTime: Long
                      , var status: Int)

/**
 * 主控室表 MAIN_CONTROL_ROOM
 * (version 1.0 data 2018-11-10)
 *  ID	        INT	            主控室ID
 *  NAME	    VARCHAR(64)	    主控室名称
 *  SUB_ID	    INT	            变电站ID
 *  DESC	    VARCHAR(512)	变电站描述
 *  CREATOR	    VARCHAR(64)	    创建人
 *  UPDATE_TIME	TIME	        创建/修改时间
 *  STATUS	    INT	            状态
 */
@Entity
data class MainControlRoom(@Id var id: Long = 0
                           , var name: String
                           , var SUB_ID: Long
                           , var desc: String?
                           , var creator: String
                           , var updateTime: Long
                           , var status: Int)

/**
 * 屏柜表：CABINET
 * (version 1.0 data 2018-11-10)
 *  ID	        INT	        屏柜ID
 *  NAME	    VARCHAR(64)	屏柜名称
 *  SUB_ID	    INT	        变电站ID
 *  MCR_ID	    INT	        主控室ID
 *  ROW_NUM	    INT	        行数
 *  COL_NUM	    INT	        列数
 *  CREATOR	    VARCHAR(64)	创建人
 *  UPDATE_TIME	TIME	    创建/修改时间
 *  STATUS	    INT	        状态
 */
@Entity
data class Cabinet(@Id var id: Long = 0
                   , var name: String
                   , var subId: Long
                   , var mcrId: Long
                   , var rowNum: Int
                   , var colNum: Int
                   , var creator: String
                   , var updateTime: Long
                   , var status: Int)

/**
 * 压板表：SWITCH_BOARD
 * (version 1.0 data 2018-11-10)
 *  ID	        INT	            压板ID
 *  NAME	    VARCHAR(64)	    压板名称
 *  DESC	    VARCHAR(512)	压板描述
 *  SUB_ID	    INT	            变电站ID
 *  MCR_ID	    INT	            主控室ID
 *  CABINET_ID	INT	            屏柜ID
 *  ROW	        INT	            行号
 *  COL	        INT	            列号
 *  CREATOR	    VARCHAR(64)	    创建人
 *  UPDATE_TIME	TIME	        创建/修改时间
 *  STATUS	    INT	            状态
 */
@Entity
data class SwitchBoard(@Id var id: Long = 0
                       , var name: String
                       , var desc: String
                       , var subId: Long
                       , var mcrId: Long
                       , var cabinetId: Long
                       , var row: Int
                       , var col: Int
                       , var creator: String
                       , var updateTime: Long
                       , var status: Int)

/**
 * 屏柜压板位置模板数据：CABINET_SB_POS_TEMPLATE
 * (version 1.0 data 2018-11-10)
 * ID	        INT	            模板ID
 * SUB_ID	    INT	            变电站ID
 * MCR_ID	    INT	            主控室ID
 * CABINET_ID	INT	            屏柜ID
 * SB_ID	    INT	            压板ID
 * NAME	        VARCHAR(64)	    压板名称
 * DESC	        VARCHAR(512)	压板描述
 * ROW	        INT	            行号
 * POSITION	    INT	            压板位置    0---开；1---关
 * COL	        INT	            列号
 * CREATOR	    VARCHAR(64)	    创建人
 * UPDATE_TIME	TIME	        创建/修改时间
 * STATUS	    INT	            状态
 */
@Entity
data class CabinetSbPosTemplate(@Id var id: Long = 0
                                , var subId: Long
                                , var mcrId: Long
                                , var cabinetId: Long
                                , var sbId: Long
                                , var name: String
                                , var desc: String
                                , var row: Int
                                , var col: Int
                                , var position: Int
                                , var creator: String
                                , var updateTime: Long
                                , var status: Int)

/**
 * 屏柜压板位置核查结果：CABINET_SB_POS_CK_RST
 * (version 1.0 data 2018-11-10)
 *  SUB_ID	        INT	            变电站ID
 *  MCR_ID	        INT	            主控室ID
 *  CABINET_ID	    INT	            屏柜ID
 *  CHECK_TIME	    TIME    	    核查时间
 *  POS_IMAGE	    VARCHAR(512)	压板实际位置图片。
 *  CHECK_VALUE	    INT	            核查值
 *  CHECKER	        VARCHAR(64)	    核查人
 *  STATUS	        INT	            状态
 */
@Entity
data class CabinetSbPosCkRst(@Id var id: Long = 0
                             , var subId: Long
                             , var mcrId: Long
                             , var cabinetId: Long
                             , var checkTime: Long
                             , var posImage: String
                             , var checkValue: String
                             , var checker: String
                             , var updateTime: Long
                             , var status: Int)

/**
 * 屏柜压板位置核查详细结果：SB_POS_CK_RST_DETAIL
 * (version 1.0 data 2018-11-10)
 *  SUB_ID	    INT	            变电站ID
 *  MCR_ID	    INT	            主控室ID
 *  CABINET_ID	INT	            屏柜ID
 *  SB_ID	    INT	            压板ID
 *  CHECK_TIME	TIME	        核查时间
 *  NAME	    VARCHAR(64)	    压板名称
 *  DESC	    VARCHAR(512)    压板描述
 *  ROW	        INT	            行号
 *  COL	        INT	            列号
 *  POS_MATCH	INT	            压板实际位置与模板位置进行比较。
 *  CHECKER	    VARCHAR(64)	    核查人
 *  STATUS	    INT	            状态
 */
@Entity
data class SbPosCjRstDetail(@Id var id: Long = 0
                            , var subId: Long
                            , var mcrId: Long
                            , var cabinetId: Long
                            , var sbId: Long
                            , var checkTime: Long
                            , var name: String
                            , var desc: String
                            , var row: Int
                            , var col: Int
                            , var posMatch: Int
                            , var checker: String
                            , var updateTime: Long
                            , var status: Int)




