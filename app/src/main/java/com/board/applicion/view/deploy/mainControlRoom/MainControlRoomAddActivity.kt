package com.board.applicion.view.deploy.mainControlRoom

import android.widget.Button
import com.board.applicion.R
import com.board.applicion.mode.MainControlRoom
import com.board.applicion.mode.MainControlRoom_
import com.board.applicion.view.deploy.BaseAddActivity
import io.objectbox.query.QueryBuilder
import kotlinx.android.synthetic.main.activity_main_control_room_add.*

class MainControlRoomAddActivity : BaseAddActivity<MainControlRoom>() {

    override fun getDataClass(): Class<MainControlRoom> {
       return  MainControlRoom::class.java
    }

    override fun getQueryBuild(): QueryBuilder<MainControlRoom> {
        return databaseStore.getQueryBuilder().equal(MainControlRoom_.id, beanID)
    }

    override fun setDataToView() {

    }

    override fun getSaveButton(): Button {
       return  saveUserButton
    }

    override fun canSave(): Boolean {
        return true
    }

    override fun getContentView(): Int {
        return R.layout.activity_main_control_room_add
    }
}