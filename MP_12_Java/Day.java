import java.time.*;
import java.util.*;

public class Day {

    HashMap<String, Float> turnoverVelocity = new HashMap<>();
    HashMap<String, Integer> numOfDeposits = new HashMap<>();
    HashMap<String, Float> totalWithdrawalAmounts = new HashMap<>();
    HashMap<String, Float> totalDepositAmounts = new HashMap<>();
    HashMap<String, String> riskFlags = new HashMap<>();
    ArrayList<Transactions> transactions = new ArrayList<>();
    ArrayList<String> accountNames = new ArrayList<>();
    LocalDate dateToday;

    Day(LocalDate date){
        this.dateToday = date;
    }

    public LocalDate getDateToday(){
        return this.dateToday;
    }
    
    public void setTurnoverVelocity(String nAcc, float turnoverVelocity){
        this.turnoverVelocity.put(nAcc, turnoverVelocity);
    }

    public void setNumOfDeposits(String nAcc, int numOfDeposits){
        this.numOfDeposits.put(nAcc, numOfDeposits);
    }

    public void setRiskFlags(String nAcc, String riskFlag){
        this.riskFlags.put(nAcc, riskFlag);
    }

    public void setTotalWithdrawalAmounts(String nAcc, float totalWithdrawalAmounts){
        this.totalWithdrawalAmounts.put(nAcc, totalWithdrawalAmounts);
    }

    public void setTotalDepositAmounts(String nAcc, float totalDepositAmounts){
        this.totalDepositAmounts.put(nAcc, totalDepositAmounts);
    }

    public void addTransactions(Transactions transaction){
        this.transactions.add(transaction);
    }

    public float getTurnoverVelocity(String nAcc){
        return this.turnoverVelocity.get(nAcc);
    }

    public int getNumOfDeposits(String nAcc){
        return this.numOfDeposits.get(nAcc);
    }

    public float getTotalWithdrawalAmounts(String nAcc){
        return this.totalWithdrawalAmounts.get(nAcc);
    }

    public float getTotalDepositAmounts(String nAcc){
        return this.totalDepositAmounts.get(nAcc);
    }

    public ArrayList<Transactions> getTransactions(){
        return this.transactions;
    }

    public String getRiskFlags(String nAcc){
        return this.riskFlags.get(nAcc);
    }

    public int getNoOfUniqueAccounts(){
        return this.accountNames.size();
    }

    public ArrayList<String> getAccountNames(){
        return this.accountNames;
    }
}