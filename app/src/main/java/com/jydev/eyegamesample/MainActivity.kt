package com.jydev.eyegamesample

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.View.TRANSLATION_X
import android.view.View.TRANSLATION_Y
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    var MAX_X = 0
    var MAX_Y = 0
    var MIN_X = 0
    val period = 1000L
    val rpCT = 5L
    lateinit var ani : AnimatorSet
    var houseData = mutableListOf<View>()
    var disposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        houseData.addAll(mutableListOf(house01,house02,house03))
        maxSet()
        test_btn.setOnClickListener {
            Observable.interval(period, TimeUnit.MILLISECONDS).map {
            }.take(rpCT).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnSubscribe {
                disposable.add(it)
                birdInit()
            }.doOnComplete {
                birdGotoHome()
            }.subscribe {
                setAnimation(getRamdomX(),getRamdomY())
            }
        }

        bird_view.setOnClickListener {
            catchBird()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    private fun maxSet(){
        main_view.viewTreeObserver.addOnGlobalLayoutListener {
            MAX_Y = main_view.height - bird_view.height
            MAX_X = house_view.width + main_view.width - bird_view.width
            MIN_X = house_view.width
        }
    }

    private fun birdGotoHome(){
        val house = houseData[Random.nextInt(houseData.size)]
        setAnimation(house.x+(bird_view.width/2),house.y+(bird_view.height/2))
        bird_view.isEnabled = false
    }

    private fun birdInit(){
        bird_view.x = getRamdomX()
        bird_view.y = getRamdomY()
        bird_view.visibility = View.GONE
    }

    private fun setAnimation(x: Float, y: Float) {
        val objectAnimator1 = ObjectAnimator.ofFloat(bird_view, TRANSLATION_X, x)
        val objectAnimator2 = ObjectAnimator.ofFloat(bird_view, TRANSLATION_Y, y)
        ani = AnimatorSet().apply {
            playTogether(objectAnimator1, objectAnimator2)
            duration = period
            start()
            bird_view.visibility = View.VISIBLE
            bird_view.isEnabled = true
        }
    }

    private fun getRamdomX() = Random.nextDouble(MIN_X.toDouble(), MAX_X.toDouble()).toFloat()
    private fun getRamdomY() = Random.nextDouble(0f.toDouble(), MAX_Y.toDouble()).toFloat()

    private fun catchBird(){
        disposable.dispose()
        disposable = CompositeDisposable()
        ani.cancel()
        bird_view.x = 0f
        bird_view.y = 0f
        bird_view.visibility = View.GONE
    }
}