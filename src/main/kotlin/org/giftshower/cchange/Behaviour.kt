package org.giftshower.cchange

import Main
import org.giftshower.cchange.State.Record

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
        when{
            (currentState == Record.VerySuccess) -> {
                if(changed < 0){
                    //Down-Up Prediction. No Down-Down Prediction Due to continuous success.
                    if(changed < Main().getTesters()[target]!!.money / 3  * -1 && changed > Main().getTesters()[target]!!.money / 3 * -1.7){
                        val transactionAmount = (15..30).random()
                        if(Main().getTesters()[target]!!.money - Main().getProduct().value * transactionAmount > 0){
                            transact(transactionAmount, target)
                            Main().getPrePredict()[target] = 2
                        } else {
                            transact(1, target)
                            Main().getPrePredict()[target] = 1
                        }
                    } else if(changed > Main().getTesters()[target]!!.money / 3 * -1.4 && changed < 0 ) {
                        val transactionAmount = (12..25).random()
                        if(Main().getTesters()[target]!!.money - Main().getProduct().value * transactionAmount > 500){
                            transact(transactionAmount, target)
                            Main().getPrePredict()[target] = 1
                        } else {
                            transact(1, target)
                            Main().getPrePredict()[target] = 0
                        }
                    }
                }
                else {
                    //Up-Down Prediction

                }
            }
        }
    }
    fun transact(transactionAmount: Int, target: Int) {
        Main().getTesters()[target]!!.money.minus(Main().getProduct().value * transactionAmount)
        Main().getTesters()[target]!!.productHolding.plus(transactionAmount)
    }
}