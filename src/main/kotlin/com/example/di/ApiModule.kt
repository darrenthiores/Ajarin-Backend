package com.example.di

import com.example.data.api.*
import org.koin.dsl.module

object ApiModule {
    val provide = module {
        single { AuthApi }
        single { UserApi }
        single { MentorApi }
        single { OrderApi }
        single { ReviewApi }
    }
}