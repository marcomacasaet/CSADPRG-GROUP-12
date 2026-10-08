#********************
#Last names: Acosta, Bunggo, Gregorio, Macasaet
#Language: R
#Paradigm(s): Object-Oriented, Functional, Vector oriented
#********************
regis_account <- function() {
  pangalan <- readline(prompt = "Account Name: ")
  cat("\n***\nAccount Name = ", pangalan, "\n")
}

deposit <- function(curr_bal, curr) {
  pangalan <- readline(prompt = "Account Name: ")
  cat("Current Balance: ", curr_bal, "\nCurrency: ", curr)
  deposit_amount <- readline(prompt = "\n\nDeposit Amount: ")
  deposit_amount <- as.numeric(deposit_amount)
  cat("\n***\nAccount Name = ", pangalan, "\nDeposit Amount: ", deposit_amount)
  cat("\n")
  list(pangalan = pangalan, deposit_amount = deposit_amount)
}

withdraw <- function(curr_bal, curr) {
  pangalan <- readline(prompt = "Account Name: ")
  cat("Current Balance: ", curr_bal, "\nCurrency: ", curr)
  withdraw_amount <- readline(prompt = "\n\nWithdraw Amount: ")
  withdraw_amount <- as.numeric(withdraw_amount)
  cat("\n***\nAccount Name = ", pangalan, "\nDeposit Amount: ", withdraw_amount)
  cat("\n")
  list(pangalan = pangalan, deposit_amount = withdraw_amount)
}

record_exc_rate <- function(currency_list) {
  cat("[1] Philippine Peso (PHP)\n")
  cat("[2] United States Dollar (USD)\n")
  cat("[3] Japanese Yen (JPY)\n")
  cat("[4] British Pound Sterling (GBP)\n")
  cat("[5] Euro (EUR)\n")
  cat("[6] Chinese Yuan Renminni (CNY)\n")
  pili_currency <- as.integer(readline(prompt = "Select Foreign Currency: "))
  bagong_rate <- as.numeric(readline(prompt = "Exchange Rate: "))

  if (!is.na(pili_currency) && pili_currency >= 1 && pili_currency <= 6){
    currency_list[[pili_currency]] <- bagong_rate
    cat("\n***\nSelect Foreign Currency: ", pili_currency)
    cat("\nExchange Rate: ", bagong_rate, "\n")
  }else{
    cat("Invalid input")
  }
  currency_list
}

curr_exc <- function(currency_list) {
  source_amount <- as.numeric(readline(prompt = "Source Amount (PHP): "))
  cat("\n\nExchanged Currency\n")
  cat("[1] Philippine Peso (PHP) = ", source_amount * currency_list[[1]], "\n")
  cat("[2] United States Dollar (USD) = ", source_amount * currency_list[[2]], "\n")
  cat("[3] Japanese Yen (JPY) = ", source_amount * currency_list[[3]], "\n")
  cat("[4] British Pound Sterling (GBP) = ", source_amount * currency_list[[4]], "\n")
  cat("[5] Euro (EUR) = ", source_amount * currency_list[[5]], "\n")
  cat("[6] Chinese Yuan Renminni (CNY) = ", source_amount * currency_list[[6]], "\n")
  cat("\n***\nSource Currency = Philippine Peso (PHP)\n")
  cat("Source Amount (PHP) = ", source_amount, "\n")
}

disp_interest_amnt <- function() {
  cat("Currenty under construction....\n")
}

buhay_pa <- TRUE
curr_bal <- 1000.00
curr <- "PHP"
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
  pili_ka <- readline(prompt = "\nChoice: ")
  cat("\n***\nChoice =", pili_ka, "\n\n")
  switch(pili_ka,
         "1" = {
           cat("Register Account Name\n")
           pangalan <- regis_account()
         },
         "2" = {
           cat("Deposit Amount\n")
           listahan <- deposit(curr_bal, curr)
           #To access: listahan$pangalan / listahan$deposit_amount
         },
         "3" = {
           cat("Withdraw Amount\n")
           listahan <- withdraw(curr_bal, curr)
           #To access: listahan$pangalan / listahan$withdraw_amount
         },
         "4" = {
           cat("Foreign Currency Exchange\n")
           curr_exc(currency_list)
         },
         "5" = {
           cat("Record Exchange Rate\n\n")
           currency_list <- record_exc_rate(currency_list)
         },
         "6" = {
           cat("Show Interest Amount\n")
           disp_interest_amnt()
         },
         "7" = {
           cat("Terminating program....\n")
           buhay_pa <- FALSE
         },
         {
           cat("Wrong input! Please use integers 1-6 only!\n")
           buhay_pa <- FALSE
         })
}
