package com.board.applicion.view.deploy.substation

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.board.applicion.R
import com.board.applicion.app.App
import com.board.applicion.mode.Substation
import com.board.applicion.mode.Substation_
import com.board.applicion.view.deploy.BaseAddActivity
import io.objectbox.query.QueryBuilder
import kotlinx.android.synthetic.main.activity_substation_add.*

class SubstationAddActivity : BaseAddActivity<Substation>() {

    override fun getDataClass(): Class<Substation> {
        return Substation::class.java
    }

    override fun getQueryBuild(): QueryBuilder<Substation> {
        return databaseStore.getQueryBuilder().equal(Substation_.id, beanID)
    }

    override fun setDataToView() {
        if (bean != null) {
            editTextName.setText(bean!!.name)
            rankText.text = bean!!.voltageRank
            desEditText.setText(bean!!.desc)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        rankText.setOnClickListener { _ ->
            //选择电压等级
            MaterialDialog.Builder(this).items(R.array.rankList)
                    .itemsCallback { _, _, _, text ->
                        rankText.text = text
                    }.build().show()
        }
    }

    override fun getSaveButton(): Button {
        return saveUserButton
    }

    override fun canSave(): Boolean {
        val name = editTextName.text.toString()
        val des = desEditText.text.toString()
        val rank = rankText.text
        if (TextUtils.isEmpty(name)) {
            return false
        }
        if (TextUtils.isEmpty(rank)) {
            return false
        }
        if (bean ==null){
            val query = databaseStore.getQueryBuilder().equal(Substation_.name, name).build()
            val userList = query.find()
            if (userList.isNotEmpty()) {
                Toast.makeText(this, "该名称已经使用", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        bean = Substation(beanID, name, rank.toString(), des
                , App.instance.getCurrentUser().realName
                , System.currentTimeMillis(), 0)
        return true
    }

    override fun getToolBarTitle(): String? {
        if (beanID == 0L) return "新增变电站"
        return "修改变电站"
    }

    override fun getContentView(): Int {
        return R.layout.activity_substation_add
    }
}