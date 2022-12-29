package cn.spacexc.learningassistant2023.entity

import cn.leancloud.LCObject
import java.text.DateFormat

/* 
LearningAssistant-2023Remake Copyright (C) 2022 XC
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
*/

/*
 * Created by XC on 2022/12/29.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

data class Problem(
    var problemObjectID: String? = null,
    var subject: String,
    var problemSource: String,
    var problemText: String,
    var wrongAnswer: String,
    var correctAnswer: String,
    var problemImageUrl: String,
    var wrongAnswerImageUrl: String,
    var correctImageUrl: String,
    var causeReason: String,
    var updateTimeString: String,
    var updateTimeStamp: String,
    var problemRate: String
)

fun LCObject.toProblemEntity(): Problem {
    return Problem(
        problemObjectID = objectId,
        subject = getString("subject") ?: "",
        problemSource = getString("problemSource") ?: "",
        problemText = getString("problem") ?: "",
        wrongAnswer = getString("wrongAnswer") ?: "",
        correctAnswer = getString("correctAnswer") ?: "",
        problemImageUrl = getString("problemImagePath") ?: "",
        wrongAnswerImageUrl = getString("wrongAnswerImagePath") ?: "",
        correctImageUrl = getString("correctAnswerImagePath") ?: "",
        causeReason = getString("reason") ?: "",
        updateTimeStamp = updatedAt.time.toString(),
        updateTimeString = DateFormat.getDateInstance().format(updatedAt),
        problemRate = getString("probRate") ?: ""
    )
}