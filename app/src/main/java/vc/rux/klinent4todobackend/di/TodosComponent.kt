package vc.rux.klinent4todobackend.di

import dagger.Module
import dagger.Subcomponent

@Subcomponent
interface TodosComponent {

    // Factory that is used to create instances of this subcomponent
    @Subcomponent.Factory
    interface Factory {
        fun create(): TodosComponent
    }

//    fun inject(loginActivity: LoginActivity)
}


@Module(subcomponents = [TodosComponent::class])
class TodosModule