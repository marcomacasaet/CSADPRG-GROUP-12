/*
********************
Last names: Acosta, Bunggo, Gregorio, Macasaet
Language: Java
Paradigm(s): Object-oriented, Imperative
********************
*/


import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args){

        boolean running = true;
        int choice;
        CurrencyExchange exCurr = new CurrencyExchange();
        ArrayList<Account> listOfAccounts = new ArrayList<>();
        int nAccounts = -1;
        
        while(running == true){
            System.out.println("================== MAIN MENU ==================");

            System.out.println("Select transaction:");
            System.out.println("[1] Register Account Name");
            System.out.println("[2] Deposit Amount");
            System.out.println("[3] Withdraw Amount");
            System.out.println("[4] Currency Exchange");
            System.out.println("[5] Record Exchange Rates");
            System.out.println("[6] Show Interest Amount");
            System.out.println("[7] Exit Program");

            System.out.print("\nChoice: ");
            choice = sc.nextInt();

            System.out.println("\n***");
            System.out.println("Choice = " + choice + "\n\n\n");
            String buffer = sc.nextLine();

            switch(choice){
                case 1: 
                    registerAccount(listOfAccounts, nAccounts);
                    System.out.println("\n\n"); break;
                case 2: 
                    depositAmount(listOfAccounts, nAccounts,exCurr);
                    System.out.println("\n\n"); break;
                case 3: 
                    withdrawAmount(listOfAccounts, nAccounts,exCurr);
                    System.out.println("\n\n"); break;
                case 4: 
                    currencyExchange(listOfAccounts, nAccounts, exCurr);
                    System.out.println("\n\n"); break;
                case 5: 
                    recordExchange(exCurr);
                    System.out.println("\n\n"); break;
                case 6: 
                    showInterestAmount(listOfAccounts, nAccounts);
                    System.out.println("\n\n"); break;
                case 7: 
                    System.out.println("Exiting program...");
                    System.out.println("Thank you for using our Banking System.");
                    running = false; break;
                default: 
                    System.out.println("Invalid choice. Please pick again.");
                    System.out.println("\n\n"); break;
            }

        }

    }

    public static void registerAccount(ArrayList<Account> listOfAccounts, int nAccounts){
        System.out.println("================== REGISTER ACCOUNT ==================");
        System.out.print("Account Name: ");
        String newName = sc.nextLine();

        listOfAccounts.add(new Account(newName));
        nAccounts++;

        System.out.println("\n***");
        System.out.println("Account Name = " + listOfAccounts.get(nAccounts).getName());

        String sChoice = "N";

        while(sChoice.equals("N") || sChoice.equals("n")){
            System.out.println("Back to the Main Menu. Y/N?");
            sChoice = sc.nextLine();

            if(sChoice.equals("Y") || sChoice.equals("y")){
            return;
        }
        }

    }

    public static void depositAmount(ArrayList<Account> listOfAccounts, int nAccounts, CurrencyExchange exCurr){
        System.out.println("================== DEPOSIT AMOUNT ==================");
        System.out.print("Account Name: ");
        String name = sc.nextLine();

        int index = getAccount(listOfAccounts,name);

        if(index == -1){
            System.out.println("There is no account with that name in the database.");
            System.out.println("Please enter a valid account name.");
            return;
        }

        System.out.printf("Current Balance: %.2f\n", listOfAccounts.get(index).getBal());
        System.out.printf("Current Currency: %s\n", listOfAccounts.get(index).getAccCurrency());

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
        System.out.printf("Account Name = %s\n", listOfAccounts.get(index).getName());
        System.out.printf("Deposit Amount = %.2f in %s\n", deposit, exCurr.getCurr(choice));
        listOfAccounts.get(index).depositMoney(exCurr.toPeso(choice,deposit));

        System.out.printf("Current Balance in PHP: %.2f\n", listOfAccounts.get(index).getBal());

        String sChoice = "N";

        while(sChoice.equals("N") || sChoice.equals("n")){
            System.out.println("Back to the Main Menu. Y/N?");
            sChoice = sc.nextLine();

            if(sChoice.equals("Y") || sChoice.equals("y")){
                return;
            }
        }

        
    }

    public static void withdrawAmount(ArrayList<Account> listOfAccounts, int nAccounts, CurrencyExchange exCurr){
        System.out.println("================== WITHDRAW AMOUNT ==================");
        System.out.print("Account Name: ");
        String name = sc.nextLine();
        
        int index = getAccount(listOfAccounts,name);

        if(index == -1){
            System.out.println("There is no account with that name in the database.");
            System.out.println("Please enter a valid account name.");
            return;
        }

        System.out.printf("Current Balance: %.2f\n", listOfAccounts.get(index).getBal());
        System.out.printf("Current Currency: %s\n", listOfAccounts.get(index).getAccCurrency());

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
        System.out.printf("Account Name = %s\n", listOfAccounts.get(index).getName());
        System.out.printf("Withdraw Amount = %.2f in %s\n", withdraw, exCurr.getCurr(choice));
        listOfAccounts.get(index).withdrawMoney(exCurr.toPeso(choice,withdraw));

        System.out.printf("Current Balance in PHP: %.2f\n", listOfAccounts.get(index).getBal());

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

    public static void currencyExchange(ArrayList<Account> listOfAccounts, int nAccounts, CurrencyExchange exCurr){
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

    public static void showInterestAmount(ArrayList<Account> listOfAccounts, int nAccounts){
        System.out.println("================== SHOW INTEREST AMOUNT ==================");
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

        System.out.printf("Currency Balance: %.2f\n", listOfAccounts.get(index).getBal());
        System.out.printf("Currency: %s\n", listOfAccounts.get(index).getAccCurrency());
        System.out.printf("Interest Rate: %.2f\n", listOfAccounts.get(index).getInterest().getIntRate());

        System.out.print("\nTotal Number of Days: ");
        int days = sc.nextInt();
        double daily;

        String buffer = sc.nextLine();

        System.out.printf("\n%3s | %10s | %10s\n","Day","Interest","Balance"); //format properly later on
        for (i = 0;i < days;i++){
            daily = listOfAccounts.get(index).getInterest().getDaily(listOfAccounts.get(index).getInterest().getIntRate(),listOfAccounts.get(index).getBal());
            System.out.printf("%3d | %10.2f | %10.2f\n", i+1, daily, listOfAccounts.get(index).getBal()+(i*daily)); 
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

    //helper functions

    public static int getAccount(ArrayList<Account> listOfAccounts, String name){

        int i = 0;
        int index = -1;
        for(Account acc : listOfAccounts){
            if(name.equals(acc.getName())){
                index = i;
            }
            i++;
        }

        return index;

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
