package vc.rux.klinent4todobackend.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import vc.rux.klinent4todobackend.ui.todoservers.TodoServersFragment


@Module
abstract class FragmentsModule {
    @ContributesAndroidInjector
    abstract fun bindServersFragment(): TodoServersFragment
}
