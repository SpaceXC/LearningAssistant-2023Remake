package cn.spacexc.learningassistant2023.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import cn.leancloud.LCException
import cn.leancloud.LCObject
import cn.leancloud.LCQuery
import cn.leancloud.LCUser
import cn.leancloud.livequery.LCLiveQuery
import cn.leancloud.livequery.LCLiveQueryEventHandler
import cn.leancloud.livequery.LCLiveQuerySubscribeCallback
import cn.spacexc.learningassistant2023.R
import cn.spacexc.learningassistant2023.TAG
import io.reactivex.Observer
import io.reactivex.disposables.Disposable


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

class ProblemListViewModel(application: Application) : AndroidViewModel(application) {
    val problemList = mutableStateOf(listOf<LCObject>())

    init {
        getProblems {
            if (!it) Toast.makeText(
                application,
                application.getString(R.string.sync_failed),
                Toast.LENGTH_SHORT
            ).show()
        }
        val query = LCQuery<LCObject>("Problems")
        query.whereEqualTo("user", LCUser.currentUser())
        val liveQuery: LCLiveQuery = LCLiveQuery.initWithQuery(query)
        liveQuery.setEventHandler(object : LCLiveQueryEventHandler() {
            override fun onObjectCreated(newObject: LCObject) {
                super.onObjectCreated(newObject)
                val temp = problemList.value.toMutableList()
                temp.add(newObject)
                problemList.value = temp
            }

            override fun onObjectUpdated(LCObject: LCObject, updateKeyList: MutableList<String>?) {
                super.onObjectUpdated(LCObject, updateKeyList)
                val temp = problemList.value.toMutableList()
                val problemToUpdate = temp.find {
                    it.objectId == LCObject.objectId
                }
                val index = temp.indexOf(problemToUpdate)
                temp[index] = LCObject
                problemList.value = temp
            }

            override fun onObjectEnter(
                updatedProblem: LCObject,
                updateKeyList: MutableList<String>?
            ) {
                super.onObjectEnter(updatedProblem, updateKeyList)
                val temp = problemList.value.toMutableList()
                temp.add(updatedProblem)
                problemList.value = temp
            }

            override fun onObjectLeave(LCObject: LCObject, updateKeyList: MutableList<String>?) {
                super.onObjectLeave(LCObject, updateKeyList)
                val temp = problemList.value.toMutableList()
                val problemToDelete = temp.find {
                    it.objectId == LCObject.objectId
                }
                temp.remove(problemToDelete)
                problemList.value = temp
            }

            override fun onObjectDeleted(objectId: String?) {
                super.onObjectDeleted(objectId)
                val temp = problemList.value.toMutableList()
                val problemToDelete = temp.find {
                    it.objectId == objectId
                }
                temp.remove(problemToDelete)
                problemList.value = temp
            }
        })
        liveQuery.subscribeInBackground(object : LCLiveQuerySubscribeCallback() {
            override fun done(e: LCException?) {
                Log.d(TAG, "done: subscribed to live query")
            }
        })
    }

    fun getProblems(callback: (Boolean) -> Unit = {}) {
        val query = LCQuery<LCObject>("Problems")
        query.whereEqualTo("user", LCUser.currentUser())
        query.findInBackground().subscribe(object : Observer<List<LCObject>> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(t: List<LCObject>) {
                problemList.value = t
                callback(true)
            }

            override fun onError(e: Throwable) {
                callback(false)
            }

            override fun onComplete() {}
        })
    }
}