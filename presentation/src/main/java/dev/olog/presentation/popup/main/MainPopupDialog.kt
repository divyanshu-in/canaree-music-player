package dev.olog.presentation.popup.main

import android.content.Context
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.extensions.findActivity
import dev.olog.domain.entity.sort.SortArranging
import dev.olog.domain.entity.sort.SortEntity
import dev.olog.domain.entity.sort.SortType
import dev.olog.domain.prefs.SortPreferences
import dev.olog.navigation.Navigator
import dev.olog.presentation.R
import timber.log.Timber
import javax.inject.Inject

internal class MainPopupDialog @Inject constructor(
    @ApplicationContext private val context: Context,
    private val popupNavigator: MainPopupNavigator,
    private val gateway: SortPreferences,
    private val navigator: Navigator

) {

    companion object {
        private const val SAVE_AS_PLAYLIST_ID = -12345
    }
    
    fun show(anchor: View, category: MainPopupCategory) {
        val popup = PopupMenu(anchor.context, anchor)
        val layoutId = when (category) {
            MainPopupCategory.ALBUMS -> R.menu.main_albums
            MainPopupCategory.SONGS -> R.menu.main_songs
            MainPopupCategory.ARTISTS -> R.menu.main_artists
            else -> R.menu.main
        }
        popup.inflate(layoutId)

        when (category) {
            MainPopupCategory.SEARCH,
            MainPopupCategory.PLAYING_QUEUE -> {
                popup.menu.removeItem(R.id.gridSize)
            }
            else -> {}
        }

        val sortModel = when (category) {
            MainPopupCategory.ALBUMS -> initializeAlbumSort(popup.menu)
            MainPopupCategory.SONGS -> initializeTracksSort(popup.menu)
            MainPopupCategory.ARTISTS -> initializeArtistSort(popup.menu)
            else -> null
        }

        if (category == MainPopupCategory.PLAYING_QUEUE) {
            popup.menu.add(
                Menu.NONE,
                SAVE_AS_PLAYLIST_ID, Menu.NONE, anchor.context.getString(R.string.save_as_playlist)
            )
        }

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.about -> navigator.toAbout(anchor.findActivity())
                R.id.equalizer -> navigator.toEqualizer(anchor.findActivity())
                R.id.settings -> navigator.toSettings(anchor.findActivity())
                R.id.sleepTimer -> popupNavigator.toSleepTimer()
                SAVE_AS_PLAYLIST_ID -> navigator.toCreatePlaylistDialogFromPlayingQueue()
//                R.id.gridSize1 -> updateSpanCount(anchor, category.toTabCategory(), 1) TODO
//                R.id.gridSize2 -> updateSpanCount(anchor, category.toTabCategory(), 2)
//                R.id.gridSize3 -> updateSpanCount(anchor, category.toTabCategory(), 3)
//                R.id.gridSize4 -> updateSpanCount(anchor, category.toTabCategory(), 4)
                else -> {
                    when (category) {
                        MainPopupCategory.ALBUMS -> handleAllAlbumsSorting(it, sortModel!!)
                        MainPopupCategory.SONGS -> handleAllSongsSorting(it, sortModel!!)
                        MainPopupCategory.ARTISTS -> handleAllArtistsSorting(it, sortModel!!)
                        else -> Timber.w("MainPopup: not handled $category")
                    }
                }
            }

            true
        }
        popup.show()
    }

//    private fun updateSpanCount(view: View, category: TabCategory, spanCount: Int){
        // TODO
//        val current = presentationPrefs.getSpanCount(category)
//        presentationPrefs.setSpanCount(category, spanCount)
//        if (current == 1 && spanCount > 1 || current > 1 && spanCount == 1){
//            (view.context as Activity).recreate()
//        }
//    }

    private fun initializeTracksSort(menu: Menu): SortEntity {
        val sort = gateway.getAllTracksSort()
        val item = when (sort.type) {
            SortType.TITLE -> R.id.by_title
            SortType.ALBUM -> R.id.by_album
            SortType.ARTIST -> R.id.by_artist
            SortType.DURATION -> R.id.by_duration
            SortType.RECENTLY_ADDED -> R.id.by_date
            else -> throw IllegalStateException("invalid for tracks ${sort.type}")
        }
        val ascending = sort.arranging == SortArranging.ASCENDING
        menu.findItem(item).isChecked = true
        menu.findItem(R.id.arranging).isChecked = ascending

        return sort
    }

    private fun initializeAlbumSort(menu: Menu): SortEntity {
        val sort = gateway.getAllAlbumsSort()
        val item = when (sort.type) {
            SortType.TITLE -> R.id.by_title
            SortType.ARTIST -> R.id.by_artist
            else -> throw IllegalStateException("invalid for albums ${sort.type}")
        }
        val ascending = sort.arranging == SortArranging.ASCENDING
        menu.findItem(item).isChecked = true
        menu.findItem(R.id.arranging).isChecked = ascending

        return sort
    }

    private fun initializeArtistSort(menu: Menu): SortEntity {
        val sort = gateway.getAllArtistsSort()
        val item = when (sort.type) {
            SortType.ARTIST -> R.id.by_artist
            SortType.ALBUM_ARTIST -> R.id.by_album_artist
            else -> throw IllegalStateException("invalid for albums ${sort.type}")
        }
        val ascending = sort.arranging == SortArranging.ASCENDING
        menu.findItem(item).isChecked = true
        menu.findItem(R.id.arranging).isChecked = ascending

        return sort
    }

    private fun handleAllSongsSorting(menuItem: MenuItem, sort: SortEntity) {
        var model = sort

        model = if (menuItem.itemId == R.id.arranging) {
            val isAscending = !menuItem.isChecked
            val newArranging =
                if (isAscending) SortArranging.ASCENDING else SortArranging.DESCENDING
            SortEntity(type = model.type, arranging = newArranging)
        } else {
            val newSortType = when (menuItem.itemId) {
                R.id.by_title -> SortType.TITLE
                R.id.by_artist -> SortType.ARTIST
                R.id.by_album -> SortType.ALBUM
                R.id.by_duration -> SortType.DURATION
                R.id.by_date -> SortType.RECENTLY_ADDED
                else -> null
            } ?: return
            SortEntity(type = newSortType, arranging = model.arranging)
        }

        gateway.setAllTracksSort(model)
        context.contentResolver.notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null)
    }

    private fun handleAllAlbumsSorting(menuItem: MenuItem, sort: SortEntity) {
        var model = sort

        model = if (menuItem.itemId == R.id.arranging) {
            val isAscending = !menuItem.isChecked
            val newArranging =
                if (isAscending) SortArranging.ASCENDING else SortArranging.DESCENDING
            SortEntity(type = model.type, arranging = newArranging)
        } else {
            val newSortType = when (menuItem.itemId) {
                R.id.by_title -> SortType.TITLE
                R.id.by_artist -> SortType.ARTIST
                else -> null
            } ?: return
            SortEntity(type = newSortType, arranging = model.arranging)
        }

        gateway.setAllAlbumsSort(model)
        context.contentResolver.notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null)
    }

    private fun handleAllArtistsSorting(menuItem: MenuItem, sort: SortEntity) {
        var model = sort

        model = if (menuItem.itemId == R.id.arranging) {
            val isAscending = !menuItem.isChecked
            val newArranging =
                if (isAscending) SortArranging.ASCENDING else SortArranging.DESCENDING
            SortEntity(type = model.type, arranging = newArranging)
        } else {
            val newSortType = when (menuItem.itemId) {
                R.id.by_artist -> SortType.ARTIST
                R.id.by_album_artist -> SortType.ALBUM_ARTIST
                else -> null
            } ?: return
            SortEntity(type = newSortType, arranging = model.arranging)
        }

        gateway.setAllArtistsSort(model)
        context.contentResolver.notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null)
    }

}