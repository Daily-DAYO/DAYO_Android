package com.daily.dayo.common

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

fun Uri.toBitmap(contentResolver: ContentResolver): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, this))
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, this)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

fun Bitmap?.toFile(path: String): File {
    val file = File(path)
    var out: OutputStream? = null
    try {
        file.createNewFile()
        out = FileOutputStream(file)
        this?.compress(Bitmap.CompressFormat.JPEG, 100, out)
    } finally {
        out?.close()
    }
    return file
}