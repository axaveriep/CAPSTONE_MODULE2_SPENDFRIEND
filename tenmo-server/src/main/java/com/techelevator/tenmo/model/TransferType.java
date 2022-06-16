package com.techelevator.tenmo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="transfer_type")
public class TransferType {

    @Id
    @Column(name = "transfer_type_id")
    @JoinColumn(name="transfer_type_id")
    private long transferTypeId;

    @Column(name="transfer_type_desc")
    private String transferTypeDescription;

    @JsonIgnore
    @OneToMany(mappedBy = "transferType")
    private List<Transfer> transfers;

    public TransferType(long transferTypeId, String transferTypeDescription, List<Transfer> transfers) {
        this.transferTypeId = transferTypeId;
        this.transferTypeDescription = transferTypeDescription;
        this.transfers = transfers;
    }

        public TransferType(long transferTypeId, String transferTypeDescription) {
        this.transferTypeId = transferTypeId;
        this.transferTypeDescription = transferTypeDescription;
    }
}
