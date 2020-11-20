package com.jydev.eyegamesample.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.jydev.eyegamesample.R
import com.jydev.eyegamesample.activity.GameBaseActivity
import kotlinx.android.synthetic.main.fragment_game_end.view.*


class GameEndFragment : Fragment() {
    private lateinit var mActivity : GameBaseActivity
    private lateinit var resultTv : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity.getGameViewModel().gameResultText.observe(this, Observer {
            resultTv.text = it
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game_end, container, false)
        view.button_ex4_next.setOnClickListener {
            mActivity.getGameViewModel().gotoFinish()
        }
        resultTv = view.textView_d82
        view.button_ex4_prev.setOnClickListener {
            mActivity.getGameViewModel().gotoInfo()
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as GameBaseActivity
    }
}