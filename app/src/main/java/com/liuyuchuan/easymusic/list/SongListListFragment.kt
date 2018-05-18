package com.liuyuchuan.easymusic.list

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.liuyuchuan.easymusic.BaseFragment
import com.liuyuchuan.easymusic.R
import com.liuyuchuan.easymusic.data.MusicList
import com.liuyuchuan.easymusic.utils.ReactiveAdapter
import com.liuyuchuan.easymusic.utils.musicListCheckableItem
import com.liuyuchuan.easymusic.utils.provideViewModel
import com.liuyuchuan.easymusic.utils.setTitle
import kotlinx.android.synthetic.main.fragment_song_list_list.*
import me.drakeet.multitype.MultiTypeAdapter

/**
 * Created by Liu Yuchuan on 2018/5/7.
 */
class SongListListFragment : BaseFragment(), ListItemViewBinder.OnListItemClickListener {
    private lateinit var listManageViewModel: ListManageViewModel
    private lateinit var adapter: MultiTypeAdapter
    private lateinit var checkableItemViewBinder: ListItemViewBinder

    private var menuItemEdit: MenuItem? = null
    private var menuItemDelete: MenuItem? = null
    private var menuItemCheckAll: MenuItem? = null
    private var menuItemFinish: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listManageViewModel = activity!!.provideViewModel()
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_song_list_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setTitle(getString(R.string.title_fragment_song_list_list))
        adapter = ReactiveAdapter(listManageViewModel.songListList)
                .apply {
                    checkableItemViewBinder = ListItemViewBinder(this@SongListListFragment)
                    register(musicListCheckableItem::class.java, checkableItemViewBinder)
                    observe(this@SongListListFragment)
                }

        rv_song_list_list.adapter = adapter
        rv_song_list_list.layoutManager = LinearLayoutManager(context)
    }

    override fun onSongListItemClick(item: MusicList) {
        listManageViewModel.selectedMusicListLiveData.value = item
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

        listManageViewModel.enableSelectLiveData.observe(this, Observer {
            enableCheckAction(it!!)
        })
    }

    override fun onDestroyOptionsMenu() {
        menuItemEdit = null
        menuItemDelete = null
        menuItemCheckAll = null
        menuItemFinish = null
        super.onDestroyOptionsMenu()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.list_edit -> {
                listManageViewModel.enableCheck(true)
                true
            }

            R.id.list_complete -> {
                listManageViewModel.enableCheck(false)
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
                val list = checkableItemViewBinder.checkedItemList()
                listManageViewModel.songListList.removeAll(list)
                true
            }

            else -> false
        }
    }

    private fun enableCheckAction(enable: Boolean) {
        if (enable) {
            menuItemEdit?.isVisible = false
            menuItemCheckAll?.isVisible = true
            menuItemDelete?.isVisible = true
            menuItemFinish?.isVisible = true
        } else {
            menuItemEdit?.isVisible = true
            menuItemCheckAll?.isVisible = false
            menuItemDelete?.isVisible = false
            menuItemFinish?.isVisible = false
        }

        checkableItemViewBinder.enableCheck = enable
    }
}
