package vc.rux.klinent4todobackend.di

import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton
import vc.rux.todoclient.servers.IServerListApi
import vc.rux.todoclient.servers.ServerListApi

@Module
class RepositoryModule {
    @Provides
    @Named(KEY_TODO_SERVER_URL)
    fun todoServerUrl() = LIVE_TODO_SERVERS_LIST

    @Provides
    @Singleton
    fun todoServerListApi(@Named(KEY_TODO_SERVER_URL) url: String): IServerListApi =
        ServerListApi.create(url)


    companion object {
        const val LIVE_TODO_SERVERS_LIST = "https://raw.githubusercontent.com/"
        const val KEY_TODO_SERVER_URL = "TODO_SERVER_URL"
    }
}
