import org.giftshower.cchange.Behaviour
import org.giftshower.cchange.State.Record
import org.giftshower.cchange.data.EconAccount
import org.giftshower.cchange.data.EconPredict
import org.giftshower.cchange.data.Product

fun main() {
    Main().main()
}
class Main {
    private val marketChanges: MutableList<Double> = mutableListOf()
    private val testerMap: MutableMap<Int, EconAccount> = mutableMapOf()
    private val predictor: MutableMap<Int, EconPredict> = mutableMapOf()
    private val prePredict: MutableMap<Int, Int> = mutableMapOf()
    private val product: Product = Product("CardBoard", 1000.0, 1000.0, 1000)
    val initHoldCount = 60
    val initBalance = 15000.0
    fun main() {
        val entityCount = 100
        var loopCount = 0
        //Initialize testers with init values declared above.
        for(i in 1..entityCount) testerMap[i] = EconAccount(initHoldCount, initBalance)

        //Start testing
        while(testerMap.count() > 3){
            var n = 0
            val test: MutableList<Int> = mutableListOf()
            while(n < 100){
                var rand: Int?
                //No Conflict with previous selections.
                while (true){
                    val randTarget = (1..100).random()
                    if(!test.contains(randTarget) && testerMap.containsKey(randTarget)) {
                        rand = randTarget
                        break
                    }
                }

                //random behaviour until 10th loop
                if(loopCount < 10){
                    Behaviour().randomBehaviour(rand!!)
                    if(loopCount == 9){
                        //Start checking if its randomized transaction was beneficial.
                        when{
                            (marketChanges.last() < 0 && testerMap[rand]!!.money < initBalance) -> when {
                                (testerMap[rand]!!.money - initBalance < -3000) -> predictor[rand]!!.record
                                    .add(Pair(Record.VeryFail, testerMap[rand]!!.money - initBalance))
                                else -> predictor[rand]!!.record.add(Pair(Record.Fail, testerMap[rand]!!.money - initBalance))
                            }
                            else -> when{
                                testerMap[rand]!!.money - initBalance > 3000 -> predictor[rand]!!.record
                                    .add(Pair(Record.VerySuccess, testerMap[rand]!!.money - initBalance))
                                else -> predictor[rand]!!.record.add(Pair(Record.Success, testerMap[rand]!!.money - initBalance))
                            }
                        }
                        //END
                    }
                }
                //This is the Actual Simulation Code.
                else {
                    Behaviour().predictingCalculation(rand!!)
                }

                //Add Data
                marketChanges.add(product.value -
                        if (marketChanges.isEmpty())
                            product.medium
                        else (product.medium + marketChanges.last())
                )

                n++
            }


            //Finalize Calculation.

            loopCount++
        }
    }

    fun getTesters(): MutableMap<Int, EconAccount>{
        return testerMap
    }

    fun getProduct(): Product {
        return product
    }

    fun getMarket(): MutableList<Double>{
        return marketChanges
    }

    fun getPredictor(): MutableMap<Int, EconPredict> {
        return predictor
    }

    fun getPrePredict(): MutableMap<Int, Int> {
        return prePredict
    }
}