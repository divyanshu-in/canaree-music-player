package dev.olog.feature.service.music.di

import android.app.Service
import android.content.ComponentName
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.session.MediaButtonReceiver
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.feature.service.music.MusicService
import dev.olog.feature.service.music.interfaces.*
import dev.olog.feature.service.music.model.PlayerMediaEntity
import dev.olog.feature.service.music.player.PlayerImpl
import dev.olog.feature.service.music.player.PlayerVolume
import dev.olog.feature.service.music.player.crossfade.CrossFadePlayerSwitcher
import dev.olog.feature.service.music.queue.QueueManager

@Module
@InstallIn(ServiceComponent::class)
abstract class MusicServiceModule {

    @Binds
    @ServiceScoped
    internal abstract fun provideQueue(queue: QueueManager): IQueue

    @Binds
    @ServiceScoped
    internal abstract fun providePlayer(player: PlayerImpl): IPlayer

    @Binds
    @ServiceScoped
    internal abstract fun providePlayerLifecycle(player: IPlayer): IPlayerLifecycle

    @Binds
    @ServiceScoped
    internal abstract fun providePlayerVolume(volume: PlayerVolume): IMaxAllowedPlayerVolume

    @Binds
    @ServiceScoped
    internal abstract fun providePlayerImpl(impl: CrossFadePlayerSwitcher): IPlayerDelegate<PlayerMediaEntity>

    companion object {

        @Provides
        @ServiceScoped
        internal fun provideMediaSession(instance: Service): MediaSessionCompat {
            return MediaSessionCompat(
                instance,
                MusicService.TAG,
                ComponentName(instance, MediaButtonReceiver::class.java),
                null
            )
        }

        @Provides
        @ServiceScoped
        internal fun provideServiceLifecycle(instance: Service): IServiceLifecycleController {
            require(instance is MusicService)
            return instance
        }

    }

}