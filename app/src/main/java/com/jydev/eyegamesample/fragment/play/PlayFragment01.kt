package com.jydev.eyegamesample.fragment.play

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
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
import java.util.concurrent.TimeUnit
import kotlin.random.Random


class PlayFragment01 : Fragment() {
    private lateinit var colorList : TypedArray
    lateinit var vibrator: Vibrator
    private var gameNum = 20L
    private val GAME_DELAY = 3000L
    private var period = 500L
    private var MAX_X = 0
    private var MAX_Y = 0
    private val imgHeight = 100.dp
    private val imgWidth = 100.dp
    private var resultCt = 0
    lateinit var ani: ViewPropertyAnimator
    var disposable = CompositeDisposable()
    private lateinit var mActivity: GameBaseActivity
    lateinit var imageLayer: LinearLayout
    private var flag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity.getGameViewModel().difficulty.observe(this, Observer {
            setDifficulty(it)
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as GameBaseActivity
        vibrator = context.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
        colorList = context.resources.obtainTypedArray(R.array.flag_img_list)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_play01, container, false)
        init(view)
        return view
    }

    /**
     * Init
     */
    private fun init(view : View){
        viewInit(view)
        setMaxPosition()
        gameInit()
    }

    /**
     * 난이도 설정
     */
    private fun setDifficulty(value : Int){
        when (value) {
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
    }

    /**
     * view = Fragment View
     * 물체가 생성될 X,Y 최대 범위 SET
     */
    private fun setMaxPosition(){
        imageLayer.viewTreeObserver.addOnGlobalLayoutListener {
            MAX_X = imageLayer.width - imgWidth
            MAX_Y = imageLayer.height - imgHeight
        }
    }

    /**
     * view = Fragment View
     * 뷰 초기화
     */
    private fun viewInit(view : View){
        // 뒤로가기 버튼 => Info Fragment로 이동
        view.back_btn.setOnClickListener {
            mActivity.getGameViewModel().gotoInfo()
        }

        // 생성된 이미지 ADD 해줄 부모 View
        imageLayer = view.img_layer.apply {
            isEnabled = false
            setOnClickListener { resultCase(false) }
        }
    }

    /**
     * Game Init
     */
    private fun gameInit(){
        Toast.makeText(mActivity, "3초뒤에 게임이 시작됩니다.", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({
            gameStart()
            imageLayer.isEnabled = true
        }, GAME_DELAY)
    }


    /**
     * Game 시작
     */
    @SuppressLint("CheckResult")
    private fun gameStart() {
        Observable.interval(period * 2 + 100, TimeUnit.MILLISECONDS)
                .take(gameNum + 1).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).doOnSubscribe {
                    disposable.add(it)
                }.doOnComplete {
                    mActivity.getGameViewModel().gotoEnd("맞춘 갯수:$resultCt / $gameNum")
                }.subscribe {
                    gameRunning()
                }
    }

    /**
     * Game 진행
     */
    private fun gameRunning() {
        imageLayer.addView(createFlagImage())
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    /**
     * 게임에서 보여줄 이미지 생성
     */
    private fun createFlagImage() : ImageView{
        val layoutParams = LinearLayout.LayoutParams(imgWidth, imgHeight)
        return ImageView(mActivity).apply {
            visibility = View.GONE
            setLayoutParams(layoutParams)
            alpha = 0f
            setOnClickListener {
                ani.cancel()
            }
            // X 위치 Animation
            animate().translationX(getRamdomX()).setDuration(0)
                    .setListener(getAnimation(cancel = {}, end = {
                        // Y 위치 Animation
                        animate().translationY(getRamdomY()).setDuration(0)
                                .setListener(getAnimation(cancel = {}, end = {
                                    visibility = View.VISIBLE
                                    // Fade In Animation
                                    ani = animate().alpha(1f).setDuration(period)
                                            .setListener(getAnimation(cancel = {
                                                resultCase(true)
                                            }, end = {
                                                // Fade Out Animcation
                                                ani = animate().alpha(0F).setDuration(period)
                                                        .setListener(getAnimation(cancel = {
                                                            resultCase(true)
                                                        }, end = {
                                                            if (!flag) resultCase(false)
                                                            flag = false
                                                        }))
                                            }))
                                }))
                    }))
            setImageDrawable(colorList.getDrawable(Random.nextInt(0, colorList.length() - 1)))
        }
    }

    /**
     * 애니메이션 리스너 생성
     */
    private fun getAnimation(cancel: () -> Unit, end: () -> Unit): Animator.AnimatorListener {
        return object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}
            override fun onAnimationEnd(p0: Animator?) = end()
            override fun onAnimationCancel(p0: Animator?) = cancel()
            override fun onAnimationStart(p0: Animator?) {}
        }
    }


    /**
     * 정답 여부 Checking
     */
    private fun resultCase(isCorrect: Boolean) {
        if (isCorrect) {
            //정답일때 점수를 올려줌
            resultCt++
            vibrator.vibrate(200)
        } else
            vibrator.vibrate(500)
        clearGameView()
    }

    /**
     * GameView 초기화
     */
    private fun clearGameView(){
        imageLayer.removeAllViews()
        flag = true
    }

    /**
     * Object 생성될 위치 Postion XY 랜덤값 생성
     */
    private fun getRamdomX() = Random.nextDouble(0.toDouble(), MAX_X.toDouble()).toFloat()
    private fun getRamdomY() = Random.nextDouble(0.toDouble(), MAX_Y.toDouble()).toFloat()
}