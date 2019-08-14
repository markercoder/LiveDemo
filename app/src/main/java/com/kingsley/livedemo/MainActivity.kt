package com.kingsley.livedemo

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var viewModel : UserViewModel? = null
    var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 获取viewModel
        viewModel = ViewModelProviders.of(this).get(UserViewModel()::class.java)
        // 设置监听
        viewModel!!.data!!.observe(this, Observer {
            Log.d("MainActivity","$it")
            it?.userName?.let { it1 -> tv_demo.text = it1 }
        })

        // 设置点击事件
        tv_demo.setOnClickListener{
            if (index == 4){
                index = 0
            }
            index++
            viewModel?.getUser("$index")
        }
    }

}
