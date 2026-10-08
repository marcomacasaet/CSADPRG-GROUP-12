public class Currency{

    double PHP = 1.00;
    double USD = 62.00;
    double JPY = 0.40;
    double GBP = 84.00;
    double EUR = 72.00;
    double CNY = 9.00;

    Currency(){}

    double getRate(int choice){
        switch(choice){
            case 1: return PHP; 
            case 2: return USD; 
            case 3: return JPY; 
            case 4: return GBP; 
            case 5: return EUR; 
            case 6: return CNY; 
            default: return 0; 
        }
    }

    public double exchangeMoney(int choice, double amt){
        switch(choice){
            case 1: return amt/PHP;
            case 2: return amt/USD;
            case 3: return amt/JPY; 
            case 4: return amt/GBP; 
            case 5: return amt/EUR;
            case 6: return amt/CNY; 
            default: return 0;
        }
    }

    
}