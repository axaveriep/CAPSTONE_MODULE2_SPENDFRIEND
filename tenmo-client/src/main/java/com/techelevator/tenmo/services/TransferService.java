package com.techelevator.tenmo.services;

import com.techelevator.tenmo.App;
import com.techelevator.tenmo.model.*;
import com.techelevator.util.BasicLogger;
import okhttp3.Response;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class TransferService {


    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;

    private String authToken;

    public void setAuthToken(String authToken){
        this.authToken = authToken;
    }

    public TransferService(String baseUrl) {
        this.baseUrl = baseUrl;
    }



    //TODO Send Transfer

    public Transfer sendTransfer (TransferCredentials transferCredentials) {
        HttpEntity<TransferCredentials> entity = createTransferCredentialsEntity(transferCredentials);
        Transfer newTransfer = new Transfer();
        System.out.println(transferCredentials.getAmount() + " test transfer amount --  before try");
        try {
            ResponseEntity<Transfer> response = restTemplate.exchange(baseUrl + "transfer/send", HttpMethod.POST, entity, Transfer.class);
            newTransfer = response.getBody();

        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getMessage());
        }
        System.out.println(newTransfer.getTransferStatusId() + " -- status ID | Transfer service -- send transfer");
        return newTransfer;
    }

    public Transfer requestTransfer (TransferCredentials transferCredentials) {
        HttpEntity<TransferCredentials> entity = createTransferCredentialsEntity(transferCredentials);
        Transfer requestedTransfer = new Transfer();
        try {
            ResponseEntity<Transfer> response = restTemplate.exchange(baseUrl + "transfer/request", HttpMethod.POST, entity, Transfer.class);
            requestedTransfer = response.getBody();
        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getMessage());
        }
        return requestedTransfer;
    }

    public Transfer approveTransfer (long transferId) {
        //HttpEntity<Transfer> entity = createTransferEntity(pendingTransfer);
        Transfer approvedTransfer = new Transfer();
        try {
            ResponseEntity<Transfer> response = restTemplate.exchange(baseUrl + "transfer/approve/" + transferId, HttpMethod.PUT, createEntity(), Transfer.class);
            approvedTransfer = response.getBody();
        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getMessage());
        }
        return approvedTransfer;
    }

    public Transfer rejectTransfer (long transferId) {
        Transfer rejectedTransfer = new Transfer();
        try {
            ResponseEntity<Transfer> response = restTemplate.exchange(baseUrl + "transfer/reject/" + transferId, HttpMethod.PUT, createEntity(), Transfer.class);
            rejectedTransfer = response.getBody();
        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getMessage());
        }
        return rejectedTransfer;
    }

    public List<Transfer> getAllTransfersByAccountId(long accountId) {
        HttpEntity<Void> entity = createEntity();
         Transfer[] transfers = new Transfer[]{};
        try {
           ResponseEntity<Transfer[]> transferEntity = restTemplate.exchange(baseUrl + "transfer/account/" + accountId, HttpMethod.GET, entity, Transfer[].class);
            transfers = transferEntity.getBody();
        } catch (RestClientException e) {
            BasicLogger.log(e.getMessage());
        }
        return Arrays.asList(transfers);
    }

    public List<Transfer> allSentTransfersByAccountId(long accountId) {
        HttpEntity<Void> entity = createEntity();
        Transfer[] sentTransfers = new Transfer[]{};
        try {
            ResponseEntity<Transfer[]> sentTransferEntity = restTemplate.exchange(baseUrl + "transfer/account/" + accountId + "/from", HttpMethod.GET, entity, Transfer[].class);
        sentTransfers = sentTransferEntity.getBody();
        } catch (RestClientException e) {
            BasicLogger.log(e.getMessage());
        }
        return Arrays.asList(sentTransfers);
    }

    public List<Transfer> allReceivedTransfersByAccountId(long accountId) {
        HttpEntity<Void> entity = createEntity();
        Transfer[] receivedTransfers = new Transfer[]{};
        try {
            ResponseEntity<Transfer[]>  receivedTransferEntity = restTemplate.exchange(baseUrl + "transfer/account/" + accountId + "/to", HttpMethod.GET, entity, Transfer[].class);
            receivedTransfers = receivedTransferEntity.getBody();
        } catch (RestClientException e) {
            BasicLogger.log(e.getMessage());
        }
        return Arrays.asList(receivedTransfers);
    }

    public List<Transfer> getAllSentTransfersByAccountIdAndStatusId(long accountId, long statusId) {
        HttpEntity<Void> entity = createEntity();
        Transfer[] transfers = new Transfer[]{};
        try {
            ResponseEntity<Transfer[]> response = restTemplate.exchange(baseUrl + "transfer/account/" + accountId + "/from/status/" + statusId, HttpMethod.GET, entity, Transfer[].class);
            transfers = response.getBody();
        } catch (RestClientException e) {
            BasicLogger.log(e.getMessage());
        }
        return Arrays.asList(transfers);
    }

    public List<Transfer> getAllReceivedTransfersByAccountIdAndStatusId(long accountId, long statusId) {
        HttpEntity<Void> entity = createEntity();
        Transfer[] transfers = new Transfer[]{};
        try {
            ResponseEntity<Transfer[]> response = restTemplate.exchange(baseUrl + "transfer/account/" + accountId + "/to/status/" + statusId, HttpMethod.GET, entity, Transfer[].class);
            transfers = response.getBody();
        } catch (RestClientException e) {
            BasicLogger.log(e.getMessage());
        }
        return Arrays.asList(transfers);
    }

    public Transfer getTransferByTransferId(long transferId) {
        Transfer transfer = null;
        HttpEntity<Void> entity = createEntity();
        try {
            ResponseEntity<Transfer> response = restTemplate.exchange(baseUrl + "transfer/" + transferId, HttpMethod.GET, entity, Transfer.class);
            transfer = response.getBody();
        } catch (RestClientException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfer;
    }

    private HttpEntity<TransferCredentials> createTransferCredentialsEntity(TransferCredentials transferCredentials) {
        HttpHeaders headers = createHeader();
        return new HttpEntity<>(transferCredentials, headers);
    }

    private HttpEntity<TransferStatus> createTransferStatusIdEntity (TransferStatus status) {
        HttpHeaders headers = createHeader();
        return new HttpEntity<>(status, headers);
    }

    private HttpEntity<Transfer> createTransferEntity(Transfer transfer) {
        HttpHeaders headers = createHeader();
        return new HttpEntity<>(transfer, headers);
    }

    private HttpHeaders createHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return headers;
    }

    private HttpEntity<Void> createEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }

}


