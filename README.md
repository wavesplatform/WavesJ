# WavesJ
A Java library for interacting with the Waves blockchain.

Supports node interaction, offline transaction signing, Matcher orders, and creating addresses and keys.

## Using WavesJ in your project
Use the codes below to add WavesJ as a dependency for your project.

##### Maven:
```
<dependency>
    <groupId>com.wavesplatform</groupId>
    <artifactId>wavesj</artifactId>
    <version>0.1</version>
</dependency>
```

##### Gradle:
```
compile group: 'com.wavesplatform', name: 'wavesj', version: '0.1'
```

##### SBT:
```
libraryDependencies += "com.wavesplatform" % "wavesj" % "0.1"
```

[This library's page at Maven Central](https://mvnrepository.com/artifact/com.wavesplatform/wavesj)

## Basic Usage
Create an account from a private key ('T' for testnet):
```
String seed = "health lazy lens fix dwarf salad breeze myself silly december endless rent faculty report beyond";
PrivateKeyAccount account = new PrivateKeyAccount(seed, 0, Account.TESTNET);
byte[] publicKey = account.getPublicKey();
String address = account.getAddress();
```

Create a Node and learn a few things about blockchain:
```
Node node = new Node("https://my.waves.node/");
System.out.println("Current height is " + node.getHeight());
System.out.println("My balance is " + node.getBalance(address));
System.out.println("With 100 confirmations: " + node.getBalance(address, 100));
```

Send some money to a buddy:
```
String buddy = "3N9gDFq8tKFhBDBTQxR3zqvtpXjw5wW3syA";
String txId = node.transfer(account, buddy, 1_00000000, 100_000, "Here's for you");
```

Sign a transaction offline:
```
Transaction tx = Transaction.makeTransferTx(account, buddy, 1_00000000, Asset.WAVES, 100_000, Asset.WAVES, "");
System.out.println("JSON encoded data: " + tx.getJson());
System.out.println("Server endpoint to send this JSON to: " + tx.getEndpoint());
```

Now send it from an online machine:
```
node.send(tx);
```

Create a DEX order:
```
Node matcher = new Node("https://testnode2.wavesnodes.com");
String matcherKey = matcher.getMatcherKey();
String wbtcId = "Fmg13HEHJHuZYbtJq8Da8wifJENq8uBxDuWoP9pVe2Qe";
String orderId = matcher.createOrder(account, matcherKey,
                // buy 1 WBTC for 1000 WAVES
                wbtcId, "", Order.Type.BUY, 1000, 1_00000000,
                // make order valid for 1 hour
                System.currentTimeMillis() + 3_600_000, 300_000);
```
There's some example code under `src/examples/java`.

## Building the library

To build from scratch, run

```
mvn clean package
```

The outputs are placed under the `target` directory.
