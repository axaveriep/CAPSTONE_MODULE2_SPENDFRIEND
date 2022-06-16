package com.techelevator.tenmo.model;

public class TransferStatus {

    // Transfer transfer;

        private long transferStatusId;

        private String transferStatusDescription;

    public TransferStatus() {}

    public TransferStatus(long transferStatusId, String transferStatusDescription) {
        this.transferStatusId = transferStatusId;
        this.transferStatusDescription = transferStatusDescription;
    }

//    public TransferStatus(long transferStatusId) {
//        this.transferStatusId = transferStatusId;
//    }

    public String getTransferStatusDescription() {
        return transferStatusDescription;
    }

    public void setTransferStatusDescription(String transferStatusDescription) {
        this.transferStatusDescription = transferStatusDescription;
    }

    public long getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(long transferStatusId) {
        this.transferStatusId = transferStatusId;
    }


}
