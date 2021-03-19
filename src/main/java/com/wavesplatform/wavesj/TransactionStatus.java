package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.transactions.common.Id;

import java.util.Objects;

@SuppressWarnings("unused")
public class TransactionStatus {

    private final Id id;
    private final Status status;
    private final ApplicationStatus applicationStatus;
    private final int height;
    private final int confirmations;

    @JsonCreator
    public TransactionStatus(@JsonProperty("id") Id id,
                             @JsonProperty("status") Status status,
                             @JsonProperty("applicationStatus") ApplicationStatus applicationStatus,
                             @JsonProperty(value = "height", defaultValue = "0") int height,
                             @JsonProperty(value = "confirmations", defaultValue = "0") int confirmations) {
        this.id = Common.notNull(id, "Id");
        this.status = Common.notNull(status, "Status");
        this.applicationStatus = Common.notNull(applicationStatus, "ApplicationStatus");
        this.height = height;
        this.confirmations = confirmations;
    }

    public Id id() {
        return id;
    }

    public Status status() {
        return status;
    }

    public ApplicationStatus applicationStatus() {
        return applicationStatus;
    }

    public int height() {
        return height;
    }

    public int confirmations() {
        return confirmations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionStatus that = (TransactionStatus) o;
        return height == that.height &&
                confirmations == that.confirmations &&
                Objects.equals(id, that.id) &&
                Objects.equals(status, that.status) &&
                applicationStatus == that.applicationStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, applicationStatus, height, confirmations);
    }

    @Override
    public String toString() {
        return "TransactionStatus{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", applicationStatus=" + applicationStatus +
                ", height=" + height +
                ", confirmations=" + confirmations +
                '}';
    }

}
