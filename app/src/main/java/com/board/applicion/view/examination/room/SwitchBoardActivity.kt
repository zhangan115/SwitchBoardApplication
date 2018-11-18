package com.board.applicion.view.examination.room

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.board.applicion.R
import com.board.applicion.app.App
import com.board.applicion.base.BaseActivity
import com.board.applicion.mode.DatabaseStore
import com.board.applicion.mode.databases.*
import com.bumptech.glide.Glide
import com.soundcloud.android.crop.Crop
import io.objectbox.android.AndroidScheduler
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_switch_board.*
import java.io.File
import java.util.concurrent.TimeUnit

class SwitchBoardActivity : BaseActivity() {

    private lateinit var cabinetStore: DatabaseStore<Cabinet>
    private lateinit var sbTemplateStore: DatabaseStore<CabinetSbPosTemplate>
    private val cabinetTemplateData = ArrayList<CabinetSbPosTemplate>()
    private var cabinet: Cabinet? = null

    private lateinit var cabinetSbPosCkRst: CabinetSbPosCkRst
    private val sbCheckData = ArrayList<SbPosCjRstDetail>()
    private var photoFile: File? = null
    private var isCheck = false
    private var isChecking = false

    override fun initView(savedInstanceState: Bundle?) {
        showPhoto.setOnClickListener {
            showTakePhotoChoose()
        }
        takePhoto.setOnClickListener {
            showTakePhotoChoose()
        }
        updateState()
        saveUserButton.setOnClickListener {
            if (photoFile == null && !isCheck) {
                return@setOnClickListener
            }
            if (photoFile != null && !isCheck) {
                //to check
                if (isChecking) {
                    return@setOnClickListener
                }
                isChecking = true
                updateState()
                checkPhoto()
                return@setOnClickListener
            }
            if (photoFile != null && isCheck) {
                //to save
                if (cabinet != null) {
                    cabinetSbPosCkRst = CabinetSbPosCkRst(0, cabinet!!.subId, cabinet!!.mcrId, cabinet!!.id
                            , System.currentTimeMillis(), photoFile!!.absolutePath, "", App.instance.getCurrentUser().name
                            , System.currentTimeMillis(), 0)
                    for (sb in sbCheckData) {
                        sb.cabinetSbPosCkRstToOne.target = cabinetSbPosCkRst
                    }
                    cabinetSbPosCkRst.sbPosCjRstDetailToMany.addAll(sbCheckData)
                    val cabinetSbPosCkRstStore = DatabaseStore(lifecycle, CabinetSbPosCkRst::class.java)
                    val sbPosCjRstDetailStore = DatabaseStore(lifecycle, SbPosCjRstDetail::class.java)
                    cabinetSbPosCkRstStore.getBox().put(cabinetSbPosCkRst)
                    sbPosCjRstDetailStore.getBox().put(this.sbCheckData)
                    finish()
                }
            }
        }
        showTempData.setOnClickListener { _ ->
            if (isCheck) {
                if (photoFile == null || !isChecking) {
                    return@setOnClickListener
                }
                //to handle 人工核查

            } else {
                //show temp 展示模版
                tempLayout.visibility = View.VISIBLE
            }
        }
        closeTempLayout.setOnClickListener {
            tempLayout.visibility = View.GONE
        }
    }

    private fun showTemp() {
        if (cabinet != null) {
            val adapter = Adapter(cabinetTemplateData, this)
            tempRecycleView.layoutManager = GridLayoutManager(this, cabinet!!.colNum)
            tempRecycleView.adapter = adapter
            if (cabinetTemplateData.isEmpty()) {
                sbTemplateStore.getQueryData(sbTemplateStore.getQueryBuilder()
                        .equal(CabinetSbPosTemplate_.cabinetId, cabinet!!.id).build()) {
                    if (it.isNotEmpty() && it.size == cabinet!!.rowNum * cabinet!!.colNum) {
                        cabinetTemplateData.clear()
                        for (i in 1..cabinet!!.rowNum) {
                            for (j in 1..cabinet!!.colNum) {
                                val sb = getCurrentData(i, j, it)
                                if (sb != null)
                                    cabinetTemplateData.add(sb)
                            }
                        }
                        tempRecycleView.adapter?.notifyDataSetChanged()
                    }
                }
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

    private fun checkPhoto() {
        val dis = Observable.just("Test")
                .delay(3, TimeUnit.SECONDS)
                .subscribe {
                    isChecking = false
                    isCheck = true
                    if (cabinet != null) {
                        sbCheckData.clear()
                        for (i in 1..cabinet!!.rowNum) {
                            for (j in 1..cabinet!!.colNum) {
                                val sb = getCurrentData(i, j, cabinetTemplateData)
                                if (sb != null) {
                                    sbCheckData.add(SbPosCjRstDetail(0, sb.subId, sb.mcrId, sb.cabinetId
                                            , sb.id, System.currentTimeMillis(), sb.name, sb.desc, sb.row, sb.col
                                            , 0, App.instance.getCurrentUser().name
                                            , System.currentTimeMillis(), 0))
                                }
                            }
                        }
                        runOnUiThread {
                            if (sbCheckData.isNotEmpty()){
                                showPhoto.visibility = View.GONE
                                sbRecycleView.adapter?.notifyDataSetChanged()
                            }
                        }
                    }
                    updateState()
                }
    }

    private fun showTakePhotoChoose() {
        if (isChecking) return
        MaterialDialog.Builder(this)
                .items(R.array.choose_photo)
                .itemsCallback { _, _, position, _ ->
                    if (position == 0) {
                        photoFile = File(App.instance.getPhotoDir(), System.currentTimeMillis().toString() + ".jpg")
                        val intent = Intent()
                        intent.action = MediaStore.ACTION_IMAGE_CAPTURE
                        intent.addCategory(Intent.CATEGORY_DEFAULT)
                        val contentValues = ContentValues(1)
                        contentValues.put(MediaStore.Images.Media.DATA, photoFile!!.absolutePath)
                        val uri = applicationContext.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                        startActivityForResult(intent, 200)
                    } else {
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.type = "image/*"
                        startActivityForResult(intent, 201)
                    }
                }
                .show()
    }

    private fun updateState() {
        if (photoFile != null && !isCheck && !isChecking) {
            //take photo
            saveUserButton.text = "检查"
            saveUserButton.background = findDrawable(R.drawable.button_check)
        } else if (photoFile != null && !isCheck && isChecking) {
            saveUserButton.text = "检查中"
            saveUserButton.background = findDrawable(R.drawable.button_check)
        } else if (photoFile != null && isCheck) {
            //to save
            saveUserButton.text = "保存"
            saveUserButton.background = findDrawable(R.drawable.button_selector)
        } else {
            //disable
            saveUserButton.text = "保存"
            saveUserButton.background = findDrawable(R.drawable.button_disable)
        }
        if (!isCheck) {
            showTempData.text = "模版"
        } else {
            showTempData.text = "人工核查"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                200 -> {
                    val photoIn = Uri.fromFile(photoFile)
                    beginCrop(photoIn)
                }
                201 -> beginCrop(data!!.data!!)
                Crop.REQUEST_CROP -> handleCrop(resultCode, data!!)
            }
        }
    }

    private fun beginCrop(source: Uri) {
        val destination = Uri.fromFile(File(this.cacheDir, "${System.currentTimeMillis()}.jpg"))
        Crop.of(source, destination).asSquare().start(this)
    }

    private fun handleCrop(resultCode: Int, result: Intent) {
        if (resultCode == RESULT_OK) {
            val uri = Crop.getOutput(result)
            Glide.with(this).load(uri).into(showPhoto)
            photoFile = File(uri.encodedPath)
            Log.d("za", "photo place is ===>${photoFile!!.absolutePath}")
            showPhoto.visibility = View.VISIBLE
            takePhoto.visibility = View.GONE
            updateState()
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun initData() {
        val id = intent.getLongExtra("id", -1)
        cabinetStore = DatabaseStore(lifecycle, Cabinet::class.java)
        cabinetStore.getQueryData(cabinetStore.getQueryBuilder().equal(Cabinet_.id, id).build()) {
            if (it.isNotEmpty() && it.size == 1) {
                this.cabinet = it[0]
                text1.text = it[0].substationToOne.target.name
                text11.text = it[0].substationToOne.target.name
                text2.text = it[0].substationToOne.target.voltageRank
                text22.text = it[0].substationToOne.target.voltageRank
                text3.text = it[0].mainControlRoomToOne.target.name
                text33.text = it[0].mainControlRoomToOne.target.name
                text4.text = it[0].name
                text44.text = it[0].name
                text5.text = "${it[0].rowNum} X ${it[0].colNum}"
                text55.text = "${it[0].rowNum} X ${it[0].colNum}"
                sbRecycleView.layoutManager = GridLayoutManager(this, cabinet!!.colNum)
                val adapter = CheckAdapter(sbCheckData, this)
                sbRecycleView.adapter = adapter
                showTemp()
            }
        }
        sbTemplateStore = DatabaseStore(lifecycle, CabinetSbPosTemplate::class.java)
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
        }
    }

    private class ViewHolder(itemView: View, val imageView: ImageView)
        : RecyclerView.ViewHolder(itemView)

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
            if (dataList[position].posMatch == 0) {
                holder.imageView.setImageDrawable(content.resources.getDrawable(R.drawable.press_plate_b_on))
            } else {
                holder.imageView.setImageDrawable(content.resources.getDrawable(R.drawable.press_plate_b_off))
            }
        }
    }

    private class CheckViewHolder(itemView: View, val imageView: ImageView)
        : RecyclerView.ViewHolder(itemView)

    override fun getContentView(): Int {
        return R.layout.activity_switch_board
    }

    override fun getToolBarTitle(): String? {
        return intent.getStringExtra("title")
    }
}