package vc.rux.klinent4todobackend.di

import dagger.Module
import dagger.Provides
import vc.rux.todoclient.servers.ServerListApi
import javax.inject.Named
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Provides
    @Named(KEY_TODO_SERVER_URL)
    fun todoServerUrl() = LIVE_TODO_SERVERS_LIST

    @Provides
    @Singleton
    fun todoServerListApi(@Named(KEY_TODO_SERVER_URL) url: String): ServerListApi =
        ServerListApi.create(url)


    companion object {
        const val LIVE_TODO_SERVERS_LIST = "https://raw.githubusercontent.com/"
        const val KEY_TODO_SERVER_URL = "TODO_SERVER_URL"
    }
}