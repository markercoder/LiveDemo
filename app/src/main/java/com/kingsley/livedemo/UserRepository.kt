package com.kingsley.livedemo

import android.arch.lifecycle.MutableLiveData

/**
 * Project: LiveDemo
 * Package: com.kingsley.livedemo
 * @date : 2019/8/14 10:41
 * @author : Kingsley
 * info:
 */
class UserRepository {
    private var users: List<User>? = null

    fun getUser(userId: String): MutableLiveData<User> {
        val data = MutableLiveData<User>()
        data.value = getUserById(userId)
        return data
    }

    init {
        users = arrayListOf(
            User("1", "渣渣"),
            User("2", "卡卡"),
            User("3", "啦啦"),
            User("4", "洒洒"))
    }

    fun getUserById(userId: String): User? {
        var tempUser: User? = null
        users?.forEach {
            if (it.userId == userId) {
                tempUser = it
            }
        }
        return tempUser
    }
}