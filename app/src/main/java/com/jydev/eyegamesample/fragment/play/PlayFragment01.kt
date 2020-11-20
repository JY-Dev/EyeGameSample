package com.jydev.eyegamesample.fragment.play

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.jydev.eyegamesample.R
import com.jydev.eyegamesample.activity.GameBaseActivity
import com.jydev.eyegamesample.util.GameDiffEnum
import com.jydev.eyegamesample.util.dp
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_play01.view.*
import kotlinx.android.synthetic.main.fragment_play01.view.back_btn
import kotlinx.android.synthetic.main.fragment_play02.view.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random


class PlayFragment01 : Fragment() {
    private val colorList = mutableListOf(
        R.drawable.nation_flag_1,
        R.drawable.nation_flag_2,
        R.drawable.nation_flag_3,
        R.drawable.nation_flag_4,
        R.drawable.nation_flag_5,
        R.drawable.nation_flag_6,
        R.drawable.nation_flag_7,
        R.drawable.nation_flag_8,
        R.drawable.nation_flag_9,
        R.drawable.nation_flag_10,
        R.drawable.nation_flag_11,
        R.drawable.nation_flag_12,
        R.drawable.nation_flag_13,
        R.drawable.nation_flag_14,
        R.drawable.nation_flag_15,
        R.drawable.nation_flag_16
    )
    lateinit var vibrator: Vibrator
    private var gameNum = 20L
    var period = 500L
    var MAX_X = 0
    var MAX_Y = 0
    val imgHeight = 100.dp
    val imgWidth = 100.dp
    var resultCt = 0
    lateinit var ani: ViewPropertyAnimator
    var disposable = CompositeDisposable()
    private lateinit var mActivity: GameBaseActivity
    lateinit var imageLayer: LinearLayout
    private var flag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity.getGameViewModel().difficulty.observe(this, Observer {
            when (it) {
                GameDiffEnum.EASY.ordinal -> {
                    period = 1000L
                    gameNum = 10
                }
                GameDiffEnum.NORMAL.ordinal -> {
                    period = 500L
                    gameNum = 20
                }
                GameDiffEnum.HARD.ordinal -> {
                    period = 400L
                    gameNum = 25
                }
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as GameBaseActivity
        vibrator = context.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_play01, container, false)
        imageLayer = view.img_layer
        imageLayer.isEnabled = false
        view.img_layer.viewTreeObserver.addOnGlobalLayoutListener {
            MAX_X = view.img_layer.width - imgWidth
            MAX_Y = view.img_layer.height - imgHeight
        }
        view.back_btn.setOnClickListener {
            mActivity.getGameViewModel().gotoInfo()
        }
        Toast.makeText(mActivity, "3초뒤에 게임이 시작됩니다.", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({
            gameInit()
            imageLayer.isEnabled = true
        }, 3000)


        view.img_layer.setOnClickListener {
            resultCase(false)
        }
        return view
    }

    @SuppressLint("CheckResult")
    private fun gameInit() {
        Observable.interval(period * 2 + 100, TimeUnit.MILLISECONDS)
            .take(gameNum+1).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).doOnSubscribe {
                disposable.add(it)
            }.doOnComplete {
                mActivity.getGameViewModel().gotoEnd("맞춘 갯수:$resultCt / $gameNum")
            }.subscribe {
                setAnimation()
            }
    }

    private fun setAnimation() {
        val layoutParams = LinearLayout.LayoutParams(imgWidth, imgHeight)
        val flagImg = ImageView(mActivity)
        flagImg.apply {
            visibility = View.GONE
            setLayoutParams(layoutParams)
            alpha = 0f
            setOnClickListener {
                ani.cancel()
            }
            animate().translationX(getRamdomX()).setDuration(0)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {}
                    override fun onAnimationEnd(p0: Animator?) {
                        animate().translationY(getRamdomY()).setDuration(0)
                            .setListener(object : Animator.AnimatorListener {
                                override fun onAnimationRepeat(p0: Animator?) {}
                                override fun onAnimationEnd(p0: Animator?) {
                                    visibility = View.VISIBLE
                                    ani = animate().alpha(1f).setDuration(period)
                                        .setListener(object : Animator.AnimatorListener {
                                            override fun onAnimationRepeat(p0: Animator?) {}
                                            override fun onAnimationEnd(p0: Animator?) {
                                                ani = animate().alpha(0F).setDuration(period)
                                                    .setListener(object :
                                                        Animator.AnimatorListener {
                                                        override fun onAnimationRepeat(p0: Animator?) {}
                                                        override fun onAnimationEnd(p0: Animator?) {
                                                            if (!flag) resultCase(false)
                                                            flag = false
                                                        }

                                                        override fun onAnimationCancel(p0: Animator?) {
                                                            resultCase(true)
                                                        }

                                                        override fun onAnimationStart(p0: Animator?) {}
                                                    })
                                            }

                                            override fun onAnimationCancel(p0: Animator?) {
                                                resultCase(true)
                                            }

                                            override fun onAnimationStart(p0: Animator?) {}
                                        })
                                }

                                override fun onAnimationCancel(p0: Animator?) {}
                                override fun onAnimationStart(p0: Animator?) {}
                            })
                    }

                    override fun onAnimationCancel(p0: Animator?) {}
                    override fun onAnimationStart(p0: Animator?) {}
                })
            setImageResource(colorList[Random.nextInt(0, colorList.size - 1)])
        }
        imageLayer.addView(flagImg)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }


    private fun resultCase(isCorrect: Boolean) {
        if (isCorrect) {
            resultCt++
            vibrator.vibrate(200)
        } else {
            vibrator.vibrate(500)
            Toast.makeText(mActivity, "틀림", Toast.LENGTH_SHORT).show()
        }
        imageLayer.removeAllViews()
        flag = true
    }

    private fun getRamdomX() = Random.nextDouble(0.toDouble(), MAX_X.toDouble()).toFloat()
    private fun getRamdomY() = Random.nextDouble(0.toDouble(), MAX_Y.toDouble()).toFloat()
}