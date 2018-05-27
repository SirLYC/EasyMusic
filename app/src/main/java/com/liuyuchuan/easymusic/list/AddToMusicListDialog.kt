package com.liuyuchuan.easymusic.list

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.liuyuchuan.easymusic.data.MusicList
import com.liuyuchuan.easymusic.utils.ReactiveAdapter
import com.liuyuchuan.easymusic.utils.musicListCheckableItem
import com.liuyuchuan.easymusic.utils.provideViewModel

/**
 * Created by Liu Yuchuan on 2018/5/27.
 */
class AddToMusicListDialog : DialogFragment(), DialogListItemViewBinder.OnMusicListItemClickListener {
    private lateinit var listManageViewModel: ListManageViewModel
    private lateinit var adapter: ReactiveAdapter

    companion object {
        private const val TAG_ADD_TO_MUSIC_LIST_DIALOG = "TAG_ADD_TO_MUSIC_LIST_DIALOG"

        fun show(fm: FragmentManager) {
            val f = fm.findFragmentByTag(TAG_ADD_TO_MUSIC_LIST_DIALOG)
            if (f != null) {
                fm.beginTransaction()
                        .remove(f)
                        .commitAllowingStateLoss()
            }

            fm.beginTransaction()
                    .add(AddToMusicListDialog(), TAG_ADD_TO_MUSIC_LIST_DIALOG)
                    .commitAllowingStateLoss()
        }

        fun dismiss(fm: FragmentManager) {
            val f = fm.findFragmentByTag(TAG_ADD_TO_MUSIC_LIST_DIALOG)
            if (f != null) {
                fm.beginTransaction()
                        .remove(f)
                        .commitAllowingStateLoss()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val ctx = context!!
        val v = RecyclerView(ctx)
        listManageViewModel = activity!!.provideViewModel()

        adapter = ReactiveAdapter(listManageViewModel.songListList).apply {
            register(musicListCheckableItem::class.java, DialogListItemViewBinder(this@AddToMusicListDialog))
            observe(this@AddToMusicListDialog)
        }

        v.adapter = adapter
        v.layoutManager = LinearLayoutManager(ctx)

        return AlertDialog.Builder(ctx)
                .setTitle("添加到...")
                .setView(v)
                .create()

    }

    override fun onMusicListClick(musicList: MusicList) {
        if (!listManageViewModel.isBusyOnMusicList()) {
            listManageViewModel.addToMusicList(musicList)
        }
    }
}
