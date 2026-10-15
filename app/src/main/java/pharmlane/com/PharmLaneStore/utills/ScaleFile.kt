package pharmlane.com.PharmLaneStore.utills

import android.content.Context
import android.database.Cursor
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class ScaleFile {
    companion object{
        fun compressImage(imageUri: String,mContext: Context): Bitmap? {
//            val filePath = getRealPathFromURI(imageUri,mContext)
            var scaledBitmap: Bitmap? = null
            val options = BitmapFactory.Options()

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
            options.inJustDecodeBounds = true
            var bmp = BitmapFactory.decodeFile(imageUri, options)
            var actualHeight = options.outHeight
            var actualWidth = options.outWidth


            Log.e("OPTIONSIZES","HEIGHT   "+actualHeight+"   Width   "+actualWidth)

//      max Height and width values of the compressed image is taken as 816x612
            val maxHeight = (options.outHeight / 4).toFloat()
            val maxWidth = (options.outWidth / 4).toFloat()
            var imgRatio = (actualWidth / actualHeight).toFloat()
            val maxRatio = maxWidth / maxHeight

//      width and height values are set maintaining the aspect ratio of the image
            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight
                    actualWidth = (imgRatio * actualWidth).toInt()
                    actualHeight = maxHeight.toInt()
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth
                    actualHeight = (imgRatio * actualHeight).toInt()
                    actualWidth = maxWidth.toInt()
                } else {
                    actualHeight = maxHeight.toInt()
                    actualWidth = maxWidth.toInt()
                }
            }

            Log.e("OPTIONSIZES","HEIGHT   "+actualHeight+"   Width   "+actualWidth)


//      setting inSampleSize value allows to load a scaled down version of the original image
            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

//      inJustDecodeBounds set to false to load the actual bitmap
            options.inJustDecodeBounds = false

//      this options allow android to claim the bitmap memory if it runs low on memory
            options.inPurgeable = true
            options.inInputShareable = true
            options.inTempStorage = ByteArray(16 * 1024)
            try {
//          load the bitmap from its path
                bmp = BitmapFactory.decodeFile(imageUri, options)
            } catch (exception: OutOfMemoryError) {
                exception.printStackTrace()
            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
            } catch (exception: OutOfMemoryError) {
                exception.printStackTrace()
            }
            val ratioX = actualWidth / options.outWidth.toFloat()
            val ratioY = actualHeight / options.outHeight.toFloat()
            val middleX = actualWidth / 2.0f
            val middleY = actualHeight / 2.0f
            val scaleMatrix = Matrix()
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
            val canvas = scaledBitmap?.let { Canvas(it) }
            canvas?.setMatrix(scaleMatrix)
            canvas?.drawBitmap(bmp, middleX - bmp.width / 2, middleY - bmp.height / 2, Paint(Paint.FILTER_BITMAP_FLAG))

//      check the rotation of the image and display it properly
            val exif: ExifInterface
            try {
                exif = ExifInterface(imageUri)
                val orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, 0)
                Log.d("EXIF", "Exif: $orientation")
                val matrix = Matrix()
                if (orientation == 6) {
                    matrix.postRotate(90f)
                    Log.d("EXIF", "Exif: $orientation")
                } else if (orientation == 3) {
                    matrix.postRotate(180f)
                    Log.d("EXIF", "Exif: $orientation")
                } else if (orientation == 8) {
                    matrix.postRotate(270f)
                    Log.d("EXIF", "Exif: $orientation")
                }
                scaledBitmap = scaledBitmap?.let {
                    Bitmap.createBitmap(
                        it, 0, 0,
                        scaledBitmap!!.width, scaledBitmap!!.height, matrix,
                        true)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            var out: FileOutputStream? = null
            val filename = getFilename()
            try {
                out = FileOutputStream(filename)

//          write the compressed bitmap at the destination specified by filename.
                scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            return scaledBitmap
        }

        fun getFilename(): String {
            val file = File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images")
            if (!file.exists()) {
                file.mkdirs()
            }
            return file.getAbsolutePath().toString() + "/" + System.currentTimeMillis() + ".jpg"
        }

        private fun getRealPathFromURI(contentURI: String,context: Context): String {
            val contentUri = Uri.parse(contentURI)
            val cursor: Cursor =
                context.contentResolver.query(contentUri, null, null, null, null)!!
            return if (cursor == null) {
                contentUri.path ?: ""
            } else {
                cursor.moveToFirst()
                val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                cursor.getString(index)
            }
        }

        fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1
            if (height > reqHeight || width > reqWidth) {
                val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
                val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
                inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
            }
            val totalPixels = (width * height).toFloat()
            val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++
            }
            return inSampleSize
        }
    }

}