package com.kingsley.livedemo

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

/**
 * Project: LiveDemo
 * Package: com.kingsley.livedemo
 * @date : 2019/8/14 9:58
 * @author : Kingsley
 * info: viewModel
 */
class UserViewModel : ViewModel() {

    private var userId = ""
    /** MutableLiveData 关联页面的数据（holder）数据更改后会通知 数据已经更改了 */
    var data: MutableLiveData<User>? = null
    /** 处理数据的类 如异步获取数据 */
    private val repository = UserRepository()

    init {
        data = repository.getUser("")
    }

    fun getUser(userId: String) {
        this.userId = userId
        if (data == null) {
            data = repository.getUser(userId)
        }else {
            data?.value = repository.getUserById(userId)
        }
    }
}