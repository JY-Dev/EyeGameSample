package com.jydev.eyegamesample.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jydev.eyegamesample.util.GameEnum

class GameViewmodel : ViewModel() {
    private val i = MutableLiveData<Int>()
    val index : LiveData<Int> get() = i
    private val diff = MutableLiveData<Int>()
    val difficulty : LiveData<Int> get() = diff
    private val text = MutableLiveData<String>()
    val gameResultText : LiveData<String> get() = text

    private fun changeIndex(index : Int){
        i.postValue(index)
    }

    fun gotoInfo(){
        changeIndex(GameEnum.INFO.ordinal)
    }

    fun gotoGame(difficulty : Int){
        changeIndex(GameEnum.GAME.ordinal)
        diff.postValue(difficulty)
    }

    fun gotoEnd(result : String){
        changeIndex(GameEnum.END.ordinal)
        text.postValue(result)
    }

    fun gotoFinish(){
        changeIndex(GameEnum.FINISH.ordinal)
    }
}