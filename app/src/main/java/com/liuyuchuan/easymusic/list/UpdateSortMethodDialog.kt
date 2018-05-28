package com.liuyuchuan.easymusic.list

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import com.liuyuchuan.easymusic.getCheckableSongComparator
import com.liuyuchuan.easymusic.utils.provideViewModel
import java.util.*

/**
 * Created by Liu Yuchuan on 2018/5/28.
 */
class UpdateSortMethodDialog : DialogFragment(), DialogInterface.OnClickListener {
    private lateinit var songListViewModel: SongListViewModel

    companion object {
        private const val TAG_UPDATE_SORT_METHOD_DIALOG = "TAG_UPDATE_SORT_METHOD_DIALOG"

        fun show(fm: FragmentManager) {
            val f = fm.findFragmentByTag(TAG_UPDATE_SORT_METHOD_DIALOG)
            if (f != null) {
                fm.beginTransaction()
                        .remove(f)
                        .commitAllowingStateLoss()
            }

            fm.beginTransaction()
                    .add(UpdateSortMethodDialog(), TAG_UPDATE_SORT_METHOD_DIALOG)
                    .commitAllowingStateLoss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        songListViewModel = parentFragment!!.provideViewModel()
        return AlertDialog.Builder(context)
                .setTitle("排序方式")
                .setItems(arrayOf("歌名", "歌手名", "专辑名"), this)
                .create()
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        getCheckableSongComparator(which)?.let {
            Collections.sort(songListViewModel.songList, it)
            songListViewModel.updateSortMethod(which)
        }
    }
}
