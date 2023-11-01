package se.umu.svke0008.thirty.di


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import se.umu.svke0008.thirty.domain.service.GameService
import se.umu.svke0008.thirty.domain.service.GameServiceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GameEventServiceModule {

    @Provides
    @Singleton
    fun provideGameOperationService(): GameService {
        return GameServiceImpl()
    }

}