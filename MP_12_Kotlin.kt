/*
********************
Last names: Acosta, Bunggo, Gregorio, Macasaet
Language: Kotlin
Paradigm(s): Object-Oriented, Functional, Imperative
********************
*/

import kotlin.io.path.*
import kotlin.time.*

fun main()
{
    
    val transaction = Transaction()

    transaction.readCSV()
    
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
        println("[7] Generate Reports")

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
            "7" -> transaction.GenerateReports()
            else -> {
                println("Exiting...")
                break
                }
        }
        
        println("\n**********\n")
        
    }


}

class AccountInfo(name: String, number: Long, balance: Double)
{
    var accNumber: Long = number
    var accName: String = name
    var accBalance: Double = balance
    
    fun addBalance(amount: Double)
    {
        accBalance += amount
    }
}

class TransactionRecord(name: String, number: Long, date: String, time: String, currency: String, amount: Double, type: String, balance: Double)
{
    var recordName: String = name
    var recordNumber: Long = number
    var recordDate: String = date
    var recordTime: String = time
    var recordCurrency: String = currency
    var recordAmount: Double = amount
    var recordType: String = type
    var recordBalance: Double = balance
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

    val transactionsList = mutableListOf<TransactionRecord>()

    var numOfTransaction = 0;

    var totalDep = 0.0
    var totalWit = 0.0
    var totalBal = 0.0
    var totalInt = 0.0
    var totalAcc = 0

    fun saveCSV()
    {
        val records = mutableListOf<List<String>>()

        records.add(listOf(transactionsList.get(numOfTransaction-1).recordName, transactionsList.get(numOfTransaction-1).recordNumber.toString(), transactionsList.get(numOfTransaction-1).recordDate, transactionsList.get(numOfTransaction-1).recordTime, transactionsList.get(numOfTransaction-1).recordCurrency, "%.2f".format(transactionsList.get(numOfTransaction-1).recordAmount), transactionsList.get(numOfTransaction-1).recordType, "%.2f".format(transactionsList.get(numOfTransaction-1).recordBalance)))

        writeToCSV("bank_transactions.csv", listOf("account_name","account_number","date","time","currency","amount","type","current_balance"), records)
    }

    fun readCSV()
    {
        val path = Path("bank_transactions.csv")

        if (!path.exists())
        {
            println("There is no \"bank_transactions.csv\" file.")
        } else
        {
            
            transactionsList.clear()
            numOfTransaction = 0
    
            val content = path.readText()
            val stuff = content.split('\n')
    
            var label: Boolean = true
    
            var additional: Int = 0
            for (data in stuff)
            {
    
                if (label)
                {
                    label = false
                } else
                {
                    val records = data.split(',')
    
                    // The last record have a \n and an empty space
                    if (records.size < 8)
                    {
                        continue
                    }
    
                    val sameAccount = accountList.any {it.accName == records[0]}
    
                    // Check whether there is already an account, if so just update the latest
                    if (!sameAccount)
                    {
                        accountList.add(AccountInfo(records[0], records[1].toLong(), records[7].toDouble()))
                    } else
                    {
                        val num = accountList.indexOfFirst { it.accName == records[0] }
                        (accountList.get(num)).accBalance = records[7].toDouble()
                    }
    
                    numOfTransaction += 1
                    transactionsList.add(TransactionRecord(records[0], records[1].toLong(), records[2], records[3], records[4], records[5].toDouble(), records[6], records[7].toDouble()))
                }
            }
    
        }

    }

    fun generateNumber(): Long
    {
        var max: Long = 0

        for (account in accountList)
        {
            if (max < account.accNumber)
            {
                max = account.accNumber
            }
        }

        if (accountList.isNullOrEmpty())
        {
            return 1000000001
        } else
        {
            return max+1
        }
    }

    fun generateDate(): List<String>
    {
        val now = Clock.System.now()

        val dateTime = now.toString().split('T')

        val aaaa = dateTime[0].split('-')
        val date = "${aaaa[2]}/${aaaa[1]}/${aaaa[0]}"
        val click = dateTime[1].split('.')
        
        return listOf<String>(date, click[0])
    }
    
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
                print("Initial Deposit: ")
                val amount = readln().toDoubleOrNull()

                if ((amount ?: 0.0) < 5000.00 || amount == null)
                {
                    println("Minimum of PHP 5000.00 initial deposit.")
                } else
                {
                    println("${name} is created with an initial deposit of PHP %.2f.".format(amount))

                    val number = generateNumber()
                    accountList.add(AccountInfo(name, number, amount))

                    val dateTime = generateDate()

                    transactionsList.add(TransactionRecord(name, number, dateTime[0], dateTime[1], "PHP", amount, "deposit", amount))
                    numOfTransaction += 1
                    saveCSV()

                }
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

                    val dateTime = generateDate()
                    transactionsList.add(TransactionRecord(account.accName, account.accNumber, dateTime[0], dateTime[1], "PHP", exchangedAmount, "deposit", account.accBalance))
                    
                    numOfTransaction += 1
                    saveCSV()

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

                        val dateTime = generateDate()
                        transactionsList.add(TransactionRecord(account.accName, account.accNumber, dateTime[0], dateTime[1], "PHP", exchangedAmount, "withdrawal", account.accBalance))
                        
                        numOfTransaction += 1
                        saveCSV()

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

    fun GenerateReports()
    {

        println("Generate Reports")
        println("[1] - Load the file")
        println("[2] - Generate Reports")

        println("")
        print("Choice: ")
        var choice = readln().trim()

        if(choice == "")
        {
            println("Invalid Input.")
        } else
        {
            
            println("\n**********\n")
    
            when (choice)
            {
                "1" -> {
                    println("Processing data...")
                    readCSV()
                    println("\n${numOfTransaction} rows loaded")
                }
                "2" -> {
                    if (transactionsList.isNullOrEmpty())
                    {
                        println("No Data Exist (Load or Create)")
                    } else
                    {
                        GenerateReport1()
                        print("\n**********\n")
                        GenerateReport2()
                        print("\n**********\n")
                        GenerateReport3()
                        print("\n**********\n")
                        GenerateReport4()
                        print("\n**********\n")
                        generateSummary()
                    }
                }
            }

        }


    }

    fun GenerateReport1()
    {

        totalAcc = 0

        println("Report 1: Banking Privilege Tiers")

        val data = mutableMapOf<String, MutableList<Int>>()

        for (transact in transactionsList)
        {
            val user = transact.recordName

            data.getOrPut(user) { mutableListOf() }.add(transactionsList.indexOf(transact))
        }

        val records = mutableListOf<List<String>>()

        for (user in data)
        {
            var sum: Double = 0.0
            var num: Double = 0.0

            var curr: Double = 0.0

            var startDate: String = ""
            var startTime: String = ""
            var endDate: String = ""
            var endTime: String = ""

            val today = generateDate()
            val todayParts = today[0].split('/')

            var prevDate: String = ""
            var prevCurr = 0.0

            var skip: Boolean = true
            var first: Boolean = true
            for (index in user.value)
            {
                if (first)
                {
                    first = false
                    startDate = transactionsList.get(index).recordDate
                    startTime = transactionsList.get(index).recordTime
                }

                endDate = transactionsList.get(index).recordDate
                endTime = transactionsList.get(index).recordTime

                var datee = endDate.split('/')
                
                curr = transactionsList.get(index).recordBalance

                if (todayParts[1].toInt()-1 == datee[1].toInt() && todayParts[2].toInt() == datee[2].toInt())
                {
                    
                    if (!skip)
                    {
                        var dayDiff = getDaysDifference(prevDate, "00:00:00", endDate, endTime)
                        
                        if (dayDiff > 0)
                        {
                            sum += (prevCurr * dayDiff)
                            num += dayDiff
                        }
                    }
    
                    prevCurr = curr
                    
                    skip = false
                    prevDate = endDate
                }

            }

            val endend = "01/${todayParts[1]}/${todayParts[2]}"

            val diff = getDaysDifference(prevDate, "00:00:00", endend, "00:00:00")

            sum += (prevCurr * (diff+1))
            num += diff+1

            val average = sum/num

            var tier: String
            if (average < 500000.00)
            {
                tier = "Silver"
            } else if (average < 1000000.00)
            {
                tier = "Gold"
            } else 
            {
                tier = "Platinum"
            }

            val age = getDaysDifference(startDate, startTime, endDate, endTime)

            if (average >= 100000.0)
            {
                records.add(listOf(user.key, "%,d".format(age), "%,.2f".format(curr), "%,.2f".format(average), tier))
            }
            
            totalAcc += 1
        }

        records.sortByDescending { it[3].replace(",", "").toDouble() }

        println("%20s | %20s | %20s | %20s | %20s".format("AccountName", "AccountAge", "CurrentBalance", "AverageDailyBalance", "PrivilegeTier"))
        println("%20s | %20s | %20s | %20s | %20s".format(records[0][0], records[0][1], records[0][2], records[0][3], records[0][4]))
        if (records.size > 1)
        {
            println("%20s | %20s | %20s | %20s | %20s".format(records[1][0], records[1][1], records[1][2], records[1][3], records[1][4]))
        }

        println("\n Saving to report1_banking_privilege_tiers.csv")

        writeToCSV("report1_banking_privilege_tiers.csv", listOf("AccountName", "AccountAge", "CurrentBalance", "AverageDailyBalance", "PrivilegeTier"), records)

    }

    fun GenerateReport2()
    {
        totalBal = 0.0
        totalInt = 0.0

        println("Report 2: Interest Expense Projection")

        val records = mutableListOf<List<String>>()

        for (acc in accountList)
        {
            val user = acc.accName

            val curr = acc.accBalance

            var last = acc.accBalance
            var interest: Double

            var totalInterest: Double = 0.0

            for (i in 1..30)
            {
                interest = last * (0.05/365)
                last += interest

                totalInterest += interest
            }

            if (curr >= 10000.00)
            {
                records.add(listOf(user, "%,.2f".format(curr), "%,.2f".format(totalInterest)))
            }

            totalBal += curr
            totalInt += last

        }

        records.sortByDescending { it[2].replace(",", "").toDouble() }

        println("%20s | %20s | %20s ".format("AccountName", "CurrentBalance", "ExpectedInterest"))
        println("%20s | %20s | %20s ".format(records[0][0], records[0][1], records[0][2]))
        if (records.size > 1)
        {
            println("%20s | %20s | %20s ".format(records[1][0], records[1][1], records[1][2]))
        }

        println("\n Saving to report2_interest_expense_projection.csv")

        writeToCSV("report2_interest_expense_projection.csv", listOf("AccountName", "CurrentBalance", "ExpectedInterest"), records)

    }

    fun GenerateReport3()
    {

        totalDep = 0.0
        totalWit = 0.0

        println("Report 3: Suspicious Client Behavior")

        /*
            Date
            {
                User: Indexes
                User: Indexes
            }, etc.        
        */

        val data = mutableMapOf<String, MutableMap<String, MutableList<Int>>>()

        for (transact in transactionsList)
        {
            val user = transact.recordName

            data.getOrPut(transact.recordDate) { mutableMapOf() }.getOrPut(user) { mutableListOf() }.add(transactionsList.indexOf(transact))   
        }

        val records = mutableListOf<List<String>>()

        for (day in data)
        {
            var totalWithdrawn: Double = 0.0
            var totalDeposit: Double = 0.0
            var numberDeposit: Int = 0

            for (user in day.value)
            {
                for (index in user.value)
                {
                    if (transactionsList.get(index).recordType == "deposit")
                    {
                        totalDeposit += transactionsList.get(index).recordAmount
                        numberDeposit++
                    } else
                    {
                        totalWithdrawn += transactionsList.get(index).recordAmount
                    }
                }

                var velocity: Double = (totalWithdrawn/totalDeposit) * 100

                if (totalDeposit == 0.0)
                {
                    velocity = 0.00
                }

                var flag: String = ""

                if (totalWithdrawn >= 500000.00 || velocity >= 90.0 )
                {
                    flag = "Suspicious"
                } else
                {
                    flag = "Normal"
                }

                records.add(listOf(user.key, day.key, "%,.2f".format(totalWithdrawn), "%,d".format(numberDeposit), "%,.2f".format(totalDeposit), "%,.2f".format(velocity), flag))
            
                totalDep += totalDeposit
                totalWit += totalWithdrawn
            
            }

        }

        records.sortWith(compareBy<List<String>>{ it[1].split('/')[2] }.thenBy { it[1].split('/')[1] }.thenBy { it[1].split('/')[0] }.thenByDescending {it[6]})

        println("%20s | %20s | %22s | %20s | %20s | %20s | %20s ".format("AccountName", "Date", "TotalWithdrawalAmounts", "NumberOfDeposits", "TotalDepositAmounts", "TurnoverVelocity", "RiskFlagLevel"))
        println("%20s | %20s | %22s | %20s | %20s | %20s | %20s ".format(records[0][0], records[0][1], records[0][2], records[0][3], records[0][4], records[0][5], records[0][6]))
        if (records.size > 1)
        {
            println("%20s | %20s | %22s | %20s | %20s | %20s | %20s ".format(records[1][0], records[1][1], records[1][2], records[1][3], records[1][4], records[1][5], records[1][6]))
        }
        
        println("\n Saving to report3_suspicious_client_behavior.csv")

        writeToCSV("report3_suspicious_client_behavior.csv", listOf("AccountName", "Date", "TotalWithdrawalAmounts", "NumberOfDeposits", "TotalDepositAmounts", "TurnoverVelocity", "RiskFlagLevel"), records)
    }

    fun GenerateReport4()
    {

        println("Report 4: Monthly Transaction Statistics")

        // Monthly Transaction Report
        // Year, Month, NumberOfTransaction, TotalDeposit, TotalWithdrawn, ChangeInTotalDeposit, ChangeInTotalWithdrawn

        // Year, (Month, (Users, Indices))
        val data = mutableMapOf<String, MutableMap<String, MutableMap<String, MutableList<Int>>>>()

        for (transact in transactionsList)
        {
            val user = transact.recordName

            val date = transact.recordDate.split('/')

            val year = date[2]
            val month = date[1]

            data.getOrPut(year) { mutableMapOf() }.getOrPut(month) { mutableMapOf() }.getOrPut(user) { mutableListOf() }.add(transactionsList.indexOf(transact))   
        }

        val records = mutableListOf<List<String>>()

        var prevWith = 0.0
        var prevDeposit = 0.0
        
        for (year in data)
        {

            for (month in year.value)
            {

                var totalWithdrawn = 0.0
                var totalDeposit = 0.0

                var number = 0

                for (user in month.value)
                {

                    for (index in user.value)
                    {

                        if (transactionsList.get(index).recordType == "deposit")
                        {
                            totalDeposit += transactionsList.get(index).recordAmount
                        } else
                        {
                            totalWithdrawn += transactionsList.get(index).recordAmount
                        }
                        number++

                    }

                }

                var changeWith = ((totalWithdrawn-prevWith)/prevWith) * 100
                var changeDep = ((totalDeposit-prevDeposit)/prevDeposit) * 100

                if (prevWith == 0.0)
                {
                    changeWith = 0.0
                } 
                if (prevDeposit == 0.0)
                {
                    changeDep = 0.0
                }

                records.add(listOf(year.key, month.key, number.toString(), "%,.2f".format(totalDeposit), "%,.2f".format(totalWithdrawn), "%.2f".format(changeDep), "%.2f".format(changeWith)))

                prevDeposit = totalDeposit
                prevWith = totalWithdrawn

            }

        }

        records.sortWith(compareBy<List<String>> { it[0] }.thenBy { it[1] } )

        println("%20s | %20s | %20s | %20s | %22s | %23s | %26s ".format("Year", "Month", "NumberOfTransaction", "TotalDepositAmounts", "TotalWithdrawalAmounts", "PercentageChangeDeposit", "PercentageChangeWithdrawal"))
        println("%20s | %20s | %20s | %20s | %22s | %23s | %26s ".format(records[0][0], records[0][1], records[0][2], records[0][3], records[0][4], records[0][5], records[0][6]))
        if (records.size > 1)
        {
            println("%20s | %20s | %20s | %20s | %22s | %23s | %26s ".format(records[1][0], records[1][1], records[1][2], records[1][3], records[1][4], records[1][5], records[1][6]))
        }

        println("\n Saving to report4_monthly_transaction_statistics.csv")

        writeToCSV("report4_monthly_transaction_statistics.csv", listOf("Year", "Month", "NumberOfTransaction", "TotalDepositAmounts", "TotalWithdrawalAmounts", "PercentageChangeDeposit", "PercentageChangeWithdrawal"), records)
    
    }

    fun getDaysDifference(beginDate: String, beginTime: String, endDate: String, endTime: String): Int
    {
        val start = beginDate.split('/')
        val finish = endDate.split('/')

        val sSplit = beginTime.split(':')
        val eSplit = endTime.split(':')

        val begining = Instant.parse("${start[2]}-${start[1]}-${start[0]}T%02d:${sSplit[1]}:${sSplit[2]}Z".format(sSplit[0].toInt()))
        val ending = Instant.parse("${finish[2]}-${finish[1]}-${finish[0]}T%02d:${eSplit[1]}:${eSplit[2]}Z".format(eSplit[0].toInt()))

        return ((ending - begining).inWholeDays).toInt()
    }

    fun writeToCSV(name: String, headers: List<String>, records: MutableList<List<String>>)
    {
        if (name == "bank_transactions.csv")
        {
            val file = Path(name)

            var first = true

            for (record in records)
            {
                first = true
                for (word in record)
                {
                    if (first)
                    {
                        file.appendText(word)
                        first = false
                    } else
                    {
                        file.appendText(",${word}")
                    }
                }
                file.appendText("\n")
            }
        } else
        {
            val file = Path(name)

            var first = true
            for (word in headers)
            {
                if (first)
                {
                    file.writeText("\"${word}\"")
                    first = false
                } else
                {
                    file.appendText(",\"${word}\"")
                }
            }

            file.appendText("\n")

            for (record in records)
            {
                first = true
                for (word in record)
                {
                    if (first)
                    {
                        file.appendText("\"${word}\"")
                        first = false
                    } else
                    {
                        file.appendText(",\"${word}\"")
                    }
                }
                file.appendText("\n")
            }
        }

    }


    fun generateSummary()
    {

        println("Summary State (summary.json): ")

        val file = Path("summary.json")

        val text = "{\" total_accounts \": %d, \"total_deposits\": %.2f, \"total_withdrawals\": %.2f, \"total_balance\": %.2f, \"total_interest_expense\": %.2f }".format(totalAcc, totalDep, totalWit, totalBal, totalInt)

        println(text)
            
        file.writeText(text)

    }
    
}