package com.jydev.eyegamesample.fragment.play

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
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
import kotlin.math.abs
import kotlin.random.Random


class PlayFragment02 : Fragment() {
    var MAX_X = 0
    var MAX_Y = 0
    var MIN_X = 0
    var MIN_Y = 0
    val period = 1000L
    val DELAY_TIME = 3000L
    lateinit var vibrator: Vibrator
    val rpCT = 8L
    lateinit var house : View
    lateinit var ani : AnimatorSet
    var houseData = mutableListOf<View>()
    var disposable = CompositeDisposable()
    lateinit var objectView : View
    lateinit var mainView : View
    lateinit var houseView : LinearLayout
    lateinit var mActivity : GameBaseActivity
    lateinit var topView : LinearLayout
    private var findCT = 0
    private var tryCT = 0
    private var gameCT = 3
    private var houseHeight = 0
    private val weight = 500
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity.getGameViewModel().difficulty.observe(this, Observer {
                setDifficulty(it)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_play02, container, false)
        init(view)
        return view
    }

    /**
     * Init
     */
    private fun init(view : View){
        viewInit(view)
        houseSet(view)
        setMaxPosition()
        gameInit()
    }

    /**
     * Game Init
     */
    private fun gameInit(){
        Toast.makeText(mActivity, "3초뒤에 게임이 시작됩니다.", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({
            startGame()
        },DELAY_TIME)
    }


    /**
     * view = Fragment View
     * 뷰 초기화
     */
    private fun viewInit(view : View){
        mainView = view.main_view
        objectView = view.object_view
        houseView = view.house_view
        topView = view.top_layer
        // 뒤로가기 버튼 InfoFragment로 이동
        view.back_btn.setOnClickListener {
            mActivity.getGameViewModel().gotoInfo()
        }
    }

    /**
     * view = Fragment View
     * House View Set (Object가 들어갈 Container)
     */
    private fun houseSet(view : View){
        houseData.addAll(mutableListOf(view.house01,view.house02,view.house03,view.house04))
        houseClickSet()
    }

    /**
     * Game 난이도 설정
     */
    private fun setDifficulty(value : Int){
        when (value) {
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
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    /**
     * Game 시작
     */
    @SuppressLint("CheckResult")
    private fun startGame(){
        //시도 카운트 증가
        tryCT++
        Observable.interval(period+100, TimeUnit.MILLISECONDS).map {
        }.take(rpCT).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnSubscribe {
            disposable.add(it)
            objectInit()
        }.doOnComplete {
            objectGotoHome()
        }.subscribe {
            setAnimation(getRamdomX(),getRamdomY(),false)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as GameBaseActivity
        vibrator = context.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
    }
    /**
     * 물체가 이동할 X,Y 최대 범위 SET
     */
    private fun setMaxPosition(){
        mainView.viewTreeObserver.addOnGlobalLayoutListener {
            MAX_Y = mainView.height - objectView.height
            MAX_X = mainView.width - objectView.width
            MIN_Y = topView.height
            houseHeight = houseView.height
        }
    }

    /**
     * Object를 Random Container로 이동
     */
    private fun objectGotoHome(){
        house = houseData[Random.nextInt(houseData.size)]
        setAnimation(house.x,MAX_Y+houseHeight.toFloat()*2,true)
        objectView.isEnabled = false
        ani.doOnEnd {
            objectView.visibility = View.GONE
            houseEnable()
        }
    }

    /**
     * Object가 Container로 들어가기 전까지 Container Click 못하기 위한 Enable set
     */
    private fun houseEnable(){
        houseData.forEach {
            it.isEnabled = !it.isEnabled
        }
    }

    /**
     * 정답 여부 Check
     */
    private fun houseClickSet(){
        houseEnable()
        houseData.forEach {
            it.setOnClickListener {view ->
                if(view==house) {
                    vibrator.vibrate(200)
                    findObject()
                }
                else {
                    vibrator.vibrate(500)
                    clearObject()
                }
                houseEnable()
            }
        }
    }

    /**
     * Object Init
     */
    private fun objectInit(){
        // Object 위치 설정 초기에는 Object 숨김
        objectView.x = getRamdomX()
        objectView.y = getRamdomY()
        objectView.visibility = View.GONE
    }

    /**
     * Object Random 이동 Animcation
     */
    private fun setAnimation(x: Float, y: Float,ishome:Boolean) {
        var trnX = x
        var trnY = y
        if(!ishome){
            while(abs(objectView.x-trnX)<weight&&abs(objectView.y-trnY)<weight){
                    trnX = getRamdomX()
                    trnY = getRamdomY()
            }
        }
        val objectAnimator1 = ObjectAnimator.ofFloat(objectView, View.TRANSLATION_X, trnX)
        val objectAnimator2 = ObjectAnimator.ofFloat(objectView, View.TRANSLATION_Y, trnY)
        ani = AnimatorSet().apply {
            playTogether(objectAnimator1, objectAnimator2)
            duration = period
            start()
            objectView.visibility = View.VISIBLE
            objectView.isEnabled = true
        }
    }

    /**
     * Object를 찾았을때
     */
    private fun findObject(){
        findCT++
        if(findCT==gameCT) mActivity.getGameViewModel().gotoEnd("총횟수 : $tryCT 잡은횟수 : $findCT")
        else clearObject()
    }

    /**
     * Object 초기화
     */
    private fun clearObject(){
        disposable.dispose()
        disposable = CompositeDisposable()
        ani.cancel()
        objectView.x = 0f
        objectView.y = 0f
        objectView.visibility = View.GONE
        startGame()
    }

    /**
     * Object가 이동할 위치 Postion XY 랜덤값 생성
     */
    private fun getRamdomX() = Random.nextDouble(MIN_X.toDouble(), MAX_X.toDouble()).toFloat()
    private fun getRamdomY() = Random.nextDouble(MIN_Y.toDouble(), MAX_Y.toDouble()).toFloat()
}