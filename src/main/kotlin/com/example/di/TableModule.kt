package com.example.di

import com.example.data.table.MentorTable
import com.example.data.table.OrderTable
import com.example.data.table.ReviewTable
import com.example.data.table.UserTable
import org.koin.dsl.module

object TableModule {
    val provide = module {
        single { UserTable }
        single { MentorTable }
        single { OrderTable }
        single { ReviewTable }
    }
}