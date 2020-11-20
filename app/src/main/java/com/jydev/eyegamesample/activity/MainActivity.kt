package com.jydev.eyegamesample.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.jydev.eyegamesample.R
import com.jydev.eyegamesample.util.NumClass
import com.jydev.eyegamesample.util.PlayEnum
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        game_btn01.setOnClickListener(onClick)
        game_btn02.setOnClickListener(onClick)
        game_btn03.setOnClickListener(onClick)
    }

    private val onClick = View.OnClickListener{
        when(it){
            game_btn01 -> NumClass.whatPlay = PlayEnum.Game01.ordinal
            game_btn02 -> NumClass.whatPlay = PlayEnum.Game02.ordinal
            game_btn03 -> NumClass.whatPlay = PlayEnum.Game03.ordinal
        }
        startActivity(Intent(this, GameActivity::class.java))
    }


}