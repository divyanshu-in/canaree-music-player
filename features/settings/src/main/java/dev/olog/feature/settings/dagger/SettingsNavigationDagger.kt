package dev.olog.feature.settings.dagger

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoMap
import dev.olog.feature.settings.SettingsFragmentWrapper
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.screens.FragmentScreen

@Module
@InstallIn(ApplicationComponent::class)
object SettingsNavigationDagger {

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.SETTINGS)
    fun provideFragment(): Fragment {
        return SettingsFragmentWrapper()
    }

}