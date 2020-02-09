package vc.rux.klinent4todobackend

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import vc.rux.klinent4todobackend.di.DaggerTodoAppComponent

class TodoApp : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
        DaggerTodoAppComponent.factory().create(this)
}