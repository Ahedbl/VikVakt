package com.example.vikvakt

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import java.io.File

class FileHandler(private val context: Context) {

    fun saveToFile(fileName: String, data: Any) {
        val gson = Gson()
        val json = gson.toJson(data)
        val file = File(context.filesDir, fileName)
        file.writeText(json)
    }

    fun getSavedData(context: Context): List<String> {
        val fileDir = context.filesDir
        return fileDir.listFiles()?.map { it.name } ?: emptyList()
    }


    fun <T> readFromFile(fileName: String, type: Class<Array<T>>): MutableList<T>? {
        val file = File(context.filesDir, fileName)
        return if (file.exists()) {
            val json = file.readText()
            val dataArray: Array<T>? = Gson().fromJson(json, type)
            dataArray?.toMutableList()
        } else {
            Log.d("readFromFile", "File not found")
            null
        }
    }
    fun printAllFileNames() {
        val fileDir = context.filesDir
        val files = fileDir.listFiles()
        if (files != null && files.isNotEmpty()) {
            Log.d("FileHandler", "Available files:")
            files.forEach { file ->
                Log.d("FileHandler", file.name)
            }
        } else {
            Log.d("FileHandler", "No files available.")
        }
    }

}
