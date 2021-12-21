package com.developer.scancardview

import android.app.Application

public class CardData : Application() {
    companion object {
        @JvmField
        var result: CardResult.CardResult? = null
    }
}