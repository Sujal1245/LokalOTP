package com.sujal.lokalotp.di

import com.sujal.lokalotp.data.OtpManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOtpManager(): OtpManager {
        return OtpManager()
    }
}
