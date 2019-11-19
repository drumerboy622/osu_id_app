package com.e.osu_id_app

data class SessionCard(var title: String)

object Supplier {

    val sessions = listOf<SessionCard>(
       SessionCard("Morning Main Campus"),
        SessionCard("Afternoon Main Campus"),
        SessionCard("Morning Annex Campus"),
        SessionCard("Afternoon Annex Campus"),
        SessionCard("Session1"),
        SessionCard("Session2"),
        SessionCard("Session3")
    )

}