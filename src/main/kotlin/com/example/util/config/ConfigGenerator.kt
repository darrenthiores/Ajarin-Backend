package com.example.util.config

import com.example.util.config.Config
import io.ktor.server.config.*

fun generateConfig(
    defaultEnvPath:String,
    hoconApplicationConfig: HoconApplicationConfig
): Config {
    val defaultHoconEnv = hoconApplicationConfig.config("ktor.deployment.$defaultEnvPath")

    return try{
        Config(
            host = System.getenv("HOST"),
            port = Integer.parseInt(System.getenv("PORT")),
            jdbcUrl = System.getenv("JDBC_URL"),
            dbUsername = System.getenv("DB_USERNAME"),
            dbPassword = System.getenv("DB_PASSWORD"),
            jwtAudience = System.getenv("JWT_AUDIENCE"),
            jwtIssuer = System.getenv("JWT_ISSUER"),
            jwtRealm = System.getenv("JWT_REALM"),
            jwtSecret = System.getenv("JWT_SECRET"),
            pwSalt = System.getenv("PW_SALT")
        )
    }catch (e:Exception){
        Config(
            host = defaultHoconEnv.property("HOST").getString(),
            port = Integer.parseInt(defaultHoconEnv.property("PORT").getString()),
            jdbcUrl = defaultHoconEnv.property("JDBC_URL").getString(),
            dbUsername = defaultHoconEnv.property("DB_USERNAME").getString(),
            dbPassword = defaultHoconEnv.property("DB_PASSWORD").getString(),
            jwtAudience = defaultHoconEnv.property("JWT_AUDIENCE").getString(),
            jwtIssuer = defaultHoconEnv.property("JWT_ISSUER").getString(),
            jwtRealm = defaultHoconEnv.property("JWT_REALM").getString(),
            jwtSecret = defaultHoconEnv.property("JWT_SECRET").getString(),
            pwSalt = defaultHoconEnv.property("PW_SALT").getString()
        )
    }
}