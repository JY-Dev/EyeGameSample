package com.jydev.eyegamesample.fragment.play

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import com.jydev.eyegamesample.R
import com.jydev.eyegamesample.activity.GameBaseActivity
import com.jydev.eyegamesample.util.GameDiffEnum
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_play02.view.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random


class PlayFragment02 : Fragment() {
    var MAX_X = 0
    var MAX_Y = 0
    var MIN_X = 0
    val period = 1000L
    val rpCT = 8L
    lateinit var ani : AnimatorSet
    var houseData = mutableListOf<View>()
    var disposable = CompositeDisposable()
    lateinit var birdView : View
    lateinit var mainView : View
    lateinit var houseView : LinearLayout
    lateinit var mActivity : GameBaseActivity
    private var catchCT = 0
    private var tryCT = 0
    private var gameCT = 3
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity.getGameViewModel().difficulty.observe(this, Observer {
            when (it) {
                GameDiffEnum.EASY.ordinal -> {
                    gameCT = 3
                }
                GameDiffEnum.NORMAL.ordinal -> {
                    gameCT = 4
                }
                GameDiffEnum.HARD.ordinal -> {
                    gameCT = 5
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_play02, container, false)
        houseData.addAll(mutableListOf(view.house01,view.house02,view.house03,view.house04))
        mainView = view.main_view
        birdView = view.bird_view
        houseView = view.house_view
        maxSet()
        Handler(Looper.getMainLooper()).postDelayed({
            startGame()
        },3000)

        view.bird_view.setOnClickListener {
            catchBird()
        }
        view.back_btn.setOnClickListener {
            mActivity.getGameViewModel().gotoInfo()
        }
        return view
    }


    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    private fun startGame(){
        tryCT++
        Observable.interval(period+100, TimeUnit.MILLISECONDS).map {
        }.take(rpCT).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnSubscribe {
            disposable.add(it)
            birdInit()
        }.doOnComplete {
            birdGotoHome()
        }.subscribe {
            setAnimation(getRamdomX(),getRamdomY())
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as GameBaseActivity
    }

    private fun maxSet(){
        mainView.viewTreeObserver.addOnGlobalLayoutListener {
            MAX_Y = mainView.height - birdView.height
            MAX_X = mainView.width - birdView.width
        }
    }

    private fun birdGotoHome(){
        val house = houseData[Random.nextInt(houseData.size)]
        setAnimation(house.x,MAX_Y+house.y+houseView.height)
        birdView.isEnabled = false
        Handler(Looper.getMainLooper()).postDelayed({
            clearBird()
        },1000)
    }

    private fun birdInit(){
        birdView.x = getRamdomX()
        birdView.y = getRamdomY()
        birdView.visibility = View.GONE
    }

    private fun setAnimation(x: Float, y: Float) {
        val objectAnimator1 = ObjectAnimator.ofFloat(birdView, View.TRANSLATION_X, x)
        val objectAnimator2 = ObjectAnimator.ofFloat(birdView, View.TRANSLATION_Y, y)
        ani = AnimatorSet().apply {
            playTogether(objectAnimator1, objectAnimator2)
            duration = period
            start()
            birdView.visibility = View.VISIBLE
            birdView.isEnabled = true
        }
    }

    private fun getRamdomX() = Random.nextDouble(MIN_X.toDouble(), MAX_X.toDouble()).toFloat()
    private fun getRamdomY() = Random.nextDouble(0f.toDouble(), MAX_Y.toDouble()).toFloat()

    private fun catchBird(){
        catchCT++
        if(catchCT==gameCT) mActivity.getGameViewModel().gotoEnd("총횟수 : $tryCT 잡은횟수 : $catchCT")
        else {
            clearBird()
        }

    }

    private fun clearBird(){
        disposable.dispose()
        disposable = CompositeDisposable()
        ani.cancel()
        birdView.x = 0f
        birdView.y = 0f
        birdView.visibility = View.GONE
        startGame()
    }
}