package com.ax.detectivetools.ImageInfo

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ax.detectivetools.Map.MapActivity
import com.ax.detectivetools.R
import kotlinx.android.synthetic.main.activity_image_info.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.IOException
import kotlin.math.pow


class ImageInfoActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private var lat = 0.0
    private var lon = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_info)
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(
                this,
                "该功能需要读文件权限",
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }

        selectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            startActivityForResult(intent, REQUEST_CODE_ALBUM_PIC)
        }

        imageInfo.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("lat", lat)
            intent.putExtra("lon", lon)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ALBUM_PIC && resultCode == RESULT_OK) {
            val imageUri: Uri? = data?.data
            val imagePath = getFilePathFromContentUri(imageUri, this.contentResolver)
            Log.i(TAG, imagePath)
            imagePath?.let { getInfo(it) }
        } else if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                finish()
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(this, "用户授权失败", Toast.LENGTH_SHORT).show()
        /**
         * 若是在权限弹窗中，用户勾选了'NEVER ASK AGAIN.'或者'不在提示'，且拒绝权限。
         * 这时候，需要跳转到设置界面去，让用户手动开启。
         */
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(this, "用户授权成功", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun getInfo(path: String) {
        Log.i(TAG, path)
        try {
            val exifInterface = ExifInterface(path)
            val guangquan = exifInterface.getAttribute(ExifInterface.TAG_APERTURE)
            val shijain = exifInterface.getAttribute(ExifInterface.TAG_DATETIME)
            val baoguangshijian =
                exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_TIME)
            val jiaoju = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH)
            val chang = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH)
            val kuan = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH)
            val moshi = exifInterface.getAttribute(ExifInterface.TAG_MODEL)
            val zhizaoshang = exifInterface.getAttribute(ExifInterface.TAG_MAKE)
            val iso = exifInterface.getAttribute(ExifInterface.TAG_ISO)
            val jiaodu = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION)
            val baiph = exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE)
            val altitude_ref =
                exifInterface.getAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF)
            val altitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_ALTITUDE)
            val latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
            val latitude_ref =
                exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
            val longitude_ref =
                exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)
            val longitude =
                exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)
            val timestamp =
                exifInterface.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP)
            val processing_method =
                exifInterface.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD)

            //转换经纬度格式
            lat = convert2Decimal(latitude)
            lon = convert2Decimal(longitude)
            val stringBuilder = StringBuilder()
            stringBuilder.append("光圈 = $guangquan\n")
                .append("时间 = $shijain\n")
                .append("曝光时长 = $baoguangshijian\n")
                .append("焦距 = $jiaoju\n")
                .append("长 = $chang\n")
                .append("宽 = $kuan\n")
                .append("型号 = $moshi\n")
                .append("制造商 = $zhizaoshang\n")
                .append("ISO = $iso\n")
                .append("角度 = $jiaodu\n")
                .append("白平衡 = $baiph\n")
                .append("海拔高度 = $altitude_ref\n")
                .append("GPS参考高度 = $altitude\n")
                .append("GPS时间戳 = $timestamp\n")
                .append("GPS定位类型 = $processing_method\n")
                .append("GPS纬度 = $lat$latitude_ref\n")
                .append("GPS经度 = $lon$longitude_ref\n")

            //将获取的到的信息设置到TextView上
            imageInfo.text = stringBuilder.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getFilePathFromContentUri(
        selectedVideoUri: Uri?,
        contentResolver: ContentResolver
    ): String? {
        val filePath: String
        val filePathColumn = arrayOf(MediaColumns.DATA)
        val cursor: Cursor =
            contentResolver.query(selectedVideoUri, filePathColumn, null, null, null)
        cursor.moveToFirst()
        val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
        filePath = cursor.getString(columnIndex)
        cursor.close()
        return filePath
    }

    private fun convert2Decimal(string: String?): Double {
        var dimensionality = 0.0
        if (null == string) {
            return dimensionality
        }
        Log.i(TAG, string)

        //用,将数值分成3份
        val split = string.split(",").toTypedArray()
        for (i in split.indices) {
            val s = split[i].split("/").toTypedArray()
            //用114/1得到度分秒数值
            val v = s[0].toDouble() / s[1].toDouble()
            //将分秒分别除以60和3600得到度，并将度分秒相加
            dimensionality += v / 60.0.pow(i.toDouble())
        }
        return dimensionality
    }

    companion object {
        private const val TAG = "ImageInfoActivity"
        private const val REQUEST_CODE_ALBUM_PIC = 1000

        private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2000
    }

}