package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.wavesplatform.wavesj.json.deser.TransactionTypeResolver;

/**
 * This class represents a Waves object. Instances are immutable, with data accessible through public final fields.
 * They are obtained using static factory methods defined in this class.
 *
 * <h2>Proofs and Signers</h2>
 * <p>Each object has a number of proofs associated with it that are used to validate the object. For non-scripted
 * accounts, the only proof needed is a signature made with the private key of the object sender. For scripted
 * accounts, the number and order of proofs are dictated by the account script. E.g. a 2-of-3 multisig account would
 * require 2 to 3 signatures made by holders of certain public keys.
 *
 * <p>Each proof is a byte array of 64 bytes max. There's a limit of 8 proofs per object.
 *
 * <p>There are two ways to sign a object:
 * <ul>
 *     <li>When an instance of {@link PrivateKeyAccount} is passed as the {@code sender} parameter to any of the factory
 *     methods, it is used to sign the object, and the signature is set as the proof number 0. This is needed for
 *     non-scripted accounts and for backward compatibility with older versions of the library.
 *     <li>It is possible to add up to 8 proofs to any object. The {@link #withProof(int, String)} method can be used
 *     to set arbitrary Base58-encoded proofs at arbitTransactionrary positions. Note that this method does not modify
 *     {@code Transaction} it is called on, but rather returns a new instance.
 * </ul>
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonTypeIdResolver(TransactionTypeResolver.class)
public interface Transaction extends ApiJson, Signable {
    public static final byte V1 = 1;
    public static final byte V2 = 2;

    public long getFee();
    public long getTimestamp();

    /**
     * Transaction ID.
     */
    @JsonIgnore
    public ByteString getId();

    public abstract PublicKeyAccount getSenderPublicKey();
    public abstract byte getType();
    public abstract byte getVersion();
}
