package pharmlane.com.PharmLaneStore.activities

import android.os.Bundle
import android.view.View
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import pharmlane.com.PharmLaneStore.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_view_photo_id.*


import android.graphics.drawable.Drawable
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target



class ViewPhotoIdActivity : AppCompatActivity(), View.OnClickListener {

    var photo_id:String?  = null

 //   lateinit var imageView:AppCompatImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_photo_id)

        img_back.setOnClickListener(this)

     //   imageView = findViewById(R.id.imageView)

        if (intent != null) {
            if (intent.getStringExtra("photo_id") != null) {

                photo_id = intent.getStringExtra("photo_id")
            }
        }

        Glide.with(this)
                .load(photo_id)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(@Nullable e: GlideException?, model: Any?, target: Target<Drawable?>, isFirstResource: Boolean): Boolean {
                        pb_image.setVisibility(View.GONE)
                        return false
                    }

                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable?>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        pb_image.setVisibility(View.GONE)
                        return false
                    }
                })
                .into(imageView)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_back -> {
                finish()
            }
        }
    }
}