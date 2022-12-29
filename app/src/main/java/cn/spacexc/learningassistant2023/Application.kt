package cn.spacexc.learningassistant2023

import cn.leancloud.LeanCloud

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

const val VERSION_NAME = "Rel-AL 0.0.2"
const val TAG = "LearningAssistantTag"

class Application : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        LeanCloud.initialize(
            this,
            "1rG00zBIl97ITGRiXHTxpGa2-gzGzoHsz",
            "Fm77njR24SpW7RWbhJJvwvpG",
            "https://1rg00zbi.lc-cn-n1-shared.com"
        )
    }
}