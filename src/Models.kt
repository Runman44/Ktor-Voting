package com.example

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

data class NewVote(val voter: String, val votes: String)
data class NewVoting(val description: String, val creator: String)

object Votings : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val description = varchar("description", 255)
    val creator = varchar("creator", 255)
    val passCode = varchar("passCode", 255)
}

data class Voting(
    val id: Int,
    val description : String,
    val creator : String,
    val passCode: String
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