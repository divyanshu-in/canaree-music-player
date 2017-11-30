package dev.olog.data.repository

import dev.olog.data.db.AppDatabase
import dev.olog.domain.entity.Song
import dev.olog.domain.gateway.FavoriteGateway
import dev.olog.domain.gateway.SongGateway
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class FavoriteRepository @Inject constructor(
    appDatabase: AppDatabase,
    private val songGateway: SongGateway

) : FavoriteGateway {

    private val favoriteDao = appDatabase.favoriteDao()

    override fun getAll(): Flowable<List<Song>> = favoriteDao.getAllImpl()
            .subscribeOn(Schedulers.io())
            .flatMapSingle { it.toFlowable()
                    .flatMapMaybe { songId ->
                        songGateway.getAll().flatMapIterable { it }
                                .filter { (id) -> id == songId }
                                .firstElement()
                    }.toSortedList { o1, o2 -> String.CASE_INSENSITIVE_ORDER.compare(o1.title, o2.title) }

            }

    override fun addSingle(songId: Long): Completable {
        return favoriteDao.addToFavorite(listOf(songId))
    }

    override fun addGroup(songListId: List<Long>): Completable {
        return favoriteDao.addToFavorite(songListId)
    }

    override fun deleteSingle(songId: Long): Completable {
        return favoriteDao.removeFromFavorite(listOf(songId))
    }

    override fun deleteGroup(songListId: List<Long>): Completable {
        return favoriteDao.removeFromFavorite(songListId)
    }

    override fun isFavorite(songId: Long): Single<Boolean> {
        return Single.fromCallable { favoriteDao.isFavorite(songId) != null }
    }
}