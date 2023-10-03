package com.example.configuration

import com.example.data.api.AuthApi.login
import com.example.data.api.AuthApi.register
import com.example.data.api.MentorApi.getMentor
import com.example.data.api.MentorApi.getMentorById
import com.example.data.api.MentorApi.getMentors
import com.example.data.api.MentorApi.registerAsMentor
import com.example.data.api.MentorApi.searchMentors
import com.example.data.api.OrderApi.createOrder
import com.example.data.api.OrderApi.getOrderById
import com.example.data.api.OrderApi.getOrdersAsMentor
import com.example.data.api.OrderApi.getOrdersAsUser
import com.example.data.api.OrderApi.updateOrder
import com.example.data.api.ReviewApi.createReview
import com.example.data.api.ReviewApi.getMentorReviews
import com.example.data.api.UserApi.getUser
import com.example.data.api.UserApi.getUserById
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        route("/user") {
            register("/register")
            login("/login")

            authenticate {
                getUser("")
                getUserById("")
            }
        }

        route("/mentor") {
            authenticate {
                registerAsMentor("/register")
                getMentors("/{page}")
                getMentor("")
                getMentorById("")
                searchMentors("/search/{page}")
            }
        }

        route("/order") {
            authenticate {
                createOrder("/create")
                updateOrder("/update")
                getOrdersAsUser("/user/{page}")
                getOrdersAsMentor("/mentor/{page}")
                getOrderById("")
            }
        }

        route("/review") {
            authenticate {
                createReview("/create")
                getMentorReviews("/mentor/{page}")
            }
        }
    }
}
