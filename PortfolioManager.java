import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/*
* Class Name: IFT210
* Author: Alexander Bahler
* Date Created: 11/25/2023
* Purpose: Final Project
*/

public class PortfolioManager {
    private static Scanner scan = new Scanner(System.in);
    private static ArrayList<TransactionHistory> portfolioList = new ArrayList<TransactionHistory>();
    private static double cash = 0.0;

    public static void main(String[] args) {

        int choice = 0;
        do {
            System.out.println("\nAlexander Bahler's Brokerage Account\n");
            System.out.println("0 - Exit");
            System.out.println("1 - Deposit Cash");
            System.out.println("2 - Withdraw Cash");
            System.out.println("3 - Buy Stock");
            System.out.println("4 - Sell Stock");
            System.out.println("5 - Display Transaction History");
            System.out.println("6 - Display Portfolio");

            try {
                System.out.print("\nEnter option (0 to 6): ");
                choice = scan.nextInt();
            }
            catch (InputMismatchException ex) {
                scan.nextLine();
                System.out.println("\nError. Please use numbers only. Do not use symbols or text");
                System.out.print("Press Enter to continue...");
                scan.nextLine();
                System.out.println();
                choice = -1;
                continue;
            }

            switch (choice) {
                case 0:
                    System.out.println("Exiting...");
                    System.exit(0);
                    break;
                case 1:
                    depositCash(); 
                    break;
                case 2:
                    withdrawCash();
                    break;
                case 3:
                    buyStock();
                    break;
                case 4:
                    sellStock();
                    break;
                case 5:
                    displayTransactionHistory();
                    break;
                case 6:
                    displayPortfolio();
                    break;
                default:
                    System.out.println("Error. Please select from the menu.\n");
                    break;
            }
        }
        while (choice != 0);
    }   
    
    private static void depositCash() {
        System.out.print("Enter deposit date: ");
        String date = scan.next();
        System.out.print("Enter amount of deposit: ");
        double amount = scan.nextDouble();
        cash += amount;
        TransactionHistory lineItem = new TransactionHistory("CASH", date , "DEPOSIT", amount, 1.0);
        portfolioList.add(lineItem);
        System.out.println();
    }   

    private static void withdrawCash() {
        System.out.print("Enter withdraw date: ");
        String date = scan.next();
        System.out.print("Enter amount of withdraw: ");
        double amount = scan.nextDouble();
        if (amount <= cash) {
            cash -= amount;
            
            TransactionHistory lineItem = new TransactionHistory("CASH", date, "WITHDRAW", amount * -1, 1.0);
            portfolioList.add(lineItem);
            System.out.println("Withdrawal successful. Updated cash balance: $" + cash);
        } else {
            System.out.println("You do not have enough money. Withdrawal unsuccessful.");
        }
    }

    private static void buyStock() {
        System.out.print("Enter stock purchase date: ");
        String date = scan.next();
        System.out.print("Enter stock ticker: ");
        String ticker = scan.next();
        ticker = ticker.toUpperCase();
        System.out.print("Enter stock quantity: ");
        double quantity = scan.nextDouble();
        System.out.print("Enter stock cost (basis): ");
        double cost = scan.nextDouble();

        double totalCost = quantity * cost;

        if (totalCost > cash) {
            System.out.println("\nError. You do not have the cash for this purchase.\n");
        }
        else {
            portfolioList.add(new TransactionHistory(ticker, date, "BUY", quantity, cost));
            cash -= totalCost;
            portfolioList.add(new TransactionHistory("CASH", date, "WITHDRAW", totalCost * -1, 1.0));
        }

        System.out.println();
    }

    private static void sellStock() {
        System.out.print("Enter stock sell date: ");
        String date = scan.next();
        System.out.print("Enter stock ticker: ");
        String ticker = scan.next();
        ticker = ticker.toUpperCase();
        System.out.print("Enter stock quantity: ");
        double quantity = scan.nextDouble();
        System.out.print("Enter stock cost (basis): ");
        double cost = scan.nextDouble();
    
        double totalCost = quantity * cost;

        if (totalCost > 0) {
            cash += totalCost;

            TransactionHistory lineItem = new TransactionHistory(ticker, date, "SELL", quantity, cost);
            portfolioList.add(lineItem);
            portfolioList.add(new TransactionHistory("CASH", date, "DEPOSIT", totalCost, 1.0));
            System.out.println("Stock sale successful.");
        } 
        else {
            System.out.println("Invalid quantity. Stock sale unsuccessful.");
        }
    }
        
    private static void displayTransactionHistory() {
        System.out.println("\n\t\tAlexander Bahler's Brokerage Account");
        System.out.println("\t\t====================================\n");

        System.out.printf("%-16s%-10s%10s%15s     %s%n", "Date", "Ticker", "Quantity", "Cost Basis", "Trans Type");
        System.out.println("==================================================================");

        for (TransactionHistory record : portfolioList) {
            String costBasis = String.format("$%.2f", record.getCostBasis());
            System.out.printf("%-16s%-10s%10.0f%15s     %s%n", record.getTransDate(), record.getTicker(), record.getQty(),
                costBasis, record.getTransType());
        }

        System.out.println();
    }

    private static void displayPortfolio() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        System.out.println("\nPortfolio as of: " + dtf.format(LocalDateTime.now()));
        System.out.println("====================================");
        System.out.printf("%-8s%s%n", "Ticker", "Quantity");
        System.out.println("================");

        System.out.printf("%-8s%.2f%n", "CASH", cash);

        ArrayList<String> stocks = new ArrayList<String>();
        for (TransactionHistory record : portfolioList) {
            if (record.getTicker().equals("CASH"))
                continue;
            
            if (stocks.contains(record.getTicker()) == false)
                stocks.add(record.getTicker());
        }

        for (String stock : stocks) {
            double total = 0.0;

            for (TransactionHistory record : portfolioList) {
                if (record.getTicker().equals(stock)) {
                    if (record.getTransType().equals("BUY"))
                        total += record.getQty();
                    else
                        total -= record.getQty();
                }
            }
    
            if (total > 0.0)
                System.out.printf("%-8s%.0f%n", stock, total);
        } 
    System.out.println();
    }
}

