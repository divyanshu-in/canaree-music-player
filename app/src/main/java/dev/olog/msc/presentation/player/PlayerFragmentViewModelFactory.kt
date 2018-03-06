package dev.olog.msc.presentation.player

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.msc.domain.interactor.favorite.ObserveFavoriteAnimationUseCase
import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import dev.olog.msc.interfaces.pro.IBilling
import javax.inject.Inject

class PlayerFragmentViewModelFactory @Inject constructor(
        private val observeFavoriteAnimationUseCase: ObserveFavoriteAnimationUseCase,
        private val billing: IBilling,
        private val appPrefsUseCase: AppPreferencesUseCase

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlayerFragmentViewModel(
                observeFavoriteAnimationUseCase, billing, appPrefsUseCase
        ) as T
    }
}