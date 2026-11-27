#********************
#Last names: Acosta, Bunggo, Gregorio, Macasaet
#Language: R
#Paradigm(s): Object-Oriented, Functional, Vector oriented
#********************

#  [1] Register Account Name - REQ-0001
#  [2] Deposit Amount        - REQ-0002
#  [3] Withdraw Amount       - REQ-0002
#  [4] Currency Exchange     - (MCO1 feature, unchanged)
#  [5] Record Exchange Rates - (MCO1 feature, unchanged)
#  [6] Show Interest Amount  - (MCO1 feature, now reads balance from the CSV)
#  [7] Generate Reports      - REQ-0003 to REQ-0010
#                              (load file, validate, clean, Reports 1-4, summary.json)
#  [8] Exit

#=============================================================
# SETTINGS

file_csv <- "bank_transactions.csv"  

# Column order of the CSV. New rows are appended in THIS order,
csv_columns <- c("account_name", "account_number", "date", "time",
                 "currency", "amount", "type", "current_balance")

# Readable Version
# basahin_csv() renames the file's columns to these right after reading
code_names <- c("AccountName", "AccountNumber", "CurrentDate", "CurrentTime",
                "Currency", "Amount", "TransactionType", "CurrentBalance")

int_rate <- 0.05          # 5% per annum
min_initial_deposit <- 5000

# readLine is buggy on Rscript for some reason, but works on R's interactive terminal
#stdin = standard input
stdin_cond <- NULL
if (!interactive()) {
  stdin_cond <- file("stdin")
  open(stdin_cond)
}

#=============================================================
# HELPER FUNCTIONS

# Asks the user for input
# Works for both Rscript and interactive R.
# Used everywhere in place of readline(prompt = ...)
ask <- function(prompt) {
  if (interactive()) {
    return(readline(prompt = prompt))
  }
  cat(prompt)
  readLines(stdin_cond, n = 1)
}

oo_o_hinde <- function() {
  repeat {
    tanong <- toupper(trimws(ask("\nBack to the Main Menu (Y/N): ")))
    if (tanong == "Y") {
      return(TRUE)
    } else if (tanong == "N") {
      return(FALSE)
    } else {
      cat("Please enter Y or N\n")
    }
  }
}

papalit_o_hinde <- function() {
  repeat {
    tanong <- toupper(trimws(ask("\nConvert another currency (Y/N)? ")))
    if (tanong == "Y") {
      return(TRUE)
    } else if (tanong == "N") {
      return(FALSE)
    } else {
      cat("Please enter Y or N\n")
    }
  }
}

# Creates the CSV with only the header row if it does not exist yet
gawa_csv_kung_wala <- function() {
  if (!file.exists(file_csv)) {
    header <- data.frame(matrix(ncol = length(csv_columns), nrow = 0))
    names(header) <- csv_columns
    write.csv(header, file_csv, row.names = FALSE)
  }
}

# Reads the whole CSV
# Every column is read as character so bad values (like "abc" in Amount) don't crash the reading of .csv
# Will get converted and checked later in load_and_clean()
# Empty cells become NA
basahin_csv <- function() {
  gawa_csv_kung_wala()
  raw <- read.csv(file_csv, colClasses = "character", na.strings = c("", "NA"))

  # rename the file's columns (csv_columns) to the names used by the rest of the code (code_names)
  # pos = for each column in the file, its position in csv_columns (NA if it is not listed there)
  pos <- match(names(raw), csv_columns)
  # only rename the columns that were found, any extra column in the file is left alone
  names(raw)[!is.na(pos)] <- code_names[pos[!is.na(pos)]]
  raw
}

# Turns text into a number. Removes anything that is not a digit, "." or "-"
# Unreadable text becomes NA. (like "abc" or "1.2.3")
# Letters mixed with digits are stripped, so "abc123" becomes 123.
to_float <- function(x) {
  suppressWarnings(as.numeric(gsub("[^0-9.-]", "", x)))
}

# REQ-0001: next account number = highest existing number + 1 (10 digits)
susunod_account_number <- function(tx) {
  nums <- suppressWarnings(as.numeric(tx$AccountNumber))
  nums <- nums[!is.na(nums)]
  if (length(nums) == 0) {
    return("1000000001")          # first ever account
  }
  sprintf("%010.0f", max(nums) + 1)   # %010.0f = whole number padded to 10 digits
}

# Finds the LATEST row (most recent transaction) of an account name.
# Returns NULL if the account does not exist.
# The balance of that row is the account's current balance.
hanapin_huling_row <- function(tx, pangalan) {
  # rows of this account (not case sensitive)
  rows <- tx[!is.na(tx$AccountName) & tolower(tx$AccountName) == tolower(pangalan), ]
  if (nrow(rows) == 0) {
    return(NULL)
  }
  # combine date + time into one timestamp to sort chronologically
  stamp <- as.POSIXct(paste(rows$CurrentDate, rows$CurrentTime), format = "%d/%m/%Y %H:%M:%S", tz = "UTC")
  rows <- rows[!is.na(stamp), ]     # throw away rows with unreadable date/time
  stamp <- stamp[!is.na(stamp)]
  if (nrow(rows) == 0) {
    return(NULL)
  }
  rows <- rows[order(stamp), ]      # oldest first, newest last
  rows[nrow(rows), ]                # the last row = newest
}

# REQ-0002: appends ONE transaction row at the bottom of the CSV
# uri = "deposit" or "withdrawal"
# Date is dd/mm/yyyy, time is 24-hour hh:mm:ss
isulat_transaction <- function(acc_num, pangalan, uri, amount, new_balance) {
  gawa_csv_kung_wala()
  ngayon <- Sys.time()
  # write.table below adds the row by POSITION (no header), 
  # so columns must be in the same order as csv_columns at the top of the file (via basahin_csv)
  bagong_row <- data.frame(
    AccountName     = pangalan,
    AccountNumber   = acc_num,
    CurrentDate     = format(ngayon, "%d/%m/%Y"),
    CurrentTime     = format(ngayon, "%H:%M:%S"),
    Currency        = "PHP",
    Amount          = sprintf("%.2f", amount),
    TransactionType = uri,
    CurrentBalance  = sprintf("%.2f", new_balance)
  )
  # append = TRUE adds to the end of the file, col.names = FALSE skips the header
  write.table(bagong_row, file_csv, sep = ",", append = TRUE, col.names = FALSE, row.names = FALSE)
}

#=============================================================
# MENU [1] to [6]

# REQ-0001: register a new account and save it (with its initial deposit) to the CSV
regis_account <- function() {
  pangalan <- trimws(ask("Account Name (First name Last name): "))

  if (pangalan == "") {
    cat("Name cannot be empty\n")
    return(invisible(NULL))
  }
  # splitting by spaces should give at least 2 words (first name + last name)
  if (length(strsplit(pangalan, " +")[[1]]) < 2) {
    cat("Please enter both first name and last name\n")
    return(invisible(NULL))
  }

  tx <- basahin_csv()
  if (!is.null(hanapin_huling_row(tx, pangalan))) {
    cat("Account name already exists\n")
    return(invisible(NULL))
  }

  initial <- suppressWarnings(as.numeric(ask("Initial Deposit: ")))
  if (is.na(initial) || initial < min_initial_deposit) {
    cat("Initial deposit must be at least", sprintf("%.2f", min_initial_deposit), "\n")
    return(invisible(NULL))
  }
  initial <- round(initial, 2)

  acc_num <- susunod_account_number(tx)
  # the initial deposit is saved as the first "deposit" row of the account
  isulat_transaction(acc_num, pangalan, "deposit", initial, initial)

  cat("\n***\nAccount Name =", pangalan, "\n")
  cat("Account Number =", acc_num, "\n")
  cat("Account created successfully!\n")
  invisible(NULL)
}

deposit <- function(currency_list) {
  pangalan_local <- trimws(ask("Account Name: "))
  tx <- basahin_csv()
  huling_row <- hanapin_huling_row(tx, pangalan_local)   # latest row = current balance
  if (is.null(huling_row)) {
    cat("Account not found. Please register an account first\n")
    return(invisible(NULL))
  }
  balance <- as.numeric(huling_row$CurrentBalance)

  cat("Base Currency: PHP\n")
  cat("Current Balance:", sprintf("%.2f", balance), "\n")
  cat("Currency: PHP\n\n")

  cat("Currency Option:\n")
  cat("[1] Philippine Peso (PHP)\n")
  cat("[2] United States Dollar (USD)\n")
  cat("[3] Japanese Yen (JPY)\n")
  cat("[4] British Pound Sterling (GBP)\n")
  cat("[5] Euro (EUR)\n")
  cat("[6] Chinese Yuan Renminni (CNY)\n")
  pili_currency <- as.integer(ask("Select Currency: "))
  if (is.na(pili_currency) || pili_currency < 1 || pili_currency > 6) {
    cat("Invalid currency\n")
    return(invisible(NULL))
  }

  deposit_amount <- as.numeric(ask("Deposit Amount: "))
  if (is.na(deposit_amount) || deposit_amount <= 0) {
    cat("Invalid amount\n")
    return(invisible(NULL))
  }

  # everything is saved in PHP, so convert the deposit first
  php_amount <- round(deposit_amount * currency_list[[pili_currency]], 2)
  new_balance <- balance + php_amount

  # save the transaction
  isulat_transaction(huling_row$AccountNumber, huling_row$AccountName, "deposit", php_amount, new_balance)

  cat("\n***\nAccount Name =", huling_row$AccountName, "\n")
  cat(sprintf("Deposit Amount: %.2f %s (%.2f PHP)\n", deposit_amount, names(currency_list)[pili_currency], php_amount))
  cat("Currency: PHP\n")
  cat(sprintf("Updated Balance: %.2f\n", new_balance))
  invisible(NULL)
}

withdraw <- function(currency_list) {
  pangalan_local <- trimws(ask("Account Name: "))
  tx <- basahin_csv()
  huling_row <- hanapin_huling_row(tx, pangalan_local)   # latest row = current balance
  if (is.null(huling_row)) {
    cat("Account not found. Please register an account first\n")
    return(invisible(NULL))
  }
  balance <- as.numeric(huling_row$CurrentBalance)

  cat("Base Currency: PHP\n")
  cat("Current Balance:", sprintf("%.2f", balance), "\n")
  cat("Currency: PHP\n\n")

  cat("Currency Option:\n")
  cat("[1] Philippine Peso (PHP)\n")
  cat("[2] United States Dollar (USD)\n")
  cat("[3] Japanese Yen (JPY)\n")
  cat("[4] British Pound Sterling (GBP)\n")
  cat("[5] Euro (EUR)\n")
  cat("[6] Chinese Yuan Renminni (CNY)\n")
  pili_currency <- as.integer(ask("Select Currency: "))
  if (is.na(pili_currency) || pili_currency < 1 || pili_currency > 6) {
    cat("Invalid currency\n")
    return(invisible(NULL))
  }

  withdraw_amount <- as.numeric(ask("Withdraw Amount: "))
  if (is.na(withdraw_amount) || withdraw_amount <= 0) {
    cat("Invalid amount\n")
    return(invisible(NULL))
  }

  # everything is saved in PHP, so convert the withdrawal first
  php_amount <- round(withdraw_amount * currency_list[[pili_currency]], 2)
  if (php_amount > balance) {
    cat("Insufficient balance\n")
    return(invisible(NULL))
  }
  new_balance <- balance - php_amount

  # save the transaction
  isulat_transaction(huling_row$AccountNumber, huling_row$AccountName, "withdrawal", php_amount, new_balance)

  cat("\n***\nAccount Name =", huling_row$AccountName, "\n")
  cat(sprintf("Withdraw Amount: %.2f %s (%.2f PHP)\n", withdraw_amount, names(currency_list)[pili_currency], php_amount))
  cat("Currency: PHP\n")
  cat(sprintf("Updated Balance: %.2f\n", new_balance))
  invisible(NULL)
}

record_exc_rate <- function(currency_list) {
  cat("[1] Philippine Peso (PHP)\n")
  cat("[2] United States Dollar (USD)\n")
  cat("[3] Japanese Yen (JPY)\n")
  cat("[4] British Pound Sterling (GBP)\n")
  cat("[5] Euro (EUR)\n")
  cat("[6] Chinese Yuan Renminni (CNY)\n")
  pili_currency <- as.integer(ask("Select Foreign Currency: "))
  if (is.na(pili_currency) || pili_currency < 1 || pili_currency > 6) {
    cat("Invalid currency\n")
    return(currency_list)
  }
  if (pili_currency == 1) {
    cat("PHP is the base currency thus its rate cannot be changed\n")
    return(currency_list)
  }

  bagong_rate <- as.numeric(ask("Exchange Rate: "))
  if (is.na(bagong_rate) || bagong_rate <= 0) {
    cat("Invalid rate\n")
    return(currency_list)
  }

  currency_list[[pili_currency]] <- bagong_rate
  cat("\n***\n")
  cat("Currency:", names(currency_list)[pili_currency], "\n")
  cat(sprintf("Exchange Rate: %.2f\n", bagong_rate))
  currency_list
}

curr_exc <- function(currency_list) {
  cat("Source Currency Option:\n")
  cat("[1] Philippine Peso (PHP)\n")
  cat("[2] United States Dollar (USD)\n")
  cat("[3] Japanese Yen (JPY)\n")
  cat("[4] British Pound Sterling (GBP)\n")
  cat("[5] Euro (EUR)\n")
  cat("[6] Chinese Yuan Renminni (CNY)\n")

  source_currency <- as.integer(ask("Source Currency: "))
  if (is.na(source_currency) || source_currency < 1 || source_currency > 6) {
    cat("Invalid currency\n")
    return()
  }

  source_amount <- as.numeric(ask("Source Amount: "))
  if (is.na(source_amount) || source_amount <= 0) {
    cat("Invalid amount\n")
    return(invisible(NULL))
  }

  cat("\n\nExchanged Currency Options:\n")
  cat("[1] Philippine Peso (PHP)\n")
  cat("[2] United States Dollar (USD)\n")
  cat("[3] Japanese Yen (JPY)\n")
  cat("[4] British Pound Sterling (GBP)\n")
  cat("[5] Euro (EUR)\n")
  cat("[6] Chinese Yuan Renminni (CNY)\n")
  exchange_currency <- as.integer(ask("\nExchange Currency: "))
  if (is.na(exchange_currency) || exchange_currency < 1 || exchange_currency > 6) {
    cat("Invalid currency\n")
    return()
  }
  exchange_amount <- source_amount * currency_list[[source_currency]] / currency_list[[exchange_currency]]
  cat(sprintf("Exchange Amount: %.2f\n", exchange_amount))
}

disp_interest_amnt <- function() {
  pangalan_local <- trimws(ask("Account Name: "))
  tx <- basahin_csv()
  huling_row <- hanapin_huling_row(tx, pangalan_local)   # latest row = current balance
  if (is.null(huling_row)) {
    cat("Account not found. Please register an account first\n")
    return(invisible(NULL))
  }

  cat("Current Balance: ", huling_row$CurrentBalance, "\nCurrency: PHP")
  cat("\nCurrent Interest Rate: 5%\n")

  interest_araw <- ask("\n\nTotal Number of Days: ")
  hiwa <- strsplit(trimws(interest_araw), " ")[[1]]
  interest_araw <- as.integer(hiwa[1])

  if (is.na(interest_araw) || interest_araw <= 0) {
    cat("Invalid number of days\n")
    return(invisible(NULL))
  }
  bal <- as.numeric(huling_row$CurrentBalance)

  cat("\nDay | Interest | Balance |\n")
  for (araw in 1:interest_araw) {
    interest <- round(bal * int_rate / 365, 2)
    bal <- bal + interest
    cat(sprintf("%-3d | %-8.2f | %-7.2f |\n", araw, interest, bal))
  }
  invisible(NULL)
}

#=============================================================
# MENU [7] GENERATE REPORTS + DATA PROCESSING (REQ-0003 to REQ-0005)

# Reads the CSV, keeps PHP rows only, validates, cleans, and returns the clean data frame.
# Returns NULL if nothing was found.
# Clean data frame has ff columns:
#   AccountNumber, AccountName, Date (Date type), DateTime (POSIXct),
#   TransactionType ("deposit"/"withdrawal"), Amount (number), CurrentBalance (number)
load_and_clean <- function() {
  raw <- basahin_csv()
  total_rows <- nrow(raw)

  # the columns every report needs
  kailangan <- c("AccountNumber", "AccountName", "CurrentDate", "CurrentTime", "TransactionType", "Amount", "CurrentBalance")
  kulang_col <- setdiff(kailangan, names(raw))
  if (length(kulang_col) > 0) {
    cat("The CSV is missing these columns:", paste(kulang_col, collapse = ", "), "\n")
    return(NULL)
  }

  # REQ-0003: keep PHP rows only (if there is no Currency column, assume all are PHP)
  if ("Currency" %in% names(raw)) {
    is_php <- !is.na(raw$Currency) & toupper(trimws(raw$Currency)) == "PHP"
    raw <- raw[is_php, ]
  }
  php_rows <- nrow(raw)
  cat(sprintf("\nProcessing dataset... (%s rows loaded, %s filtered for PHP currency)\n",
              format(total_rows, big.mark = ","), format(php_rows, big.mark = ",")))

  if (php_rows == 0) {
    cat("No PHP rows to process\n")
    return(NULL)
  }

  # REQ-0004 + REQ-0005: validate and clean
  # Missing values - TRUE for each row that has an empty cell in any needed column
  may_kulang <- !complete.cases(raw[, kailangan])

  # Convert financial fields to numbers, bad text becomes NA
  raw$Amount <- to_float(raw$Amount)
  raw$CurrentBalance <- to_float(raw$CurrentBalance)
  bad_amount <- !may_kulang & (is.na(raw$Amount) | is.na(raw$CurrentBalance))

  # Parse the date + time, an invalid one (like 31/02/2025 ONLY - no hours,mins,secs) becomes NA
  raw$DateTime <- as.POSIXct(paste(raw$CurrentDate, raw$CurrentTime),
                             format = "%d/%m/%Y %H:%M:%S", tz = "UTC")
  bad_date <- !may_kulang & is.na(raw$DateTime)

  # Transaction type must be deposit or withdrawal
  raw$TransactionType <- tolower(trimws(raw$TransactionType))
  bad_type <- !may_kulang & !(raw$TransactionType %in% c("deposit", "withdrawal"))

  # log what was found
  cat("Validation log:\n")
  cat("  Rows with missing values  :", sum(may_kulang), "\n")
  cat("  Rows with invalid amounts :", sum(bad_amount), "\n")
  cat("  Rows with invalid dates   :", sum(bad_date), "\n")
  cat("  Rows with invalid type    :", sum(bad_type), "\n")

  # DROP incomplete / invalid rows instead of imputing them
  keep <- !(may_kulang | bad_amount | bad_date | bad_type)
  data <- raw[keep, ]
  cat("  Rows removed:", sum(!keep), "| Clean rows:", nrow(data), "\n")

  if (nrow(data) == 0) {
    cat("No clean rows left to process\n")
    return(NULL)
  }

  # sort oldest to newest (rows with the same timestamp keep their file order),
  data <- data[order(data$DateTime), ]

  data.frame(
    AccountNumber   = trimws(data$AccountNumber),
    AccountName     = trimws(data$AccountName),
    Date            = as.Date(data$DateTime),
    DateTime        = data$DateTime,
    TransactionType = data$TransactionType,
    Amount          = data$Amount,
    CurrentBalance  = data$CurrentBalance
  )
}

#=============================================================
# MENU [7] GENERATE REPORTS - REPORT HELPERS

# Turns the numeric columns into text like "1,234,567.89" and dates into dd/mm/yyyy
# (REQ-0012) Whole-number (integer) columns and text columns are left alone.
format_report <- function(df) {
  for (col in names(df)) {
    if (inherits(df[[col]], "Date")) {                 # check Date first, a Date is also a "double"
      df[[col]] <- format(df[[col]], "%d/%m/%Y")
    } else if (is.double(df[[col]])) {
      df[[col]] <- formatC(df[[col]], format = "f", digits = 2, big.mark = ",")
    }
  }
  df
}

# Prints the title + first 2 rows, then saves the full report to a CSV file
show_and_save <- function(df, filename, title) {
  cat("\n", title, "\n", sep = "")
  nice <- format_report(df)
  if (nrow(nice) == 0) {
    cat("(no rows matched)\n")
  } else {
    print(head(nice, 2), row.names = FALSE)
  }
  write.csv(nice, filename, row.names = FALSE)
  cat("(Full table exported to ", filename, ")\n", sep = "")
}

# Sums the Amount per account per day for a set of rows.
# Returns columns: AccountNumber, Date, Total, Count
# (used by Report 3 once for withdrawals and once for deposits)
sum_per_day <- function(df) {
  if (nrow(df) == 0) {      # aggregate() fails on empty data, so return an empty table
    return(data.frame(AccountNumber = character(), Date = as.Date(character()),
                      Total = numeric(), Count = integer()))
  }
  totals <- aggregate(Amount ~ AccountNumber + Date, data = df, FUN = sum)
  counts <- aggregate(Amount ~ AccountNumber + Date, data = df, FUN = length)
  names(totals)[3] <- "Total"
  names(counts)[3] <- "Count"
  merge(totals, counts, by = c("AccountNumber", "Date"))
}

#=============================================================
# REPORTS (REQ-0006 to REQ-0009)
# All reports take the clean data frame from load_and_clean()

# REQ-0006 Report 1: Banking Privilege Tiers
make_report1 <- function(data) {
  # "last month" = the last COMPLETE calendar month in the data:
  # the month before the month of the most recent transaction (so it is never partial)
  # (ex: latest transaction is 20/11/2025, so last month = October 2025)
  last_date <- max(data$Date)     # latest date in the data, also used for AccountAge below

  # first day of the month of the latest transaction, ex: 2025-11-01
  this_month_start <- as.Date(format(last_date, "%Y-%m-01"))

  # first day of the month before it, ex: 2025-10-01
  # (by = "-1 month" goes backwards, and [2] picks the second date in the pair)
  month_start <- seq(this_month_start, by = "-1 month", length.out = 2)[2]

  # every day of that month: from its 1st up to the day before this month starts
  days <- seq(month_start, this_month_start - 1, by = "day")
  num_days <- length(days)

  acc_nums <- unique(data$AccountNumber)
  n <- length(acc_nums)
  # one slot per account
  pangalan <- character(n)
  age <- integer(n)
  cur_bal <- numeric(n)
  avg_bal <- numeric(n)

  for (i in seq_len(n)) {
    acc <- data[data$AccountNumber == acc_nums[i], ]   # all rows of this account (oldest first)

    # End-of-Day balance for each day of the month:
    # the balance of the last transaction on or before that day (0 if not opened yet)
    eod <- sapply(days, function(d) {
    balances <- acc$CurrentBalance[acc$Date <= d]
    if (length(balances) == 0) {
      0                                   # account not opened yet on this day
    } else {
      balances[length(balances)]          # balance of the last transaction on or before this day
    }
    })

    pangalan[i] <- acc$AccountName[1]
    age[i] <- as.integer(last_date - min(acc$Date))    # days since the first transaction
    cur_bal[i] <- acc$CurrentBalance[nrow(acc)]        # last row = current balance
    avg_bal[i] <- sum(eod) / num_days                  # AverageDailyBalance
  }

  # privilege tier from the average daily balance
  tier <- ifelse(avg_bal < 500000, "Silver", ifelse(avg_bal < 1000000, "Gold", "Platinum"))

  report <- data.frame(AccountName = pangalan, AccountAge = age,
                       CurrentBalance = cur_bal, AverageDailyBalance = avg_bal,
                       PrivilegeTier = tier)
  report <- report[report$AverageDailyBalance >= 100000, ]     # filter >= 100,000
  report[order(-report$AverageDailyBalance), ]                 # highest first
}

# REQ-0007 Report 2: Interest Expense Projection
make_report2 <- function(data) {
  # keep only the newest row of each account (fromLast = TRUE keeps the last one)
  latest <- data[!duplicated(data$AccountNumber, fromLast = TRUE), ]
  latest <- latest[latest$CurrentBalance >= 10000, ]           # filter >= 10,000

  # all accounts are computed at the same time, day by day
  bal <- latest$CurrentBalance
  expected <- rep(0, length(bal))
  for (araw in 1:30) {
    interest <- round(bal * int_rate / 365, 2)   # Daily Interest = balance x (rate / 365)
    bal <- bal + interest                        # interest is added to the balance (same as menu 6)
    expected <- expected + interest
  }

  report <- data.frame(AccountName = latest$AccountName,
                       CurrentBalance = latest$CurrentBalance,
                       ExpectedInterest = expected)
  report[order(-report$ExpectedInterest), ]                    # highest first
}

# REQ-0008 Report 3: Suspicious Client Behavior (one row per account per day)
make_report3 <- function(data) {
  w <- sum_per_day(data[data$TransactionType == "withdrawal", ])
  d <- sum_per_day(data[data$TransactionType == "deposit", ])
  names(w)[3:4] <- c("TotalWithdrawalAmounts", "NumberOfWithdrawals")
  names(d)[3:4] <- c("TotalDepositAmounts", "NumberOfDeposits")

  # every account + day that has at least one transaction
  r <- unique(data[, c("AccountNumber", "Date")])
  # add the withdrawal and deposit totals (all.x = TRUE keeps days with none)
  r <- merge(r, w, by = c("AccountNumber", "Date"), all.x = TRUE)
  r <- merge(r, d, by = c("AccountNumber", "Date"), all.x = TRUE)
  # days with no withdrawal / deposit came out as NA, change them to 0
  r$TotalWithdrawalAmounts[is.na(r$TotalWithdrawalAmounts)] <- 0
  r$TotalDepositAmounts[is.na(r$TotalDepositAmounts)] <- 0
  r$NumberOfDeposits[is.na(r$NumberOfDeposits)] <- 0

  # TurnoverVelocity = TotalWithdrawal / TotalDeposit x 100
  # If there was no deposit that day we can't divide: 0 if nothing was withdrawn either,
  # otherwise 100 (money went out with nothing coming in). Change the 100 if needed.
  r$TurnoverVelocity <- ifelse(r$TotalDepositAmounts > 0,
                               r$TotalWithdrawalAmounts / r$TotalDepositAmounts * 100,
                               ifelse(r$TotalWithdrawalAmounts > 0, 100, 0))

  # RiskFlagLevel
  r$RiskFlagLevel <- ifelse(r$TotalWithdrawalAmounts >= 500000 | r$TurnoverVelocity >= 90,
                            "Suspicious", "Normal")

  # look up the account name using the account number
  r$AccountName <- data$AccountName[match(r$AccountNumber, data$AccountNumber)]

  report <- data.frame(AccountName = r$AccountName, Date = r$Date,
                       TotalWithdrawalAmounts = r$TotalWithdrawalAmounts,
                       NumberOfDeposits = as.integer(r$NumberOfDeposits),
                       TotalDepositAmounts = r$TotalDepositAmounts,
                       TurnoverVelocity = r$TurnoverVelocity,
                       RiskFlagLevel = r$RiskFlagLevel)

  # sort: Date ascending, then Suspicious before Normal (so rank 1 = Suspicious)
  flag_rank <- ifelse(report$RiskFlagLevel == "Suspicious", 1, 2)
  report[order(report$Date, flag_rank, report$AccountName), ]
}

# REQ-0009 Report 4: Peak Transaction Hours (customized report)
# Why?
# The bank needs to know WHEN clients transact so it can plan around it.
# For each hour of the day (00 to 23) this report shows how many transactions happened,
# how many were deposits and withdrawals, and how much money moved. The busiest hours
# ("peak" hours) come first. The bank can use this to schedule more tellers and
# customer support at busy hours, to keep enough cash on hand, and to do system
# maintenance during the quietest hours so that fewer clients are affected.
make_report4 <- function(data) {
  # the hour of each transaction: format() with "%H" gives "00" to "23",
  # as.integer() turns it into a number (DateTime was parsed from the file's own date + time)
  data$Hour <- as.integer(format(data$DateTime, "%H"))

  # all 24 hours, so an hour with no transactions still gets a row of zeros
  oras <- 0:23
  n <- length(oras)

  # empty columns that we fill in the loop below, one slot per hour
  hour_label <- character(n)
  bilang <- integer(n)         # total transactions in that hour
  bilang_dep <- integer(n)     # number of deposits
  bilang_wd <- integer(n)      # number of withdrawals
  total_dep <- numeric(n)      # total amount deposited
  total_wd <- numeric(n)       # total amount withdrawn

  for (i in seq_len(n)) {
    rows <- data[data$Hour == oras[i], ]      # all transactions made during this hour
    dep <- rows[rows$TransactionType == "deposit", ]
    wd <- rows[rows$TransactionType == "withdrawal", ]

    # sprintf with %02d pads the hour to 2 digits, ex: 9 becomes "09"
    hour_label[i] <- sprintf("%02d:00 - %02d:59", oras[i], oras[i])
    bilang[i] <- nrow(rows)
    bilang_dep[i] <- nrow(dep)
    bilang_wd[i] <- nrow(wd)
    total_dep[i] <- sum(dep$Amount)           # sum of nothing is 0, so empty hours are aight
    total_wd[i] <- sum(wd$Amount)
  }

  report <- data.frame(Hour = hour_label,
                       TotalTransactions = bilang,
                       NumberOfDeposits = bilang_dep,
                       NumberOfWithdrawals = bilang_wd,
                       TotalDeposits = total_dep,
                       TotalWithdrawals = total_wd)

  # busiest hour first (if two hours have the same count, the earlier hour comes first)
  report #[order(-report$TotalTransactions, report$Hour), ]
}

# Runs all reports, prints previews, saves the CSV files and summary.json (REQ-0010)
generate_reports <- function(data) {
  cat("\nGenerating reports...\nOutputs saved to individual files...\n")

  r1 <- make_report1(data)
  show_and_save(r1, "report1_banking_privilege_tiers.csv", "Report 1: Banking Privilege Tiers")

  r2 <- make_report2(data)
  show_and_save(r2, "report2_interest_expense_projection.csv", "Report 2: Interest Expense Projection")

  r3 <- make_report3(data)
  show_and_save(r3, "report3_suspicious_client_behavior.csv", "Report 3: Suspicious Client Behavior")

  r4 <- make_report4(data)
  show_and_save(r4, "report4_account_activity_summary.csv", "Report 4: Account Activity Summary")

  # summary.json: key numbers across the reports
  latest <- data[!duplicated(data$AccountNumber, fromLast = TRUE), ]  # newest row per account
  total_accounts <- length(unique(data$AccountNumber))
  total_deposits <- sum(data$Amount[data$TransactionType == "deposit"])
  total_withdrawals <- sum(data$Amount[data$TransactionType == "withdrawal"])
  total_balance <- sum(latest$CurrentBalance)
  total_interest <- sum(r2$ExpectedInterest)       # from Report 2 (30-day projection)

  # numbers in JSON can't have commas, so they are plain with 2 decimals.
  json <- sprintf('{"total_accounts": %d, "total_deposits": %.2f, "total_withdrawals": %.2f, "total_balance": %.2f, "total_interest_expense": %.2f}',
                  total_accounts, total_deposits, total_withdrawals, total_balance, total_interest)
  writeLines(json, "summary.json")
  cat("\nSummary State (summary.json):\n", json, "\n", sep = "")
}

# The [7] menu: [1] loads + cleans the file, [2] makes the reports.
# Returns the loaded data so the main loop can remember it between choices.
reports_menu <- function(cleaned_data) {
  cat("\n[1] Load the file\n")
  cat("[2] Generate Reports\n")
  pili <- trimws(ask("Enter choice: "))

  if (pili == "1") {
    cleaned_data <- load_and_clean()      # NULL if loading failed
  } else if (pili == "2") {
    if (is.null(cleaned_data)) {
      cat("Please load the file first (choice 1)\n")
    } else {
      generate_reports(cleaned_data)
    }
  } else {
    cat("Please enter 1 or 2 only\n")
  }
  cleaned_data
}

#=============================================================
# MAIN LOOP
buhay_pa <- TRUE
cleaned_data <- NULL     # filled by [7] -> [1] Load the file
currency_list <- list(PHP = 1.00, USD = 62.00,
                      JPY = 0.40, GBP = 84.00,
                      EUR = 72.00, CNY = 9.00)
while (buhay_pa) {
  cat("\n\nSelect Transaction:\n")
  cat("[1] Register Account Name\n")
  cat("[2] Deposit Amount\n")
  cat("[3] Withdraw Amount\n")
  cat("[4] Currency Exchange\n")
  cat("[5] Record Exchange Rates\n")
  cat("[6] Show Interest Amount\n")
  cat("[7] Generate Reports\n")
  cat("[8] Exit\n")
  pili_ka <- trimws(ask("\nChoice: "))
  cat("\n***\nChoice =", pili_ka, "\n\n")
    switch(pili_ka,
         "1" = {
           repeat {
             cat("Register Account Name\n")
             regis_account()
             if (oo_o_hinde()) break
           }
         },
         "2" = {
           repeat {
             cat("Deposit Amount\n")
             deposit(currency_list)
             if (oo_o_hinde()) break
           }
         },
         "3" = {
           repeat {
             cat("Withdraw Amount\n")
             withdraw(currency_list)
             if (oo_o_hinde()) break
           }
         },
         "4" = {
           repeat {
             cat("Foreign Currency Exchange\n")
             curr_exc(currency_list)
             if (!papalit_o_hinde()) break
           }
         },
         "5" = {
           repeat {
             cat("Record Exchange Rate\n\n")
             currency_list <- record_exc_rate(currency_list)
             if (oo_o_hinde()) break
           }
         },
         "6" = {
           repeat {
             cat("Show Interest Amount\n")
             disp_interest_amnt()
             if (oo_o_hinde()) break
           }
         },
         "7" = {
           repeat {
             cat("Generate Reports\n")
             cleaned_data <- reports_menu(cleaned_data)
             if (oo_o_hinde()) break
           }
         },
         "8" = {
           cat("Terminating program....\n")
           buhay_pa <- FALSE
         },
         {
           cat("Wrong input! Please use integers 1-8 only!\n")
         })
}