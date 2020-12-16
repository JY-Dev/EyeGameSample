package com.jydev.eyegamesample.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.jydev.eyegamesample.*
import com.jydev.eyegamesample.fragment.GameEndFragment
import com.jydev.eyegamesample.fragment.GameInfoFragment
import com.jydev.eyegamesample.fragment.play.PlayFragment01
import com.jydev.eyegamesample.fragment.play.PlayFragment02
import com.jydev.eyegamesample.fragment.play.PlayFragment03
import com.jydev.eyegamesample.util.GameEnum
import com.jydev.eyegamesample.util.GameMethod
import com.jydev.eyegamesample.util.NumClass.whatPlay
import com.jydev.eyegamesample.util.PlayEnum
import com.jydev.eyegamesample.viewmodel.GameViewmodel

open class GameBaseActivity() : AppCompatActivity(),
    GameMethod {
    private val gameViewmodel: GameViewmodel by viewModels()
    private lateinit var fragmentTransaction: FragmentTransaction
    private val fragmentList = mutableListOf<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game01)
        viewInit()
        viewModelInit()
    }

    private fun viewInit(){
        fragmentList.addAll(mutableListOf(
            GameInfoFragment(), Fragment() ,
            GameEndFragment()
        ))
        gameViewmodel.gotoInfo()
    }

    /**
     * Game Fragment 초기화
     */
    fun clearPlayFragent(){
        fragmentList[GameEnum.GAME.ordinal] = getPlayFragment()
    }

    /**
     *  선택한 Game Fragment
     */
    private fun getPlayFragment() : Fragment {
        return when (whatPlay) {
            PlayEnum.Game01.ordinal -> PlayFragment01()
            PlayEnum.Game02.ordinal -> PlayFragment02()
            PlayEnum.Game03.ordinal -> PlayFragment03()
            else -> PlayFragment01()
        }
    }

    /**
     * 화면 이동
     */
    override fun changeFragment(index: Int) {
        fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragmentList[index]).commitAllowingStateLoss()
    }

    /**
     * ViewModel Init
     */
    private fun viewModelInit(){
        gameViewmodel.index.observe(this, Observer {
            if(it != GameEnum.FINISH.ordinal)
                changeFragment(it)
            else finish()
        })
    }

    /**
     * Get ViewModel
     */
    override fun getGameViewModel(): GameViewmodel {
        return gameViewmodel
    }
}