package com.martypants.silentpartner.Gfx

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.martypants.silentpartner.models.GIF

/**
 * Created by Martin Rehder on 2020-01-26.
 */
public class DisplayGif(val context: Context, val imageView: ImageView) {


    fun showGifData(data: GIF) {
        val thumbnailRequest: RequestBuilder<Drawable> =
            GlideApp.with(context).load(data).decode(
                Bitmap::class.java
            )

        GlideApp.with(context)
            .load(data.data[0].images.original.url)
            .thumbnail(thumbnailRequest)
            .listener( object :
                RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any,
                    target: Target<Drawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
            .into(imageView)


    }

}