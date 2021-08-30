package org.giftshower.cchange.data

import org.giftshower.cchange.State.Record

//Test Entity with product and money
data class EconAccount(var productHolding: Int, var money: Double)

//Test Entity's past data for prediction.
//Record: Was Successful?
//Double: How Successful?
data class EconPredict(val record: MutableList<Pair<Record, Double>>)

//Product
data class Product(val name: String, val medium: Double, var value: Double, var stock: Int)
