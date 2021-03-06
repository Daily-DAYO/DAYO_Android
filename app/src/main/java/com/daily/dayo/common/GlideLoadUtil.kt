package com.daily.dayo.common

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.daily.dayo.R

object GlideLoadUtil {
    private const val BASE_URL_IMG = "http://117.17.198.45:8080/images/"

    fun loadImageView(
        requestManager: RequestManager,
        width: Int,
        height: Int,
        img: Bitmap,
        imgView: ImageView,
        placeholderImg: Int? = null,
        errorImg: Int? = null
    ) {
        requestManager.load(img)
            .override(width, height)
            .thumbnail(0.1f)
            .placeholder(placeholderImg ?: R.color.gray_3_9C9C9C_alpha_30)
            .error(errorImg ?: R.drawable.ic_dayo_circle_grayscale)
            .priority(Priority.HIGH)
            .centerCrop()
            .into(imgView)
    }

    fun loadImageViewProfile(
        requestManager: RequestManager,
        width: Int,
        height: Int,
        img: Bitmap,
        imgView: ImageView,
        placeholderImg: Int? = null,
        errorImg: Int? = null
    ) {
        val requestOption = RequestOptions()
        if (placeholderImg != null) requestOption.placeholder(placeholderImg)
        if (errorImg != null) requestOption.error(errorImg)
        if (placeholderImg != null && errorImg != null) {
            requestOption.placeholder(placeholderImg)
                .error(errorImg)
        }

        requestManager.load(img)
            .override(width, height)
            .apply(requestOption)
            .centerCrop()
            .into(imgView)
    }

    fun loadImageBackground(
        context: Context,
        width: Int,
        height: Int,
        imgName: String,
        placeholderImg: Int? = null,
        errorImg: Int? = null
    ): Bitmap {
        val requestOption = RequestOptions()
        if (placeholderImg != null) requestOption.placeholder(placeholderImg)
        if (errorImg != null) requestOption.error(errorImg)
        if (placeholderImg != null && errorImg != null) {
            requestOption.placeholder(placeholderImg)
                .error(errorImg)
        }

        return Glide.with(context)
            .asBitmap()
            .override(width, height)
            .load("$BASE_URL_IMG${imgName}")
            .apply(requestOption)
            .priority(Priority.HIGH)
            .submit()
            .get()
    }

    fun loadImageBackground(
        requestManager: RequestManager,
        width: Int,
        height: Int,
        imgName: String,
        placeholderImg: Int? = null,
        errorImg: Int? = null
    ): Bitmap {
        return requestManager.asBitmap()
            .override(width, height)
            .placeholder(placeholderImg ?: R.color.gray_3_9C9C9C_alpha_30)
            .error(errorImg ?: R.drawable.ic_dayo_circle_grayscale)
            .load("$BASE_URL_IMG${imgName}")
            .priority(Priority.HIGH)
            .submit()
            .get()
    }

    fun loadImageBackgroundProfile(
        requestManager: RequestManager,
        width: Int,
        height: Int,
        imgName: String,
        placeholderImg: Int? = null,
        errorImg: Int? = null
    ): Bitmap {
        val requestOption = RequestOptions()
        if (placeholderImg != null) requestOption.placeholder(placeholderImg)
        if (errorImg != null) requestOption.error(errorImg)
        if (placeholderImg != null && errorImg != null) {
            requestOption.placeholder(placeholderImg)
                .error(errorImg)
        }

        return requestManager.asBitmap()
            .override(width, height)
            .load("$BASE_URL_IMG${imgName}")
            .apply(requestOption)
            .submit()
            .get()
    }

    fun loadImagePreload(requestManager: RequestManager, width: Int, height: Int, imgName: String) {
        requestManager.load("$BASE_URL_IMG${imgName}").preload(width, height)
    }
}