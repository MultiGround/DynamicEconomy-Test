package org.giftshower.cchange

import Main
import org.giftshower.cchange.State.Record
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class Behaviour {

    //Random Behaviour. Does not work with other changes.
    fun randomBehaviour(target: Int) {
        val rand = (1..100).random()
        val product = Main().getProduct()
        val transactionAmount = (10..20).random()
        if(rand <= 50) {
            //Buy
            Main().getTesters()[target]!!.money.minus(product.value * transactionAmount)
            Main().getTesters()[target]!!.productHolding.plus(transactionAmount)
        }
        else {
            //Sell
            Main().getTesters()[target].run {
                this!!.money.plus(product.value * transactionAmount)
                this.productHolding.minus(transactionAmount)
            }
        }

        //recalculate value
        product.value = product.run {
            (this.medium * this.value) / this.stock
        }
    }

    fun predictingCalculation(target: Int){
        val record = Main().getPredictor()[target]!!.record
        val recordState: MutableMap<Record, Int> = mutableMapOf()
        var most: Int =0
        var currentState: Record = Record.VerySuccess
        record.map {
            recordState.run {
                when(this[it.first]){
                    null -> this[it.first] = 1
                    else -> this[it.first]!!.plus(1)
                }
            }
        }
        for(i in recordState.entries){
            if(i.value > most) {
                most = i.value
                currentState = i.key
            }
        }
        var changed = 0.0
        for(j in 0..Main().getMarket().lastIndex){
            changed += Main().getMarket()[j]
        }
        if(changed < 0){
            //Down-Up or Down-Down Prediction.
            if(changed < Main().getTesters()[target]!!.money / 3  * -1
                && changed > Main().getTesters()[target]!!.money / 3 * -1.7){ //Big
                val transactionAmount = (15..30).random()
                if(Main().getTesters()[target]!!.money - Main().getProduct().value * transactionAmount > 0){
                    if((1..100).random() <= 50){
                        transactBuy(transactionAmount, target)
                        Main().getPrePredict()[target] = 2
                    } else {
                        if(transactionAmount - Main().getTesters()[target]!!.productHolding < 0){
                            transactSell(transactionAmount, target)
                            Main().getPrePredict()[target] = -1
                        } else if(Main().getTesters()[target]!!.productHolding > 0
                            && transactionAmount - Main().getTesters()[target]!!.productHolding > 0) {
                            transactSell(1, target)
                            Main().getPrePredict()[target] = -1
                        }
                        else{
                            transactBuy(transactionAmount, target)
                            Main().getPrePredict()[target] = 2
                        }
                    }

                } else {
                    transactBuy(1, target)
                    Main().getPrePredict()[target] = 1
                }
            } else if(changed > Main().getTesters()[target]!!.money / 3 * -1.4 && changed < 0 ) { //Small. No D-d due to small.
                val transactionAmount = (12..25).random()
                if(Main().getTesters()[target]!!.money - Main().getProduct().value * transactionAmount > 500){
                    transactBuy(transactionAmount, target)
                    Main().getPrePredict()[target] = 1
                } else {
                    transactBuy(1, target)
                    Main().getPrePredict()[target] = 0
                }
            }
        } else {
            //Up-Down or Up-Up Prediction
            if(changed > Main().getTesters()[target]!!.money / 3 * 1.5){ //BIG
                val have = Main().getTesters()[target]!!.productHolding
                val transactionAmount = if(have > 30){
                    (have-20..have-5).random()
                } else if(have in 1..29) {
                    (1..15).random()
                } else {
                    return
                }
                if((1..100).random() <= 50){
                    //Down
                    transactSell(transactionAmount, target)
                    if(Main().getTesters()[target]!!.money < Main().initBalance / 2) {
                        Main().getPrePredict()[target] = -2
                    }
                    else Main().getPrePredict()[target] = -1
                } else {
                    //Up , if no afford then stay else give up.
                    transactBuy(if(transactionAmount * Main().getProduct().value > Main().getTesters()[target]!!.money)
                        (Main().getTesters()[target]!!.money
                                / Main().getProduct().value).roundToInt().coerceAtMost(transactionAmount)
                    else if(Main().getTesters()[target]!!.productHolding > 7) 1
                    else 0, target)

                    if(Main().getPrePredict()[target] != null){
                        Main().getPrePredict()[target] = 2
                    }
                }
            }
            else if(changed > Main().getTesters()[target]!!.money / 4
                && changed < Main().getTesters()[target]!!.money / 3 * 1.5){ //SMALL
                val have = Main().getTesters()[target]!!.productHolding
                val transactionAmount = if(have > 30){
                    (have-20..have-10).random()
                } else if(have in 1..29) {
                    (1..10).random()
                } else {
                    return
                }
                if((1..100).random() <= 70){
                    //Down
                    transactSell(transactionAmount, target)
                    Main().getPrePredict()[target] = -1
                } else {
                    //Up , if no afford then stay else give up.
                    transactBuy(if(transactionAmount * Main().getProduct().value > Main().getTesters()[target]!!.money)
                        (Main().getTesters()[target]!!.money
                                / Main().getProduct().value).roundToInt().coerceAtMost(transactionAmount - 3)
                    else if(Main().getTesters()[target]!!.productHolding > 5) 1
                    else 0, target)

                    if(Main().getPrePredict()[target] != null){
                        Main().getPrePredict()[target] = 2
                    }
                }
            }
        }

        if(currentState == Record.Fail || currentState == Record.VeryFail){
            val prPR = Main().getPrePredict()[target]

            if(prPR == 1){
                 Main().getPrePredict()[target] = 2
            } else if(prPR == -1){
                Main().getPrePredict()[target] = -2
            }
        }
    }

    private fun transactBuy(transactionAmount: Int, target: Int) {
        Main().getTesters()[target]!!.money.minus(Main().getProduct().value * transactionAmount)
        Main().getTesters()[target]!!.productHolding.plus(transactionAmount)
    }

    private fun transactSell(transactionAmount: Int, target: Int){
        if(transactionAmount == -1){
            Main().getPrePredict()[target] = -10
        }
        val applyTO = Main().getTesters()[target]!!
        applyTO.money.plus(Main().getProduct().value * transactionAmount)
        applyTO.productHolding.minus(transactionAmount)
    }
}