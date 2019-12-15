package com.example

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

data class NewVoting(val type: String)
data class NewVote(val voter: String, val votes: String)

object Votings : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val type = varchar("type", 255)
}

data class Voting(
    val id: Int,
    val type: String
)

object Votes : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val votingId = integer("votingId")
        .references(Votings.id, onDelete = ReferenceOption.CASCADE)
    val voter = varchar("voter", 255)
    val votes = varchar("votes", 255)
}

data class Vote(
    val id: Int,
    val votingId: Int,
    val voter: String,
    val votes: String
)