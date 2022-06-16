package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.TransferCredentials;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.util.BasicLogger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Scanner;

@Service
public class ConsoleService {

    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    /* first attempt */
    public TransferCredentials promptForTransferDetails(long fromId) {
        long toID = promptForInt("Enter ID of user you are sending to: ");
        if (toID == fromId) {
            toID = promptForInt("You cannot send bucks to yourself! " +
                    "Please Enter ID of user you are sending to: ");
        }
        BigDecimal amount = promptForBigDecimal("Please enter the amount to send: ");
        return new TransferCredentials(fromId, toID, amount);
    }
    public TransferCredentials promptForRequestTransferDetails(long toId) {
        long fromId = promptForInt("Enter ID of user you would like to request money from: ");
        if (toId == fromId) {
            fromId = promptForInt("You cannot send bucks to yourself! " +
                    "Please Enter ID of user you would like to request money from: ");
        }
        BigDecimal amount = promptForBigDecimal("Please enter the amount to request: ");
        return new TransferCredentials(fromId, toId, amount);
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid ID number.");
                BasicLogger.log(e.getMessage() + " Invalid ID Entered");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid amount.");
                BasicLogger.log(e.getMessage() + " Invalid Amount Entered");
            }
        }
    }

    public void pause() {
        System.out.println("\n-------------------------------");
        System.out.println("...Press Enter to continue...");
        System.out.println("-------------------------------");

        scanner.nextLine();
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }


    public void printApproveOrRejectOptions() {
        System.out.println("1: Approve");
        System.out.println("2: Reject");
        System.out.println("0: Return to Main Menu\n");
    }

    public void printGetTransferDetailsOption () {
        System.out.println("1: See Details of a Transfer");
        System.out.println("0: Return to Main Menu\n");
    }

    public void printTransferDetails(long id, String from, String to, String type, String status, BigDecimal amount) {
        System.out.println("-------------------------------");
        System.out.println("Transfer Details");
        System.out.println("-------------------------------");
        System.out.println("Id: " + id);
        System.out.println("From: " + from);
        System.out.println("To: " + to);
        System.out.println("Type: " + type);
        System.out.println("Status: " + status);
        System.out.println("Amount: $" + amount);
    }


}
