## 0.5

Added multisig support in Transaction. Factory methods now accept PublicKeyAccount for `sender` rather than PrivateKeyAccount, and a separate `signers` array. This is a source-compatible change, however, existing code may need to be recompiled.

Also replaced `signature` with `proofs`, and added methods to add proofs to a transaction.

## 0.6

Reworked signing and proofs:
- Removed `signers` parameter from factory methods
- Moved `sign` method from `Transaction` to `PrivateKeyAccount`
- In `Transaction`, `addProof(String)` becomes `setProof(int, String)` so that it's possible to create non-contiguous lists of proofs, e.g. define proofs 1 and 3 but leave proof 2 out.

This is a non-compatible change, but hopefully not many people have started using features from version 0.5 given that they are three days old now. 