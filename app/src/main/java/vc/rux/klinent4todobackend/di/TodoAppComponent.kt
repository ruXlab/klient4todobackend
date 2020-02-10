package vc.rux.klinent4todobackend.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton
import vc.rux.klinent4todobackend.TodoApp

@Singleton
@Component(modules = [
    AndroidInjectionModule::class, AppModule::class, RepositoryModule::class,
    FragmentsModule::class, ViewModelFactoryModule::class, ViewModelModule::class
])
interface TodoAppComponent : AndroidInjector<TodoApp> {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: TodoApp): TodoAppComponent
    }
}
