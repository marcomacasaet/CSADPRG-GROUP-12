public class Account{

    String name;
    double balance = 1000;
    //String exRate;
    //Currency accCurrencyEx = new Currency();
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