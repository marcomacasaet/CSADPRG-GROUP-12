#********************
#Last names: Acosta, Bunggo, Gregorio, Macasaet
#Language: R
#Paradigm(s): Object-Oriented, Functional, Vector oriented
#********************

#  [1] Register Account Name - REQ-0009
#  [2] Deposit Amount        - REQ-0004, 0008
#  [3] Withdraw Amount       - REQ-0005, s0008
#  [4] Currency Exchange     - REQ-0002, 0003
#  [5] Record Exchange Rates - REQ-0001
#  [6] Show Interest Amount -  REQ-006(UNFIN), 0007(UNFIN)

oo_o_hinde <- function() {
  repeat {
    tanong <- toupper(trimws(readline(prompt = "\nBack to the Main Menu (Y/N): ")))
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
    tanong <- toupper(trimws(readline(prompt = "\nConvert another currency (Y/N)? ")))
    if (tanong == "Y") {
      return(TRUE)
    } else if (tanong == "N") {
      return(FALSE)
    } else {
      cat("Please enter Y or N\n")
    }
  }
}

regis_account <- function(account) {
  pangalan <- trimws(readline(prompt = "Account Name: "))

  if (pangalan == "") {
    cat("Name cannot be empty\n")
    return(account)
  }
  account$pangalan <- pangalan
  cat("\n***\nAccount Name = ", account$pangalan, "\n")
  account
}

deposit <- function(account, currency_list) {
  if (is.null(account$pangalan)) {
    cat("Please register an account first\n")
    return(account)
  }

  pangalan_local <- trimws(readline(prompt = "Account Name: "))
  if (pangalan_local != account$pangalan) {
    cat("Account name does not match\n")
    return(account)
  }

  cat("Base Currency: PHP\n")
  cat("Current Balance:", sprintf("%.2f", account$balance), "\n")
  cat("Currency:", account$currency, "\n\n")

  cat("Currency Option:\n")
  cat("[1] Philippine Peso (PHP)\n")
  cat("[2] United States Dollar (USD)\n")
  cat("[3] Japanese Yen (JPY)\n")
  cat("[4] British Pound Sterling (GBP)\n")
  cat("[5] Euro (EUR)\n")
  cat("[6] Chinese Yuan Renminni (CNY)\n")
  pili_currency <- as.integer(readline(prompt = "Select Currency: "))
  if (is.na(pili_currency) || pili_currency < 1 || pili_currency > 6) {
    cat("Invalid currency\n")
    return(account)
  }

  deposit_amount <- as.numeric(readline(prompt = "Deposit Amount: "))
  if (is.na(deposit_amount) || deposit_amount <= 0) {
    cat("Invalid amount\n")
    return(account)
  }

  php_amount <- deposit_amount * currency_list[[pili_currency]]
  account$balance <- account$balance + php_amount

  cat("\n***\nAccount Name =", account$pangalan, "\n")
  cat(sprintf("Deposit Amount: %.2f %s (%.2f PHP)\n", deposit_amount, names(currency_list)[pili_currency], php_amount))
  cat("Currency:", account$currency, "\n")
  cat(sprintf("Updated Balance: %.2f\n", account$balance))
  account
}

withdraw <- function(account, currency_list) {
  if (is.null(account$pangalan)) {
    cat("Please register an account first\n")
    return(account)
  }

  pangalan_local <- trimws(readline(prompt = "Account Name: "))
  if (pangalan_local != account$pangalan) {
    cat("Account name does not match\n")
    return(account)
  }

  cat("Base Currency: PHP\n")
  cat("Current Balance:", sprintf("%.2f", account$balance), "\n")
  cat("Currency:", account$currency, "\n\n")

  cat("Currency Option:\n")
  cat("[1] Philippine Peso (PHP)\n")
  cat("[2] United States Dollar (USD)\n")
  cat("[3] Japanese Yen (JPY)\n")
  cat("[4] British Pound Sterling (GBP)\n")
  cat("[5] Euro (EUR)\n")
  cat("[6] Chinese Yuan Renminni (CNY)\n")
  pili_currency <- as.integer(readline(prompt = "Select Currency: "))
  if (is.na(pili_currency) || pili_currency < 1 || pili_currency > 6) {
    cat("Invalid currency\n")
    return(account)
  }

  withdraw_amount <- as.numeric(readline(prompt = "Withdraw Amount: "))
  if (is.na(withdraw_amount) || withdraw_amount <= 0) {
    cat("Invalid amount\n")
    return(account)
  }

  php_amount <- withdraw_amount * currency_list[[pili_currency]]
  if (php_amount > account$balance) {
    cat("Insufficient balance\n")
    return(account)
  }

  account$balance <- account$balance - php_amount

  cat("\n***\nAccount Name =", account$pangalan, "\n")
  cat(sprintf("Withdraw Amount: %.2f %s (%.2f PHP)\n", withdraw_amount, names(currency_list)[pili_currency], php_amount))
  cat("Currency:", account$currency, "\n")
  cat(sprintf("Updated Balance: %.2f\n", account$balance))
  account
}

record_exc_rate <- function(currency_list) {
  cat("[1] Philippine Peso (PHP)\n")
  cat("[2] United States Dollar (USD)\n")
  cat("[3] Japanese Yen (JPY)\n")
  cat("[4] British Pound Sterling (GBP)\n")
  cat("[5] Euro (EUR)\n")
  cat("[6] Chinese Yuan Renminni (CNY)\n")
  pili_currency <- as.integer(readline(prompt = "Select Foreign Currency: "))
  if (is.na(pili_currency) || pili_currency < 1 || pili_currency > 6) {
    cat("Invalid currency\n")
    return(currency_list)
  }
  if (pili_currency == 1) {
    cat("PHP is the base currency thus its rate cannot be changed\n")
    return(currency_list)
  }

  bagong_rate <- as.numeric(readline(prompt = "Exchange Rate: "))
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

  source_currency <- as.integer(readline(prompt = "Source Currency: "))
  if (is.na(source_currency) || source_currency < 1 || source_currency > 6) {
    cat("Invalid currency\n")
    return()
  }

  source_amount <- as.numeric(readline(prompt = "Source Amount (PHP): "))
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
  exchange_currency <- as.integer(readline(prompt = "\nExchange Currency: "))
  if (is.na(exchange_currency) || exchange_currency < 1 || exchange_currency > 6) {
    cat("Invalid currency\n")
    return()
  }
  exchange_amount <- source_amount * currency_list[[source_currency]] / currency_list[[exchange_currency]]
  cat(sprintf("Exchange Amount: %.2f\n", exchange_amount))
}

disp_interest_amnt <- function(account) {
  if (is.null(account$pangalan)) {
    cat("Please register an account first\n")
    return(account)
  }

  pangalan_local <- trimws(readline(prompt = "Account Name: "))
  if (pangalan_local != account$pangalan) {
    cat("Account name does not match\n")
    return(account)
  }

  cat("Current Balance: ", account$balance, "\nCurrency: ", account$currency)
  cat("\nCurrent Interest Rate: 5%\n")

  interest_araw <- readline(prompt = "\n\nTotal Number of Days: ")
  hiwa <- strsplit(trimws(interest_araw), " ")[[1]]
  interest_araw <- as.integer(hiwa[1])

  if (is.na(interest_araw) || interest_araw <= 0) {
    cat("Invalid number of days\n")
    return(account)
  }
  int_rate <- 0.05
  bal <- account$balance

  cat("\nDay | Interest | Balance |\n")
  for (araw in 1:interest_araw) {
    interest <- round(bal * int_rate / 365, 2)
    bal <- bal + interest
    cat(sprintf("%-3d | %-8.2f | %-7.2f |\n", araw, interest, bal))
  }
  account
}

#=============================================================
# MAIN LOOP
buhay_pa <- TRUE
account <- list(
  pangalan = NULL,
  balance = 0.00,
  currency = "PHP"
)
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
  cat("[7] Exit\n")
  pili_ka <- trimws(readline(prompt = "\nChoice: "))
  cat("\n***\nChoice =", pili_ka, "\n\n")
    switch(pili_ka,
         "1" = {
           repeat {
             cat("Register Account Name\n")
             account <- regis_account(account)
             if (oo_o_hinde()) break
           }
         },
         "2" = {
           repeat {
             cat("Deposit Amount\n")
             account <- deposit(account, currency_list)
             if (oo_o_hinde()) break
           }
         },
         "3" = {
           repeat {
             cat("Withdraw Amount\n")
             account <- withdraw(account, currency_list)
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
             account <- disp_interest_amnt(account)
             if (oo_o_hinde()) break
           }
         },
         "7" = {
           cat("Terminating program....\n")
           buhay_pa <- FALSE
         },
         {
           cat("Wrong input! Please use integers 1-7 only!\n")
         })
}
