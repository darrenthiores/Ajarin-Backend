package com.example.data.table

import com.example.model.response.user.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object UserTable : Table("user") {
    val id = uuid("id").autoGenerate()
    val username = varchar("username", length = 255)
    val imageUrl = varchar("imageUrl", length = 255).nullable()
    val roleId = integer("roleId")
    val email = varchar("email", length = 255)
    val password = varchar("password", length = 512)
    val number = varchar("number", length = 255)
    val createdAt = long("createdAt")
    val modifiedAt = long("modifiedAt")

    override val primaryKey = PrimaryKey(id)

    fun ResultRow.toUser(): User {
        return User(
            id = this[id].toString(),
            username = this[username],
            roleId = this[roleId],
            email = this[email],
            number = this[number],
            createdAt = this[createdAt],
            modifiedAt = this[modifiedAt]
        )
    }
}