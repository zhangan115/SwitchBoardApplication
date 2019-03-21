package com.board.applicion.view.deploy.switchBoard

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.board.applicion.R
import com.board.applicion.app.App
import com.board.applicion.mode.DatabaseStore
import com.board.applicion.mode.databases.*
import com.board.applicion.view.deploy.BaseAddActivity
import com.board.applicion.view.deploy.cabinet.CabinetChooseActivity
import com.board.applicion.view.deploy.mainControlRoom.MainControlRoomChooseActivity
import com.board.applicion.view.deploy.substation.SubstationChooseActivity
import io.objectbox.query.QueryBuilder
import kotlinx.android.synthetic.main.activity_switch_board_add.*

class SwitchBoardAddActivity : BaseAddActivity<CabinetSbPosTemplate>() {

    private var rowValue: Int = 0
    private var currentRowValue: Int = -1
    private var colValue: Int = 0
    private var currentColValue: Int = -1

    private var substation: Substation? = null
    private lateinit var subStore: DatabaseStore<Substation>
    private var mainControlRoom: MainControlRoom? = null
    private lateinit var mainStore: DatabaseStore<MainControlRoom>
    private var cabinet: Cabinet? = null
    private lateinit var cabinetStore: DatabaseStore<Cabinet>

    override fun getDataClass(): Class<CabinetSbPosTemplate> {
        return CabinetSbPosTemplate::class.java
    }

    override fun getQueryBuild(): QueryBuilder<CabinetSbPosTemplate> {
        return databaseStore.getQueryBuilder().equal(CabinetSbPosTemplate_.id, beanID)
    }

    override fun initData() {
        super.initData()
        subStore = DatabaseStore(lifecycle, Substation::class.java)
        mainStore = DatabaseStore(lifecycle, MainControlRoom::class.java)
        cabinetStore = DatabaseStore(lifecycle, Cabinet::class.java)
    }

    @SuppressLint("SetTextI18n")
    override fun setDataToView() {
        if (bean != null) {
            if (bean!!.substationToOne != null) {
                substation = bean!!.substationToOne.target
            }
            if (bean!!.mainControlRoomToOne != null) {
                mainControlRoom = bean!!.mainControlRoomToOne.target
            }
            if (bean!!.cabinetToOne != null) {
                cabinet = bean!!.cabinetToOne.target
            }
            currentRowValue = bean!!.row
            currentColValue = bean!!.col
            desEditText.setText(bean!!.desc)
            editTextName.setText(bean!!.name)
            resultChange()
        }
    }

    private fun resultChange() {
        if (substation != null) {
            subName.text = substation!!.name
        }
        if (mainControlRoom != null) {
            roomText.text = mainControlRoom!!.name
        }
        if (cabinet != null) {
            cabinetName.text = cabinet!!.name
            val text = "${cabinet!!.rowNum} X  ${cabinet!!.colNum} "
            formatName.text = text
            rowValue = cabinet!!.rowNum
            colValue = cabinet!!.colNum
        } else {
            currentColValue = -1
            currentRowValue = -1
        }
        if (currentRowValue != -1 && currentColValue != -1) {
            positionName.text = "第$currentRowValue 行，   第$currentColValue 列"
        } else {
            positionName.text = "点击选择位置"
        }

    }

    @SuppressLint("InflateParams")
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        if (bean == null) {
            subName.setOnClickListener {
                val intent = Intent(this, SubstationChooseActivity::class.java)
                startActivityForResult(intent, 200)
            }
            roomText.setOnClickListener {
                if (substation == null) return@setOnClickListener
                val intent = Intent(this, MainControlRoomChooseActivity::class.java)
                intent.putExtra("subId", substation!!.id)
                startActivityForResult(intent, 201)
            }
            cabinetName.setOnClickListener {
                if (mainControlRoom == null) return@setOnClickListener
                val intent = Intent(this, CabinetChooseActivity::class.java)
                intent.putExtra("subId", mainControlRoom!!.id)
                startActivityForResult(intent, 202)
            }
            positionName.setOnClickListener {
                if (cabinet == null) return@setOnClickListener
                val dialogView = LayoutInflater.from(this).inflate(R.layout.pick_number_layout, null)
                val dialog = MaterialDialog.Builder(this).customView(dialogView, true)
                        .build()
                val rowPick = dialogView.findViewById<NumberPicker>(R.id.rowPicker)
                rowPick.maxValue = rowValue
                rowPick.minValue = 1
                if (currentRowValue != -1)
                    rowPick.value = currentRowValue
                val colPick = dialogView.findViewById<NumberPicker>(R.id.colPicker)
                colPick.maxValue = colValue
                colPick.minValue = 1
                if (currentColValue != -1)
                    colPick.value = currentColValue
                dialogView.findViewById<Button>(R.id.sureBtn).setOnClickListener {
                    currentRowValue = rowPick.value
                    currentColValue = colPick.value
                    resultChange()
                    dialog.dismiss()
                }
                dialogView.findViewById<Button>(R.id.cancelBtn).setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val id = data.getLongExtra("chooseId", 0)
            when (requestCode) {
                200 -> {
                    substation = subStore.getBox().query().equal(Substation_.id, id).build().findUnique()
                    if (substation != null) {
                        mainControlRoom = null
                        cabinet = null
                    }
                }
                201 -> {
                    mainControlRoom = mainStore.getBox().query().equal(MainControlRoom_.id, id).build().findUnique()
                    if (mainControlRoom != null) {
                        cabinet = null
                    }
                }
                202 -> {
                    cabinet = cabinetStore.getBox().query().equal(Cabinet_.id, id).build().findUnique()
                }
            }
            resultChange()
        }
    }

    override fun getSaveButton(): Button {
        return saveUserButton
    }

    override fun canSave(): Boolean {
        if (substation == null) return false
        if (mainControlRoom == null) return false
        if (cabinet == null) return false
        val name = editTextName.text.toString()
        if (TextUtils.isEmpty(name)) {
            return false
        }
        val des = desEditText.text.toString()
        if (bean == null) {
            val queryRowCol = databaseStore.getQueryBuilder()
                    .equal(CabinetSbPosTemplate_.cabinetId, cabinet!!.id)
                    .equal(CabinetSbPosTemplate_.row, currentRowValue.toLong())
                    .equal(CabinetSbPosTemplate_.col, currentColValue.toLong())
                    .build()
            val rowCol = queryRowCol.find()
            if (rowCol.isNotEmpty()) {
                Toast.makeText(this, "该位置已经存在", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        val query = databaseStore.getQueryBuilder()
                .equal(CabinetSbPosTemplate_.name, name)
                .equal(CabinetSbPosTemplate_.cabinetId, cabinet!!.id)
                .build()
        val list = query.find()
        if (list.isNotEmpty()) {
            Toast.makeText(this, "该名称已经使用", Toast.LENGTH_SHORT).show()
            return false
        }
        bean = CabinetSbPosTemplate(beanID, name, des, substation!!.id, mainControlRoom!!.id
                , cabinet!!.id, currentRowValue, currentColValue, 0, App.instance.getCurrentUser().name
                , System.currentTimeMillis(), 0)
        bean!!.substationToOne.target = substation!!
        bean!!.mainControlRoomToOne.target = mainControlRoom!!
        bean!!.cabinetToOne.target = cabinet!!
        return true
    }

    override fun getContentView(): Int {
        return R.layout.activity_switch_board_add
    }

    override fun getToolBarTitle(): String? {
        if (beanID == 0L) return "新增压板"
        return "修改压板"
    }
}