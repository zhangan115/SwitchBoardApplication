package com.board.applicion.view.deploy.template

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
import android.widget.ImageView
import android.widget.Toast
import com.board.applicion.R
import com.board.applicion.app.App
import com.board.applicion.base.BaseActivity
import com.board.applicion.mode.DatabaseStore
import com.board.applicion.mode.databases.*
import com.board.applicion.view.deploy.cabinet.CabinetChooseActivity
import com.board.applicion.view.deploy.mainControlRoom.MainControlRoomChooseActivity
import com.board.applicion.view.deploy.substation.SubstationChooseActivity
import kotlinx.android.synthetic.main.activity_template_setting.*

class TemplateSettingActivity : BaseActivity() {

    lateinit var subStore: DatabaseStore<Substation>
    var substation: Substation? = null
    lateinit var mainStore: DatabaseStore<MainControlRoom>
    var mainRoom: MainControlRoom? = null
    lateinit var cabinetStore: DatabaseStore<Cabinet>
    var cabinet: Cabinet? = null
    lateinit var switchBoardStore: DatabaseStore<CabinetSbPosTemplate>

    private var rowValue: Int = 0
    private var colValue: Int = 0

    private val switchBoard = ArrayList<CabinetSbPosTemplate>()

    override fun initView(savedInstanceState: Bundle?) {
        layout_1.setOnClickListener {
            val intent = Intent(this, SubstationChooseActivity::class.java)
            startActivityForResult(intent, 200)
        }
        layout_2.setOnClickListener {
            if (substation == null) {
                Toast.makeText(this, "选择变电站", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, MainControlRoomChooseActivity::class.java)
            intent.putExtra("subId", substation!!.id)
            startActivityForResult(intent, 201)
        }
        layout_3.setOnClickListener {
            if (mainRoom == null) {
                Toast.makeText(this, "选择主控室", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, CabinetChooseActivity::class.java)
            intent.putExtra("subId", mainRoom!!.id)
            startActivityForResult(intent, 202)
        }
        saveUserButton.setOnClickListener {
            if (switchBoard.isNotEmpty()){
                switchBoardStore.getBox().put(switchBoard)
                finish()
            }
        }
    }

    private fun getCurrentData(row: Int, col: Int, list: List<CabinetSbPosTemplate>): CabinetSbPosTemplate? {
        var cab: CabinetSbPosTemplate? = null
        for (cab1 in list) {
            if (cab1.row == row && cab1.col == col) {
                cab = cab1
                break
            }
        }
        return cab
    }

    private fun resultChange() {
        if (substation != null) {
            subName.text = substation!!.name
            substationLevel.text = substation!!.voltageRank
        } else {
            subName.text = ""
            substationLevel.text = ""
        }
        if (mainRoom != null) {
            roomName.text = mainRoom!!.name
        } else {
            roomName.text = ""
        }
        if (cabinet != null) {
            cabinetName.text = cabinet!!.name
            rowValue = cabinet!!.rowNum
            rowText.text = cabinet!!.rowNum.toString()
            colValue = cabinet!!.colNum
            colText.text = cabinet!!.colNum.toString()
            getData()
        } else {
            cabinetName.text = ""
            rowText.text = ""
            colText.text = ""
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
                        mainRoom = null
                        cabinet = null
                    }
                }
                201 -> {
                    mainRoom = mainStore.getBox().query().equal(MainControlRoom_.id, id).build().findUnique()
                    if (mainRoom != null) {
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

    private fun getData() {
        switchBoard.clear()
        recycleView.adapter?.notifyDataSetChanged()
        switchBoardStore.getQueryData(switchBoardStore.getQueryBuilder()
                .equal(CabinetSbPosTemplate_.cabinetId, cabinet!!.id).build()) {
            if (it.isNotEmpty() && it.size == cabinet!!.rowNum * cabinet!!.colNum) {
                //板子配置完整，可以进行模版设置
                //通过行列进行排序
                for (i in 0..rowValue) {
                    for (j in 0..colValue) {
                        val sb = getCurrentData(i, j, it)
                        if (sb != null)
                            switchBoard.add(sb)
                    }
                }
                if (switchBoard.size == it.size) {
                    noDataTv.visibility = View.GONE
                    recycleView.layoutManager = GridLayoutManager(this, colValue)
                    recycleView.adapter?.notifyDataSetChanged()
                } else {
                    noDataTv.visibility = View.VISIBLE
                }
            } else {
                //板子配置不完整
                noDataTv.visibility = View.VISIBLE
            }
        }
    }

    override fun initData() {
        subStore = DatabaseStore(lifecycle, Substation::class.java)
        mainStore = DatabaseStore(lifecycle, MainControlRoom::class.java)
        cabinetStore = DatabaseStore(lifecycle, Cabinet::class.java)
        switchBoardStore = DatabaseStore(lifecycle, CabinetSbPosTemplate::class.java)
        val adapter = Adapter(switchBoard, this)
        recycleView.adapter = adapter
    }

    override fun getContentView(): Int {
        return R.layout.activity_template_setting
    }

    override fun getToolBarTitle(): String? {
        return "模版预置"
    }

    private class Adapter(private val dataList: ArrayList<CabinetSbPosTemplate>, private val content: Context)
        : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(content).inflate(R.layout.item_switch_board, parent, false)
            val icon = view.findViewById<ImageView>(R.id.icon)
            return ViewHolder(view, icon)
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (dataList[position].position == 0) {
                holder.imageView.setImageDrawable(content.resources.getDrawable(R.drawable.press_plate_b_on))
            } else {
                holder.imageView.setImageDrawable(content.resources.getDrawable(R.drawable.press_plate_b_off))
            }
            holder.itemView.setOnClickListener {
                if (dataList[position].position == 0) {
                    dataList[position].position = 1
                } else {
                    dataList[position].position = 0
                }
                if (dataList[position].position == 0) {
                    holder.imageView.setImageDrawable(content.resources.getDrawable(R.drawable.press_plate_b_on))
                } else {
                    holder.imageView.setImageDrawable(content.resources.getDrawable(R.drawable.press_plate_b_off))
                }
            }
        }
    }

    private class ViewHolder(itemView: View, val imageView: ImageView)
        : RecyclerView.ViewHolder(itemView)


}