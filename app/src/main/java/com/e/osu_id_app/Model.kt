package com.e.osu_id_app

data class SessionCard(var title: String)

object Supplier {


    val sessions = listOf<SessionCard>(
       SessionCard("Morning Main Campus"),
        SessionCard("Afternoon Main Campus"),
        SessionCard("Morning Annex Campus"),
        SessionCard("Afternoon Annex Campus"),
        SessionCard("Session1 Session1"),
        SessionCard("Session2 Session2"),
        SessionCard("Session3 Session3"),
        SessionCard("Session4"),
        SessionCard("Session5"),
        SessionCard("Session6"),
        SessionCard("Session7"),
        SessionCard("Session8"),
        SessionCard("Session9")
    )

    /*
    val sessions = listOf<SessionCard>()

    // Get directories from storage
    */

}