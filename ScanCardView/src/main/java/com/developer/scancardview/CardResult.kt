package com.developer.scancardview
class CardResult {

    data class CardResult (
        val cardId: String,
        val state: String,
        val createdAt: String,
        val details: Details,
    )

    data class Details (
        val memberNumber:Data,
        val groupNumber:Data,
        val payerName:Data,
        val rxBin:Data,
        val rxPCN:Data,
        val memberName:Data,
        val dependentNames:Array<Data>,
        val planName:Data,
        val planId:Data
        )


    data class Data (
        val value: String,
        val scores: Array<String>,
    )
}