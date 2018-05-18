package com.liuyuchuan.easymusic.utils

import com.liuyuchuan.easymusic.data.MusicList
import com.liuyuchuan.easymusic.data.Song

/**
 * Created by Liu Yuchuan on 2018/5/10.
 */
val musicListCheckableItem = CheckableItem(MusicList("test",
        ObservableList(mutableListOf())))

val songCheckableItem =
        CheckableItem(Song("", "", "", "", 0, 0))
