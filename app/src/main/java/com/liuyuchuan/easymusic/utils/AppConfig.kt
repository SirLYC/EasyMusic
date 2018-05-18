package com.liuyuchuan.easymusic.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.liuyuchuan.easymusic.App
import com.liuyuchuan.easymusic.data.MusicList
import com.liuyuchuan.easymusic.data.Song

/**
 * Created by Liu Yuchuan on 2018/5/6.
 */
object AppConfig {

    private const val KEY_FIRST = "KEY_FIRST"
    private const val KEY_SCAN = "KEY_SCAN"

    private val ssp = App.INSTANCE.getSharedPreferences("config", Context.MODE_PRIVATE)

    val gson: Gson by lazy {
        GsonBuilder()
                .registerTypeAdapter(MusicList::class.java, JsonDeserializer<MusicList> { json, typeOfT, context ->
                    val obj = json.asJsonObject
                    val name = obj["name"].asString
                    val list = ObservableList(
                            obj["list"].asJsonArray.map {
                                it.asJsonObject.run {
                                    Song(get("name").asString, get("singer").asString,
                                            get("album").asString, get("path").asString,
                                            get("duration").asInt, get("size").asLong)
                                }
                            }.toMutableList()
                    )
                    MusicList(name, list)
                })
                .create()
    }

    fun isFirstInstall() = ssp.getBoolean(KEY_FIRST, true).apply {
        ssp.edit().putBoolean(KEY_FIRST, false).apply()
    }

    fun hasScanMusic() = ssp.getBoolean(KEY_SCAN, false)

    fun afterScanMusic() {
        ssp.edit().putBoolean(KEY_FIRST, true).apply()
    }
}
