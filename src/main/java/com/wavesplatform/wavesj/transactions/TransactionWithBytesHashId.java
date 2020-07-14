package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.ApplicationStatus;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.TransactionStCh;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;
import static com.wavesplatform.wavesj.ByteUtils.hash;

public abstract class TransactionWithBytesHashId implements TransactionStCh {
    @JsonProperty(value = "height", access = WRITE_ONLY)
    private int height;

    @JsonProperty(value = "applicationStatus", defaultValue = "unknown", access = WRITE_ONLY)
    private ApplicationStatus applicationStatus;

    public ByteString getId() {
        return new ByteString(hash(getBodyBytes()));
    }

    public int getHeight() {
        return height;
    }

    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    /**
     * By default all transactions don't support this information
     * @return whether transaction can provide information about state changes
     */
    @JsonIgnore
    public boolean isStateChangesSupported() {
        return false;
    }

    /**
     *
     * @return state changes which are done by transaction or <code>null</code>
     */
    @JsonIgnore
    public StateChanges getStateChanges() {
        return null;
    }
}
