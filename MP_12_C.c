/*
********************
Last names: Acosta, Bunggo, Gregorio, Macasaet
Language: C
Paradigm(s): Procedural, Imperative
********************
*/
#include <stdio.h>
#include <string.h>

struct Account {
    char lastName[200];
    char firstName[200];
    float balance;
    char currency[4];
    float interestRate;
};

struct exchangeRate {
    char currencyName[50];
    char currency[4];
    float rate;
};

void characterScan(int num){
    char c;

    while (1) {
        if (scanf("%d", &num) == 1) {
            while ((c = getchar()) != '\n' && c != EOF); 
            break; 
        } else {
            printf("Invalid input! Please enter a number: \n\n");
            while ((c = getchar()) != '\n' && c != EOF); 
        }
    }
}

void menuPrint () {
    printf("Select Transaction:\n");
    printf("[1] Register Account Name\n");
    printf("[2] Deposit Amount\n");
    printf("[3] Withdraw Amount\n");
    printf("[4] Currency Exchange\n");
    printf("[5] Record Exchange Rates\n");
    printf("[6] Show Interest Amount\n");
    printf("[7] Exit\n\n");
}

void accountRegister(struct Account *account) {
    printf("Register Account Name\n");
    printf("Account Name: ");
    scanf("%199[^,], %199[^\n]", account->lastName, account->firstName);
    printf("\n\nAccount Name: %s, %s\n", account->lastName, account->firstName);
    account->balance = 1000.0;
    strcpy(account->currency, "PHP");
    printf("Initial Balance: %.2f\n", account->balance);
    printf("Currency: %s\n\n", account->currency);
}

void changeAccountCurrency(struct Account *account, struct exchangeRate *rate, int currency) {
    float newBalance;
    printf("\n\nCurrent currency: %s\n\n", account->currency);

    if (strcmp(account->currency, rate[currency-1].currency) == 0) {
        printf("The account is already in %s currency.\n\n", account->currency);
        return;
    }

    float currentRate = 1.00; 
    for (int i = 0; i < 6; i++) {
        if (strcmp(account->currency, rate[i].currency) == 0) {
            currentRate = rate[i].rate;
            break;
        }
    }
    
    newBalance = account->balance * (currentRate / rate[currency-1].rate);
    strcpy(account->currency, rate[currency-1].currency);
    account->balance = newBalance;

    printf("***\n");
    printf("Account Name: %s, %s\n", account->lastName, account->firstName);
    printf("New Balance: %.2f\n", account->balance);
    printf("New Currency: %s\n\n", account->currency);
}

void depositAmount(struct Account *account, struct exchangeRate *rate) {
    float amount;
    int currency;
    printf("Deposit Amount\n");
    printf("Account Name: %s, %s\n", account->lastName, account->firstName);
    printf("Current Balance: %.2f\n", account->balance);
    printf("Currency: %s\n\n", account->currency);

    for (int i = 0; i < 6; i++) {
        printf("[%d] %s (%s)\n", i+1, rate[i].currencyName, rate[i].currency);
    }
    printf("Currency to Deposit: ");
    scanf("%d", &currency);

    printf("Deposit Amount: ");
    scanf("%f", &amount);
    amount = amount * rate[currency-1].rate;
    account->balance += amount;
    printf("***\n");
    printf("Account Name: %s, %s\n", account->lastName, account->firstName);
    printf("Deposit Amount: %.2f\n", amount);


    printf("Would you like to change account currency to this one? (Y/N): ");
    char choice;
    scanf(" %c", &choice);
    if (choice == 'Y' || choice == 'y') {
        changeAccountCurrency(account, rate, currency);
    }
    else
        printf("\n***\n");
}

void withdrawAmount(struct Account *account, struct exchangeRate *rate) {
    float amount;
    int currency;
    printf("Withdraw Amount\n");
    printf("Account Name: %s, %s\n", account->lastName, account->firstName);
    printf("Current Balance: %.2f\n", account->balance);
    printf("Currency: %s\n\n", account->currency);

    for (int i = 0; i < 6; i++) {
        printf("[%d] %s (%s)\n", i+1, rate[i].currencyName, rate[i].currency);
    }
    printf("Currency to Withdraw: ");
    scanf("%d", &currency);

    printf("Withdraw Amount: ");
    scanf("%f", &amount);

    amount = amount * rate[currency-1].rate;
    account->balance -= amount;
    printf("***\n");
    printf("Account Name: %s, %s\n", account->lastName, account->firstName);
    printf("Withdraw Amount: %.2f\n", amount);

    printf("Would you like to change account currency to this one? (Y/N): ");
    char choice;
    scanf(" %c", &choice);
    if (choice == 'Y' || choice == 'y') {
        changeAccountCurrency(account, rate, currency);
    }
    else
        printf("\n***\n");
}

void recordExchangeRates(struct exchangeRate *rate) {
    int num;
    printf("Record Exchange Rates\n");
    for (int i = 0; i < 6; i++) {
        printf("[%d] %s (%s)\n", i+1, rate[i].currencyName, rate[i].currency);
    }
    printf("\n");
    printf("Select Foreign Currency: ");
    characterScan(num);
    printf("Exchange Rate: ");
    scanf("%f", &rate[num-1].rate);
    printf("\n***\n");
    printf("Select Foreign Currency: %d\n", num);
    printf("Exchange Rate: %.2f\n\n", rate[num-1].rate);
}

void exchangeRates(struct exchangeRate *rate) {
    double amount;
    printf("Foreign Currency Exchange\n");
    printf("Source Amount (PHP): ");
    scanf("%lf", &amount);
    printf("\n\n");
    for (int i = 0; i < 6; i++) {
        printf("[%d] %s (%s) = %.2f\n", i+1, rate[i].currencyName, rate[i].currency, rate[i].rate * amount);
    }
    printf("\n***\n");
    printf("Source Currency: %s (%s)\n", rate[0].currencyName, rate[0].currency);
    printf("Source Amount (%s) %.2f\n\n", rate[0].currency, amount);
}

void interestRates(struct Account *account) {
    account->interestRate = 0.05;
    char choice;
    do {
        int num = 0;
        
        float interestAmount = account->balance * account->interestRate;
        printf("Show Interest Amount\n");
        printf("Account Name: %s, %s\n", account->lastName, account->firstName);
        printf("Current Balance: %.2f\n", account->balance);
        printf("Currency: %s\n\n", account->currency);
        printf("Interest Rate: %.2f%%\n\n", account->interestRate * 100);


        printf("Total Number of days: ");   
        scanf("%d", &num);
        float dailyInterest = interestAmount/365;

        for (int day = 1; day <= num; day++) {
            float dailybal = account->balance + (dailyInterest * day);
            printf("%-4d | %-4.2f | %-4.2f |\n", day, dailyInterest, dailybal);
        }

        printf("\nReturn to menu? Y/N: ");
        scanf(" %c", &choice);
    } while (choice != 'Y' && choice != 'y');
}

int menu(struct Account *acc, struct exchangeRate *rate){
    int num;
    char c;

    while (1) {
        menuPrint();
        printf("Choice: ");
        if (scanf("%d", &num) == 1) {
            while ((c = getchar()) != '\n' && c != EOF); 
            printf("\n***\n");
            break; 
        } else {
            printf("Invalid input! Please enter a number: \n\n");
            while ((c = getchar()) != '\n' && c != EOF); 
        }
    }
    printf("Choice = %d\n", num);

    switch(num){
        case 1:
            accountRegister(acc);
            break;
        case 2:
            depositAmount(acc, rate);
            break;
        case 3:
            withdrawAmount(acc, rate);
            break;
        case 4:
            exchangeRates(rate);
            break;
        case 5:
            recordExchangeRates(rate);
            break;
        case 6:
            interestRates(acc);
            break;
        case 7:
            printf("Exiting program. bye bye!\n");
            return 0;
        default:
            printf("Error. Invalid input. Please enter a number between 1 and 7. Terminating program.\n");
    }
    return 1;
}


int main() {
    // variables for the rest of the program
    struct Account account;
    int menuChoice = 1;

    //default values, kinda ehh to make a separate file defaults for ts..
    struct exchangeRate rate[6] = {
        {"Philippine Peso", "PHP", 1.00},
        {"United States Dollar", "USD", 62.00},
        {"Japanese Yen", "JPY", 0.40},
        {"British Pound Sterling", "GBP", 84.00},
        {"Euro", "EUR", 72.00},
        {"Chinese Yuan Renminbi", "CNY", 9.00}
    };

    while(menuChoice)
        menuChoice = menu(&account, rate); 
}