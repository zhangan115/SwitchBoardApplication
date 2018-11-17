package com.board.applicion.view.deploy.mainControlRoom

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.board.applicion.R
import com.board.applicion.app.App
import com.board.applicion.mode.*
import com.board.applicion.mode.databases.MainControlRoom
import com.board.applicion.mode.databases.MainControlRoom_
import com.board.applicion.mode.databases.Substation
import com.board.applicion.mode.databases.Substation_
import com.board.applicion.view.deploy.BaseAddActivity
import com.board.applicion.view.deploy.substation.SubstationChooseActivity
import io.objectbox.query.QueryBuilder
import kotlinx.android.synthetic.main.activity_main_control_room_add.*

class MainControlRoomAddActivity : BaseAddActivity<MainControlRoom>() {

    var substation: Substation? = null
    private lateinit var subStore: DatabaseStore<Substation>

    override fun initData() {
        super.initData()
        subStore = DatabaseStore(lifecycle, Substation::class.java)
    }

    override fun getDataClass(): Class<MainControlRoom> {
        return MainControlRoom::class.java
    }

    override fun getQueryBuild(): QueryBuilder<MainControlRoom> {
        return databaseStore.getQueryBuilder().equal(MainControlRoom_.id, beanID)
    }

    override fun setDataToView() {
        if (bean != null) {
            substation = bean!!.substationToOne.target
            editTextName.setText(bean!!.name)
            substationText.text = substation?.name
            desEditText.setText(bean!!.desc)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        substationText.setOnClickListener { _ ->
            startActivityForResult(Intent(this, SubstationChooseActivity::class.java), 200)
        }
    }

    override fun getToolBarTitle(): String? {
        if (bean == null) {
            return "新增主控室"
        }
        return "修改主控室"
    }

    override fun getSaveButton(): Button {
        return saveUserButton
    }

    override fun canSave(): Boolean {
        if (substation == null) return false
        val roomName = editTextName.text.toString()
        val des = desEditText.text.toString()
        if (TextUtils.isEmpty(roomName)) {
            return false
        }
        if (bean == null) {
            val query = databaseStore.getQueryBuilder().equal(MainControlRoom_.name, roomName).build()
            val userList = query.find()
            if (userList.isNotEmpty()) {
                Toast.makeText(this, "该名称已经使用", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        bean = MainControlRoom(beanID, roomName, substation!!.id, des
                , App.instance.getCurrentUser().name, System.currentTimeMillis(), 0)
        bean!!.substationToOne.target = substation
        substation!!.mainControlRoomToMany.add(bean!!)
        subStore.getBox().put(substation!!)
        return true
    }

    override fun getContentView(): Int {
        return R.layout.activity_main_control_room_add
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200 && resultCode == Activity.RESULT_OK && data != null) {
            val subId = data.getLongExtra("chooseId", 0)
            if (subId == 0L) return
            substation = subStore.getQueryBuilder().equal(Substation_.id, subId).build().findUnique()
            if (substation != null) {
                substationText.text = substation!!.name
            }
        }
    }
}