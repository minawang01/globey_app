package APP_DESIGN_PROJECT.globeyapp.tools

import APP_DESIGN_PROJECT.globeyapp.R
import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun checkDateValidity(date:String):Boolean {
    val regex = Regex("[0-9]{2}/[0-9]{2}/[0-9]{4}")
    return date.matches(regex) || date.isNullOrBlank()
}

fun setDefaultImage(imgBtn: ImageButton, context: Context, contentResolver: ContentResolver) {
    val selectedImageUri = Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .authority(context.resources.getResourcePackageName(R.drawable.trip_placeholder))
        .appendPath(context.resources.getResourceTypeName(R.drawable.trip_placeholder))
        .appendPath(context.resources.getResourceEntryName(R.drawable.trip_placeholder))
        .build()

    val selectedImageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
    imgBtn.setImageBitmap(selectedImageBitmap)
}

fun imageChooser(launchSomeActivity: ActivityResultLauncher<Intent>) {
    val i = Intent()
    i.type = "image/*"
    i.action = Intent.ACTION_GET_CONTENT
    launchSomeActivity.launch(i)
}

fun createFileName(uri:String):String {
    val strings = uri.split("%")
    return strings[strings.size - 1]
}

fun saveToInternalStorage(bitmap: Bitmap, uri:String, applicationContext: Context):String {
    val root_dir: File = ContextWrapper(applicationContext).getDir("imageDir", Context.MODE_PRIVATE)
    val fp = File(root_dir,createFileName(uri))
    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(fp)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
    } catch (e: Exception) {
        Log.e("GlobeyApp", e.printStackTrace().toString())

    } finally {
        try {
            fos?.close()
        } catch (e: IOException) {
            Log.e("GlobeyApp", e.printStackTrace().toString())
        }
    }
    return fp.toString()
}