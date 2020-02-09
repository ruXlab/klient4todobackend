package vc.rux.klinent4todobackend.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.HasAndroidInjector
import vc.rux.klinent4todobackend.TodoApp
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, AppModule::class, RepositoryModule::class, FragmentsModule::class])
interface TodoAppComponent: AndroidInjector<TodoApp> {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: TodoApp): TodoAppComponent
    }


}