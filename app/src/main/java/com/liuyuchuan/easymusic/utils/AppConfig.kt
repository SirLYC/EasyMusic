package com.liuyuchuan.easymusic.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.liuyuchuan.easymusic.data.MusicList
import com.liuyuchuan.easymusic.data.Song

/**
 * Created by Liu Yuchuan on 2018/5/6.
 */
object AppConfig {
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
}
