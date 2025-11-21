package com.icc.practica11

import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.OutputStream

class ImageSaver(private val activity: AppCompatActivity) {

    fun saveBitmap(bitmap: Bitmap) {
        try {
            val filename = "Dibujo_${System.currentTimeMillis()}.png"
            val saved = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveToMediaStore(bitmap, filename)
            } else {
                saveLegacy(bitmap, filename)
            }

            showMessage(if (saved) "Imagen guardada en la galería" else "Error al guardar")
        } catch (e: Exception) {
            showMessage("Error: ${e.message}")
        }
    }

    private fun saveToMediaStore(bitmap: Bitmap, filename: String): Boolean {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val uri = activity.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ) ?: return false

        return try {
            activity.contentResolver.openOutputStream(uri)?.use { saveStream(bitmap, it) }
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            activity.contentResolver.update(uri, contentValues, null, null)
            true
        } catch (e: Exception) {
            false
        }
    }

    @Suppress("DEPRECATION")
    private fun saveLegacy(bitmap: Bitmap, filename: String): Boolean {
        // Para versiones antiguas necesitarías manejar permisos WRITE_EXTERNAL_STORAGE
        // Esta implementación es básica y requiere permisos adicionales
        showMessage("Guardado en versiones antiguas no implementado completamente")
        return false
    }

    private fun saveStream(bitmap: Bitmap, outputStream: OutputStream): Boolean {
        return try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun showMessage(text: String) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
    }
}
