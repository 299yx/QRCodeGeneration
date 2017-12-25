package com.android.org.qrcode

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    var smallerDimension = 0
    private val mWHITE: Int = 0xFFFFFFFF.toInt()
    private val mBLACK: Int = 0xFF000000.toInt()


    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = manager.defaultDisplay
        val point = Point()
        display.getSize(point)
        val width = point.x
        val height = point.y
        smallerDimension = if (width < height) width else height
        smallerDimension = smallerDimension * 7 / 8

        mEditText.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                val text: String = mEditText.text.toString()
                if (text != "") {
                    Log.d("123", "进入事件")
                    mImageView.setImageBitmap(encode(text))
                }
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

    }

    private fun encode(text: String): Bitmap? {
        if (text == "") {
            return null
        }
        var hints: EnumMap<EncodeHintType, Any> = EnumMap(EncodeHintType::class.java)
        val encoding = guessAppropriateEncoding(text)//返回编码形式
        if (encoding != null) {
            hints.put(EncodeHintType.CHARACTER_SET, encoding)
        }

        var result: BitMatrix
        val format = BarcodeFormat.valueOf(BarcodeFormat.QR_CODE.toString())//调整编码形式

        try {
            result = MultiFormatWriter().encode(text, format, smallerDimension, smallerDimension, hints)
        } catch (i: IllegalArgumentException) {
            return null
        }
        val width = result.width
        val height = result.height
        var pixels: IntArray = kotlin.IntArray(width * height)
        for (i in 0 until height) {
            var offset = i * width
            for (ii in 0 until width) {
                pixels[offset + ii] = if (result.get(i, ii)) mBLACK else mWHITE
            }
        }

        var bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        Log.d("123", "编码完成")
        return bitmap
    }

    private fun guessAppropriateEncoding(str: CharSequence): String? {
        for (i in 0 until str.length) {
            if (str[i].toInt() > 0xff) {
                return "UTF-8"
            }
        }
        return null
    }
}
