package com.e.osu_id_app

data class SessionCard(var photo1: String, var photo2: String, var photo3: String, var path: String, var title: String, var lstModDt: Long, var dateStr: String, var progressStr: String)

fun selector(p: SessionCard): Long = p.lstModDt
