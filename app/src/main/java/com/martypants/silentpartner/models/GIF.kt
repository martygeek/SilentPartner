package com.martypants.silentpartner.models

import com.bumptech.glide.util.Util

data class GIF(
    val data: List<Data>,
    val meta: Meta,
    val pagination: Pagination
) {
    class GifImage {
        var url: String? = null
        var width = 0
        var height = 0
        override fun hashCode(): Int {
            var result = if (url != null) url.hashCode() else 17
            result = 31 * result + width
            result = 31 * result + height
            return result
        }

        override fun equals(obj: Any?): Boolean {
            if (obj is GifImage) {
                val other = obj
                return other.width == width && other.height == height && Util.bothNullOrEqual(
                    url,
                    other.url
                )
            }
            return false
        }

        override fun toString(): String {
            return "GifImage{url='$url', width=$width, height=$height}"
        }
    }
}