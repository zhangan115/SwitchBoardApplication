package com.board.applicion.view.deploy.cabinet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.board.applicion.R
import com.board.applicion.app.App
import com.board.applicion.mode.DatabaseStore
import com.board.applicion.mode.databases.*
import com.board.applicion.view.deploy.BaseAddActivity
import com.board.applicion.view.deploy.mainControlRoom.MainControlRoomChooseActivity
import com.board.applicion.view.deploy.substation.SubstationChooseActivity
import com.board.applicion.view.deploy.switchBoard.SwitchBoardAddActivity
import io.objectbox.query.QueryBuilder
import kotlinx.android.synthetic.main.activity_cabinet_add.*

class CabinetAddActivity : BaseAddActivity<Cabinet>() {

    private var substation: Substation? = null//变电站
    private var mainControlRoom: MainControlRoom? = null//主控室
    private val switchBoard = ArrayList<CabinetSbPosTemplate>()//编辑的压板

    private lateinit var subStore: DatabaseStore<Substation>
    private lateinit var mainStore: DatabaseStore<MainControlRoom>
    private lateinit var sbStore: DatabaseStore<CabinetSbPosTemplate>
    private var rowValue: Int = 0
    private var colValue: Int = 0
    private var canEdit = true
    override fun getDataClass(): Class<Cabinet> {
        return Cabinet::class.java
    }

    override fun initData() {
        super.initData()
        subStore = DatabaseStore(lifecycle, Substation::class.java)
        mainStore = DatabaseStore(lifecycle, MainControlRoom::class.java)
        sbStore = DatabaseStore(lifecycle, CabinetSbPosTemplate::class.java)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        rowText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                val value = s?.toString()
                rowValue = if (!TextUtils.isEmpty(value)) {
                    value!!.toInt()
                } else {
                    0
                }
                rowOrColValueChange()

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
                    value!!.toInt()
                } else {
                    0
                }
                rowOrColValueChange()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        substationText.setOnClickListener {
            if (!canEdit) return@setOnClickListener
            val name = editTextName.text.toString()
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "请输入名称", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivityForResult(Intent(this, SubstationChooseActivity::class.java), 200)
        }
        roomText.setOnClickListener {
            if (!canEdit) return@setOnClickListener
            if (substation == null) {
                Toast.makeText(this, "请选择配电室", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, MainControlRoomChooseActivity::class.java)
            intent.putExtra("subId", substation!!.id)
            startActivityForResult(intent, 201)
        }
        recycleView.adapter = Adapter(this)
        if (beanID != 0L && bean != null) {
            switchBoard.addAll(bean!!.cabinetSbPosTemplateToMany)
            setEnableEt()
            showSwitchBoard(bean!!.colNum)
        }
    }

    fun setEnableEt() {
        rowText.isEnabled = false
        colText.isEnabled = false
        canEdit = false
    }

    private fun rowOrColValueChange() {
        if (substation == null || mainControlRoom == null || rowValue == 0 || colValue == 0) {
            switchBoard.clear()
        } else {
           showSwitchBoard(colValue)
        }
        recycleView.adapter?.notifyDataSetChanged()
    }

    private fun showSwitchBoard(colNum:Int){
        recycleView.layoutManager = GridLayoutManager(this, colNum)
        val tempList = ArrayList<CabinetSbPosTemplate>()
        for (i in 1..rowValue) {
            for (j in 1..colValue) {
                val sb = getCurrentData(i, j, switchBoard)
                tempList.add(sb)
            }
        }
        switchBoard.clear()
        switchBoard.addAll(tempList)
    }

    private fun getCurrentData(row: Int, col: Int, list: List<CabinetSbPosTemplate>): CabinetSbPosTemplate {
        var cab: CabinetSbPosTemplate? = null
        for (cab1 in list) {
            if (cab1.row == row && cab1.col == col) {
                cab = cab1
                break
            }
        }
        if (cab == null) {
            cab = CabinetSbPosTemplate(0, "", "", substation!!.id, mainControlRoom!!.id, -1, row, col
                    , -1, App.instance.getCurrentUser().name, System.currentTimeMillis(), 1)
            cab.substationToOne.target = substation
            cab.mainControlRoomToOne.target = mainControlRoom
        }
        return cab
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
                    bean?.mainControlRoomToOne?.target = null
                    roomText.text = "点击选择主控室"
                }
            } else if (requestCode == 201) {
                val roomId = data.getLongExtra("chooseId", 0)
                if (roomId == 0L) return
                mainControlRoom = mainStore.getQueryBuilder().equal(MainControlRoom_.id, roomId).build().findUnique()
                if (mainControlRoom != null) {
                    roomText.text = mainControlRoom!!.name
                }
            } else if (requestCode == 202) {
                sbStore.getQueryData(sbStore.getQueryBuilder().equal(CabinetSbPosTemplate_.cabinetId, beanID)
                        .build()) {
                    if (it.isNotEmpty()) {
                        switchBoard.clear()
                        switchBoard.addAll(it)
                        recycleView.adapter?.notifyDataSetChanged()
                    }
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
        return if (saveBean()) {
            bean!!.status = 0
            beanID = databaseStore.getBox().put(bean!!)
            saveSwitchBoardList(0)
            databaseStore.getBox().put(bean!!)
            true
        } else {
            false
        }
    }

    override fun onBackAction() {
        if (beanID == 0L && bean != null && bean!!.status == 1) {
            MaterialDialog.Builder(this)
                    .content("确定不再保存该数据?")
                    .positiveText("确定").onPositive { dialog, _ ->
                        databaseStore.getBox().remove(bean!!)
                        val deleteList = ArrayList<CabinetSbPosTemplate>()
                        for (item in switchBoard) {
                            if (item.id > 0) {
                                deleteList.add(item)
                            }
                        }
                        sbStore.getBox().remove(deleteList)
                        dialog.dismiss()
                        super.onBackAction()
                    }
                    .negativeText("取消")
                    .build().show()

        } else {
            super.onBackAction()
        }
    }

    /**
     * 检测数据完整性，是否可以进行保存
     */
    private fun saveBean(): Boolean {
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
            bean = Cabinet(beanID, name, substation!!.id, mainControlRoom!!.id, rowValue, colValue
                    , App.instance.getCurrentUser().name, System.currentTimeMillis(), 0)
        }
        bean!!.name = name
        bean!!.substationToOne.target = substation!!
        bean!!.mainControlRoomToOne.target = mainControlRoom!!
        return true
    }

    fun saveSwitchBoardList(state: Int) {
        if (switchBoard.isNotEmpty() && bean != null) {
            for (item in switchBoard) {
                item.status = state
                if (state == 0) {
                    if (TextUtils.isEmpty(item.name)) {
                        //保存时候 发现名称没有，临时状态。
                        item.status = 1
                    }
                    bean!!.cabinetSbPosTemplateToMany.add(item)
                }
                item.cabinetId = beanID
                item.cabinetToOne.target = bean
                item.updateTime = System.currentTimeMillis()
            }

            sbStore.getBox().put(switchBoard)
        }
    }

    /**
     * 临时保存
     */
    fun saveBeanTemp(): Boolean {
        bean!!.status = 1
        beanID = databaseStore.getBox().put(bean!!)
        return beanID > 0L
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

    private inner class Adapter(private val content: Context)
        : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(content).inflate(R.layout.item_switch_board, parent, false)
            val icon = view.findViewById<ImageView>(R.id.icon)
            return ViewHolder(view, icon)
        }

        override fun getItemCount(): Int {
            return switchBoard.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (switchBoard[position].status == 0) {
                holder.imageView.setImageDrawable(content.resources.getDrawable(R.drawable.press_plate_b_on))
            } else {
                holder.imageView.setImageDrawable(content.resources.getDrawable(R.drawable.press_plate_b_off))
            }
            holder.itemView.setOnClickListener {
                if (!canEdit) {
                    editSwitchBord(position)
                } else {
                    if (saveBean()) {
                        MaterialDialog.Builder(this@CabinetAddActivity)
                                .content("是否确认屏柜信息，确认之后则无法修改!!")
                                .positiveText("确定")
                                .onPositive { dialog, _ ->
                                    dialog.dismiss()
                                    canEdit = false
                                    setEnableEt()
                                    if (saveBeanTemp()) {
                                        saveSwitchBoardList(1)
                                        editSwitchBord(position)
                                    }
                                }
                                .negativeText("取消")
                                .build().show()
                    }
                }
            }
        }

        private fun editSwitchBord(position: Int) {
            val intent = Intent(this@CabinetAddActivity, SwitchBoardAddActivity::class.java)
            intent.putExtra("ID", switchBoard[position].id)
            startActivityForResult(intent, 202)
        }
    }


    private class ViewHolder(itemView: View, val imageView: ImageView)
        : RecyclerView.ViewHolder(itemView)
}