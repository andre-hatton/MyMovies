package com.yoshizuka.mymovies.managers

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.yoshizuka.mymovies.models.entities.Movie
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

object Image {

    /**
     * URL de base des image (j'ai mis seulement original comme taille d'image)
     */
    const val IMAGE_URL = "https://image.tmdb.org/t/p/original"

    /**
     * Element picasso qui va permettre de recupérer l'image souhaité
     */
    private var mTarget : Target? = null

    /**
     * Partage l'image du film ainsi que son titre
     * @param context Le context
     * @param url Url de base de l'image
     * @param movie Le film a partagé
     */
    fun shareImage(context: Context, url: String, movie: Movie) {
        val extension = movie.posterPath.substringAfterLast(".", "")
        mTarget = object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                e?.printStackTrace()
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                // fonction de partage si l'image n'est pas null
                if(bitmap != null) {
                    val uri = bitmapToUri(context, bitmap, extension)
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.setType("image/*")
                    intent.putExtra(Intent.EXTRA_TEXT, movie.getNameOrTitle())
                    intent.putExtra(Intent.EXTRA_STREAM, uri)
                    context.startActivity(intent)
                }
            }
        }

        if(mTarget != null) {
            Picasso.get().load("${url}${movie.posterPath}").into(mTarget as Target)
        }
    }

    /**
     * Convertit une image bitmap en une Uri pour pouvoir la partager (ou faire autre chose)
     * @param context Le context
     * @param bitmap L'image bitmap
     * @param extension L'extension de l'image
     *
     * @return Uri Le lien vers le fichier correspondant au bitmap, null si une erreur d'écriture ou d'extension
     */
    fun bitmapToUri(context: Context, bitmap: Bitmap, extension: String) : Uri? {
        var bmpUri : Uri? = null
        try {
            // Chemin du fichier a créer
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "image_${System.currentTimeMillis()}.${extension}")
            val out = FileOutputStream(file)
            if(extension == "png") {
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, out)
            } else if(extension == "jpg" || extension == "jpeg") {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
            } else {
                return null
            }
            out.close()
            bmpUri = Uri.fromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmpUri
    }

}