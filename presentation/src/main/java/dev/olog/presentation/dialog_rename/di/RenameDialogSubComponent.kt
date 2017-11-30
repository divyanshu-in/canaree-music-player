package dev.olog.presentation.dialog_rename.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.dialog_rename.RenameDialog


@Subcomponent(modules = arrayOf(
        RenameDialogModule::class
))
@PerFragment
interface RenameDialogSubComponent : AndroidInjector<RenameDialog> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<RenameDialog>() {

        abstract fun module(module: RenameDialogModule): Builder

        override fun seedInstance(instance: RenameDialog) {
            module(RenameDialogModule(instance))
        }
    }

}