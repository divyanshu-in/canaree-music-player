package dev.olog.domain.interactor.dialog

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.FavoriteGateway
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.base.CompletableUseCaseWithParam
import dev.olog.shared.MediaIdHelper
import io.reactivex.Completable
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject

class AddToFavoriteUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val favoriteGateway: FavoriteGateway,
        private val getSongListByParamUseCase: GetSongListByParamUseCase

) : CompletableUseCaseWithParam<String>(scheduler) {

    override fun buildUseCaseObservable(param: String): Completable {
        val category = MediaIdHelper.extractCategory(param)

        return when (category) {
            MediaIdHelper.MEDIA_ID_BY_ALL -> {
                val songId = MediaIdHelper.extractLeaf(param).toLong()
                favoriteGateway.addSingle(songId)
            }
            else -> getSongListByParamUseCase.execute(param)
                    .flatMapSingle { it.toFlowable()
                            .map { it.id }
                            .toList()
                    }.flatMapCompletable(favoriteGateway::addGroup)
        }
    }
}