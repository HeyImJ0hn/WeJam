package dam.a47471.wejam.utils

import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

object ViewBinding {

    @JvmStatic
    @BindingAdapter("circleImage")
    fun bindCircleImage(view: CircleImageView, url: String) {
        Glide.with(view.context)
            .load(url)
            .into(view)
    }

    @JvmStatic
    @BindingAdapter("appCompatImage")
    fun bindAppCompatImage(imageView: AppCompatImageView, imageUrl: String?) {
        Glide.with(imageView.context)
            .load(imageUrl)
            .into(imageView)
    }
}