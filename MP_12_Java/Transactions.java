import java.time.*;

public class Transactions {
    Account account;
    float amount;
    String type;
    LocalDate date;
    LocalTime time;
    float balanceAfterTransaction;
    String currency;

    Transactions(Account account, float amount, String type, LocalDate date, LocalTime time, float balanceAfterTransaction, String currency){
        this.account = account;
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.time = time;
        this.balanceAfterTransaction = balanceAfterTransaction;
        this.currency = currency;
    }

    String getAccountName(){
        return this.account.getName();
    }

    int getAccountNo(){
        return this.account.getNumber();
    }

    String getType(){
        return this.type;
    }

    LocalDate getDate(){
        return this.date;
    }

    LocalTime getTime(){
        return this.time;
    }

    float getAmount(){
        return this.amount;
    }

    String getCurrency(){
        return this.currency;
    }

    float balanceAfterTransaction(){
        return this.balanceAfterTransaction;
    }
}
