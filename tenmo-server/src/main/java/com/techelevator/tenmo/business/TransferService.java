package com.techelevator.tenmo.business;

import com.techelevator.tenmo.dao.AccountRepository;
import com.techelevator.tenmo.dao.TransferRepository;
import com.techelevator.tenmo.exceptions.InvalidTransferAmountException;
import com.techelevator.tenmo.exceptions.NonSufficentFundsException;
import com.techelevator.tenmo.exceptions.TransferApprovalException;
import com.techelevator.tenmo.exceptions.TransferSenderReceiverException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Service
public class TransferService {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountService accountService;
    @Autowired
    UserService userService;
    @Autowired
    TransferRepository transferRepository;

    private final long STATUS_PENDING = 1;
    private final long STATUS_APPROVED = 2;
    private final long STATUS_REJECTED = 3;
    private final long STATUS_INVALID_TRANSFER = 4;
    private final long STATUS_INVALID_AMOUNT = 5;
    private final long STATUS_USER_NOT_FOUND = 6;
    private final long STATUS_NSF = 7;
    private final long STATUS_UNAUTHORIZED_APPROVAL = 8;

    private final long TYPE_REQUEST = 1;
    private final long TYPE_SENDING = 2;



    public Transfer createTransfer(TransferDTO transferDetails) {
        Transfer newTransfer = new Transfer();
        try {
             if(!accountRepository.findAll().contains(accountRepository.findByUserId(transferDetails.getFromID()))) {
                newTransfer.setTransferStatusId(STATUS_USER_NOT_FOUND);
                throw new UsernameNotFoundException("This user was not found.");
            } else if (transferDetails.getAmount().compareTo(BigDecimal.valueOf(0)) <= 0) {
                newTransfer.setTransferStatusId(STATUS_INVALID_AMOUNT);
                throw new InvalidTransferAmountException("You must select a transfer amount larger then $0.00.");
            }else if(!accountRepository.findAll().contains(accountRepository.findByUserId(transferDetails.getToID()))) {
                newTransfer.setTransferStatusId(STATUS_USER_NOT_FOUND);
                throw new UsernameNotFoundException("This user was not found.");
            } else if (transferDetails.getFromID() == transferDetails.getToID()) {
                newTransfer.setTransferStatusId(STATUS_INVALID_TRANSFER);
                throw new TransferSenderReceiverException();
            } else {
                 newTransfer.setAccountIdFrom(accountRepository.findByUserId(transferDetails.getFromID()).getId());
                 newTransfer.setAccountIdTo(accountRepository.findByUserId(transferDetails.getToID()).getId());
                 newTransfer.setAmount(transferDetails.getAmount());
             }
        } catch (InvalidTransferAmountException | TransferSenderReceiverException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (Exception x) {
            System.out.println(x.getMessage());
            x.printStackTrace();
        }
        return newTransfer;
    }

    public Transfer completeTransfer (Transfer transfer) throws NonSufficentFundsException {

            Account fromAccount = accountService.findByAccountId(transfer.getAccountIdFrom());
            Account toAccount = accountService.findByAccountId(transfer.getAccountIdTo());

            BigDecimal newFromBalance = accountService.subtractFromBalance(fromAccount.getId(),
                    transfer.getAmount());
            BigDecimal newToBalance = accountService.addToBalance(toAccount.getId(),
                    transfer.getAmount());

          fromAccount = accountService.updateBalance(fromAccount, newFromBalance);
           toAccount = accountService.updateBalance(toAccount, newToBalance);

            transfer.setTransferStatusId(STATUS_APPROVED);

            accountRepository.saveAndFlush(fromAccount);
            accountRepository.saveAndFlush(toAccount);

        return transfer;
    }

    public Transfer sendTransfer (Transfer transfer) {
        if (transfer.getTransferStatusId() < 4 ) {
            Transfer sentTransfer = transfer;
            try {
                sentTransfer = completeTransfer(transfer);
                sentTransfer.setTransferTypeId(TYPE_SENDING);
                transferRepository.saveAndFlush(sentTransfer);
            } catch (NonSufficentFundsException e) {
                sentTransfer.setTransferStatusId(STATUS_NSF);
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            return sentTransfer;
        } else {
            return transfer;
        }
    }

    public Transfer approveTransfer (Transfer requestedTransfer, String principalName) {
        String sendingUsername = requestedTransfer.getFromAccount().getUser().getUsername();

        if (requestedTransfer.getTransferStatusId() < 4) {
            Transfer approvedTransfer = requestedTransfer;
            try {
                if (!sendingUsername.equals(principalName) || requestedTransfer.getTransferStatusId() != STATUS_PENDING) {
                    throw new TransferApprovalException();
                } else {
                approvedTransfer = completeTransfer(requestedTransfer);
                transferRepository.saveAndFlush(approvedTransfer);
                }
            } catch (NonSufficentFundsException e) {
                approvedTransfer.setTransferStatusId(STATUS_NSF);
                System.out.println(e.getMessage());
                e.printStackTrace();
            } catch (TransferApprovalException ex) {
                approvedTransfer.setTransferStatusId(STATUS_UNAUTHORIZED_APPROVAL);
                System.out.println(ex.getMessage());
                System.out.println(ex.getCause() + " ***E  GET CAUSE ******");
                ex.printStackTrace();
            }
            return approvedTransfer;
        } else {
            return requestedTransfer;
        }
    }

    public Transfer rejectTransfer (Transfer rejectedTransfer, String principalName) {
        String sendingUsername = userService.findByUserId(rejectedTransfer.getAccountIdFrom()).getUsername();
        try {
            if (!sendingUsername.equals(principalName) || rejectedTransfer.getTransferStatusId() != STATUS_PENDING) {
                throw new TransferApprovalException();
            } else {
            rejectedTransfer.setTransferStatusId(STATUS_REJECTED);
            transferRepository.saveAndFlush(rejectedTransfer);
            }
        } catch (TransferApprovalException e) {
            rejectedTransfer.setTransferStatusId(STATUS_UNAUTHORIZED_APPROVAL);
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return rejectedTransfer;
    }

    public Transfer requestTransfer (Transfer transfer) {
        if (transfer.getTransferStatusId() < 4 ) {
            transfer.setTransferTypeId(TYPE_REQUEST);
            transfer.setTransferStatusId(STATUS_PENDING);
            transferRepository.saveAndFlush(transfer);
        }
        return transfer;
    }

    public List<Transfer> findAllByAccountId (long id) {
        return transferRepository.findAllByAccountId(id);
    }

    public Transfer findById (long transferId) {
        return transferRepository.findById(transferId);
    }

//    public List<Transfer> findAllByStatusAndUser (long transferStatusId, long userId) {
//        long accountId = accountService.findByUserId(userId).getId();
//        return transferRepository.findAllByStatusAndUser(transferStatusId, accountId);
//    }

    public List<Transfer> findAllByAccountFrom(long accountFrom) {
        return transferRepository.findAllByAccountIdFrom(accountFrom);
    }

    public List<Transfer> findAllByAccountTo(long accountTo) {
        return transferRepository.findAllByAccountIdTo(accountTo);
    }

    public List<Transfer> findAllByAccountFromAndTransferStatusId(long accountId, long transferStatusId) {
        return transferRepository.findAllByAccountFromAndTransferStatusId(accountId, transferStatusId);
    }

//    public List<Transfer> findAllByAccountToAndTransferStatusId(long accountId, long transferStatusId) {
//        return transferRepository.findAllByAccountToAndTransferStatusId(accountId, transferStatusId);
//    }

}
