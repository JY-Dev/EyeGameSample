package com.jydev.eyegamesample.util

import com.jydev.eyegamesample.viewmodel.GameViewmodel

interface GameMethod {
    fun changeFragment(index : Int)
    fun getGameViewModel() : GameViewmodel
}