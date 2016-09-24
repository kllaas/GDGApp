package com.alexey_klimchuk.gdgapp.models

import java.util.*

/**
 * Created by Alex on 17.07.2016.
 */
class NoteKotlin() {

    var id: String = ""
    var name: String = ""
    var content: String = ""
    var date: Date = Date()
    var image: String = ""
    var mood: String = ""

    fun NoteKotlin(id: String, name: String, content: String,
                   date: Date, image: String, mMood: Mood) {
    }

    /**
     * Mood states
     */
    enum class Mood {
        GOOD,
        NORMAL,
        BAD
    }
}

