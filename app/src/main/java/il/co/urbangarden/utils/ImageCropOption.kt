package il.co.urbangarden.utils

import com.bumptech.glide.request.RequestOptions

enum class ImageCropOption {
    NONE {
        override fun getGlideTransform() = RequestOptions.noTransformation()
    },
    CIRCLE {
        override fun getGlideTransform() = RequestOptions.circleCropTransform()
    },
    SQUARE {
        override fun getGlideTransform() = RequestOptions.centerCropTransform()
    };

    abstract fun getGlideTransform(): RequestOptions
}