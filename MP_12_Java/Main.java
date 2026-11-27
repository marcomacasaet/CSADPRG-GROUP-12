/*
********************
Last names: Acosta, Bunggo, Gregorio, Macasaet
Language: Java
Paradigm(s): Object-oriented, Imperative
********************
*/


import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

public class Main {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args){

        boolean running = true;
        int choice;
        CurrencyExchange exCurr = new CurrencyExchange();
        HashMap<String, Integer> mapOfNumbers = new HashMap<>();
        HashMap<Integer, Account> mapOfAccounts = new HashMap<>();
        ArrayList<Transactions> transactionList = new ArrayList<>();
        int nAccounts = -1;
        readTransactions(mapOfAccounts, mapOfNumbers, transactionList);
        String[] headers = {"AccountName","AccountNumber","Date","Time","Currency","Amount","Type","BalanceAfterTransaction"};
        
        while(running == true){
            System.out.println("================== MAIN MENU ==================");

            System.out.println("Select transaction:");
            System.out.println("[1] Register Account Name");
            System.out.println("[2] Deposit Amount");
            System.out.println("[3] Withdraw Amount");
            System.out.println("[4] Currency Exchange");
            System.out.println("[5] Record Exchange Rates");
            System.out.println("[6] Show Interest Amount");
            System.out.println("[7] Generate Reports");
            System.out.println("[8] Exit Program");

            System.out.print("\nChoice: ");
            choice = sc.nextInt();

            System.out.println("\n***");
            System.out.println("Choice = " + choice + "\n\n\n");
            String buffer = sc.nextLine();

            switch(choice){
                case 1: 
                    registerAccount(mapOfAccounts, mapOfNumbers, transactionList, headers);
                    System.out.println("\n\n"); break;
                case 2: 
                    depositAmount(mapOfAccounts, mapOfNumbers, transactionList, exCurr, headers);
                    System.out.println("\n\n"); break;
                case 3: 
                    withdrawAmount(mapOfAccounts, mapOfNumbers, transactionList, exCurr, headers);
                    System.out.println("\n\n"); break;
                case 4: 
                    currencyExchange(mapOfAccounts, mapOfNumbers, exCurr);
                    System.out.println("\n\n"); break;
                case 5: 
                    recordExchange(exCurr);
                    System.out.println("\n\n"); break;
                case 6: 
                    showInterestAmount(mapOfAccounts, mapOfNumbers);
                    System.out.println("\n\n"); break;
                case 7:
                    generateReport(mapOfAccounts, mapOfNumbers, transactionList);
                    System.out.println("\n\n"); break;
                case 8: 
                    System.out.println("Exiting program...");
                    System.out.println("Thank you for using our Banking System.");
                    running = false; break;
                default: 
                    System.out.println("Invalid choice. Please pick again.");
                    System.out.println("\n\n"); break;
            }

        }

    }

    public static void registerAccount(HashMap<Integer,Account> mapAccounts, HashMap<String, Integer> mapOfNumbers, ArrayList<Transactions> transactionList, String[] headers){
        System.out.println("================== REGISTER ACCOUNT ==================");
        System.out.print("Account Name: ");
        String newName = sc.nextLine();

        if(mapOfNumbers.containsKey(newName)){
            System.out.println("There is already an account with that name in the database.");
            System.out.println("Please enter a valid account name.");
        } else {
            int newNum = Collections.max(mapAccounts.keySet()) + 1;
            mapAccounts.put(newNum, new Account(newName, newNum, LocalDate.now(), 5000));
            mapOfNumbers.put(newName, newNum);
            System.out.printf("Account Name = %s\n", newName);
            System.out.printf("Account Number = %d\n", newNum);
            System.out.println("Account successfully registered.");
            transactionList.add(new Transactions(mapAccounts.get(newNum), 5000f, "deposit", LocalDate.now(), LocalTime.now(), (float)mapAccounts.get(newNum).getBal(), "PHP"));
            saveTransactions("bank_transactions.csv", headers, transactionList);
        }
        
        //write account as deposit to bank_transactions.csv
        
        

        String sChoice = "N";

        while(sChoice.equals("N") || sChoice.equals("n")){
            System.out.println("Back to the Main Menu. Y/N?");
            sChoice = sc.nextLine();

            if(sChoice.equals("Y") || sChoice.equals("y")){
            return;
        }
        }
    

    }

    public static void depositAmount(HashMap<Integer,Account> mapOfAccounts, HashMap<String, Integer> mapOfNumbers, ArrayList<Transactions> transactionList, CurrencyExchange exCurr, String[] headers){
        System.out.println("================== DEPOSIT AMOUNT ==================");
        System.out.print("Account Name: ");
        String name = sc.nextLine();

        if(mapOfNumbers.containsKey(name) == false){
            System.out.println("There is no account with that name in the database.");
            System.out.println("Please enter a valid account name.");
            return;
        }

        int accNum = mapOfNumbers.get(name);

        System.out.printf("Current Balance: %.2f\n", mapOfAccounts.get(accNum).getBal());
        System.out.printf("Current Currency: %s\n", mapOfAccounts.get(accNum).getAccCurrency());

        int choice = getCurrency();
        if(choice == -1){
            System.out.println("Please enter a valid currency.");
            return;
        }

        System.out.printf("New Currency: %s\n", exCurr.getCurr(choice));
        System.out.print("Deposit Amount: ");
        double deposit = sc.nextDouble();
        String buffer = sc.nextLine();

        System.out.println("\n***");
        System.out.printf("Account Name = %s\n", mapOfAccounts.get(accNum).getName());
        System.out.printf("Deposit Amount = %.2f in %s\n", deposit, exCurr.getCurr(choice));
        mapOfAccounts.get(accNum).depositMoney(exCurr.toPeso(choice,deposit));

        System.out.printf("Current Balance in PHP: %.2f\n", mapOfAccounts.get(accNum).getBal());

        transactionList.add(new Transactions(mapOfAccounts.get(accNum), (float)deposit, "deposit", LocalDate.now(), LocalTime.now(), (float)mapOfAccounts.get(accNum).getBal(), exCurr.getCurr(choice)));
        saveTransactions("bank_transactions.csv", headers, transactionList);

        String sChoice = "N";

        while(sChoice.equals("N") || sChoice.equals("n")){
            System.out.println("Back to the Main Menu. Y/N?");
            sChoice = sc.nextLine();

            if(sChoice.equals("Y") || sChoice.equals("y")){
                return;
            }
        }

        
    }

    public static void withdrawAmount(HashMap<Integer,Account> mapOfAccounts, HashMap<String, Integer> mapOfNumbers, ArrayList<Transactions> transactionList, CurrencyExchange exCurr, String[] headers){
        System.out.println("================== WITHDRAW AMOUNT ==================");
        System.out.print("Account Name: ");
        String name = sc.nextLine();

        if(mapOfNumbers.containsKey(name) == false){
            System.out.println("There is no account with that name in the database.");
            System.out.println("Please enter a valid account name.");
            return;
        }
        
        int accNum = mapOfNumbers.get(name);

        System.out.printf("Current Balance: %.2f\n", mapOfAccounts.get(accNum).getBal());
        System.out.printf("Current Currency: %s\n", mapOfAccounts.get(accNum).getAccCurrency());

        int choice = getCurrency();
        if(choice == -1){
            System.out.println("Please enter a valid currency.");
            return;
        }

        System.out.printf("New Currency: %s\n", exCurr.getCurr(choice));
        System.out.print("Withdraw Amount: ");
        double withdraw = sc.nextDouble();
        String buffer = sc.nextLine();
        
        System.out.println("\n***");
        System.out.printf("Account Name = %s\n", mapOfAccounts.get(accNum).getName());
        System.out.printf("Withdraw Amount = %.2f in %s\n", withdraw, exCurr.getCurr(choice));
        mapOfAccounts.get(accNum).withdrawMoney(exCurr.toPeso(choice,withdraw));

        System.out.printf("Current Balance in PHP: %.2f\n", mapOfAccounts.get(accNum).getBal());

        transactionList.add(new Transactions(mapOfAccounts.get(accNum), (float)withdraw, "withdrawal", LocalDate.now(), LocalTime.now(), (float)mapOfAccounts.get(accNum).getBal(), exCurr.getCurr(choice)));
        saveTransactions("bank_transactions.csv", headers, transactionList);

        String sChoice = "N";

        while(sChoice.equals("N") || sChoice.equals("n")){
            System.out.println("Back to the Main Menu. Y/N?");
            sChoice = sc.nextLine();

            if(sChoice.equals("Y") || sChoice.equals("y")){
                return;
            }
        }

        
    }

    public static void recordExchange(CurrencyExchange exCurr){
        System.out.println("================== RECORD EXCHANGE ==================");

        int choice = getCurrency();
        if(choice == -1){
            System.out.println("Please enter a valid currency.");
            return;
        }
        

        String buffer = sc.nextLine();

        System.out.println("\n***");
        System.out.printf("Select Foreign Currency = [%d]\n", choice);
        System.out.printf("Exchange Currency = %s\n", exCurr.getCurr(choice));
        System.out.printf("Exchange Rate = %.2f\n", exCurr.getRate(choice));

        String sChoice = "N";

        while(sChoice.equals("N") || sChoice.equals("n")){
            System.out.println("Back to the Main Menu. Y/N?");
            sChoice = sc.nextLine();

            if(sChoice.equals("Y") || sChoice.equals("y")){
                return;
        }
        }

        
    }

    public static void currencyExchange(HashMap<Integer,Account> mapAccounts, HashMap<String, Integer> mapOfNumbers, CurrencyExchange exCurr){
        System.out.println("================== CURRENCY EXCHANGE ==================");
        System.out.print("Source Amount (PHP): ");
        int amt = sc.nextInt();

        int choice = getCurrency();
        if(choice == -1){
            System.out.println("Please enter a valid currency.");
            return;
        }

        String buffer = sc.nextLine();
        
        System.out.println("\n***");
        System.out.printf("Exchange Currency = %s\n", exCurr.getCurr(choice));
        System.out.printf("Exchange Rate = %.2f\n", exCurr.getRate(choice));
        System.out.printf("Converted Amount = %.2f\n", exCurr.exchangeMoney(choice,amt));

        String sChoice = "N";

        while(sChoice.equals("N") || sChoice.equals("n")){
            System.out.println("Back to the Main Menu. Y/N?");
            sChoice = sc.nextLine();

            if(sChoice.equals("Y") || sChoice.equals("y")){
                return;
        }
        }

        
    }

    public static void showInterestAmount(HashMap<Integer,Account> mapAccounts, HashMap<String, Integer> mapOfNumbers){
        System.out.println("================== SHOW INTEREST AMOUNT ==================");
        System.out.print("Account Name: ");
        String name = sc.nextLine();
        
        if(mapOfNumbers.containsKey(name) == false){
            System.out.println("There is no account with that name in the database.");
            System.out.println("Please enter a valid account name.");
            return;
        }

        int index = mapOfNumbers.get(name);
        System.out.printf("Currency Balance: %.2f\n",  mapAccounts.get(index).getBal());
        System.out.printf("Currency: %s\n", mapAccounts.get(index).getAccCurrency());
        System.out.printf("Interest Rate: %.2f\n", mapAccounts.get(index).getInterest().getIntRate());

        System.out.print("\nTotal Number of Days: ");
        int days = sc.nextInt();
        double daily;

        String buffer = sc.nextLine();

        System.out.printf("\n%3s | %10s | %10s\n","Day","Interest","Balance"); 
        int i;
        for (i = 0;i < days;i++){
            daily = mapAccounts.get(index).getInterest().getDaily(mapAccounts.get(index).getInterest().getIntRate(),(float)mapAccounts.get(index).getBal());
            System.out.printf("%3d | %10.2f | %10.2f\n", i+1, daily, mapAccounts.get(index).getBal()+(i*daily)); 
        }

        String choice = "N";

        while(choice.equals("N") || choice.equals("n")){
            System.out.println("Back to the Main Menu. Y/N?");
            choice = sc.nextLine();

            if(choice.equals("Y") || choice.equals("y")){
                return;
        }
        }

        

    }
    //report generation

    public static void generateReport(HashMap<Integer,Account> mapOfAccounts, HashMap<String, Integer> mapOfNumbers, ArrayList<Transactions> transactionList){
        
        int nTransactions = 0;
        int nAccounts = 0;
        Boolean reportsGenerated = false;
        Boolean running = true;

        while(running == true){

        System.out.println("================== GENERATE REPORTS ==================");

        if(reportsGenerated == false){
            System.out.println("Please load file to report the most accurate and recent data.");
        }

        System.out.println("\n[1] Load File");
        System.out.println("[2] Generate Reports");
        System.out.println("[3] Back to Main Menu");
        System.out.print("Enter choice: ");
        int choice = sc.nextInt();

        switch(choice){
            case 1:
                mapOfAccounts.clear();
                mapOfNumbers.clear();
                transactionList.clear(); 
                readTransactions(mapOfAccounts, mapOfNumbers, transactionList);
                reportsGenerated = true;
                break;
            case 2:
                if (reportsGenerated == false){
                    System.out.println("Please load file to report the most accurate and recent data.");
                    break;
                }

                System.out.println("The date today is: " + LocalDate.now() + ". Generating reports for the previous month: " + LocalDate.now().minusMonths(1).getMonth() + ". \n");
                privilegeTierReport(0, new ArrayList<>(mapOfAccounts.values()), transactionList);
                System.out.println();
                interestExpenseProjectionReport(0, new ArrayList<>(mapOfAccounts.values()), transactionList);
                System.out.println();
                suspiciousClientReport(0, new ArrayList<>(mapOfAccounts.values()), transactionList);
                System.out.println();
                topClients(0, new ArrayList<>(mapOfAccounts.values()), transactionList); 
                System.out.println();
                summarizeData(0, new ArrayList<>(mapOfAccounts.values()), transactionList);
                break;
            case 3:
                System.out.println("Going back to main menu.");
                running = false;
                break;
            default: System.out.println("Invalid choice."); break;
        }
    }
    }

    public static void privilegeTierReport(int i, ArrayList<Account> accounts, ArrayList<Transactions> transactions){
        ArrayList<Account> privilegedAccounts = new ArrayList<>();
        int nPrivilege = 0;
        System.out.println("================== REPORT 1: BANKING PRIVILEGE TIERS ==================\n");
        System.out.printf("\n%20s | %4s | %20s | %20s | %15s ","Account Name","Account Age","Current Balance", "Average Daily Balance","Privilege Tier");

        for(Account account : accounts){

            if (account == null || accounts.isEmpty()) {
                System.out.println("No accounts available for report.");
                return;
            }

            account.setAvgBalance(avgBalancesOfTheMonth(transactions, account));
            if(account.getAvgBalance()>=100000){
                nPrivilege++;
                privilegedAccounts.add(account);
            }
        }

        Collections.sort(privilegedAccounts,Comparator.comparing(Account::getAvgBalance,Comparator.reverseOrder()));

        for(i=0;i<5;i++){
            System.out.printf("%20s | %4s | %20.2f | %20.2f | %15s \n",privilegedAccounts.get(i).getName(),privilegedAccounts.get(i).getAccountAge(),privilegedAccounts.get(i).getBal(),privilegedAccounts.get(i).getAvgBalance(),privilegedAccounts.get(i).getPrivilegeTier());
        }

        //saving

        try(FileWriter writer = new FileWriter("AverageDailyBalance.csv");){

            String[] headers = {"AccountName","AccountAge","CurrentBalance", "AverageDailyBalance","PrivilegeTier"};
            writer.append(String.join(",", headers)).append("\n");
            for(Account account : privilegedAccounts){
                String[] accountData = {
                    account.getName(),
                    String.format("%d", account.getAccountAge()),
                    String.format("%.2f", account.getBal()),
                    String.format("%.2f", account.getAvgBalance()),
                    account.getPrivilegeTier()
                };
                writer.append(String.join(",", accountData)).append("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Saving complete date in AverageDailyBalance.csv");
    }

    public static void interestExpenseProjectionReport(int i, ArrayList<Account> accounts, ArrayList<Transactions> transactions){
        ArrayList<Account> interestedAccounts = new ArrayList<>();
        int nInterest = 0;
        System.out.println("================== REPORT 2: INTEREST EXPENSE PROJECTION ==================\n");
        System.out.printf("\n%20s | %20s | %20s \n","Account Name","Current Balance", "Expected Interest");

        for(Account account : accounts){

            if (account == null || accounts.isEmpty()) {
                System.out.println("No accounts available for report.");
                return;
            }

            if(account.getBal()>=10000){
                nInterest++;
                interestedAccounts.add(account);
            }
        }

        Collections.sort(interestedAccounts,Comparator.comparing(Account::getExpectedInterest,Comparator.reverseOrder()));

        for(i=0;i<5;i++){
            System.out.printf("%20s | %20.2f | %20.2f \n",interestedAccounts.get(i).getName(),interestedAccounts.get(i).getBal(),interestedAccounts.get(i).getExpectedInterest());
        }

        //saving

        try(FileWriter writer = new FileWriter("ExpectedInterest.csv");){

            String[] headers = {"AccountName","CurrentBalance", "ExpectedInterest"};
            writer.append(String.join(",", headers)).append("\n");
            for(Account account : interestedAccounts){
                String[] accountData = {
                    account.getName(),
                    String.format("%.2f", account.getBal()),
                    String.format("%.2f", account.getExpectedInterest())
                };
                writer.append(String.join(",", accountData)).append("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Saving complete date in ExpectedInterest.csv");
    }

    public static void suspiciousClientReport(int i, ArrayList<Account> accounts, ArrayList<Transactions> transactions){
        int nSuspicious = 0;
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm:ss");
        ArrayList<Day> days = new ArrayList<>();
        System.out.println("================== REPORT 3: SUSPICIOUS CLIENT BEHAVIOR ==================\n");
        System.out.printf("\n%20s | %20s | %20s | %20s | %20s | %20s | %20s \n","Account Name","Date", "Total Withdrawals Amount","No. of Deposits", "Total Deposits Amount", "Turnover Velocity", "Risk Flag Level");

        //split transactions by date in array list
        if (transactions == null || transactions.isEmpty()) {
            System.out.println("No transactions available for report.");
            return;
        }

        LocalDate currentDate = transactions.get(0).getDate();
        days.add(new Day(currentDate));

        for (Transactions transaction : transactions) {
            LocalDate newDate = transaction.getDate();
            if (!currentDate.equals(newDate)) {
                currentDate = newDate;
                days.add(new Day(currentDate));
            }
            days.get(days.size() - 1).addTransactions(transaction);
        }

        //get risk levels and turnover velocity for each account per day
        for(Day day : days){
            for(Account account : accounts){
                double totalWithdrawals = 0;
                double totalDeposits = 0;
                int numDeposits = 0;
                for(Transactions transaction : day.getTransactions()){
                    if(transaction.getAccountNo() == account.getNumber()){
                        if(transaction.getType().contentEquals("withdrawal")){
                            totalWithdrawals += transaction.getAmount();
                        } else if (transaction.getType().contentEquals("deposit")){
                            totalDeposits += transaction.getAmount();
                            numDeposits++;
                        }
                    }
                }
                day.setNumOfDeposits(account.getName(), numDeposits);
                day.setTotalDepositAmounts(account.getName(), (float)totalDeposits);
                day.setTotalWithdrawalAmounts(account.getName(), (float)totalWithdrawals);
                if(totalDeposits == 0){
                    day.setTurnoverVelocity(account.getName(), 0);
                    day.setRiskFlags(account.getName(), "None");
                    continue;
                }

                float turnoverVelocity = (float)((totalWithdrawals / totalDeposits) * 100);
                

                if(totalWithdrawals > 500000 || turnoverVelocity >= 90){
                    day.setRiskFlags(account.getName(), "Suspicious");
                } else {
                    day.setRiskFlags(account.getName(), "None");
                }

                day.setTurnoverVelocity(account.getName(), turnoverVelocity);
                day.accountNames.add(account.getName());
            }
        }

        for(i=0;i<5;i++){
            String name = days.get(0).getTransactions().get(i).getAccountName();
            System.out.printf("%20s | %20s | %20.2f | %20s | %20.2f | %20.2f | %20s \n",name,days.get(0).getDateToday().format(monthFormat), days.get(0).getTotalWithdrawalAmounts(name), days.get(0).getNumOfDeposits(name), days.get(0).getTotalDepositAmounts(name), days.get(0).getTurnoverVelocity(name), days.get(0).getRiskFlags(name));

        //saving

        try(FileWriter writer = new FileWriter("SuspiciousClientBehavior.csv");){

            String[] headers = {"AccountName","Date", "TotalWithdrawalsAmount","NoOfDeposits", "TotalDepositsAmount", "TurnoverVelocity", "RiskFlagLevel"};
            writer.append(String.join(",", headers)).append("\n");
            for(Day day : days){
                for(String name1 : day.getAccountNames()){
                    String[] transactionData = {
                        name1,
                        day.getDateToday().format(monthFormat),
                        String.format("%.2f", day.getTotalWithdrawalAmounts(name1)),
                        String.valueOf(day.getNumOfDeposits(name1)),
                        String.format("%.2f", day.getTotalDepositAmounts(name1)),
                        String.format("%.2f", day.getTurnoverVelocity(name1)),
                        day.getRiskFlags(name1)
                    };
                    writer.append(String.join(",", transactionData)).append("\n");
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Saving complete date in ExpectedInterest.csv");
    }
    }

    public static void topClients(int i, ArrayList<Account> accounts, ArrayList<Transactions> transactions){
        ArrayList<Account> topAccounts = new ArrayList<>();
        int nTops = 0;
        System.out.println("========================= REPORT 4: TOP CLIENTS =========================\n");
        System.out.printf("\n%20s | %20s | %20s | %20s | %20s | %20s | %20s ","Account Name","Current Balance\n", "Total Withdrawn Money", "Total Withdrawals", "Total Deposited Money", "Total Deposits", "Total Transactions");

        for(Account account : accounts){
            if(account.getTotalTransactions() >= 50){
                nTops++;
                topAccounts.add(account);
            }
        }

        Collections.sort(topAccounts,Comparator.comparing(Account::getTotalTransactions,Comparator.reverseOrder()));

        for(i=0;i<5;i++){
            System.out.printf("%20s | %20.2f | %20.2f | %20d | %20.2f | %20d | %20d \n",topAccounts.get(i).getName(),topAccounts.get(i).getBal(),topAccounts.get(i).getOvrWithdrawnAmount(), topAccounts.get(i).getTotalWithdraws(), topAccounts.get(i).getOvrDepositedAmount(), topAccounts.get(i).getTotalDeposits(), topAccounts.get(i).getTotalTransactions());
        }

        //saving

        try(FileWriter writer = new FileWriter("TopClients.csv");){

            String[] headers = {"AccountName","CurrentBalance", "TotalWithdrawnMoney", "TotalWithdrawals", "TotalDepositedMoney", "TotalDeposits", "TotalTransactions"};
            writer.append(String.join(",", headers)).append("\n");
            for(Account account : topAccounts){
                String[] accountData = {
                    account.getName(),
                    String.format("%.2f", account.getBal()),
                    String.format("%.2f", account.getOvrWithdrawnAmount()),
                    String.valueOf(account.getTotalWithdraws()),
                    String.format("%.2f", account.getOvrDepositedAmount()),
                    String.valueOf(account.getTotalDeposits()),
                    String.format("%d", account.getTotalTransactions())
                };
                writer.append(String.join(",", accountData)).append("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Saving complete date in TopClients.csv");
    }

    public static void summarizeData(int i, ArrayList<Account> accounts, ArrayList<Transactions> transactions){
        System.out.println("Summarizing data in summary.json");

        int deposits = 0;
        int withdrawals = 0;
        int totalbalance = 0;
        int totalinterest = 0;

        for(Transactions transaction : transactions){
            if(transaction.getType().contentEquals("deposit")){
                deposits++;
            } else if (transaction.getType().contentEquals("withdrawal")){
                withdrawals++;
            }
        }

        for(Account account : accounts){
            totalbalance += account.getBal();
            totalinterest += account.getExpectedInterest();
        }

        try(FileWriter writer = new FileWriter("summary.json")){
            writer.write("{totalAccounts: " + accounts.size() + 
                        ", totalDeposits: " + deposits + 
                        ", totalWithdrawals: " + withdrawals + 
                        ", totalBalance: " + totalbalance + 
                        ", totalInterest: " + totalinterest + "}");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //managing data ingestion REQ 3 to 5
    public static void readTransactions(HashMap<Integer, Account> mapAccounts, HashMap<String, Integer> mapOfNumbers, ArrayList<Transactions> listTransactions){
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm:ss");
        
        String file = "bank_transactions.csv";
        BufferedReader reader = null;
        String line = "";
        ArrayList<Integer> accountTracker = new ArrayList<>();

        try{
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine())!=null) {

                try{

                    String[] row = line.split(",");
                    String accName = row[0].trim();
                    int accNum = Integer.parseInt(row[1]);
                    LocalDate transactionDate = LocalDate.parse(row[2], monthFormat);
                    LocalTime transactionTime = LocalTime.parse(row[3], timeFormat);
                    String currency = row[4].trim();
                    float amount = Float.parseFloat(row[5]);
                    String type = row[6].trim();
                    
                    if(!type.contentEquals("deposit") && !type.contentEquals("withdrawal")){
                        continue;
                    } else if (!currency.contentEquals("PHP")){
                        continue;
                    }

                    if(accountTracker.contains(accNum)){
                        if(type.contentEquals("deposit")){
                            mapAccounts.get(accNum).depositMoney(amount);
                            Transactions transaction = new Transactions(mapAccounts.get(accNum), (float)amount, type, transactionDate, transactionTime, (float)mapAccounts.get(accNum).getBal(), currency);
                            listTransactions.add(transaction);
                        } else if (type.contentEquals("withdrawal")){
                            mapAccounts.get(accNum).withdrawMoney(amount);
                            Transactions transaction = new Transactions(mapAccounts.get(accNum), (float)amount, type, transactionDate, transactionTime, (float)mapAccounts.get(accNum).getBal(), currency);
                            listTransactions.add(transaction);
                        }
                    } else {
                        Account account = new Account(accName, accNum, transactionDate, (float)amount);
                        Transactions transaction = new Transactions(account, (float)amount, type, transactionDate, transactionTime, (float)account.getBal(), currency);
                        mapAccounts.put(accNum, account);
                        mapOfNumbers.put(accName, accNum);
                        accountTracker.add(accNum);
                        listTransactions.add(transaction);
                    }

                } catch(Exception e){
                    System.out.println("Error parsing line: " + line);
                    e.printStackTrace();
                }

            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        }

    }

    public static void saveTransactions(String fileName, String[] headers, ArrayList<Transactions> listTransactions){
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm:ss");
        try(FileWriter writer = new FileWriter(fileName);){

            writer.append(String.join(",", headers)).append("\n");
            for(Transactions transaction : listTransactions){
                String[] transactionData = {
                    transaction.getAccountName(),
                    String.valueOf(transaction.getAccountNo()),
                    transaction.getDate().format(monthFormat),
                    transaction.getTime().format(timeFormat),
                    transaction.getCurrency(),
                    String.valueOf(transaction.getAmount()),
                    transaction.getType(),
                    String.valueOf(transaction.balanceAfterTransaction())
                };
                writer.append(String.join(",", transactionData)).append("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    //helper functions

    public static float avgBalancesOfTheMonth(ArrayList<Transactions> listTransactions, Account account){

        YearMonth targetMonth = YearMonth.now().minusMonths(1);
        LocalDate monthStart = targetMonth.atDay(1);
        LocalDate monthEnd = targetMonth.atEndOfMonth();

        Map<LocalDate, Float> endOfDayBalances = new HashMap<>();
        for (Transactions transaction : listTransactions) {
            LocalDate tDate = transaction.getDate(); // if LocalDateTime, use transaction.getDate().toLocalDate()
            if (transaction.getAccountNo() == account.getNumber() && !tDate.isBefore(monthStart) && !tDate.isAfter(monthEnd)) {
                endOfDayBalances.put(tDate, (float)transaction.balanceAfterTransaction());
            }
        }

        float totalDailyBalance = 0.0f;
        for (LocalDate day = monthStart; !day.isAfter(monthEnd); day = day.plusDays(1)) {
            if (endOfDayBalances.containsKey(day)) {
                float currentBalance = endOfDayBalances.get(day);
                totalDailyBalance += currentBalance;
            }
        }

        return totalDailyBalance/targetMonth.lengthOfMonth();
    }

    public static int getCurrency(){
        System.out.println("\nSelect Currency\n");

        System.out.println("[1] Philippine Peso (PHP)");
        System.out.println("[2] United States Dollar (USD)");
        System.out.println("[3] Japanese Yen (JPY)");
        System.out.println("[4] British Pound Sterling (GBP)");
        System.out.println("[5] Euro (EUR)");
        System.out.println("[6] Chinese Yuan Renminni (CNY)\n");

        System.out.print("Select Foreign Currency: ");
        int choice = sc.nextInt();

        if (choice>0 && choice <7) return choice;
        else return -1;
    }
}
