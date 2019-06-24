package dev.olog.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.core.entity.track.Song
import dev.olog.data.db.entities.HistoryEntity
import dev.olog.data.db.entities.PodcastHistoryEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
abstract class HistoryDao {


    @Query("""
        SELECT * FROM song_history
        ORDER BY dateAdded
        DESC LIMIT 200
    """)
    internal abstract fun getAllImpl(): Flowable<List<HistoryEntity>>

    @Query("""
        SELECT * FROM podcast_song_history
        ORDER BY dateAdded
        DESC LIMIT 200
    """)
    internal abstract fun getAllPodcastsImpl(): Flowable<List<PodcastHistoryEntity>>

    @Query("""DELETE FROM song_history""")
    abstract fun deleteAll()

    @Query("""DELETE FROM podcast_song_history""")
    abstract fun deleteAllPodcasts()

    @Query("""
        DELETE FROM song_history
        WHERE id = :songId
    """)
    abstract fun deleteSingle(songId: Long)

    @Query("""
        DELETE FROM podcast_song_history
        WHERE id = :podcastId
    """)
    abstract fun deleteSinglePodcast(podcastId: Long)

    fun getAllAsSongs(songList: Single<List<Song>>): Observable<List<Song>> {
        return getAllImpl().toObservable()
                .flatMapSingle { ids -> songList.flatMap { songs ->
                    val result : List<Song> = ids
                            .asSequence()
                            .mapNotNull { historyEntity ->
                                val song = songs.firstOrNull { it.id == historyEntity.songId }
                                song?.copy(idInPlaylist = historyEntity.id)
                            }.toList()
                    Single.just(result)
                } }
    }

    fun getAllPodcasts(podcastList: Single<List<Song>>): Observable<List<Song>> {
        return getAllPodcastsImpl().toObservable()
                .flatMapSingle { ids -> podcastList.flatMap { songs ->
                    val result : List<Song> = ids
                            .asSequence()
                            .mapNotNull { historyEntity ->
                                val song = songs.firstOrNull { it.id == historyEntity.podcastId }
                                song?.copy(idInPlaylist = historyEntity.id)
                            }.toList()
                    Single.just(result)
                } }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertImpl(entity: HistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertPodcastImpl(entity: PodcastHistoryEntity)

    fun insert(id: Long): Completable {
        return Completable.fromCallable{ insertImpl(HistoryEntity(songId = id)) }
    }

    fun insertPodcasts(id: Long): Completable {
        return Completable.fromCallable{ insertPodcastImpl(PodcastHistoryEntity(podcastId = id)) }
    }

}
