package com.liuyuchuan.easymusic.utils

import android.util.Log
import com.liuyuchuan.easymusic.ifDebug
import java.io.File
import java.io.PrintWriter
import java.lang.reflect.Type
import java.util.*

/**
 * Created by Liu Yuchuan on 2018/5/6.
 */
fun File.read(): String {
    val sb = StringBuilder()
    Scanner(this).use {
        while (it.hasNextLine()) {
            sb.append(it.nextLine())
        }

        it.close()
    }

    ifDebug {
        Log.d(name, sb.toString())
    }
    return sb.toString()
}

fun File.write(content: String) {
    PrintWriter(this).use {
        it.print(content)
        it.flush()
        it.close()
    }
}

fun File.writeJson(obj: Any) {
    try {
        write(AppConfig.gson.toJson(obj))
    } catch (e: Exception) {
        ifDebug { e.printStackTrace() }
    }
}


fun <T> File.readJson(clazz: Class<T>): T? {
    var t: T? = null
    try {
        t = AppConfig.gson.fromJson(read(), clazz)
    } catch (e: Exception) {
        ifDebug { e.printStackTrace() }
    } finally {
        return t
    }
}


fun <T> File.readJson(type: Type): T? {
    var t: T? = null
    try {
        t = AppConfig.gson.fromJson<T>(read(), type)
    } catch (e: Exception) {
        ifDebug { e.printStackTrace() }
    } finally {
        return t
    }
}

// I've already do my best to make sure the directory exists...
fun File.checkDir(): File? {
    if (this.exists() && !this.isDirectory) {
        this.delete()
    }

    if (!this.exists()) {
        return if (this.mkdir()) {
            this
        } else {
            null
        }
    }

    return this
}
