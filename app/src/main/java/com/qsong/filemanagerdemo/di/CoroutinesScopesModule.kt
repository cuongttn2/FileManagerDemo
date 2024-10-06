package com.qsong.filemanagerdemo.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScopeIO

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScopeMain

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScopeMainIm

@InstallIn(SingletonComponent::class)
@Module
object CoroutinesScopesModule {

    @Singleton
    @ApplicationScope
    @Provides
    fun providesCoroutineAppScope(
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
    ): CoroutineScope = CoroutineScope(SupervisorJob() + defaultDispatcher)

    @Singleton
    @ApplicationScopeIO
    @Provides
    fun providesIOCoroutineAppScope(
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): CoroutineScope = CoroutineScope(SupervisorJob() + ioDispatcher)

    @Singleton
    @ApplicationScope
    @Provides
    fun providesMainCoroutineAppScope(
        @MainDispatcher mainDispatcher: CoroutineDispatcher,
    ): CoroutineScope = CoroutineScope(SupervisorJob() + mainDispatcher)

    @Singleton
    @ApplicationScope
    @Provides
    fun providesMainImCoroutineAppScope(
        @MainImmediateDispatcher mainImDispatcher: CoroutineDispatcher,
    ): CoroutineScope = CoroutineScope(SupervisorJob() + mainImDispatcher)


}
