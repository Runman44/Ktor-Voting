package com.example

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import java.text.DateFormat

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(DefaultHeaders)
    install(CallLogging)
    install(CORS)
    {
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        method(HttpMethod.Options)
        anyHost()
        allowCredentials = true
        allowNonSimpleContentTypes = true
    }
    install(Routing) {
        votes(VotingService())
    }

    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }

    DatabaseFactory.init()
}

fun Route.votes(votingService: VotingService) {
    route("") {

        get("/v1/voting") {
            val allVotings = votingService.getAllVoting()
            call.respond(allVotings)
        }

        post("/v1/voting") {
            val newVoting = call.receive<NewVoting>()
            val addVoting = votingService.createVoting(newVoting)
            if (addVoting != null) {
                call.respond(addVoting)
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("/v1/voting/{passCode}") {
            val passCode = call.parameters["passCode"]
            if (passCode == null) {
                call.respond(HttpStatusCode.BadRequest)
            } else {
                val enterVoting = votingService.enterVoting(passCode)
                if (enterVoting == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(enterVoting)
                }
            }
        }

        delete("/v1/voting/{votingId}") {
            val removed = votingService.deleteVoting(call.parameters["votingId"]?.toInt()!!)
            if (removed) call.respond(HttpStatusCode.OK)
            else call.respond(HttpStatusCode.NotFound)
        }

        post("/v1/vote/{votingId}") {
            val vote = call.receive<NewVote>()
            val votingId = call.parameters["votingId"]?.toInt()!!
            call.respond(votingService.addVote(votingId, vote))
        }

        get("/v1/vote/{votingId}") {
            val votes = votingService.getVotes(call.parameters["votingId"]?.toInt()!!)
            call.respond(votes)
        }
    }
}

