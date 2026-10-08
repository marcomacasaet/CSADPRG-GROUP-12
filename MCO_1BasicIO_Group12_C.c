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
    printf("[6] Show Interest Amount\n\n");
}

void accountRegister(struct Account *account) {
    printf("Register Account Name\n");
    printf("Account Name: ");
    scanf("%199[^,], %199[^\n]", account->lastName, account->firstName);
    printf("Account Name: %s, %s\n", account->lastName, account->firstName);
    account->balance = 1000.0;
    strcpy(account->currency, "PHP");
}

void depositAmount(struct Account *account) {
    float amount;
    printf("Deposit Amount\n");
    printf("Account Name: %s, %s\n", account->lastName, account->firstName);
    printf("Current Balance: %.2f\n", account->balance);
    printf("Currency: %s\n\n", account->currency);
    printf("Deposit Amount: ");
    scanf("%f", &amount);
    account->balance += amount;
    printf("***\n");
    printf("Account Name: %s, %s\n", account->lastName, account->firstName);
    printf("Deposit Amount: %.2f\n", amount);
}

void withdrawAmount(struct Account *account) {
    float amount;
    printf("Withdraw Amount\n");
    printf("Account Name: %s, %s\n", account->lastName, account->firstName);
    printf("Current Balance: %.2f\n", account->balance);
    printf("Currency: %s\n\n", account->currency);
    printf("Withdraw Amount: ");
    scanf("%f", &amount);
    account->balance -= amount;
    printf("***\n");
    printf("Account Name: %s, %s\n", account->lastName, account->firstName);
    printf("Withdraw Amount: %.2f\n", amount);
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

void menu(struct Account *acc, struct exchangeRate *rate){
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
            depositAmount(acc);
            break;
        case 3:
            withdrawAmount(acc);
            break;
        case 4:
            exchangeRates(rate);
            break;
        case 5:
            recordExchangeRates(rate);
            break;
        case 6:
            printf("not implemented yet in specs.\n");
            break;
        default:
            printf("Error. Invalid input. Please enter a number between 1 and 6. Terminating program.\n");
    }
}


int main() {
    // variables for the rest of the program
    struct Account account;

    //default values, kinda ehh to make a separate file defaults for ts..
    struct exchangeRate rate[6] = {
        {"Philippine Peso", "PHP", 1.00},
        {"United States Dollar", "USD", 62.00},
        {"Japanese Yen", "JPY", 0.40},
        {"British Pound Sterling", "GBP", 84.00},
        {"Euro", "EUR", 72.00},
        {"Chinese Yuan Renminbi", "CNY", 9.00}
    };

    while(1)
        menu(&account, rate); 
}