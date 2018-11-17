package com.board.applicion.view.deploy.cabinet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Button
import android.widget.Toast
import com.board.applicion.R
import com.board.applicion.app.App
import com.board.applicion.mode.DatabaseStore
import com.board.applicion.mode.databases.*
import com.board.applicion.view.deploy.BaseAddActivity
import com.board.applicion.view.deploy.mainControlRoom.MainControlRoomChooseActivity
import com.board.applicion.view.deploy.substation.SubstationChooseActivity
import io.objectbox.query.QueryBuilder
import kotlinx.android.synthetic.main.activity_cabinet_add.*

class CabinetAddActivity : BaseAddActivity<Cabinet>() {

    var substation: Substation? = null//变电站
    var mainControlRoom: MainControlRoom? = null//主控室
    //    val switchBoardList = ArrayList<SwitchBoard>()
    private lateinit var subStore: DatabaseStore<Substation>
    private lateinit var mainStore: DatabaseStore<MainControlRoom>
    private var rowValue: Int = 0
    private var colValue: Int = 0
    override fun getDataClass(): Class<Cabinet> {
        return Cabinet::class.java
    }

    override fun initData() {
        super.initData()
        subStore = DatabaseStore(lifecycle, Substation::class.java)
        mainStore = DatabaseStore(lifecycle, MainControlRoom::class.java)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        rowText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                val value = s?.toString()
                rowValue = if (!TextUtils.isEmpty(value)) {
                    rowOrColValueChange()
                    value!!.toInt()
                } else {
                    rowOrColValueChange()
                    0
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        colText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                val value = s?.toString()
                colValue = if (!TextUtils.isEmpty(value)) {
                    rowOrColValueChange()
                    value!!.toInt()
                } else {
                    rowOrColValueChange()
                    0
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        substationText.setOnClickListener {
            startActivityForResult(Intent(this, SubstationChooseActivity::class.java), 200)
        }
        roomText.setOnClickListener {
            if (substation == null) {
                Toast.makeText(this, "请选择配电室", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, MainControlRoomChooseActivity::class.java)
            intent.putExtra("subId", substation!!.id)
            startActivityForResult(intent, 201)
        }
    }

    private fun rowOrColValueChange() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == 200) {
                val subId = data.getLongExtra("chooseId", 0)
                if (subId == 0L) return
                substation = subStore.getQueryBuilder().equal(Substation_.id, subId).build().findUnique()
                if (substation != null) {
                    substationText.text = substation!!.name
                    mainControlRoom = null
                    roomText.text = "点击选择主控室"
                }
            } else if (requestCode == 201) {
                val roomId = data.getLongExtra("chooseId", 0)
                if (roomId == 0L) return
                mainControlRoom = mainStore.getQueryBuilder().equal(MainControlRoom_.id, roomId).build().findUnique()
                if (mainControlRoom != null) {
                    roomText.text = mainControlRoom!!.name
                }
            }
        }
    }

    override fun getQueryBuild(): QueryBuilder<Cabinet> {
        return databaseStore.getQueryBuilder().equal(Cabinet_.id, beanID)
    }

    override fun setDataToView() {
        if (bean != null) {
            editTextName.setText(bean!!.name)
            rowText.setText(bean!!.rowNum.toString())
            rowValue = bean!!.rowNum
            colText.setText(bean!!.colNum.toString())
            colValue = bean!!.colNum
            substationText.text = bean!!.substationToOne.target.name
            substation = bean!!.substationToOne.target
            roomText.text = bean!!.mainControlRoomToOne.target.name
            mainControlRoom = bean!!.mainControlRoomToOne.target
        }
    }

    override fun getSaveButton(): Button {
        return saveUserButton
    }

    override fun canSave(): Boolean {
        if (substation == null) {
            return false
        }
        if (mainControlRoom == null) {
            return false
        }
        if (rowValue == 0 || colValue == 0) {
            return false
        }
        val name = editTextName.text.toString()
        if (TextUtils.isEmpty(name)) {
            return false
        }
        if (bean == null) {
            val query = databaseStore.getQueryBuilder()
                    .equal(Cabinet_.name, name)
                    .equal(Cabinet_.mcrId, mainControlRoom!!.id)
                    .build()
            val userList = query.find()
            if (userList.isNotEmpty()) {
                Toast.makeText(this, "该名称已经使用", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        bean = Cabinet(beanID, name, substation!!.id, mainControlRoom!!.id, rowValue, colValue
                , App.instance.getCurrentUser().name, System.currentTimeMillis(), 0)
        bean!!.substationToOne.target = substation!!
        bean!!.mainControlRoomToOne.target = mainControlRoom!!
        mainControlRoom!!.cabinetToMany.add(bean!!)
        mainStore.getBox().put(mainControlRoom!!)
        return true
    }

    override fun getContentView(): Int {
        return R.layout.activity_cabinet_add
    }

    override fun getToolBarTitle(): String? {
        if (bean == null) {
            return "新增屏柜"
        }
        return "修改屏柜"
    }
}