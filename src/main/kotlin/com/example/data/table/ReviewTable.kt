package com.example.data.table

import org.jetbrains.exposed.sql.Table

object ReviewTable : Table("review") {
    val id = uuid("id").autoGenerate()
    val orderId = uuid("orderId").references(OrderTable.id)
    val rating = double("rating")
    val comment = varchar("comment", length = 512)
    val imagesUrl = varchar("imagesUrl", length = 512)
    val createdAt = long("createdAt")
    val modifiedAt = long("modifiedAt")

    override val primaryKey = PrimaryKey(id)
}