/*
********************
Last names: Acosta, Bunggo, Gregorio, Macasaet
Language: Kotlin
Paradigm(s): Object-Oriented, Functional, Imperative
********************
*/

fun main()
{
    
    val transaction = Transaction()
    
    while (true)
    {
        // Main menu
        println("Select Transaction: ")
        println("[1] Register Account Name")
        println("[2] Deposit Amount")
        println("[3] Withdraw Amount")
        println("[4] Currency Exchange")
        println("[5] Record Exchange Rates")
        println("[6] Show Interest Amount")

        print("Choice: ")
        var choice = readln().trim()

        if(choice == "")
        {
            continue
        }
        
        println("\n**********\n")

        when (choice)
        {
            "1" -> transaction.RegisterAccountName()
            "2" -> transaction.DepositAmount()
            "3" -> transaction.WithdrawAmount()
            "4" -> transaction.CurrencyExchange()
            "5" -> transaction.RecordExchangeRate()
            "6" -> transaction.ShowInterestAmount()
            else -> {
                println("Exiting...")
                break
                }
        }
        
        println("\n**********\n")
        
    }


}

class AccountInfo(name: String)
{
    var accName: String = name
    var accBalance: Double = 0.0
    
    fun addBalance(amount: Double)
    {
        accBalance += amount
    }
}

class ExchangeRate()
{
    
    val exchangeRates = mutableMapOf(
        "PHP" to mutableMapOf(
            "Philippine Peso" to 1.00
        ),
        "USD" to mutableMapOf(
            "United States Dollar" to 62.00
        ),
        "JPY" to mutableMapOf(
            "Japanese Yen" to 0.40
        ),
        "GBP" to mutableMapOf(
            "British Pound Sterling" to 84.00
        ),
        "EUR" to mutableMapOf(
            "Euro" to 72.00
        ),
        "CNY" to mutableMapOf(
            "Chinese Yuan Renminni" to 9.00
        )
    )
    
    fun getCurrName(key: String): String
    {
        return exchangeRates[key]?.keys?.first() as String
    }
    
    fun getCurrRate(key: String): Double
    {
        return exchangeRates[key]?.values?.first() as Double
    }
    
    fun getCurrList(): Map<String, Map<String, Any>>
    {
        return exchangeRates
    }
    
    fun setCurrRate(key : String, rate : Double)
    {
        val nam = exchangeRates[key]?.keys?.first()
        
        if (nam != null)
        {
            exchangeRates[key]?.put(nam, rate)
        }
    }
    
}

class Transaction()
{
    
    val exchangeRates = ExchangeRate()
    // val accountList = mutableMapOf<String, Double>()
    val accountList = mutableListOf<AccountInfo>()
    
    fun RegisterAccountName()
    {
        var isExited: Boolean = false
        var choice: String
        
        while (!isExited)
        {
            println("Register Account Name")
            print("Account Name: ")

            val name = readln().trim()
            
            val sameAccount = accountList.any {it.accName == name}
            if (name.isBlank())
            {
                println("Cannot be blanked.")
            } else if (!sameAccount)
            {
                accountList.add(AccountInfo(name))
                println("${name} is created.")
                println()
            } else
            {
                println("${name} already existed.")
            }
            
            print("\n\nBack to the Main Menu (Y/N): ")
            choice = readln().trim()
            
            if (choice == "Y" || choice == "y")
            {
                isExited = !isExited
            } else 
            {
                println("\n")
            }
        }

    }

    fun DepositAmount()
    {

        var isExited: Boolean = false
        var input: String
        
        while (!isExited)
        {
            
            println("Deposit Amount")
            print("Account Name: ")
            val name = readln().trim()
            
            val account = accountList.find {it.accName == name}

            if (account != null)
            {
                println("Current Balance: PHP ${"%.2f".format(account.accBalance)}")
                
                println("Currency Option: ")
                var i : Int = 1
                for (currency in (exchangeRates.getCurrList()).keys)
                {
                    println("[$i] ${exchangeRates.getCurrName(currency)} ($currency)")
                    i += 1
                }

                print("Select Currency: ")
                val source = readln().toIntOrNull()

                if (source == null)
                {
                    println("Invalid Input.\n")
                    continue
                }

                if (source > 0 && source <= (exchangeRates.getCurrList()).count())
                {
                    val convert: Double = exchangeRates.getCurrRate(exchangeRates.getCurrList().keys.elementAt(source-1))

                    println("")

                    print("Deposit Amount: ")
                    val amount = readln().toDoubleOrNull()

                    if (amount == null)
                    {
                        println("Invalid Input.\n")
                        continue
                    }

                    val exchangedAmount = (amount*convert)
                    
                    account.accBalance = account.accBalance + exchangedAmount

                    println("\n${exchangeRates.getCurrList().keys.elementAt(source-1)} ${"%.2f".format(amount)} = PHP ${"%.2f".format(exchangedAmount)}")

                    println("Updated Amount = PHP %.2f".format(account.accBalance))
                } else
                {
                    println("Invalid Input")
                }
                
            } else
            {
                println("${name} does not exist.")
            }  
        
            print("\n\nBack to the Main Menu (Y/N): ")
            input = readln()
            
            if (input == "Y" || input == "y")
            {
                isExited = !isExited
            } else 
            {
                println("\n")
            }
        }

    }

    fun WithdrawAmount()
    {

        var isExited: Boolean = false
        var input: String
        
        while (!isExited)
        {
            println("Withdraw Amount")
            print("Account Name: ")
            val name = readln().trim()

            val account = accountList.find {it.accName == name}

            if (account != null)
            {
                println("Current Balance: PHP ${"%.2f".format(account.accBalance)}")

                println("Currency Option: ")
                var i : Int = 1
                for (currency in (exchangeRates.getCurrList()).keys)
                {
                    println("[$i] ${exchangeRates.getCurrName(currency)} ($currency)")
                    i += 1
                }

                print("Select Currency: ")
                val source = readln().toIntOrNull()

                if (source == null)
                {
                    println("Invalid Input.\n")
                    continue
                }

                if (source > 0 && source <= exchangeRates.getCurrList().count())
                {
                    val convert: Double = exchangeRates.getCurrRate(exchangeRates.getCurrList().keys.elementAt(source-1))

                    println("")

                    print("Deposit Amount: ")
                    val amount = readln().toDoubleOrNull()

                    if (amount == null)
                    {
                        println("Invalid Input.\n")
                        continue
                    }

                    val exchangedAmount = (amount*convert)
                
                    if (account.accBalance >= amount)
                    {
                        account.accBalance = account.accBalance - exchangedAmount

                        println("\n${exchangeRates.getCurrList().keys.elementAt(source-1)} ${"%.2f".format(amount)} = PHP ${"%.2f".format(exchangedAmount)}")
                        
                        println("Updated Amount = PHP %.2f".format(account.accBalance))
                    } else
                    {
                        println("Insufficient Balance")
                    }
                } else
                {
                    println("Invalid Input")
                }
                
            } else
            {
                println("${name} does not exist.")
            }  

            print("\n\nBack to the Main Menu (Y/N): ")
            input = readln()
            
            if (input == "Y" || input == "y")
            {
                isExited = !isExited
            } else 
            {
                println("\n")
            }
        }
    }

    fun CurrencyExchange()
    {

        var isExited: Boolean = false
        var input: String
        
        while (!isExited)
        {
            println("Foreign Currency Exchange")
            println("Source Currency Option: ")
            var i : Int = 1
            for (currency in (exchangeRates.getCurrList()).keys)
            {
                println("[$i] ${exchangeRates.getCurrName(currency)} ($currency)")
                i += 1
            }
            
            println("")
            
            print("Source Currency: ")
            val source = readln().toIntOrNull()

            if (source == null)
            {
                println("Invalid Input.\n")
                continue
            }
        
            print("Source Amount: ")
            val amount = readln().toDoubleOrNull()

            if (amount == null)
            {
                println("Invalid Input.\n")
                continue
            }
            
            val curr1: String = exchangeRates.getCurrList().keys.elementAt(source-1)
            val ex1: Double = exchangeRates.getCurrRate(curr1)
            
            println("")
            
            println("Exchanged Currency Option: ")
            var j : Int = 1
            for (currency in (exchangeRates.getCurrList()).keys)
            {
                println("[$j] ${exchangeRates.getCurrName(currency)} ($currency)")
                j += 1
            }
            
            println("")
            
            print("Exchange Currency: ")
            val exchange = readln().toIntOrNull()

            if (exchange == null)
            {
                println("Invalid Input.\n")
                continue
            }
            
            val curr2: String = exchangeRates.getCurrList().keys.elementAt(exchange-1)
            val ex2: Double = exchangeRates.getCurrRate(curr2)
            
            println("Exchange Amount: %.2f".format((amount*ex1) / ex2))

            println("\n%.2f $curr1 == %.2f $curr2".format(amount, (amount*ex1) / ex2))
            
            print("\n\nConvert another currency (Y/N)? ")
            input = readln().trim()
            
            if (input == "N" || input == "n")
            {
                isExited = !isExited
            } else 
            {
                println("\n")
            }
        }
    }

    fun RecordExchangeRate()
    {

        var isExited: Boolean = false
        var input: String
        
        while (!isExited)
        {
            println("Record Exchange Rate")
            println("")
            var i : Int = 1
            for (currency in (exchangeRates.getCurrList()).keys)
            {
                println("[$i] ${exchangeRates.getCurrName(currency)} ($currency)")
                i += 1
            }
            println("")
            
            print("Select Foreign Currency: ")
            val choice = readln().toIntOrNull()

            if (choice == null)
            {
                println("Invalid Input.\n")
                continue
            }
            print("Exchange Rate: ")
            val rate = readln().toDoubleOrNull()

            if (rate == null)
            {
                println("Invalid Input.\n")
                continue
            }
            
            val curr: String = exchangeRates.getCurrList().keys.elementAt(choice-1)
            exchangeRates.setCurrRate(curr, rate)
            
            println("\n$curr == %.2f".format(rate))
        
            print("\n\nBack to the Main Menu (Y/N): ")
            input = readln().trim()
            
            if (input == "Y" || input == "y")
            {
                isExited = !isExited
            } else
            {
                println("\n")
            }
        }
    }

    fun ShowInterestAmount()
    {

        var isExited: Boolean = false
        var input: String
        
        while (!isExited)
        {
            println("Show Interest Amount")
            print("Account Name: ")
            
            val name = readln().trim()
            
            val account = accountList.find {it.accName == name}

            if (account != null)
            {
                println("Current Balance: ${account.accBalance}")
                println("Currency: PHP")
                println("Interest Rate: 5%")
                
                print("Total Number of Days: ")
                val input = readln().trim().split(" ") // Splits into max 2 parts
        
                val number = input[0].toIntOrNull()

                if (number == null)
                {
                    println("Invalid Input.\n\n")
                    continue
                }
                
                var last = account.accBalance
                var interest: Double
                
                println("Day | Interest | Balance |")
                
                for (i in 1..number)
                {
                    interest = last * (0.05/365)
                    last += interest
                    println("%-3d | %-8.2f | %-8.2f".format(i, interest, last))
                }
                
            } else
            {
                println("${name} does not exist.")
            }  
        
            print("\n\nBack to the Main Menu (Y/N): ")
            input = readln().trim()
            
            if (input == "Y" || input == "y")
            {
                isExited = !isExited
            } else
            {
                println("\n")
            }
            
        }
    }
    
}