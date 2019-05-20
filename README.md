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
    <version>0.14.1</version>
</dependency>
```

##### Gradle:
```
compile group: 'com.wavesplatform', name: 'wavesj', version: '0.14.1'
```

##### SBT:
```
libraryDependencies += "com.wavesplatform" % "wavesj" % "0.14.1"
```

[This library's page at Maven Central](https://mvnrepository.com/artifact/com.wavesplatform/wavesj)

## Basic Usage
Create an account from a private key ('T' for testnet):
```java
String seed = "health lazy lens fix dwarf salad breeze myself silly december endless rent faculty report beyond";
PrivateKeyAccount account = PrivateKeyAccount.fromSeed(seed, 0, Account.TESTNET);
byte[] publicKey = account.getPublicKey();
String address = account.getAddress();
```

Create a Node and learn a few things about blockchain:
```java
Node node = new Node("https://testnode2.wavesnodes.com/", Account.TESTNET);
System.out.println("Current height is " + node.getHeight());
System.out.println("My balance is " + node.getBalance(address));
System.out.println("With 100 confirmations: " + node.getBalance(address, 100));
```

Send some money to a buddy:
```java
String buddy = "3N9gDFq8tKFhBDBTQxR3zqvtpXjw5wW3syA";
String txId = node.transfer(account, buddy, 1_00000000, 100_000, "Here's for you");
```

Set a script on an account. Be careful with the script you pass here, as it may lock the account forever!
```java
String setScriptTxId = node.setScript(alice, "tx.type == 13 && height > " + height, Account.TESTNET, SCRIPT_FEE);
```

Sign a transaction offline:
```java
Transaction tx = Transaction.makeTransferTx(account, buddy, 1_00000000, Asset.WAVES, 100_000, Asset.WAVES, "");
System.out.println("JSON encoded data: " + tx.getJson());
System.out.println("Server endpoint to send this JSON to: " + tx.getEndpoint());
```

Now send it from an online machine:
```java
node.send(tx);
```

Create a DEX order:
```java
Node matcher = new Node("https://testnode2.wavesnodes.com", Account.TESTNET);
String matcherKey = matcher.getMatcherKey();
String wbtcId = "Fmg13HEHJHuZYbtJq8Da8wifJENq8uBxDuWoP9pVe2Qe";
Order order = matcher.createOrder(alice, matcherKey,
                new AssetPair(Asset.WAVES, wbtcId),
                // buy 10 WAVES at 0.00090000 WBTC each
                Order.Type.BUY, 90_000, 10 * Asset.TOKEN,
                // make order valid for 1 hour
                System.currentTimeMillis() + 3_600_000, MATCHER_FEE);
System.out.printf("Filed order " + order.id);
```
There are some examples under `src/examples/java`.

## Building the library

To build from scratch, prepare testnet account with 10 Waves and run

```
mvn clean package -DbenzPrivateKey=YourPrivateKeyOnTestnetWith10Waves
```

The outputs are placed under the `target` directory.
