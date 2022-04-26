[![Maven Central](https://img.shields.io/maven-central/v/com.wavesplatform/wavesj.svg?label=Maven%20Central)](https://search.maven.org/artifact/com.wavesplatform/wavesj)

# WavesJ
A Java library for interacting with the Waves blockchain.

Supports node interaction, offline transaction signing and creating addresses and keys.

## Using WavesJ in your project
Use the codes below to add WavesJ as a dependency for your project.

##### Requirements:
- JDK 1.8 or above

##### Maven:
```
<dependency>
    <groupId>com.wavesplatform</groupId>
    <artifactId>wavesj</artifactId>
    <version>1.2.5</version>
</dependency>
```

##### Gradle:
```
compile group: 'com.wavesplatform', name: 'wavesj', version: '1.2.5'
```

##### SBT:
```
libraryDependencies += "com.wavesplatform" % "wavesj" % "1.2.5"
```

[This library's page at Maven Central](https://mvnrepository.com/artifact/com.wavesplatform/wavesj)

### Getting started
Create an account from a private key ('T' for testnet) from random seed phrase:
```java
String seed = Crypto.getRandomSeedPhrase();
PrivateKey privateKey = PrivateKey.fromSeed(seed);
PublicKey publicKey = PublicKey.from(privateKey);
Address address = Address.from(publicKey);
```

Create a Node and learn a few things about blockchain:
```java
Node node = new Node(Profile.MAINNET);
System.out.println("Current height is " + node.getHeight());
System.out.println("My balance is " + node.getBalance(address));
System.out.println("With 100 confirmations: " + node.getBalance(address, 100));
```

Send some money to a buddy:
```java
Address buddy = new Address("3N9gDFq8tKFhBDBTQxR3zqvtpXjw5wW3syA");
node.broadcast(TransferTransaction.builder(buddy, Amount.of(1_00000000, Asset.WAVES)).getSignedWith(privateKey));
```

Set a script on an account. Be careful with the script you pass here, as it may lock the account forever!
```java
Base64String script = node
    .compile("{-# CONTENT_TYPE EXPRESSION #-} sigVerify(tx.bodyBytes, tx.proofs[0], tx.senderPublicKey)")
    .script();
node.broadcast(new SetScriptTransaction(publicKey, script).addProof(privateKey));
```

### Reading transaction info
[Same transaction from REST API](https://nodes-stagenet.wavesnodes.com/transactions/info/CWuFY42te67sLmc5gwt4NxwHmFjVfJdHkKuLyshTwEct)

```java
Id ethTxId = new Id("CWuFY42te67sLmc5gwt4NxwHmFjVfJdHkKuLyshTwEct");
EthereumTransactionInfo ethInvokeTxInfo = node.getTransactionInfo(ethTxId, EthereumTransactionInfo.class);

EthereumTransaction ethInvokeTx = ethInvokeTxInfo.tx();
EthereumTransaction.Invocation payload = (EthereumTransaction.Invocation) ethInvokeTx.payload();

System.out.println("is ethereum invoke transaction: " + ethInvokeTxInfo.isInvokeTransaction());

System.out.println("type: " + ethInvokeTx.type());
System.out.println("id: " + ethInvokeTx.id().encoded());
System.out.println("fee: " + ethInvokeTx.fee().value());
System.out.println("feeAssetId: " + ethInvokeTx.fee().assetId().encoded());
System.out.println("timestamp: " + ethInvokeTx.timestamp());
System.out.println("version: " + ethInvokeTx.version());
System.out.println("chainId: " + ethInvokeTx.chainId());
System.out.println("bytes: " + ethInvokeTxInfo.getBytes());
System.out.println("sender: " + ethInvokeTx.sender().address().encoded());
System.out.println("senderPublicKey: " + ethInvokeTx.sender().encoded());
System.out.println("height: " + ethInvokeTxInfo.height());
System.out.println("applicationStatus: " + ethInvokeTxInfo.applicationStatus());
System.out.println("payload dApp: " + payload.dApp().encoded());
System.out.println("payload call function: " + payload.function().name());
List<Arg> args = payload.function().args();
System.out.println("payload call function arguments type: " + args.get(0).type());
System.out.println("payload call function arguments value: " + ((StringArg) args.get(0)).value());
DataEntry dataEntry = ethInvokeTxInfo.getStateChanges().data().get(0);
System.out.println("state changes data key: " + dataEntry.key());
System.out.println("state changes data type: " + dataEntry.type().name());
System.out.println("state changes data value: " + ((StringEntry) dataEntry).value());
```

### Broadcasting transactions
#### Creating accounts (see Getting started for more info about account creation)
```java
PrivateKey alice = createAccountWithBalance(10_00000000);
PrivateKey bob = createAccountWithBalance(10_00000000);
```
#### Broadcasting exchange transaction
```java
AssetId assetId = node.waitForTransaction(node.broadcast(
        IssueTransaction.builder("Asset", 1000, 2).getSignedWith(alice)).id(),
        IssueTransactionInfo.class).tx().assetId();

Amount amount = Amount.of(1);
Amount price = Amount.of(100, assetId);
long matcherFee = 300000;
Order buy = Order.builder(OrderType.BUY, amount, price, alice.publicKey()).getSignedWith(alice);
Order sell = Order.builder(OrderType.SELL, amount, price, alice.publicKey()).getSignedWith(bob);

ExchangeTransaction tx = ExchangeTransaction
        .builder(buy, sell, amount.value(), price.value(), matcherFee, matcherFee).getSignedWith(alice);
node.waitForTransaction(node.broadcast(tx).id());

TransactionInfo commonInfo = node.getTransactionInfo(tx.id());
ExchangeTransactionInfo txInfo = node.getTransactionInfo(tx.id(), ExchangeTransactionInfo.class);
```

### Working with dApp
#### Creating accounts (see Getting started for more info about account creation)
```java
PrivateKey alice = createAccountWithBalance(10_00000000);
PrivateKey bob = createAccountWithBalance(10_00000000);
```
#### Broadcasting issue transaction
```java
AssetId assetId = node.waitForTransaction(node.broadcast(
        IssueTransaction.builder("Asset", 1000, 2).getSignedWith(alice)).id(),
        IssueTransactionInfo.class).tx().assetId();
```

#### Compiling and broadcasting RIDE script
```java
Base64String script = node.compileScript(
        "{-# STDLIB_VERSION 5 #-}\n" +
        "{-# CONTENT_TYPE DAPP #-}\n" +
        "{-# SCRIPT_TYPE ACCOUNT #-}\n" +
        "@Callable(inv)\n" +
        "func call(bv: ByteVector, b: Boolean, int: Int, str: String, list: List[Int]) = {\n" +
        "  let asset = Issue(\"Asset\", \"\", 1, 0, true)\n" +
        "  let assetId = asset.calculateAssetId()\n" +
        "  let lease = Lease(inv.caller, 7)\n" +
        "  let leaseId = lease.calculateLeaseId()\n" +
        "  [\n" +
        "    BinaryEntry(\"bin\", assetId),\n" +
        "    BooleanEntry(\"bool\", true),\n" +
        "    IntegerEntry(\"int\", 100500),\n" +
        "    StringEntry(\"assetId\", assetId.toBase58String()),\n" +
        "    StringEntry(\"leaseId\", leaseId.toBase58String()),\n" +
        "    StringEntry(\"del\", \"\"),\n" +
        "    DeleteEntry(\"del\"),\n" +
        "    asset,\n" +
        "    SponsorFee(assetId, 1),\n" +
        "    Reissue(assetId, 4, false),\n" +
        "    Burn(assetId, 3),\n" +
        "    ScriptTransfer(inv.caller, 2, assetId),\n" +
        "    lease,\n" +
        "    LeaseCancel(lease.calculateLeaseId())\n" +
        "  ]\n" +
        "}").script();
node.waitForTransaction(node.broadcast(
        SetScriptTransaction.builder(script).getSignedWith(bob)).id());
```

#### Calling dApp
```java
InvokeScriptTransaction tx = InvokeScriptTransaction
        .builder(bob.address(), Function.as("call",
                BinaryArg.as(alice.address().bytes()),
                BooleanArg.as(true),
                IntegerArg.as(100500),
                StringArg.as(alice.address().toString()),
                ListArg.as(IntegerArg.as(100500))
        )).payments(
                Amount.of(1, assetId),
                Amount.of(2, assetId),
                Amount.of(3, assetId),
                Amount.of(4, assetId),
                Amount.of(5, assetId),
                Amount.of(6, assetId),
                Amount.of(7, assetId),
                Amount.of(8, assetId),
                Amount.of(9, assetId),
                Amount.of(10, assetId)
        ).extraFee(1_00000000)
        .getSignedWith(alice);
node.waitForTransaction(node.broadcast(tx).id());
```

#### Receiving invoke script transaction info
```java
TransactionInfo commonInfo = node.getTransactionInfo(tx.id());
InvokeScriptTransactionInfo txInfo = node.getTransactionInfo(tx.id(), InvokeScriptTransactionInfo.class);
```
