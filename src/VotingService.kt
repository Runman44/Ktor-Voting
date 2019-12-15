package com.example

import com.example.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*

class VotingService {

    suspend fun addVoting(newVoting: NewVoting): Voting {
        var key: Int? = 0
        dbQuery {
            key = Votings.insert({
                it[type] = newVoting.type
            }) get Votings.id
        }
        return getVoting(key!!)!!
    }

    suspend fun getVoting(id: Int): Voting? = dbQuery {
        Votings.select {
            (Votings.id eq id)
        }.mapNotNull { toVoting(it) }
            .singleOrNull()
    }

    suspend fun deleteVoting(votingId: Int): Boolean = dbQuery {
        Votings.deleteWhere { Votings.id eq votingId } > 0
    }

    suspend fun addVote(votingIdz : Int, vote: NewVote): Vote {
        var key: Int? = 0
        dbQuery {
            key = Votes.insert({
                it[votingId] = votingIdz
                it[voter] = vote.voter
                it[votes] = vote.votes
            }) get Votes.id
        }
        return getVote(key!!)!!
    }

    suspend fun getVote(voteId: Int): Vote? = dbQuery {
        Votes.select {
            (Votes.id eq voteId)
        }.mapNotNull { toVotes(it) }
            .singleOrNull()
    }

    suspend fun getVotes(votingId: Int): List<Vote> =
        dbQuery { Votes.select { (Votes.votingId eq votingId) }.map { toVotes(it) } }

    suspend fun getAllVoting(): List<Voting> {
        return dbQuery { Votings.selectAll().map { toVoting(it) } }
    }

    private fun toVoting(row: ResultRow): Voting =
        Voting(
            id = row[Votings.id],
            type = row[Votings.type]
        )

    private fun toVotes(row: ResultRow): Vote =
        Vote(
            id = row[Votes.id],
            votingId = row[Votes.votingId],
            voter = row[Votes.voter],
            votes = row[Votes.votes]
        )
}