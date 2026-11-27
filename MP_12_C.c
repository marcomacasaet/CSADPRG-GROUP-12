/*
********************
Last names: Acosta, Bunggo, Gregorio, Macasaet
Language: C
Paradigm(s): Procedural, Imperative
********************
*/
#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <math.h>
#include <time.h>
#include <stdlib.h>

#define APP_MAX_ACCOUNTS      2000
#define APP_MAX_TX            25000
#define APP_MIN_INIT_DEPOSIT  5000.0f
#define APP_ANNUAL_INT_RATE   0.05f

struct Account {
    char lastName[200];
    char firstName[200];
    char accountNumber[16];
    char accountName[200];
    float balance;
    char currency[4];
    float interestRate;
};

struct exchangeRate {
    char currencyName[50];
    char currency[4];
    float rate;
};

struct TransactionRecord {
    char accountNumber[16];
    char accountName[200];
    char currentDate[11];
    char currentTime[9];
    char currency[4];
    char transactionType[16];
    float amount;
    float currentBalance;
    int valid;
};

struct SimpleDate {
    int day;
    int month;
    int year;
    int yyyymmdd;
};

void characterScan(int *num){
    char c;

    while (1) {
        if (scanf("%d", num) == 1) {
            while ((c = getchar()) != '\n' && c != EOF); 
            break; 
        } else {
            printf("Invalid input! Please enter a number: \n\n");
            while ((c = getchar()) != '\n' && c != EOF); 
        }
    }
}

static int parseDmY(const char *str, struct SimpleDate *out) {
    int d = 0, m = 0, y = 0;
    if (!str || sscanf(str, "%d/%d/%d", &d, &m, &y) != 3) {
        return 0;
    }
    if (m < 1 || m > 12 || d < 1 || d > 31 || y < 1900) {
        return 0;
    }
    if (out) {
        out->day = d;
        out->month = m;
        out->year = y;
        out->yyyymmdd = y * 10000 + m * 100 + d;
    }
    return 1;
}

static int dayInMonth(int year, int month) {
    int leap = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
    int days[12] = {31, leap ? 29 : 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    return (month >= 1 && month <= 12) ? days[month - 1] : 30;
}

static int diffDays(struct SimpleDate start, struct SimpleDate end) {
    struct tm a = {0}, b = {0};
    a.tm_year = start.year - 1900; a.tm_mon = start.month - 1; a.tm_mday = start.day; a.tm_isdst = -1;
    b.tm_year = end.year - 1900;   b.tm_mon = end.month - 1;   b.tm_mday = end.day;   b.tm_isdst = -1;
    time_t ta = mktime(&a), tb = mktime(&b);
    if (ta == (time_t)-1 || tb == (time_t)-1) {
        return 0;
    }
    return (int)llround(difftime(tb, ta) / 86400.0);
}

static void currentTime(char *dateBuff, size_t dateSize, char *timeBuff, size_t timeSize) {
    time_t now = time(NULL);
    struct tm *tm_info = localtime(&now);
    if (tm_info != NULL) {
        strftime(dateBuff, dateSize, "%d/%m/%Y", tm_info);
        strftime(timeBuff, timeSize, "%H:%M:%S", tm_info);
    } else {
        snprintf(dateBuff, dateSize, "01/01/2026");
        snprintf(timeBuff, timeSize, "00:00:00");
    }
}

static unsigned long long getHighAccNum(const struct TransactionRecord tx[], int txCount) {
    unsigned long long highest = 1092000000ULL;

    for (int i = 0; i < txCount; i++) {
        if (!tx[i].valid || tx[i].accountNumber[0] == '\0') {
            continue;
        }
        char *endPtr = NULL;
        unsigned long long num = strtoull(tx[i].accountNumber, &endPtr, 10);
        if (endPtr != tx[i].accountNumber && num > highest) {
            highest = num;
        }
    }

    return highest;
}

int loadTransactionsFromCSV(const char *filename, struct TransactionRecord tx[], int maxRecords) {
    FILE *fp = fopen(filename, "r");
    if (fp == NULL) {
        printf("Error opening CSV file");
        return 0;
    }

    char line[1024];
    int count = 0;

    if (fgets(line, sizeof(line), fp) == NULL) {
        fclose(fp);
        return 0;
    }

    while (count < maxRecords && fgets(line, sizeof(line), fp) != NULL) {
        line[strcspn(line, "\r\n")] = '\0';
        if (line[0] == '\0') {
            continue;
        }

        struct TransactionRecord rec = {0};
        int parsed = sscanf(
            line,
            " %199[^,],%15[^,],%10[^,],%8[^,],%3[^,],%f,%15[^,],%f",
            rec.accountName,
            rec.accountNumber,
            rec.currentDate,
            rec.currentTime,
            rec.currency,
            &rec.amount,
            rec.transactionType,
            &rec.currentBalance
        );

        if (parsed == 8) {
            rec.valid = 1;
            tx[count++] = rec;
        }
    }

    fclose(fp);
    return count;
}

int recordTransaction(const char *filename, const struct Account *account, const char *txType, float amount, float currentBalance, struct TransactionRecord tx[], int *txCount) {
    char dateStr[11];
    char timeStr[9];
    currentTime(dateStr, sizeof(dateStr), timeStr, sizeof(timeStr));

    int writeHeader = 0;
    FILE *checkFp = fopen(filename, "r");
    if (checkFp == NULL) {
        writeHeader = 1;
    } else {
        fseek(checkFp, 0, SEEK_END);
        if (ftell(checkFp) == 0) {
            writeHeader = 1;
        }
        fclose(checkFp);
    }

    FILE *fp = fopen(filename, "a");
    if (fp == NULL) {
        printf("Error opening CSV file for writing");
        return 0;
    }

    if (writeHeader) {
        fprintf(fp, "account_name,account_number,date,time,currency,amount,type,current_balance\n");
    }

    fprintf(
        fp,
        "%s,%s,%s,%s,%s,%.2f,%s,%.2f\n",
        account->accountName,
        account->accountNumber,
        dateStr,
        timeStr,
        account->currency,
        amount,
        txType,
        currentBalance
    );
    fclose(fp);

    if (tx != NULL && txCount != NULL && *txCount < APP_MAX_TX) {
        struct TransactionRecord rec = {0};
        snprintf(rec.accountNumber, sizeof(rec.accountNumber), "%s", account->accountNumber);
        snprintf(rec.accountName, sizeof(rec.accountName), "%s", account->accountName);
        snprintf(rec.currentDate, sizeof(rec.currentDate), "%s", dateStr);
        snprintf(rec.currentTime, sizeof(rec.currentTime), "%s", timeStr);
        snprintf(rec.currency, sizeof(rec.currency), "%s", account->currency);
        snprintf(rec.transactionType, sizeof(rec.transactionType), "%s", txType);
        rec.amount = amount;
        rec.currentBalance = currentBalance;
        rec.valid = 1;
        tx[(*txCount)++] = rec;
    }

    return 1;
}

void menuPrint () {
    printf("Select Transaction:\n");
    printf("[1] Register Account Name\n");
    printf("[2] Deposit Amount\n");
    printf("[3] Withdraw Amount\n");
    printf("[4] Currency Exchange\n");
    printf("[5] Record Exchange Rates\n");
    printf("[6] Show Interest Amount\n");
    printf("[7] Show Reports\n");
    printf("[8] Exit\n\n");
}

void accountRegister(struct Account *account, struct TransactionRecord tx[], int *txCount, const char *csvFilename) {
    char c;
    printf("Register Account Name\n");
    printf("Account Name: ");
    scanf("%199[^,], %199[^\n]", account->lastName, account->firstName);
    while ((c = getchar()) != '\n' && c != EOF);

    snprintf(account->accountName, sizeof(account->accountName), "%s %s", account->firstName, account->lastName);

    do {
        printf("Initial Deposit: ");
        scanf("%f", &account->balance);
        while ((c = getchar()) != '\n' && c != EOF);
        if (account->balance < APP_MIN_INIT_DEPOSIT) {
            printf("Minimum initial deposit is PHP %.2f.\n", APP_MIN_INIT_DEPOSIT);
        }
    } while (account->balance < APP_MIN_INIT_DEPOSIT);

    unsigned long long highest = getHighAccNum(tx, (txCount != NULL) ? *txCount : 0);
    snprintf(account->accountNumber, sizeof(account->accountNumber), "%010llu", highest + 1);
    strcpy(account->currency, "PHP");
    account->interestRate = APP_ANNUAL_INT_RATE;

    recordTransaction(csvFilename, account, "deposit", account->balance, account->balance, tx, txCount);

    printf("Account Number: %s\n", account->accountNumber);
    printf("Account created successfully!\n");
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

void depositAmount(struct Account *account, struct exchangeRate *rate, struct TransactionRecord tx[], int *txCount, const char *csvFilename) {
    float amount;
    int currency;
    char c;

    printf("Deposit Amount\n");
    printf("Account Name: %s, %s\n", account->lastName, account->firstName);
    printf("Current Balance: %.2f\n", account->balance);
    printf("Currency: %s\n\n", account->currency);

    for (int i = 0; i < 6; i++) {
        printf("[%d] %s (%s)\n", i+1, rate[i].currencyName, rate[i].currency);
    }

    while (1) {
        printf("Deposit Amount: ");
        if (scanf("%f", &amount) == 1 && amount > 0.0f) {
            while ((c = getchar()) != '\n' && c != EOF);
            break;
        }
        while ((c = getchar()) != '\n' && c != EOF);
        printf("Invalid amount! Please enter a positive number.\n");
    }

    account->balance += amount;

    printf("***\n");
    printf("Account Name: %s, %s\n", account->lastName, account->firstName);
    printf("Deposit Amount: %.2f\n", amount);
    printf("New Balance: %.2f\n", account->balance);

    recordTransaction(
        csvFilename,
        account,
        "deposit",
        amount,
        account->balance,
        tx,
        txCount
    );
}

void withdrawAmount(struct Account *account, struct exchangeRate *rate, struct TransactionRecord tx[], int *txCount, const char *csvFilename) {
    float amount;
    int currency;
    char c;

    printf("Withdraw Amount\n");
    printf("Account Name: %s, %s\n", account->lastName, account->firstName);
    printf("Current Balance: %.2f\n", account->balance);
    printf("Currency: %s\n\n", account->currency);

    for (int i = 0; i < 6; i++) {
        printf("[%d] %s (%s)\n", i+1, rate[i].currencyName, rate[i].currency);
    }
    while (1) {
        printf("Withdraw Amount: ");
        if (scanf("%f", &amount) == 1 && amount > 0.0f) {
            while ((c = getchar()) != '\n' && c != EOF);
            break;
        } else {
            while ((c = getchar()) != '\n' && c != EOF);
            printf("Invalid amount! Please enter a positive number.\n");
        }
    }

    account->balance -= amount;

    printf("***\n");
    printf("Account Name: %s, %s\n", account->lastName, account->firstName);
    printf("Withdraw Amount: %.2f\n", amount);
    printf("New Balance: %.2f\n", account->balance);

    recordTransaction(
        csvFilename,
        account,
        "withdrawal",
        amount,
        account->balance,
        tx,
        txCount
    );
}

void recordExchangeRates(struct exchangeRate *rate) {
    int num;
    printf("Record Exchange Rates\n");
    for (int i = 0; i < 6; i++) {
        printf("[%d] %s (%s)\n", i+1, rate[i].currencyName, rate[i].currency);
    }
    printf("\n");
    printf("Select Foreign Currency: ");
    characterScan(&num);
    printf("Exchange Rate: ");
    scanf("%f", &rate[num-1].rate);
    printf("\n***\n");
    printf("Select Foreign Currency: %d\n", num);
    printf("Exchange Rate: %.2f\n\n", rate[num-1].rate);
}

void exchangeRates(struct exchangeRate *rate) {
    double amount;
    int num = 0;
    int currency = 0;
    printf("Foreign Currency Exchange\n");
    printf("\n\n");
    for (int i = 0; i < 6; i++) {
        printf("[%d] %s (%s) \n", i+1, rate[i].currencyName, rate[i].currency);
    }
    printf("Source Currency: ");
    scanf("%d", &num);
    printf("Source Amount: ");
    scanf("%lf", &amount);
    printf("Exchange Currency: ");
    scanf("%d", &currency);
    amount = amount * rate[num-1].rate;
    float finalAmount = amount * rate[currency-1].rate;
    printf("Exchange Amount: %.2f \n", finalAmount);
    printf("\n***\n");
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

void generateReport1(const struct TransactionRecord tx[], int txCount, const char *outFilename) {
    struct rowReport {
        char accountName[200];
        char accountNumber[16];
        int accountAge;
        float currentBalance;
        float averageDailyBalance;
        char privilegeTier[16];
    };

    struct SimpleDate maxDt = {0};
    int hasMax = 0;

    for (int i = 0; i < txCount; i++) {
        struct SimpleDate dt;
        if (!tx[i].valid || !parseDmY(tx[i].currentDate, &dt)) continue;
        if (!hasMax || dt.yyyymmdd > maxDt.yyyymmdd) {
            maxDt = dt;
            hasMax = 1;
        }
    }

    int year = (maxDt.month == 1) ? (maxDt.year - 1) : maxDt.year;
    int month = (maxDt.month == 1) ? 12 : (maxDt.month - 1);
    int totalDays = dayInMonth(year, month);
    int monthStart = year * 10000 + month * 100 + 1;

    FILE *fp = fopen(outFilename, "w");
    if (!fp) {
        printf("Error writing Report 1 CSV\n\n");
        return;
    }

    char accNums[2000][16];
    char accNames[2000][200];
    int uniqueAccs = 0;

    for (int i = 0; i < txCount && uniqueAccs < 2000; i++) {
        if (!tx[i].valid || tx[i].accountNumber[0] == '\0') continue;
        int found = 0;
        for (int j = 0; j < uniqueAccs; j++) {
            if (strcmp(accNums[j], tx[i].accountNumber) == 0) {
                found = 1;
                break;
            }
        }
        if (!found) {
            snprintf(accNums[uniqueAccs], sizeof(accNums[uniqueAccs]), "%s", tx[i].accountNumber);
            snprintf(accNames[uniqueAccs], sizeof(accNames[uniqueAccs]), "%s", tx[i].accountName);
            uniqueAccs++;
        }
    }

    struct rowReport rows[2000];
    int rowCount = 0;

    for (int a = 0; a < uniqueAccs; a++) {
        struct SimpleDate openDt = maxDt;
        int hasOpen = 0;
        int latestInt = -1;
        float latestBal = 0.0f;

        float runningBal = 0.0f;
        int priorBestInt = -1;

        for (int i = 0; i < txCount; i++) {
            struct SimpleDate dt;
            if (!tx[i].valid || strcmp(tx[i].accountNumber, accNums[a]) != 0) continue;
            if (parseDmY(tx[i].currentDate, &dt)) continue;

            if (!hasOpen || dt.yyyymmdd < openDt.yyyymmdd) {
                openDt = dt;
                hasOpen = 1;
            }
            if (dt.yyyymmdd >= latestInt) {
                latestInt = dt.yyyymmdd;
                latestBal = tx[i].currentBalance;
            }
            if (dt.yyyymmdd < monthStart && dt.yyyymmdd >= priorBestInt) {
                priorBestInt = dt.yyyymmdd;
                runningBal = tx[i].currentBalance;
            }
        }

        double theSum = 0.0;
        for (int day = 1; day <= totalDays; day++) {
            int curInt = year * 10000 + month * 100 + day;
            for (int i = 0; i < txCount; i++) {
                struct SimpleDate dt;
                if (!tx[i].valid || strcmp(tx[i].accountNumber, accNums[a]) != 0) continue;
                if (parseDmY(tx[i].currentDate, &dt) && dt.yyyymmdd == curInt) {
                    runningBal = tx[i].currentBalance;
                }
            }
            theSum += runningBal;
        }

        float avgDB = (totalDays > 0) ? (float)(theSum / totalDays) : 0.0f;
        if (avgDB >= 100000.0f) {
            struct rowReport r = {0};
            snprintf(r.accountName, sizeof(r.accountName), "%s", accNames[a]);
            snprintf(r.accountNumber, sizeof(r.accountNumber), "%s", accNums[a]);
            r.accountAge = diffDays(openDt, maxDt);
            r.currentBalance = latestBal;
            r.averageDailyBalance = avgDB;

            if (avgDB < 500000.0f) {
                snprintf(r.privilegeTier, sizeof(r.privilegeTier), "Silver");
            } else if (avgDB < 1000000.0f) {
                snprintf(r.privilegeTier, sizeof(r.privilegeTier), "Gold");
            } else {
                snprintf(r.privilegeTier, sizeof(r.privilegeTier), "Platinum");
            }
            rows[rowCount++] = r;
        }
    }

    for (int i = 0; i < rowCount - 1; i++) {
        for (int j = i + 1; j < rowCount; j++) {
            if (rows[j].averageDailyBalance > rows[i].averageDailyBalance) {
                struct rowReport tmp = rows[i];
                rows[i] = rows[j];
                rows[j] = tmp;
            }
        }
    }

    fprintf(fp, "AccountName,AccountAge,CurrentBalance,AverageDailyBalance,PrivilegeTier\n");
    for (int i = 0; i < rowCount; i++) {
        fprintf(fp, "%s,%d,%.2f,%.2f,%s\n", rows[i].accountName, rows[i].accountAge, rows[i].currentBalance, rows[i].averageDailyBalance, rows[i].privilegeTier);
    }
    fclose(fp);
    printf("Report 1 saved to %s (%d rows).\n", outFilename, rowCount);
}

void generateReport2(const struct TransactionRecord tx[], int txCount, const char *outFilename) {
    struct rowReport {
        char accountName[200];
        char accountNumber[16];
        float currentBalance;
        float expectedInterest;
    };

    char accNums[2000][16];
    char accNames[2000][200];
    int latestInt[2000];
    float latestBal[2000];
    int uniqueAccs = 0;

    for (int i = 0; i < txCount; i++) {
        struct SimpleDate dt;
        if (!tx[i].valid || tx[i].accountNumber[0] == '\0') continue;
        if (!parseDmY(tx[i].currentDate, &dt)) continue;

        int idx = -1;
        for (int j = 0; j < uniqueAccs; j++) {
            if (strcmp(accNums[j], tx[i].accountNumber) == 0) {
                idx = j;
                break;
            }
        }
        if (idx == -1 && uniqueAccs < 2000) {
            idx = uniqueAccs++;
            snprintf(accNums[idx], sizeof(accNums[idx]), "%s", tx[i].accountNumber);
            snprintf(accNames[idx], sizeof(accNames[idx]), "%s", tx[i].accountName);
            latestInt[idx] = -1;
            latestBal[idx] = 0.0f;
        }
        if (idx != -1 && dt.yyyymmdd >= latestInt[idx]) {
            latestInt[idx] = dt.yyyymmdd;
            latestBal[idx] = tx[i].currentBalance;
        }
    }

    FILE *fp = fopen(outFilename, "w");
    if (!fp) {
        printf("Error writing Report 2 CSV\n\n");
        return;
    }

    struct rowReport rows[2000];
    int rowCount = 0;

    for (int i = 0; i < uniqueAccs; i++) {
        if (latestBal[i] >= 10000.0f) {
            struct rowReport r = {0};
            snprintf(r.accountName, sizeof(r.accountName), "%s", accNames[i]);
            snprintf(r.accountNumber, sizeof(r.accountNumber), "%s", accNums[i]);
            r.currentBalance = latestBal[i];
            r.expectedInterest = latestBal[i] * (APP_ANNUAL_INT_RATE / 365.0f) * 30.0f;
            rows[rowCount++] = r;
        }
    }

    for (int i = 0; i < rowCount - 1; i++) {
        for (int j = i + 1; j < rowCount; j++) {
            if (rows[j].expectedInterest > rows[i].expectedInterest) {
                struct rowReport tmp = rows[i];
                rows[i] = rows[j];
                rows[j] = tmp;
            }
        }
    }

    fprintf(fp, "AccountName,CurrentBalance,ExpectedInterest\n");
    for (int i = 0; i < rowCount; i++) {
        fprintf(fp, "%s,%.2f,%.2f\n", rows[i].accountName, rows[i].currentBalance, rows[i].expectedInterest);
    }
    fclose(fp);
    printf("Report 2 saved to %s (%d rows).\n", outFilename, rowCount);
}

void generateReport3(const struct TransactionRecord tx[], int txCount, const char *outFilename) {
    struct rowReport {
        char accountName[200];
        char dateStr[11];
        int dateInt;
        float totalWithdrawalAmounts;
        int numberOfDeposits;
        float totalDepositAmounts;
        float turnoverVelocity;
        char riskFlagLevel[16];
    };

    struct rowReport *rows = (struct rowReport *)calloc((size_t)(txCount > 0 ? txCount : 1), sizeof(struct rowReport));
    if (!rows) {
        printf("Memory failure\n\n");
        return;
    }
    int rowCount = 0;

    for (int i = 0; i < txCount; i++) {
        struct SimpleDate dt;
        if (!tx[i].valid || !parseDmY(tx[i].currentDate, &dt)) continue;

        int idx = -1;
        for (int j = 0; j < rowCount; j++) {
            if (rows[j].dateInt == dt.yyyymmdd && strcmp(rows[j].accountName, tx[i].accountName) == 0) {
                idx = j;
                break;
            }
        }
        if (idx == -1) {
            idx = rowCount++;
            snprintf(rows[idx].accountName, sizeof(rows[idx].accountName), "%s", tx[i].accountName);
            snprintf(rows[idx].dateStr, sizeof(rows[idx].dateStr), "%s", tx[i].currentDate);
            rows[idx].dateInt = dt.yyyymmdd;
        }

        if (strcmp(tx[i].transactionType, "withdrawal") == 0) {
            rows[idx].totalWithdrawalAmounts += tx[i].amount;
        } else if (strcmp(tx[i].transactionType, "deposit") == 0) {
            rows[idx].numberOfDeposits += 1;
            rows[idx].totalDepositAmounts += tx[i].amount;
        }
    }

    for (int i = 0; i < rowCount; i++) {
        rows[i].turnoverVelocity = (rows[i].totalDepositAmounts > 0.0f)
            ? (rows[i].totalWithdrawalAmounts / rows[i].totalDepositAmounts) * 100.0f
            : 0.0f;

        if (rows[i].totalWithdrawalAmounts >= 500000.0f || rows[i].turnoverVelocity >= 90.0f) {
            strcpy(rows[i].riskFlagLevel, "Suspicious");
        } else {
            strcpy(rows[i].riskFlagLevel, "Normal");
        }
    }

    for (int i = 0; i < rowCount - 1; i++) {
        for (int j = i + 1; j < rowCount; j++) {
            int swap = 0;
            if (rows[j].dateInt < rows[i].dateInt) {
                swap = 1;
            } else if (rows[j].dateInt == rows[i].dateInt) {
                int rCmp = strcmp(rows[j].riskFlagLevel, rows[i].riskFlagLevel);
                if (rCmp > 0 || (rCmp == 0 && strcmp(rows[j].accountName, rows[i].accountName) < 0)) {
                    swap = 1;
                }
            }
            if (swap) {
                struct rowReport tmp = rows[i];
                rows[i] = rows[j];
                rows[j] = tmp;
            }
        }
    }

    FILE *fp = fopen(outFilename, "w");
    if (!fp) {
        printf("Error writing Report 3 CSV\n\n");
        free(rows);
        return;
    }
    fprintf(fp, "AccountName,Date,TotalWithdrawalAmounts,NumberOfDeposits,TotalDepositAmounts,TurnoverVelocity,RiskFlagLevel\n");
    for (int i = 0; i < rowCount; i++) {
        fprintf(fp, "%s,%s,%.2f,%d,%.2f,%.2f,%s\n", rows[i].accountName, rows[i].dateStr, rows[i].totalWithdrawalAmounts, rows[i].numberOfDeposits, rows[i].totalDepositAmounts, rows[i].turnoverVelocity, rows[i].riskFlagLevel);
    }
    fclose(fp);
    free(rows);
    printf("Report 3 saved to %s (%d rows).\n", outFilename, rowCount);
}

void generateReport4(const struct TransactionRecord tx[], int txCount, const char *outFilename) {
    struct rowReport {
        char accountName[200];
        char accountNumber[16];
        char currentDate[11];
        char currentTime[9];
        char transactionType[16];
        float amount;
        float currentBalance;
        float amountRatio;
    };

    char accNums[APP_MAX_ACCOUNTS][16];
    double accSums[APP_MAX_ACCOUNTS];
    int accCounts[APP_MAX_ACCOUNTS];
    int uniqueAccs = 0;

    for (int i = 0; i < txCount; i++) {
        if (!tx[i].valid || tx[i].accountNumber[0] == '\0') continue;

        int idx = -1;
        for (int j = 0; j < uniqueAccs; j++) {
            if (strcmp(accNums[j], tx[i].accountNumber) == 0) {
                idx = j;
                break;
            }
        }
        if (idx == -1 && uniqueAccs < APP_MAX_ACCOUNTS) {
            idx = uniqueAccs++;
            snprintf(accNums[idx], sizeof(accNums[idx]), "%s", tx[i].accountNumber);
            accSums[idx] = 0.0;
            accCounts[idx] = 0;
        }
        if (idx != -1) {
            accSums[idx] += tx[i].amount;
            accCounts[idx] += 1;
        }
    }

    struct rowReport *rows = (struct rowReport *)calloc((size_t)(txCount > 0 ? txCount : 1), sizeof(struct rowReport));
    if (!rows) {
        printf("Memory failure\n\n");
        return;
    }
    int rowCount = 0;

    for (int i = 0; i < txCount; i++) {
        if (!tx[i].valid || tx[i].amount <= 50000.0f) continue;

        float avgAmount = 0.0f;
        for (int j = 0; j < uniqueAccs; j++) {
            if (strcmp(accNums[j], tx[i].accountNumber) == 0 && accCounts[j] > 0) {
                avgAmount = (float)(accSums[j] / accCounts[j]);
                break;
            }
        }

        struct rowReport r;
        snprintf(r.accountName, sizeof(r.accountName), "%s", tx[i].accountName);
        snprintf(r.accountNumber, sizeof(r.accountNumber), "%s", tx[i].accountNumber);
        snprintf(r.currentDate, sizeof(r.currentDate), "%s", tx[i].currentDate);
        snprintf(r.currentTime, sizeof(r.currentTime), "%s", tx[i].currentTime);
        snprintf(r.transactionType, sizeof(r.transactionType), "%s", tx[i].transactionType);
        r.amount = tx[i].amount;
        r.currentBalance = tx[i].currentBalance;
        r.amountRatio = (avgAmount > 0.0f) ? (tx[i].amount / avgAmount) : 0.0f;

        rows[rowCount++] = r;
    }

    for (int i = 0; i < rowCount - 1; i++) {
        for (int j = i + 1; j < rowCount; j++) {
            if (rows[j].amountRatio > rows[i].amountRatio) {
                struct rowReport tmp = rows[i];
                rows[i] = rows[j];
                rows[j] = tmp;
            }
        }
    }

    FILE *fp = fopen(outFilename, "w");
    if (!fp) {
        printf("Error writing Report 4 CSV\n\n");
        free(rows);
        return;
    }

    fprintf(fp, "AccountName,AccountNumber,Date,Time,Type,Amount,CurrentBalance,AmountRatio\n");
    for (int i = 0; i < rowCount; i++) {
        fprintf(fp, "%s,%s,%s,%s,%s,%.2f,%.2f,%.2f\n", rows[i].accountName, rows[i].accountNumber, rows[i].currentDate, rows[i].currentTime, rows[i].transactionType, rows[i].amount, rows[i].currentBalance, rows[i].amountRatio);
    }

    fclose(fp);
    free(rows);
    printf("Report 4 saved to %s (%d rows).\n", outFilename, rowCount);
}

void summaryJSON(const struct TransactionRecord tx[], int txCount, const char *outFilename) {
    FILE *fp = fopen(outFilename, "w");
    if (!fp) {
        printf("Error writing summary JSON\n");
        return;
    }

    char accNums[APP_MAX_ACCOUNTS][16];
    double latestDate[APP_MAX_ACCOUNTS];
    float latestBalance[APP_MAX_ACCOUNTS];
    int uniqueAccs = 0;
    int totalDeposits = 0;
    int totalWithdrawals = 0;

    for (int i = 0; i < txCount; i++) {
        if (!tx[i].valid || tx[i].accountNumber[0] == '\0') continue;

        if (strcmp(tx[i].transactionType, "deposit") == 0) {
            totalDeposits++;
        } if (strcmp(tx[i].transactionType, "withdrawal") == 0) {
            totalWithdrawals++;
        }

        struct SimpleDate dt;
        int dtInt = parseDmY(tx[i].currentDate, &dt) ? dt.yyyymmdd : -1;

        int idx = -1;
        for (int j = 0; j < uniqueAccs; j++) {
            if (strcmp(accNums[j], tx[i].accountNumber) == 0) {
                idx = j;
                break;
            }
        }

        if (idx == -1 && uniqueAccs < APP_MAX_ACCOUNTS) {
            idx = uniqueAccs++;
            snprintf(accNums[idx], sizeof(accNums[idx]), "%s", tx[i].accountNumber);
            latestDate[idx] = -1;
            latestBalance[idx] = 0.0f;
        }
        if (idx != -1 && dtInt >= latestDate[idx]) {
            latestDate[idx] = dtInt;
            latestBalance[idx] = tx[i].currentBalance;
        }
    }

    float totalBalance = 0.0f;
    float totalInterests = 0.0f;

    for (int i = 0; i < uniqueAccs; i++) {
        totalBalance += latestBalance[i];
        if (latestBalance[i] >= 10000.0f) {
            totalInterests += latestBalance[i] * (APP_ANNUAL_INT_RATE / 365.0f) * 30.0f;
        }
    }


    fprintf(fp, "{\n");
    fprintf(fp, "  \"total_accounts\": %d,\n", uniqueAccs);
    fprintf(fp, "  \"total_transactions\": %d,\n", totalDeposits + totalWithdrawals);
    fprintf(fp, "  \"total_deposits\": %d,\n", totalDeposits);
    fprintf(fp, "  \"total_withdrawals\": %d,\n", totalWithdrawals);
    fprintf(fp, "  \"total_balance\": %.2f,\n", totalBalance);
    fprintf(fp, "  \"total_interest_expense\": %.2f\n", totalInterests);
    fprintf(fp, "}\n");

    fclose(fp);
    printf("json has been saved to %s.\n", outFilename);
}

void generateReports(const struct TransactionRecord tx[], int txCount) {
    printf("Generating Reports.\n\n");
    printf("This process will automatically load the files and generate the reports.\n");
    printf(" [1] Start\n [2] Summary\n [3] Cancel\n");
    int choice;
    characterScan(&choice);
    if (choice == 2) {
        summaryJSON(tx, txCount, "summary.json");
        return;
    }
    else if (choice != 1) {
        printf("Report generation canceled.\n");
        return;
    }

    generateReport1(tx, txCount, "report1.csv");
    generateReport2(tx, txCount, "report2.csv");
    generateReport3(tx, txCount, "report3.csv");
    generateReport4(tx, txCount, "report4.csv");
}

int menu(struct Account *acc, struct exchangeRate *rate, int *txCount, struct TransactionRecord txRecords[]) {
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
            accountRegister(acc, txRecords, txCount, "bank_transactions-1.csv");
            break;
        case 2:
            depositAmount(acc, rate, txRecords, txCount, "bank_transactions-1.csv");
            break;
        case 3:
            withdrawAmount(acc, rate, txRecords, txCount, "bank_transactions-1.csv");
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
            generateReports(txRecords, *txCount);
            break;
        case 8:
            printf("Exiting program. bye bye!\n");
            return 0;
        default:
            printf("Error. Invalid input. Please enter a number between 1 and 8. Terminating program.\n");
    }
    return 1;
}


int main() {
    // variables for the rest of the program
    struct Account account = {0};
    struct TransactionRecord txRecords[APP_MAX_TX];
    int txCount = 0;
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

    txCount = loadTransactionsFromCSV("bank_transactions-1.csv", txRecords, APP_MAX_TX);

    while(menuChoice)
        menuChoice = menu(&account, rate, &txCount, txRecords); 
}