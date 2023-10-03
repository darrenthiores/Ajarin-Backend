package com.example

import com.example.configuration.configureRouting
import com.example.configuration.configureSecurity
import com.example.configuration.configureSerialization
import com.example.data.database.DatabaseProvider
import com.example.di.ApiModule
import com.example.di.TableModule
import com.example.util.manager.PasswordManager
import com.example.util.config.generateConfig
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin

fun main() {
    val config = generateConfig(
        defaultEnvPath = "dev",
        hoconApplicationConfig = HoconApplicationConfig(ConfigFactory.load())
    )

    embeddedServer(
        Netty,
        port = config.port,
        host = config.host
    ) {
        org.koin.dsl.module {
            install(Koin) {
                modules(
                    org.koin.dsl.module {
                        single { config }
                        single { PasswordManager }
                    },
                    TableModule.provide,
                    ApiModule.provide,
                    org.koin.dsl.module {
                        single { DatabaseProvider() }
                    }
                )
            }
            module()
        }
    }.start(wait = true)
}

fun Application.module() {
    val databaseProvider by inject<DatabaseProvider>()
    databaseProvider.init()

    configureSerialization()
    configureSecurity()
    configureRouting()
}
