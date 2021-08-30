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
                            transactBuy(transactionAmount, target)
                            Main().getPrePredict()[target] = 2
                        } else {
                            transactBuy(1, target)
                            Main().getPrePredict()[target] = 1
                        }
                    } else if(changed > Main().getTesters()[target]!!.money / 3 * -1.4 && changed < 0 ) {
                        val transactionAmount = (12..25).random()
                        if(Main().getTesters()[target]!!.money - Main().getProduct().value * transactionAmount > 500){
                            transactBuy(transactionAmount, target)
                            Main().getPrePredict()[target] = 1
                        } else {
                            transactBuy(1, target)
                            Main().getPrePredict()[target] = 0
                        }
                    }
                }
                else {
                    //Up-Down Prediction
                    if(changed > Main().getTesters()[target]!!.money / 3 * 1.5){ //Up-Down Big
                        val have = Main().getTesters()[target]!!.productHolding
                        val transactionAmount = if(have > 30){
                            (have-20..have-5).random()
                        } else if(have in 1..29) {
                            (1..15).random()
                        } else {
                            return
                        }
                        transactSell(transactionAmount, target)
                        if(Main().getTesters()[target]!!.money < Main().initBalance / 2) {
                            Main().getPrePredict()[target] = -2
                        }
                        else Main().getPrePredict()[target] = -1
                    }

                    else if(changed > Main().getTesters()[target]!!.money / 4
                        && changed < Main().getTesters()[target]!!.money / 3 * 1.5){ //Up-Down Small
                        val have = Main().getTesters()[target]!!.productHolding
                        val transactionAmount = if(have > 30){
                            (have-20..have-10).random()
                        } else if(have in 1..29) {
                            (1..10).random()
                        } else {
                            return
                        }
                        transactSell(transactionAmount, target)
                        TODO("UD SMALL NOT FINISHED")

                    }
                }
            }
        }
    }
    private fun transactBuy(transactionAmount: Int, target: Int) {
        Main().getTesters()[target]!!.money.minus(Main().getProduct().value * transactionAmount)
        Main().getTesters()[target]!!.productHolding.plus(transactionAmount)
    }

    private fun transactSell(transactionAmount: Int, target: Int){
        val applyTO = Main().getTesters()[target]!!
        applyTO.money.plus(Main().getProduct().value * transactionAmount)
        applyTO.productHolding.minus(transactionAmount)

    }

}