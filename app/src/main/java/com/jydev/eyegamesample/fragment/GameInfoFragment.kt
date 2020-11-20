package com.jydev.eyegamesample.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jydev.eyegamesample.R
import com.jydev.eyegamesample.activity.GameActivity
import com.jydev.eyegamesample.util.GameDiffEnum
import kotlinx.android.synthetic.main.fragment_game_info.*
import kotlinx.android.synthetic.main.fragment_game_info.view.*


class GameInfoFragment : Fragment() {
    private lateinit var mActivity : GameActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_game_info, container, false)
        view.button_ex_5_next.setOnClickListener {
            mActivity.clearPlayFragent()
            mActivity.getGameViewModel().gotoGame(getDifficulty())
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as GameActivity
    }

    private fun getDifficulty():Int{
        return when(rg.checkedRadioButtonId){
            R.id.rd_easy -> GameDiffEnum.EASY.ordinal
            R.id.rd_normal -> GameDiffEnum.NORMAL.ordinal
            R.id.rd_hard -> GameDiffEnum.HARD.ordinal
            else -> GameDiffEnum.EASY.ordinal
        }
    }
}