package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TransferService;

import java.math.BigDecimal;
import java.util.List;


public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService(API_BASE_URL);
    private final TransferService transferService = new TransferService(API_BASE_URL);

    private final User user = new User();

    private final long STATUS_PENDING = 1;
    private final long STATUS_APPROVED = 2;
    private final long STATUS_REJECTED = 3;
    private final long STATUS_INVALID_TRANSFER = 4;
    private final long STATUS_INVALID_AMOUNT = 5;
    private final long STATUS_USER_NOT_FOUND = 6;
    private final long STATUS_NSF = 7;
    private final long STATUS_UNAUTHORIZED_APPROVAL = 8;

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {

            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        try {
            transferService.setAuthToken(currentUser.getToken());
            accountService.setAuthToken(currentUser.getToken());
        } catch (Exception e) {
            consoleService.printErrorMessage();
        }
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
//            System.out.println(currentUser.getUser().getUsername());
//            System.out.println(currentUser.getUser().getId());
//            System.out.println(currentUser.getUser().getAccount().getId());
//            System.out.println(currentUser.getUser().getAccount().getUserId());
//            System.out.println(currentUser.getUser().getAccount().getBalance());
//            System.out.println("Testing.....");
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
		// TODO Auto-generated method stub
        System.out.println("Your current balance is:  $" + accountService.getBalance(currentUser.getUser().getAccount().getId()));
	}

	private void viewTransferHistory() {

        System.out.println("-------------------------------");
        System.out.println("Transfer History");
        System.out.println("ID     From/To          Amount");
        System.out.println("-------------------------------");

    List<Transfer> transferHistory = transferService.getAllTransfersByAccountId(currentUser.getUser().getAccount().getId());
        for (Transfer t : transferHistory) {
            User toUser = accountService.getUserByUserId(t.getToAccount().getUserId());
            User fromUser = accountService.getUserByUserId(t.getFromAccount().getUserId());
            System.out.println("Transfer ID: " + t.getTransferId() + "   From User: " + fromUser.getUsername() + "     To User: " + toUser.getUsername() + "     $" + t.getAmount());
        }

        consoleService.printGetTransferDetailsOption();
        int selection = consoleService.promptForMenuSelection("Please choose an option: ");
        if (selection == 1) {
            long transferID = consoleService.promptForInt("Enter a transfer ID to view transfer details: ");
            showTransferDetails(transferID);
        }

	}

	private void viewPendingRequests() {
        List<Transfer> pendingRequests = transferService.getAllSentTransfersByAccountIdAndStatusId(currentUser.getUser().getAccount().getId(), STATUS_PENDING);

        System.out.println("-------------------------------");
        System.out.println("      Pending Requests         ");
        System.out.println("ID          To           Amount");
        System.out.println("-------------------------------");

        for (Transfer t : pendingRequests) {
            User toUser = accountService.getUserByUserId(t.getToAccount().getUserId());
            System.out.println(t.getTransferId() + "     " + toUser.getUsername() + "     $" + t.getAmount());
        }

        consoleService.printApproveOrRejectOptions();
        int selection = consoleService.promptForMenuSelection("Please choose an option:");
        boolean validOption = false;
        while (!validOption) {
            if (selection == 1) {
                // APPROVE TRANSFER
                long transferId = consoleService.promptForInt("Please enter the ID of the transfer you would like to approve: ");
                Transfer approvedTransfer = transferService.approveTransfer(transferId);
                if (approvedTransfer.getTransferStatusId() == STATUS_NSF) {
                    System.out.println("Non sufficient funds.");
                    consoleService.printMainMenu();
                } else if (approvedTransfer.getTransferStatusId() == STATUS_UNAUTHORIZED_APPROVAL) {
                    System.out.println("You are not authorized to approve or decline this transaction.");
                }else if (approvedTransfer.getTransferStatusId() == STATUS_APPROVED) {
                    System.out.println("Approved transfer ID: " + approvedTransfer.getTransferId() +
                            "\nSent: $" + approvedTransfer.getAmount() + " to: " + accountService.getUserByUserId(approvedTransfer.getToAccount().getUserId()).getUsername() +
                            "\nYour updated balance: $" + accountService.getAccountByUserId(currentUser.getUser().getId()).getBalance());
                }

                validOption = true;
            } else if (selection == 2) {
                // REJECT TRANSFER
                long transferId = consoleService.promptForInt("Please enter the ID of the transfer you would like to reject: ");
                Transfer rejectedTransfer = transferService.rejectTransfer(transferId);
                if (rejectedTransfer.getTransferStatusId() == STATUS_UNAUTHORIZED_APPROVAL) {
                    System.out.println("You are not authorized to approve or decline this transaction.");
                }else if (rejectedTransfer.getTransferStatusId() == STATUS_REJECTED) {
                    System.out.println("Rejected transfer ID: " + rejectedTransfer.getTransferId() + " for: $" + rejectedTransfer.getAmount());
                }
                validOption = true;
            } else if (selection == 0) {
                validOption = true;
            } else {
                System.out.println("Invalid selection");
                break;
            }
        }

        }


	private void sendBucks() {


       List<User> users = accountService.getAllUsers();

        for (User u : users) {
            System.out.println(u.getId() + " " + u.getUsername());
        }

        TransferCredentials td = consoleService.promptForTransferDetails(currentUser.getUser().getId());

        Transfer transfer = transferService.sendTransfer(td);

        if (transfer.getTransferStatusId() == STATUS_INVALID_TRANSFER) {
            System.out.println("Transfer invalid.");
            consoleService.printMainMenu();
        } else if (transfer.getTransferStatusId() == STATUS_USER_NOT_FOUND) {
            System.out.println("User not found.");
            consoleService.printMainMenu();
        } else if (transfer.getTransferStatusId() == STATUS_INVALID_AMOUNT) {
            System.out.println("Invalid transfer amount.");
            consoleService.printMainMenu();
        } else if (transfer.getTransferStatusId() == STATUS_NSF) {
            System.out.println("Non sufficient funds.");
            consoleService.printMainMenu();
        } else if (transfer.getTransferStatusId() == STATUS_APPROVED) {
            System.out.println("Transferred $" + transfer.getAmount());
        }
	}

	private void requestBucks() {
        List<User> users = accountService.getAllUsers();

        for (User u : users) {
            System.out.println(u.getId() + " " + u.getUsername());
        }

        TransferCredentials td = consoleService.promptForRequestTransferDetails(currentUser.getUser().getId());

        Transfer transfer = transferService.requestTransfer(td);

        if (transfer.getTransferStatusId() == STATUS_INVALID_TRANSFER) {
            System.out.println("Transfer invalid.");
            consoleService.printMainMenu();
        } else if (transfer.getTransferStatusId() == STATUS_USER_NOT_FOUND) {
            System.out.println("User not found.");
            consoleService.printMainMenu();
        } else if (transfer.getTransferStatusId() == STATUS_INVALID_AMOUNT) {
            System.out.println("Invalid transfer amount.");
            consoleService.printMainMenu();
        } else if (transfer.getTransferStatusId() == STATUS_NSF) {
            System.out.println("Non sufficient funds.");
            consoleService.printMainMenu();
        } else if (transfer.getTransferStatusId() == STATUS_PENDING) {
            System.out.println("Requested: $" + transfer.getAmount() + " From: " + accountService.getUserByUserId(td.getFromId()).getUsername());
        }

	}

    private void showTransferDetails (long transferId) {
        Transfer transfer = transferService.getTransferByTransferId(transferId);
        long id = transfer.getTransferId();
        String fromUsername = accountService.getUserByUserId(transfer.getFromAccount().getUserId()).getUsername();
        String toUsername = accountService.getUserByUserId(transfer.getToAccount().getUserId()).getUsername();
        String type = transfer.getTransferType().getTransferTypeDescription();
        String status = transfer.getTransferStatus().getTransferStatusDescription();
        BigDecimal amount = transfer.getAmount();

        consoleService.printTransferDetails(id, fromUsername, toUsername, type, status, amount);
    }

}
