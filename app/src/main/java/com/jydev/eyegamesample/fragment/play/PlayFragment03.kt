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
    private val collectAnswerList = mutableListOf(R.drawable.imground_01_01,R.drawable.imground_02_02,R.drawable.imganimal_01_06
            ,R.drawable.imganimal_01_03,R.drawable.imganimal_03_04,R.drawable.imganimal_01_05)
    private val totalRoundList = mutableListOf(roundList01,roundList02,roundList03,roundList04,roundList05,roundList06)
    private var period = 0
    lateinit var vibrator: Vibrator
    private lateinit var timeTv : TextView
    private lateinit var mainView : LinearLayout
    var time = Timer()
    var roundCT = 0
    var answerCT = 0
    private lateinit var mActivity : GameBaseActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shuffle()
        mActivity.getGameViewModel().difficulty.observe(this, Observer {
            setDifficulty(it)
            initGame()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_play03, container, false)
        init(view)
        return view
    }

    /**
     * Init
     */
    private fun init(view : View){
        viewInit(view)
    }

    /**
     * 문제 이미지 순서 Shuffle
     */
    private fun shuffle(){
        totalRoundList.forEach {
            it.shuffle()
        }
    }

    /**
     * Game 난이도 설정
     */
    private fun setDifficulty(value : Int){
        when (value) {
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
    }

    /**
     * view = Fragment View
     * 뷰 초기화
     */
    private fun viewInit(view : View){
        view.back_btn.setOnClickListener {
            mActivity.getGameViewModel().gotoInfo()
        }
        timeTv = view.time_tv
        mainView = view.main_view
    }

    /**
     * Game Init
     */
    private fun initGame(){
        gameStart()
    }

    /**
     * Game 시작
     */
    private fun gameStart(){
        setPage()
        setGameTimer()
    }

    /**
     * Game 제한시간 설정
     */
    private fun setGameTimer(){
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

    /**
     * Game Next Round 끝날시에는 EndFragment로 이동
     */
    private fun gotoNext(){
        time.cancel()
        if(roundCT==totalRoundList.size-1) mActivity.getGameViewModel().gotoEnd("총문항 : $roundCT/ 맞힌갯수 : $answerCT")
        else gameStart()
    }

    /**
     * Game Page SET
     */
    private fun setPage(){
        mainView.removeAllViews()
        totalRoundList[roundCT].forEachIndexed { index, data ->
            mainView.addView(getImageView((index==totalRoundList[roundCT].size-1),data))
        }
        roundCT++
    }

    /**
     * 문제 Image 생성
     */
    private fun getImageView(isLast : Boolean,image : Int) : ImageView{
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        if(!isLast) layoutParams.bottomMargin = 20.dp
        val img = ImageView(mActivity).apply {
            setLayoutParams(layoutParams)
            setImageResource(image)
            id = image
            setOnClickListener {
                collectCheck(it.id)
                gotoNext()
            }

        }
        return img
    }

    /**
     * 정답일때
     */
    private fun collectAnswer(){
        answerCT++
        vibrator.vibrate(200)
    }

    /**
     * 정답 체크
     */
    private fun collectCheck(selectAnswer : Int){
        when(collectAnswerList.contains(selectAnswer)){
            true -> collectAnswer()
            false -> wrongAnswer()
        }
    }

    /**
     * 정답이 아닐때
     */
    private fun wrongAnswer(){
        vibrator.vibrate(500)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as GameBaseActivity
        vibrator = context.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
    }
}