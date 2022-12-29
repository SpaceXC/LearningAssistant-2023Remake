package cn.spacexc.learningassistant2023.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.spacexc.learningassistant2023.R

/* 
LearningAssistant-2023Remake Copyright (C) 2022 XC
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
*/

/*
 * Created by XC on 2022/12/27.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

@Composable
fun ProblemCard(problemTitle: String, subject: String, updateTime: String, rate: Float) {
    Card(
        modifier = Modifier
            .clip(CardDefaults.shape)
            .fillMaxWidth()
            .clickable {

            }
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = problemTitle, fontSize = 20.sp)
            Text(text = subject)
            Text(text = "${stringResource(id = R.string.update_at_text)} $updateTime")
            Text(
                text = if (rate == 5f) stringResource(id = R.string.fully_understanded) else if (rate == 0f) stringResource(
                    id = R.string.completely_misunderstand
                ) else "${stringResource(id = R.string.understanded)} ${(rate * 10).toInt()}%",
                color = if (rate == 5f) MaterialTheme.colorScheme.primary else if (rate == 0f) MaterialTheme.colorScheme.error else Color.Unspecified
            )
        }
    }
}