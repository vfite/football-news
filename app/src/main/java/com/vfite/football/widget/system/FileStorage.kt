package com.vfite.football.widget.system

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.vfite.football.models.NewsItem
import timber.log.Timber
import java.io.*

class FileStorage(val context: Context) {

    companion object {
        const val MAIN_DIR = "data"
        const val FILE_NAME = "news_feed.txt"
    }

    private fun writeImage(newsItem: NewsItem, onSuccess: (String?) -> Unit) {
        Timber.d("writeImage: ${newsItem.imageSmall}")
        Glide.with(context)
            .asBitmap()
            .load(newsItem.imageSmall)
            .into(object: CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val image = saveImage(resource, context)
                    onSuccess(image)
                }
            })
    }

    private fun saveImage(image: Bitmap, context: Context): String? {
        val dir = File(context.cacheDir, MAIN_DIR)
        if (!dir.exists()) {
            dir.mkdir()
        }

        val imageFile: File = File(dir, "imageFileName")
        var savedImagePath : String? = imageFile.absolutePath
        try {
            val fOut: OutputStream = FileOutputStream(imageFile)
            image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
            fOut.close()
            Timber.d("writing image succesful")
        } catch (e: IOException) {
            savedImagePath = null
            e.printStackTrace()
        }

        return savedImagePath
    }

    fun writeToFile(context: Context, body: String) {
        val dir = File(context.cacheDir, MAIN_DIR)
        if (!dir.exists()) {
            dir.mkdir()
        }

        try {
            val feedfile = File(dir, "news_feed.txt");
            val writer = FileWriter(feedfile);
            writer.append(body)
            writer.flush();
            writer.close();
            Timber.d("File write succcesfully : ${feedfile.absolutePath}")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadImage(newsItem: NewsItem): Bitmap? {
        return newsItem?.let {
            Timber.d("load image for $newsItem")
            BitmapFactory.decodeFile(newsItem.imageSmall)
        }
    }

}