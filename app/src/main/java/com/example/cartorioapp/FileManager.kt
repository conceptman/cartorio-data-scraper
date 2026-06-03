package com.example.cartorioapp

import android.content.Context
import android.os.Environment
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileOutputStream

open class FileManager(private val context: Context) {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    open fun getCartorioDataFolder(): File {
        val folder = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "CartorioData")
        if (!folder.exists()) {
            folder.mkdirs()
        }
        return folder
    }

    fun exportToJson(data: List<Cartorio>, fileName: String): File? {
        return try {
            val folder = getCartorioDataFolder()
            val file = File(folder, fileName)
            val jsonString = gson.toJson(data)

            FileOutputStream(file).use { output ->
                output.write(jsonString.toByteArray())
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun readJson(fileName: String): String? {
        return try {
            val file = File(getCartorioDataFolder(), fileName)
            if (file.exists()) {
                file.readText()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
