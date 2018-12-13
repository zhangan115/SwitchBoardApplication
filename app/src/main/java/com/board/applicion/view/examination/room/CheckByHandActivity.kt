package com.board.applicion.view.examination.room

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.afollestad.materialdialogs.MaterialDialog
import com.board.applicion.R
import com.board.applicion.base.BaseActivity
import com.board.applicion.mode.DatabaseStore
import com.board.applicion.mode.databases.CabinetSbPosCkRst
import com.board.applicion.mode.databases.CabinetSbPosCkRst_
import com.board.applicion.mode.databases.SbPosCjRstDetail
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_check_hand.*

class CheckByHandActivity : BaseActivity() {

    private lateinit var cabinetSbPosCkRstStore: DatabaseStore<CabinetSbPosCkRst>//核查记录保存
    private lateinit var sbPosCjRstDetailStore: DatabaseStore<SbPosCjRstDetail>//核查结果保存
    private var cabinetSbPosCkRst: CabinetSbPosCkRst? = null//检查记录
    private val sbCheckData = ArrayList<SbPosCjRstDetail>()//检查结果集合

    override fun initView(savedInstanceState: Bundle?) {
        saveButton.setOnClickListener {
            var notMatchCount = 0
            cabinetSbPosCkRst!!.status = 0
            cabinetSbPosCkRst!!.checkTime = System.currentTimeMillis()
            cabinetSbPosCkRst!!.updateTime = System.currentTimeMillis()
            for (sb in sbCheckData) {
                if (sb.posMatch == 1) {
                    notMatchCount++
                }
                sb.status = 0
                sb.checkTime = System.currentTimeMillis()
                sb.updateTime = System.currentTimeMillis()
                sb.cabinetSbPosCkRstToOne.target = cabinetSbPosCkRst
            }
            if (notMatchCount == 0) {
                cabinetSbPosCkRstStore.getBox().put(cabinetSbPosCkRst!!)
                sbPosCjRstDetailStore.getBox().put(this.sbCheckData)
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                MaterialDialog.Builder(this)
                        .content("当前有异常结果，是否保存?")
                        .negativeText("否")
                        .positiveText("是").onPositive { _, _ ->
                            cabinetSbPosCkRst!!.checkResult = 1
                            cabinetSbPosCkRstStore.getBox().put(cabinetSbPosCkRst!!)
                            sbPosCjRstDetailStore.getBox().put(this.sbCheckData)
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                        .build().show()
            }
        }
    }

    override fun initData() {
        cabinetSbPosCkRstStore = DatabaseStore(lifecycle, CabinetSbPosCkRst::class.java)
        sbPosCjRstDetailStore = DatabaseStore(lifecycle, SbPosCjRstDetail::class.java)
        val id = intent.getLongExtra("id", 0)
        cabinetSbPosCkRst = cabinetSbPosCkRstStore.getQueryBuilder().equal(CabinetSbPosCkRst_.id, id).build().findUnique()
        Glide.with(this).load(cabinetSbPosCkRst!!.posImage).into(photoImage)
        sbRecycleView.layoutManager = GridLayoutManager(this, cabinetSbPosCkRst!!.cabinetToOne.target.colNum)
        sbCheckData.clear()
        sbCheckData.addAll(cabinetSbPosCkRst!!.sbPosCjRstDetailToMany!!)
        sbRecycleView.adapter = CheckAdapter(sbCheckData, this)
    }

    override fun getContentView(): Int {
        return R.layout.activity_check_hand
    }

    override fun getToolBarTitle(): String? {
        return "人工核查"
    }

    override fun onBackAction() {
        if (cabinetSbPosCkRst != null) {
            MaterialDialog.Builder(this).content("确定放弃当前数据?")
                    .negativeText("取消").onNegative { dialog, _ -> dialog.dismiss() }
                    .positiveText("确定").onPositive { dialog, _ ->
                        //将临时保存的数据删除掉
                        dialog.dismiss()
                        setResult(Activity.RESULT_CANCELED)
                        super.onBackAction()
                    }.build().show()
        } else {
            super.onBackAction()
        }
    }

    private class CheckAdapter(private val dataList: ArrayList<SbPosCjRstDetail>, private val content: Context)
        : RecyclerView.Adapter<CheckViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckViewHolder {
            val view = LayoutInflater.from(content).inflate(R.layout.item_switch_board, parent, false)
            val icon = view.findViewById<ImageView>(R.id.icon)
            return CheckViewHolder(view, icon)
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        override fun onBindViewHolder(holder: CheckViewHolder, position: Int) {
            itemChange(dataList[position].posMatch, holder.imageView)
            holder.imageView.setOnClickListener {
                var match = dataList[position].posMatch
                ++match
                if (match > 1) {
                    match = 0
                }
                dataList[position].posMatch = match
                itemChange(match, holder.imageView)
            }
        }

        private fun itemChange(type: Int, imageView: ImageView) {
            when (type) {
                0 -> imageView.setImageDrawable(content.resources.getDrawable(R.drawable.press_plate__off_success))
                else -> imageView.setImageDrawable(content.resources.getDrawable(R.drawable.press_plate__on_fail))
            }
        }
    }

    private class CheckViewHolder(itemView: View, val imageView: ImageView)
        : RecyclerView.ViewHolder(itemView)
}