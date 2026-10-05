/*
********************
Last names:
Language:
Paradigm(s):
********************
*/


import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static Currency exCurr = new Currency();
    static Account[] listOfAccounts;
    static int nAccounts = -1;
    
    public static void main(String[] args){

        boolean running = true;
        int choice;
        
        while(running == true){

            System.out.println("Select transaction:");
            System.out.println("[1] Register Account Name");
            System.out.println("[2] Deposit Amount");
            System.out.println("[3] Withdraw Amount");
            System.out.println("[4] Currency Exchange");
            System.out.println("[5] Record Exchange Rates");
            System.out.println("[6] Show Interest Amount");

            System.out.println("\nChoice: ");
            choice = sc.nextInt();

            System.out.println("\n***");
            System.out.println("Choice = " + choice + "\n\n\n");

            switch(choice){
                case 1: 
                    registerAccount();
                    System.out.println("\n\n");
                break;
                case 2: break;
                case 3: break;
                case 4: break;
                case 5: break;
                case 6: break;
                default: break;
            }

        }

    }

    public static void registerAccount(){
        System.out.println("Register Account Name");
        System.out.println("Account Name: ");
        String newName = sc.nextLine();

        listOfAccounts[nAccounts+1] = new Account(newName);
        nAccounts++;

        System.out.println("\n***");
        System.out.println("Account Name = " + listOfAccounts[nAccounts].getName());
    }

    public static void depositAmount(){
        System.out.println("Deposit Amount");
        System.out.println("Account Name: ");
        String name = sc.nextLine();

        int i = 0, index;
        for(listOfAccounts acc : Account){
            if(name == acc.getName()){
                index = i;
            }
            i++;
        }

        System.out.println("Currency Balance: " + listOfAccounts[index].getBal());
        System.out.println("Currency: " + listOfAccounts[index].getAccCurrency());

        System.out.println("Deposit Amount: ");
        double deposit = sc.nextDouble();

        System.out.println("\n***");
        System.out.println("Account Name = " + listOfAccounts[index].getName());
        System.out.println("Deposit Amount = " + deposit);
        listOfAccounts[index].depositMoney(deposit);
    }

    public static void withdrawAmount(){
        System.out.println("Withdraw Amount");
        System.out.println("Account Name: ");
        String name = sc.nextLine();
        
        int i = 0, index;
        for(listOfAccounts acc: Account){
            if(name == acc.getName()){
                index = i;
            }
            i++;
        }

        System.out.println("Currency Balance: " + listOfAccounts[index].getBal());
        System.out.println("Currency: " + listOfAccounts[index].getAccCurrency());

        System.out.println("Withdraw Amount: ");
        double withdraw = sc.nextDouble();

        listOfAccounts[index].withdrawMoney(withdraw);
        System.out.println("\n***");
        System.out.println("Account Name = " + listOfAccounts[index].getName());
        System.out.println("Withdraw Amount = " + withdraw);
    }

    public static void recordExchange(){
        System.out.println("Record Exchange");

        System.out.println("[1] Philippine Peso (PHP)");
        System.out.println("[2] United States Dollar (USD)");
        System.out.println("[3] Japanese Yen (JPY)");
        System.out.println("[4] British Pound Sterling (GBP)");
        System.out.println("[5] Euro (EUR)");
        System.out.println("[6] Chinese Yuan Renminni (CNY)\n");

        System.out.println("Select Foreign Currency: ");
        int choice = sc.nextInt();
        System.out.println("Exchange Rate: " + exCurr.getRate(choice));

        System.out.println("\n***");
        System.out.println("Select Foreign Currency = [" + choice + "]");
        System.out.println("Exchange Rate = " + exCurr.getRate(choice));
    }

    public static void currencyExchange(){
        System.out.println("Foreign Currency Exchange");
        System.out.println("Source Amount (PHP): " + listOfAccounts[0].getBal());

        System.out.println("\nExchanged Currency");
        System.out.println("[1] Philippine Peso (PHP) = " + exCurr.exchangeMoney(1,listOfAccounts[0].getBal()));
        System.out.println("[2] United States Dollar (USD) = " + exCurr.exchangeMoney(2,listOfAccounts[0].getBal()));
        System.out.println("[3] Japanese Yen (JPY) = " + exCurr.exchangeMoney(3,listOfAccounts[0].getBal()));
        System.out.println("[4] British Pound Sterling (GBP) = " + exCurr.exchangeMoney(4,listOfAccounts[0].getBal()));
        System.out.println("[5] Euro (EUR) = " + exCurr.exchangeMoney(5,listOfAccounts[0].getBal()));
        System.out.println("[6] Chinese Yuan Renminni (CNY) = " + exCurr.exchangeMoney(6,listOfAccounts[0].getBal()));

        System.out.println("\n***");
        System.out.println("Source Currency = Philippine Peso (PHP)");
        System.out.println("Source Amount (PHP) = " + listOfAccounts[0].getBal());
    }
}

public class Account{

    String name;
    double balance = 1000;
    String exRate;
    Currency accCurrencyEx = new Currency();
    String accCurrency = "PHP";

    Account(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void depositMoney(double money){
        this.balance+=money;
    }

    public double getBal(){
        return this.balance;
    }

    public void withdrawMoney(double money){
        if(this.balance==0){
            System.out.println("Balance is at 0. You cannot withdraw any money.");
        } else if (money>this.balance){
            System.out.println("Balance is at " + this.balance + ". You can't withdraw " + money + " from the account.");
        } else if (money<=this.balance){
            this.balance-=money;
        }
    }

    public String getAccCurrency(){
        return this.accCurrency;
    }
}

public class Currency{

    double PHP = 1.00;
    double USD = 62.00;
    double JPY = 0.40;
    double GBP = 84.00;
    double EUR = 72.00;
    double CNY = 9.00;

    Currency(){}

    public double getRate(int choice){
        switch(choice){
            case 1: return PHP; break;
            case 2: return USD; break;
            case 3: return JPY; break;
            case 4: return GBP; break;
            case 5: return EUR; break;
            case 6: return CNY; break;
        }
    }

    public double exchangeMoney(int choice, double amt){
        switch(rate){
            case 1: return PHP*amt; break;
            case 2: return USD*amt; break;
            case 3: return JPY*amt; break;
            case 4: return GBP*amt; break;
            case 5: return EUR*amt; break;
            case 6: return CNY*amt; break;
        }
    }

    
}
