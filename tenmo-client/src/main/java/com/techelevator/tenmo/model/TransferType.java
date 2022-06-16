package com.techelevator.tenmo.model;

public class TransferType {

    private long transferTypeId;
    private String transferTypeDescription;

    //<editor-fold desc="Getters & Setters">
    public long getTransferTypeId() {
        return transferTypeId;
    }

    public void setTransferTypeId(long transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public String getTransferTypeDescription() {
        return transferTypeDescription;
    }

    public void setTransferTypeDescription(String transferTypeDescription) {
        this.transferTypeDescription = transferTypeDescription;
    }
    //</editor-fold>

    public TransferType() {}

    public TransferType(long transferTypeId, String transferType) {
        this.transferTypeId = transferTypeId;
        this.transferTypeDescription = transferType;
    }
}
