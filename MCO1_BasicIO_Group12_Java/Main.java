/*
********************
Last names: Acosta, Bunggo, Gregorio, Macasaet
Language: Java
Paradigm(s):
********************
*/


import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args){

        boolean running = true;
        int choice;
        Currency exCurr = new Currency();
        ArrayList<Account> listOfAccounts = new ArrayList<>();
        int nAccounts = -1;
        
        while(running == true){

            System.out.println("Select transaction:");
            System.out.println("[1] Register Account Name");
            System.out.println("[2] Deposit Amount");
            System.out.println("[3] Withdraw Amount");
            System.out.println("[4] Currency Exchange");
            System.out.println("[5] Record Exchange Rates");
            System.out.println("[6] Show Interest Amount");

            System.out.print("\nChoice: ");
            choice = sc.nextInt();

            System.out.println("\n***");
            System.out.println("Choice = " + choice + "\n\n\n");
            String buffer = sc.nextLine();

            switch(choice){
                case 1: 
                    registerAccount(listOfAccounts, nAccounts);
                    System.out.println("\n\n");
                break;
                case 2: 
                    depositAmount(listOfAccounts, nAccounts);
                    System.out.println("\n\n"); break;
                case 3: 
                    withdrawAmount(listOfAccounts, nAccounts);
                    System.out.println("\n\n"); break;
                case 4: 
                    currencyExchange(listOfAccounts, nAccounts, exCurr);
                    System.out.println("\n\n"); break;
                case 5: 
                    recordExchange(exCurr);
                    System.out.println("\n\n"); break;
                case 6: 
                    System.out.println("Function not yet implemented!");
                    System.out.println("\n\n"); break;
                default: 
                    System.out.println("Invalid choice. Please pick again.");
                    System.out.println("\n\n"); break;
            }

        }

    }

    public static void registerAccount(ArrayList<Account> listOfAccounts, int nAccounts){
        System.out.println("Register Account Name");
        System.out.print("Account Name: ");
        String newName = sc.nextLine();

        listOfAccounts.add(new Account(newName));
        nAccounts++;

        System.out.println("\n***");
        System.out.println("Account Name = " + listOfAccounts.get(nAccounts).getName());
    }

    public static void depositAmount(ArrayList<Account> listOfAccounts, int nAccounts){
        System.out.println("Deposit Amount");
        System.out.print("Account Name: ");
        String name = sc.nextLine();

        int i = 0;
        int index = -1;
        for(Account acc : listOfAccounts){
            if(acc.getName().equals(name)){
                index = i;
            }
            i++;
        }
        if(index == -1){
            System.out.println("There is no account with that name in the database.");
            System.out.println("Please enter a valid account name.");
            return;
        }

        System.out.println("Currency Balance: " + listOfAccounts.get(index).getBal());
        System.out.println("Currency: " + listOfAccounts.get(index).getAccCurrency());

        System.out.print("Deposit Amount: ");
        double deposit = sc.nextDouble();

        System.out.println("\n***");
        System.out.println("Account Name = " + listOfAccounts.get(index).getName());
        System.out.println("Deposit Amount = " + deposit);
        listOfAccounts.get(index).depositMoney(deposit);
    }

    public static void withdrawAmount(ArrayList<Account> listOfAccounts, int nAccounts){
        System.out.println("Withdraw Amount");
        System.out.print("Account Name: ");
        String name = sc.nextLine();
        
        int i = 0;
        int index = -1;
        for(Account acc : listOfAccounts){
            if(name.equals(acc.getName())){
                index = i;
            }
            i++;
        }
        if(index == -1){
            System.out.println("There is no account with that name in the database.");
            System.out.println("Please enter a valid account name.");
            return;
        }

        System.out.println("Currency Balance: " + listOfAccounts.get(index).getBal());
        System.out.println("Currency: " + listOfAccounts.get(index).getAccCurrency());

        System.out.print("Withdraw Amount: ");
        double withdraw = sc.nextDouble();

        listOfAccounts.get(index).withdrawMoney(withdraw);
        System.out.println("\n***");
        System.out.println("Account Name = " + listOfAccounts.get(index).getName());
        System.out.println("Withdraw Amount = " + withdraw);
    }

    public static void recordExchange(Currency exCurr){
        System.out.println("Record Exchange");

        System.out.println("[1] Philippine Peso (PHP)");
        System.out.println("[2] United States Dollar (USD)");
        System.out.println("[3] Japanese Yen (JPY)");
        System.out.println("[4] British Pound Sterling (GBP)");
        System.out.println("[5] Euro (EUR)");
        System.out.println("[6] Chinese Yuan Renminni (CNY)\n");

        System.out.print("Select Foreign Currency: ");
        int choice = sc.nextInt();
        System.out.println("Exchange Rate: " + exCurr.getRate(choice));

        System.out.println("\n***");
        System.out.println("Select Foreign Currency = [" + choice + "]");
        System.out.println("Exchange Rate = " + exCurr.getRate(choice));
    }

    public static void currencyExchange(ArrayList<Account> listOfAccounts, int nAccounts, Currency exCurr){
        System.out.println("Foreign Currency Exchange");
        System.out.println("Source Amount (PHP): " + listOfAccounts.get(0).getBal());

        System.out.println("\nExchanged Currency");
        System.out.println("[1] Philippine Peso (PHP) = " + exCurr.exchangeMoney(1,listOfAccounts.get(0).getBal()));
        System.out.println("[2] United States Dollar (USD) = " + exCurr.exchangeMoney(2,listOfAccounts.get(0).getBal()));
        System.out.println("[3] Japanese Yen (JPY) = " + exCurr.exchangeMoney(3,listOfAccounts.get(0).getBal()));
        System.out.println("[4] British Pound Sterling (GBP) = " + exCurr.exchangeMoney(4,listOfAccounts.get(0).getBal()));
        System.out.println("[5] Euro (EUR) = " + exCurr.exchangeMoney(5,listOfAccounts.get(0).getBal()));
        System.out.println("[6] Chinese Yuan Renminni (CNY) = " + exCurr.exchangeMoney(6,listOfAccounts.get(0).getBal()));

        System.out.println("\n***");
        System.out.println("Source Currency = Philippine Peso (PHP)");
        System.out.println("Source Amount (PHP) = " + listOfAccounts.get(0).getBal());
    }
}
