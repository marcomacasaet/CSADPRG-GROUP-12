import java.time.*;
import java.time.temporal.ChronoUnit;

public class Account{

    String name;
    float balance;
    //String exRate;
    //Currency accCurrencyEx = new Currency();
    String accCurrency = "PHP";
    Interest interest = new Interest();
    int accountNum;
    float ovrWithdrawnAmount=0;
    float ovrDepositedAmount=0;
    float avgbalance = 0;
    int totalWithdrawTransactions=0;
    int totalDepositTransactions = 1;
    int totalTransactions=1;
    LocalDate accountCreation;


    Account(String name, int accountNum, LocalDate accountCreation, float balance){
        this.name = name;
        this.accountNum = accountNum; //zero fill and implement change in main
        this.accountCreation = accountCreation;
        this.balance = balance;


        this.totalTransactions++;
        this.totalDepositTransactions++;
        this.ovrDepositedAmount = balance;
    }

    public String getName(){
        return this.name;
    }

    public int getNumber(){
        return this.accountNum;
    }

    public int getAccountAge(){

        return (int)ChronoUnit.DAYS.between(this.accountCreation, LocalDate.now());
    }

    public float getOvrDepositedAmount(){
        return this.ovrDepositedAmount;
    }

    public float getOvrWithdrawnAmount(){
        return this.ovrWithdrawnAmount;
    }

    public float getExpectedInterest(){
        return interest.getDaily(interest.getIntRate(),this.balance)*30;
    }

    public String getPrivilegeTier(){
        if(500000>this.avgbalance && this.avgbalance>=100000){
            return "Silver";
        } else if(1000000>this.avgbalance && this.avgbalance>=500000){
            return "Gold";
        } else if(this.avgbalance>=1000000){
            return "Platinum";
        }

        return "None";
    }

    public float getAvgBalance(){
        return this.avgbalance;
    }

    public int getTotalTransactions(){
        return this.totalTransactions;
    }

    public int getTotalWithdraws(){
        return this.totalWithdrawTransactions;
    }

    public int getTotalDeposits(){
        return this.totalDepositTransactions;
    }

    public void setAvgBalance(float bal){
        this.avgbalance = bal;
    }

    public void depositMoney(double money){
        this.balance+=money;
        System.out.println("Depositing "+String.format("%.2f", money)+" into the account. Your balance is now at "+String.format("%.2f", this.balance));
        this.totalDepositTransactions++;
        this.totalTransactions++;
        this.ovrDepositedAmount+=money;
    }

    public double getBal(){
        return this.balance;
    }

    public void withdrawMoney(double money){
        if (money>this.balance){
            System.out.println("Balance is at " + String.format("%.2f", this.balance) + ". You can't withdraw " + String.format("%.2f", money) + " from the account.");
        } else if (money<=this.balance){
            System.out.println("Withdrawing "+String.format("%.2f", money)+" from the account. Your balance is now at "+String.format("%.2f", this.balance));
            this.balance-=money;
            this.totalWithdrawTransactions++;
            this.totalTransactions++;
            this.ovrWithdrawnAmount+=money;
        }
    }

    public String getAccCurrency(){
        return this.accCurrency;
    }

    public Interest getInterest(){
        return this.interest;
    }

}