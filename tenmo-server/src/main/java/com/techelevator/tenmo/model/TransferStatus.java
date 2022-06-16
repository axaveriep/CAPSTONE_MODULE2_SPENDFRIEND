package com.techelevator.tenmo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="transfer_status")
public class TransferStatus {

    @Id
    @Column(name = "transfer_status_id")
    @JoinColumn(name="transfer_status_id")
    @JsonProperty("transferStatusId")
    private long transferStatusId;

    @Column(name="transfer_status_desc")
    @JsonProperty("transferStatusDescription")
    private String transferStatusDescription;

    @JsonIgnore
    @OneToMany(mappedBy = "transferStatus")
    private List<Transfer> transfers;

    public TransferStatus(long transferStatusId, String transferStatusDescription) {
        this.transferStatusId = transferStatusId;
        this.transferStatusDescription = transferStatusDescription;
    }

    public TransferStatus(long transferStatusId, String transferStatusDescription, List<Transfer> transfers) {
        this.transferStatusId = transferStatusId;
        this.transferStatusDescription = transferStatusDescription;
        this.transfers = transfers;
    }
}
