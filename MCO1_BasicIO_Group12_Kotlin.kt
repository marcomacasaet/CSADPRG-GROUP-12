/*
********************
Last names:
Language:
Paradigm(s):
********************
*/

class ExchangeRate()
{
    
    val exchangeRates = mapOf(
        "PHP" to mapOf(
            "Name" to "Philippine Peso",
            "Rate" to 1.00
        ),
        "USD" to mapOf(
            "Name" to "United States Dollar",
            "Rate" to 62.00
        ),
        "JPY" to mapOf(
            "Name" to "Japanese Yen",
            "Rate" to 0.40
        ),
        "GBP" to mapOf(
            "Name" to "British Pound Sterling",
            "Rate" to 84.00
        ),
        "EUR" to mapOf(
            "Name" to "Euro",
            "Rate" to 72.00
        ),
        "CNY" to mapOf(
            "Name" to "Chinese Yuan Renminni",
            "Rate" to 9.00
        )
    )
    
    fun getName(key: String): String
    {
        return exchangeRates[key]?.get("Name") as String
    }
    
    fun getRate(key: String): Double
    {
        return exchangeRates[key]?.get("Rate") as Double
    }
    
    fun getList(): Map<String, Map<String, Any>>
    {
        return exchangeRates
    }
    
}



fun main()
{
    
    val exchangeRates = ExchangeRate()
    
    while (true)
    {
        // Main menu
        println("Select Transaction: ")
        println("[1] Register Account Name")
        println("[2] Deposit Amount")
        println("[3] Withdraw Amount")
        println("[4] Currency Exchange")
        println("[5] Record Exchange Rates")
        println("[6] Show Interest Amount\n")

        print("Choice: ")
        var choice = readln()

        println("\n***")
        println("Choice = $choice")

        when (choice)
        {
            "1" -> RegisterAccountName()
            "2" -> DepositAmount()
            "3" -> WithdrawAmount()
            "4" -> CurrencyExchange(exchangeRates)
            "5" -> RecordExchangeRate(exchangeRates)
            "6" -> println("Nothing Yet")
            else -> {
                println("ERROR")
                break
                }
        }
        
        println("\n\n\n")
        
    }


}

fun RegisterAccountName()
{

    println("Register Account Name")
    print("Account Name: ")

    val name = readln()

    println("\n***")
    println("Account Name = $name")

}

fun DepositAmount()
{

    println("Deposit Amount")
    print("Account Name: ")
    val name = readln()

    println("Current Balance: 1000.00")
    println("Currency: PHP")

    println("")

    print("Deposit Amount: ")
    val amount = readln()

    println("\n***")
    println("Account Name = $name")
    println("Deposit Amount = %.2f".format(amount))

}

fun WithdrawAmount()
{

    println("Withdraw Amount")
    print("Account Name: ")
    val name = readln()

    println("Current Balance: 1000.0")
    println("Currency: PHP")

    println("")

    print("Withdraw Amount: ")
    val amount = readln()

    println("\n***")
    println("Account Name = $name")
    println("Withdraw Amount = %.2f".format(amount))

}

fun CurrencyExchange(ExchangeRates: ExchangeRate)
{
    
    println("Foreign Currency Exchange")
    print("Source Amount (PHP): ")
    
    val amount = readln().toDouble()
    
    println("")
    
    println("Exchanged Currency")
    var i : Int = 1
    for (currency in (ExchangeRates.getList()).keys)
    {
        println("[$i] ${ExchangeRates.getName(currency)} ($currency) = %.2f".format(amount*ExchangeRates.getRate(currency)))
        i += 1
    }
    
    
    
    println("\n***")
    println("Source Currency = Philippine Peso (PHP)")
    println("Source Amount (PHP) = $amount")
    
}

fun RecordExchangeRate(ExchangeRates: ExchangeRate)
{
    
    println("Record Exchange Rate")
    println("")
    var i : Int = 1
    for (currency in (ExchangeRates.getList()).keys)
    {
        println("[$i] ${ExchangeRates.getName(currency)} ($currency)")
        i += 1
    }
    println("")
    
    print("Select Foreign Currency: ")
    val choice = readln()
    print("Exchange Rate: ")
    val rate = readln().toDouble()
    
    
    
    println("\n***")
    println("Select Foreign Currency = [$choice]")
    println("Exchange Rate = %.2f".format(rate))    
    
}