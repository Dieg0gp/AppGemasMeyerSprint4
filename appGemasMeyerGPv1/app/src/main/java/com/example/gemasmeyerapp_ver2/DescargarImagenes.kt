package com.example.gemasmeyerapp_ver2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.LruCache
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.gemasmeyerapp_ver2.Data.Constantes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class DescargarImagenes {
    //caché para guardar la imagen
    private val imageCache: LruCache<String, Bitmap> = LruCache(10 * 1024 * 1024) // 10 MB cache size
    //descargar imagen
    suspend fun downloadImage(context: Context, imageUrl: String, fileName: String) {
        // Verificar si la imagen ya existe
        val cachedBitmap = imageCache.get(imageUrl)
        if (cachedBitmap == null) {
            //Si la imagen no existe se guarda
            val imageLoader = ImageLoader.Builder(context)
                .build()

            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .build()

            val result = withContext(Dispatchers.IO) {
                imageLoader.execute(request)
            }

            if (result is SuccessResult) {
                val bitmap = (result.drawable).toBitmap()
                // Save the bitmap to cache
                imageCache.put(imageUrl, bitmap)
                // Save the bitmap to file
                guardarImagenEnMemoria(context, bitmap, fileName)
            }
        }
        else
        {
            Constantes.showAlert(context,"Mensaje","La imagen ya ha sido guardada",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
        }
    }

    private suspend fun guardarImagenEnMemoria(context: Context, bitmap: Bitmap, fileName: String) {
        val directory =
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            } else {
                context.filesDir
            }

        val file = File(directory, fileName)

        withContext(Dispatchers.IO) {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        }
    }
    suspend fun obtenerImagen(context: Context, fileName: String): Bitmap? {
        val directory =
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            } else {
                context.filesDir
            }

        val file = File(directory, fileName)

        if (file.exists()) {
            return withContext(Dispatchers.IO) {
                FileInputStream(file).use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
            }
        }

        return null
    }
    suspend fun eliminarArchivosEnMemoria(context: Context) {
        val directory =
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            } else {
                context.filesDir
            }
        if (directory != null) {
            if (directory.exists() && directory.isDirectory) {
                withContext(Dispatchers.Main) {
                    val files = directory.listFiles()
                    if(files?.count()!! > 0) {
                        files.forEach { file ->
                            file.delete()
                        }
                        Constantes.showAlert(context,"Mensaje","Archivos eliminados",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
                    }
                    else
                    {
                        Toast.makeText(context,"No existen archivos en el directorio",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    suspend fun mostrarArchivosEnMemoria(context: Context) {
        val directory =
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            } else {
                context.filesDir
            }
        if (directory != null) {
            if (directory.exists() && directory.isDirectory) {
                withContext(Dispatchers.Main) {
                    val files = directory.listFiles()
                    if(files?.count()!! > 0) {
                        files.forEach { file ->
                            Constantes.showAlert(context,"Mensaje",file.name,Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
                        }
                    }
                    else
                    {
                        Toast.makeText(context,"No existen archivos en el directorio",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    fun borrarCache(imageUrl: String) {
        imageCache.remove(imageUrl)
    }

}