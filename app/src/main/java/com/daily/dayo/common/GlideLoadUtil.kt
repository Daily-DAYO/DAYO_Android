package com.daily.dayo.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.daily.dayo.R

object GlideLoadUtil {
    private const val BASE_URL_IMG = "http://117.17.198.45:8080/images/"
    const val HOME_POST_THUMBNAIL_SIZE = 158
    const val HOME_USER_THUMBNAIL_SIZE = 17

    fun loadImageView(
        requestManager: RequestManager,
        width: Int,
        height: Int,
        img: Bitmap,
        imgView: ImageView,
        placeholderImg: Int? = null,
        errorImg: Int? = null,
        placeholderLottie: LottieAnimationView?= null
    ) {
        requestManager.load(img)
            .override(width, height)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    placeholderLottie?.let { lottieView ->
                        lottieView.visibility = View.GONE
                    }
                    return false
                }
            })
            .thumbnail(0.1f)
            .placeholder(placeholderImg ?: R.color.gray_3_9FA5AE)
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
        val requestOption =
            if (placeholderImg != null && errorImg != null) {
                RequestOptions()
                    .placeholder(placeholderImg)
                    .error(errorImg)
            } else if (placeholderImg != null) {
                RequestOptions().placeholder(placeholderImg)
            } else if (errorImg != null) {
                RequestOptions().error(errorImg)
            } else {
                RequestOptions()
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
        val requestOption =
            if (placeholderImg != null && errorImg != null) {
                RequestOptions()
                    .placeholder(placeholderImg)
                    .error(errorImg)
            } else if (placeholderImg != null) {
                RequestOptions().placeholder(placeholderImg)
            } else if (errorImg != null) {
                RequestOptions().error(errorImg)
            } else {
                RequestOptions()
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
            .placeholder(placeholderImg ?: R.color.gray_3_9FA5AE)
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
        val requestOption =
            if (placeholderImg != null && errorImg != null) {
                RequestOptions()
                    .placeholder(placeholderImg)
                    .error(errorImg)
            } else if (placeholderImg != null) {
                RequestOptions().placeholder(placeholderImg)
            } else if (errorImg != null) {
                RequestOptions().error(errorImg)
            } else {
                RequestOptions()
            }

        return requestManager.asBitmap()
            .override(width, height)
            .load("$BASE_URL_IMG${imgName}")
            .apply(requestOption)
            .submit()
            .get()
    }

    fun loadImagePreload(requestManager: RequestManager, width: Int, height: Int, imgName: String) {
        requestManager.asBitmap()
            .load("$BASE_URL_IMG${imgName}")
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .preload(width, height)
    }
}