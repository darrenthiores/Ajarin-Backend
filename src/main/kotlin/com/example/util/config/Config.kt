package com.example.util.config

data class Config(
    val host:String,
    val port:Int,
    val jdbcUrl:String,
    val dbUsername:String,
    val dbPassword:String,
    val jwtSecret:String,
    val jwtIssuer:String,
    val jwtAudience:String,
    val jwtRealm:String,
    val pwSalt:String
)
