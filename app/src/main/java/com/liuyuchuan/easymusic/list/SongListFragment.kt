package com.liuyuchuan.easymusic.list

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.liuyuchuan.easymusic.BaseFragment
import com.liuyuchuan.easymusic.R
import com.liuyuchuan.easymusic.data.MusicList
import com.liuyuchuan.easymusic.data.Song
import com.liuyuchuan.easymusic.play.PlayActivity
import com.liuyuchuan.easymusic.utils.*
import kotlinx.android.synthetic.main.fragment_song_list.*

/**
 * Created by Liu Yuchuan on 2018/5/7.
 */
class SongListFragment : BaseFragment(), CheckableItemViewBinder.OnRealItemClickListener<Song> {
    private lateinit var musicList: MusicList
    private lateinit var adapter: ReactiveAdapter
    private lateinit var listManageViewModel: ListManageViewModel
    private lateinit var songListViewModel: SongListViewModel
    private lateinit var checkableItemViewBinder: SongItemViewBinder

    private var menuItemEdit: MenuItem? = null
    private var menuItemDelete: MenuItem? = null
    private var menuItemCheckAll: MenuItem? = null
    private var menuItemFinish: MenuItem? = null
    private var menuItemAddToList: MenuItem? = null
    private var menuItemAddToPlayList: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        listManageViewModel = activity!!.provideViewModel()
        songListViewModel = provideViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_song_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val list = listManageViewModel.selectedMusicListLiveData.value
        if (list == null) {
            toast(R.string.error_read_list_error)
            listManageViewModel.selectedMusicListLiveData.value = null
            return
        }

        musicList = list

        setTitle(musicList.name)
        songListViewModel.init(musicList)

        adapter = ReactiveAdapter(songListViewModel.songList)
                .apply {
                    checkableItemViewBinder = SongItemViewBinder(this@SongListFragment)
                    register(songCheckableItem::class.java, checkableItemViewBinder)
                    observe(this@SongListFragment)
                }

        rv_song_list.adapter = adapter
        rv_song_list.layoutManager = LinearLayoutManager(context)

        listManageViewModel.addToMusicListEvent.observe(this, Observer {
            when (it) {
                is RefreshState.NotEmpty -> {
                    AddToMusicListDialog.dismiss(childFragmentManager)
                    songListViewModel.enableCheckLiveData.value = false
                }
            }
        })

        listManageViewModel.deleteSongEvent.observe(this, Observer {
            when (it) {
                is RefreshState.NotEmpty -> {
                    songListViewModel.enableCheckLiveData.value = false
                }
            }
        })
    }

    override fun onCheckRealItemClicked(realItem: Song) {
        songListViewModel.chooseSongToPlay(realItem)
        PlayActivity.start(activity!!, true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_list_editable, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menuItemEdit = menu.findItem(R.id.list_edit)
        menuItemDelete = menu.findItem(R.id.list_delete)
        menuItemCheckAll = menu.findItem(R.id.list_check_all)
        menuItemFinish = menu.findItem(R.id.list_complete)
        menuItemAddToList = menu.findItem(R.id.list_add_to_music_list)
        menuItemAddToPlayList = menu.findItem(R.id.list_add_to_play_list)

        songListViewModel.enableCheckLiveData.observe(this, Observer {
            enableCheckAction(it!!)
        })
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyOptionsMenu() {
        menuItemEdit = null
        menuItemDelete = null
        menuItemCheckAll = null
        menuItemFinish = null
        menuItemAddToList = null
        menuItemAddToPlayList = null
        super.onDestroyOptionsMenu()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.list_edit -> {
                songListViewModel.enableCheck(true)
                true
            }

            R.id.list_complete -> {
                songListViewModel.enableCheck(false)
                true
            }

            R.id.list_check_all -> {
                if (checkableItemViewBinder.isCheckAll()) {
                    checkableItemViewBinder.uncheckAll()
                } else {
                    checkableItemViewBinder.checkAll()
                }
                true
            }

            R.id.list_delete -> {
                if (listManageViewModel.isBusyOnMusicList()) {
                    return true
                }
                val list = checkableItemViewBinder.checkedItemList()
                if (list.isEmpty()) {
                    toast(R.string.error_no_songs_checked)
                } else {
                    listManageViewModel.checkedList.clear()
                    listManageViewModel.checkedList.addAll(list)
                    listManageViewModel.deleteSongsFromList(musicList)
                }
                true
            }

            R.id.list_add_to_music_list -> {
                val list = checkableItemViewBinder.checkedItemList()
                if (list.isEmpty()) {
                    toast(R.string.error_no_songs_checked)
                } else {
                    listManageViewModel.checkedList.clear()
                    listManageViewModel.checkedList.addAll(list)
                    AddToMusicListDialog.show(childFragmentManager)
                }
                true
            }

            R.id.list_add_to_play_list -> {
                val list = checkableItemViewBinder.checkedItemList()
                if (list.isEmpty()) {
                    toast(R.string.error_no_songs_checked)
                } else {
                    songListViewModel.addListToPlayList(list)
                    songListViewModel.enableCheckLiveData.value = false
                }
                true
            }

            else -> false
        }
    }

    override fun onBackPressed(): Boolean {
        if (songListViewModel.enableCheckLiveData.value) {
            songListViewModel.enableCheckLiveData.value = false
            return true
        }

        return false
    }

    private fun enableCheckAction(enable: Boolean) {
        if (enable) {
            menuItemEdit?.isVisible = false
            menuItemCheckAll?.isVisible = true
            menuItemDelete?.isVisible = true
            menuItemFinish?.isVisible = true
            menuItemAddToList?.isVisible = true
            menuItemAddToPlayList?.isVisible = true
        } else {
            menuItemEdit?.isVisible = true
            menuItemCheckAll?.isVisible = false
            menuItemDelete?.isVisible = false
            menuItemFinish?.isVisible = false
            menuItemAddToList?.isVisible = false
            menuItemAddToPlayList?.isVisible = false
        }

        checkableItemViewBinder.enableCheck = enable
    }
}
