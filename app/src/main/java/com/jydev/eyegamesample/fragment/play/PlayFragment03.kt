package com.jydev.eyegamesample.fragment.play

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.jydev.eyegamesample.R
import com.jydev.eyegamesample.activity.GameBaseActivity
import com.jydev.eyegamesample.util.GameDiffEnum
import com.jydev.eyegamesample.util.dp
import kotlinx.android.synthetic.main.fragment_play02.view.*
import kotlinx.android.synthetic.main.fragment_play03.view.*
import kotlinx.android.synthetic.main.fragment_play03.view.back_btn
import kotlinx.android.synthetic.main.fragment_play03.view.main_view
import java.util.*

class PlayFragment03 : Fragment() {
    private val roundList01 = mutableListOf(R.drawable.imground_01_01,R.drawable.imground_02_01)
    private val roundList02 = mutableListOf(R.drawable.imground_01_02,R.drawable.imground_02_02)
    private val roundList03 = mutableListOf(R.drawable.imganimal_01_03,R.drawable.imganimal_02_03,R.drawable.imganimal_03_03,R.drawable.imganimal_04_03)
    private val roundList04 = mutableListOf(R.drawable.imganimal_01_04,R.drawable.imganimal_02_04,R.drawable.imganimal_03_04,R.drawable.imganimal_04_04)
    private val roundList05 = mutableListOf(R.drawable.imganimal_01_05,R.drawable.imganimal_02_05,R.drawable.imganimal_03_05,R.drawable.imganimal_04_05)
    private val roundList06 = mutableListOf(R.drawable.imganimal_01_06,R.drawable.imganimal_02_06,R.drawable.imganimal_03_06,R.drawable.imganimal_04_06)
    private val totalRoundList = mutableListOf(roundList01,roundList02,roundList03,roundList04,roundList05,roundList06)
    private var period = 0
    lateinit var vibrator: Vibrator
    private lateinit var timeTv : TextView
    private lateinit var mainView : LinearLayout
    private var isFirst = true
    var time = Timer()
    var roundCT = 0
    var answerCT = 0
    private lateinit var mActivity : GameBaseActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        totalRoundList.forEach {
            it.shuffle()
        }
        mActivity.getGameViewModel().difficulty.observe(this, Observer {
            when (it) {
                GameDiffEnum.EASY.ordinal -> {
                    period = 15
                }
                GameDiffEnum.NORMAL.ordinal -> {
                    period = 10
                }
                GameDiffEnum.HARD.ordinal -> {
                    period = 7
                }
            }
            startGame()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_play03, container, false)
        view.back_btn.setOnClickListener {
            mActivity.getGameViewModel().gotoInfo()
        }
        timeTv = view.time_tv
        mainView = view.main_view
        return view
    }

    private fun startGame(){
        initTime()
    }

    private fun initTime(){
        setPage()
        time = Timer()
        var ct = period
        val t = object : TimerTask(){
            override fun run() {
                mActivity.runOnUiThread {
                    timeTv.text = ct.toString()
                    ct--
                    if(ct<0) {
                        wrongAnswer()
                        gotoNext()
                    }
                }
            }
        }
        time.schedule(t,0,1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        time.cancel()
    }

    private fun gotoNext(){
        time.cancel()
        if(roundCT==totalRoundList.size-1) mActivity.getGameViewModel().gotoEnd("총문항 : $roundCT/ 맞힌갯수 : $answerCT")
        else initTime()
    }

    private fun setPage(){
        mainView.removeAllViews()
        totalRoundList[roundCT].forEachIndexed { index, data ->
            mainView.addView(getImageView((index==totalRoundList[roundCT].size-1),data))
        }
        roundCT++
    }

    private fun getImageView(isLast : Boolean,image : Int) : ImageView{
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        if(!isLast) layoutParams.bottomMargin = 20.dp
        val img = ImageView(mActivity).apply {
            setLayoutParams(layoutParams)
            setImageResource(image)
            id = image
            setOnClickListener {
                when(it.id){
                    R.drawable.imground_01_01,R.drawable.imground_02_02,R.drawable.imganimal_01_06
                        ,R.drawable.imganimal_01_03,R.drawable.imganimal_03_04,R.drawable.imganimal_01_05 -> collectAnswer()
                    else -> wrongAnswer()
                }
                gotoNext()
            }

        }
        return img
    }

    private fun collectAnswer(){
        answerCT++
        vibrator.vibrate(200)
    }

    private fun wrongAnswer(){
        vibrator.vibrate(500)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as GameBaseActivity

        vibrator = context.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
    }
}