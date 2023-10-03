package com.example.data.database

import com.example.data.table.MentorTable
import com.example.data.table.OrderTable
import com.example.data.table.ReviewTable
import com.example.data.table.UserTable
import com.example.util.config.Config
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DatabaseProvider: KoinComponent {
    private val config by inject<Config>()
    private val userTable by inject<UserTable>()
    private val mentorTable by inject<MentorTable>()
    private val orderTable by inject<OrderTable>()
    private val reviewTable by inject<ReviewTable>()

    fun init(){
        val database = Database.connect(
            url = config.jdbcUrl,
            user = config.dbUsername,
            driver = "org.postgresql.Driver",
            password = config.dbPassword
        )

        transaction(database) {
            SchemaUtils.create(userTable)
            SchemaUtils.create(mentorTable)
            SchemaUtils.create(orderTable)
            SchemaUtils.create(reviewTable)
        }
    }
}