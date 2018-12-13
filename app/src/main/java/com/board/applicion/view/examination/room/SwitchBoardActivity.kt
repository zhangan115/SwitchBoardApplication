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
import android.text.TextUtils
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
import com.example.xty.ndkdemo.java2c
import com.soundcloud.android.crop.Crop
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_switch_board.*
import java.io.File

class SwitchBoardActivity : BaseActivity() {

    private lateinit var cabinetStore: DatabaseStore<Cabinet>
    private lateinit var sbTemplateStore: DatabaseStore<CabinetSbPosTemplate>
    private val cabinetTemplateData = ArrayList<CabinetSbPosTemplate>()//模板数据
    private var cabinet: Cabinet? = null//屏柜

    private var cabinetSbPosCkRst: CabinetSbPosCkRst? = null//检查记录
    private val sbCheckData = ArrayList<SbPosCjRstDetail>()//检查结果集合
    private var photoFile: File? = null//文件
    private var photoPath: String? = null//图片地址
    private var isCheck = false//是否检查
    private var isChecking = false//正在检查
    private var checkDis: Disposable? = null

    private lateinit var cabinetSbPosCkRstStore: DatabaseStore<CabinetSbPosCkRst>//核查记录保存
    private lateinit var sbPosCjRstDetailStore: DatabaseStore<SbPosCjRstDetail>//核查结果保存


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
        cabinetSbPosCkRstStore = DatabaseStore(lifecycle, CabinetSbPosCkRst::class.java)
        sbPosCjRstDetailStore = DatabaseStore(lifecycle, SbPosCjRstDetail::class.java)
    }


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
            if (!TextUtils.isEmpty(photoPath) && isCheck) {
                //to save
                if (cabinet != null && cabinetSbPosCkRst != null) {
                    var isPass = true

                    cabinetSbPosCkRst!!.status = 0
                    cabinetSbPosCkRst!!.checkTime = System.currentTimeMillis()
                    cabinetSbPosCkRst!!.updateTime = System.currentTimeMillis()
                    for (sb in sbCheckData) {
                        sb.status = 0
                        sb.checkTime = System.currentTimeMillis()
                        sb.updateTime = System.currentTimeMillis()
                        if (sb.posMatch != 1) {
                            isPass = false
                        }
                        sb.cabinetSbPosCkRstToOne.target = cabinetSbPosCkRst
                    }
                    if (isPass) {
                        cabinetSbPosCkRstStore.getBox().put(cabinetSbPosCkRst!!)
                        sbPosCjRstDetailStore.getBox().put(this.sbCheckData)
                        finish()
                    } else {
                        MaterialDialog.Builder(this)
                                .content("当前有异常结果，是否保存?")
                                .negativeText("否")
                                .positiveText("是").onPositive { _, _ ->
                                    cabinetSbPosCkRstStore.getBox().put(cabinetSbPosCkRst!!)
                                    sbPosCjRstDetailStore.getBox().put(this.sbCheckData)
                                    finish()
                                }
                                .build().show()
                    }

                }
            }
        }
        showTempData.setOnClickListener { _ ->
            if (isCheck) {
                if (TextUtils.isEmpty(photoPath) || isChecking) {
                    return@setOnClickListener
                }
                //to handle 人工核查
                if (cabinet == null) return@setOnClickListener
                val intent = Intent(this, CheckByHandActivity::class.java)
                intent.putExtra("id", cabinetSbPosCkRst!!.id)
                startActivityForResult(intent, 203)
            } else {
                //show temp 展示模版
                tempLayout.visibility = View.VISIBLE
            }
        }
        closeTempLayout.setOnClickListener {
            tempLayout.visibility = View.GONE
        }
    }

    override fun onBackAction() {
        if (isCheck && cabinetSbPosCkRst != null) {
            MaterialDialog.Builder(this).content("确定放弃当前数据?")
                    .negativeText("取消").onNegative { dialog, _ -> dialog.dismiss() }
                    .positiveText("确定").onPositive { dialog, _ ->
                        //将临时保存的数据删除掉
                        cabinetSbPosCkRstStore.getBox().remove(cabinetSbPosCkRst!!)
                        sbPosCjRstDetailStore.getBox().remove(sbCheckData)
                        dialog.dismiss()
                        super.onBackAction()
                    }.build().show()
        } else {
            super.onBackAction()
        }
    }

    /**
     * 展示模板
     */
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

    /**
     * 获取当前位置的数据
     */
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

    /**
     * 检查结果
     */
    private fun checkPhoto() {
        val observable = Observable.create<String> {
            try {
                val j2c = java2c()
                val result = j2c.getResult(photoPath)
                if (!TextUtils.isEmpty(result)) {
                    it.onNext(result)
                } else {
                    it.onError(Throwable("检查中出现问题!"))
                }
            } catch (e: Exception) {
                it.onError(e.fillInStackTrace())
                it.onComplete()
            } finally {
                it.onComplete()
            }
        }
        checkDis = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val rowCount = cabinet!!.rowNum.toString().length
                    val colCount = cabinet!!.colNum.toString().length
                    val result: String
                    if (it.startsWith(cabinet!!.rowNum.toString() + cabinet!!.colNum.toString())) {
                        result = it.substring(rowCount + colCount)
                        if (result.length != cabinet!!.rowNum * cabinet!!.colNum) {
                            var currentPosition = 0
                            if (cabinet != null) {
                                sbCheckData.clear()
                                for (i in 1..cabinet!!.rowNum) {
                                    for (j in 1..cabinet!!.colNum) {
                                        val sb = getCurrentData(i, j, cabinetTemplateData)
                                        if (sb != null) {
                                            sbCheckData.add(SbPosCjRstDetail(0, sb.subId, sb.mcrId, sb.cabinetId
                                                    , sb.id, System.currentTimeMillis(), sb.name, sb.desc, sb.row, sb.col
                                                    , result[currentPosition].toInt(), App.instance.getCurrentUser().name
                                                    , System.currentTimeMillis(), 1))// 读取出数据
                                        }
                                        currentPosition++
                                    }
                                }
                                cabinetSbPosCkRst = CabinetSbPosCkRst(0, cabinet!!.subId, cabinet!!.mcrId, cabinet!!.id
                                        , System.currentTimeMillis(), photoPath, "", App.instance.getCurrentUser().name
                                        , System.currentTimeMillis(), 1)//读取出本次核查记录(临时保存 status 为1 正式保存后为0)
                                for (sb in sbCheckData) {
                                    sb.cabinetSbPosCkRstToOne.target = cabinetSbPosCkRst
                                }
                                cabinetSbPosCkRst!!.substationToOne.target = cabinet!!.substationToOne.target
                                cabinetSbPosCkRst!!.mainControlRoomToOne.target = cabinet!!.mainControlRoomToOne.target
                                cabinetSbPosCkRst!!.cabinetToOne.target = cabinet
                                cabinetSbPosCkRst!!.sbPosCjRstDetailToMany.addAll(sbCheckData)

                                val rstId = cabinetSbPosCkRstStore.getBox().put(cabinetSbPosCkRst!!)
                                cabinetSbPosCkRst!!.id = rstId
                                sbPosCjRstDetailStore.getBox().put(sbCheckData)
                            }
                        }
                        if (sbCheckData.isNotEmpty()) {
                            isChecking = false
                            isCheck = true
                            showPhoto.visibility = View.GONE
                            showResultLayout.visibility = View.VISIBLE
                            sbRecycleView.adapter?.notifyDataSetChanged()
                        } else {
                            isChecking = false
                            isCheck = true
                        }
                        updateState()
                    }
                }, {
                    isChecking = false
                    isCheck = false
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    updateState()
                }, {

                })
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
                203 -> {
                    finish()
                }
            }
        }
    }

    private fun beginCrop(source: Uri) {
        val destination = Uri.fromFile(File(this.cacheDir, "${System.currentTimeMillis()}.jpg"))
        Crop.of(source, destination).start(this)
    }

    private fun handleCrop(resultCode: Int, result: Intent) {
        if (resultCode == RESULT_OK) {
            val uri = Crop.getOutput(result)
            Glide.with(this).load(uri).into(showPhoto)
            photoPath = uri.path
            photoFile = File(photoPath)
            showPhoto.visibility = View.VISIBLE
            takePhoto.visibility = View.GONE
            updateState()
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).message, Toast.LENGTH_SHORT).show()
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
            itemChange(dataList[position].posMatch, holder.imageView)
        }

        private fun itemChange(type: Int, imageView: ImageView) {
            when (type) {
                0 -> imageView.setImageDrawable(content.resources.getDrawable(R.drawable.press_plate__off_success))
                1 -> imageView.setImageDrawable(content.resources.getDrawable(R.drawable.press_plate__on_fail))
                else -> imageView.setImageDrawable(content.resources.getDrawable(R.drawable.press_plate_b_off))
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