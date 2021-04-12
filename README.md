[![Maven Central](https://img.shields.io/maven-central/v/com.wavesplatform/wavesj.svg?label=Maven%20Central)](https://search.maven.org/artifact/com.wavesplatform/wavesj)

# WavesJ
A Java library for interacting with the Waves blockchain.

Supports node interaction, offline transaction signing and creating addresses and keys.

## Using WavesJ in your project
Use the codes below to add WavesJ as a dependency for your project.

##### Maven:
```
<dependency>
    <groupId>com.wavesplatform</groupId>
    <artifactId>wavesj</artifactId>
    <version>1.1.0</version>
</dependency>
```

##### Gradle:
```
compile group: 'com.wavesplatform', name: 'wavesj', version: '1.1.0'
```

##### SBT:
```
libraryDependencies += "com.wavesplatform" % "wavesj" % "1.1.0"
```

[This library's page at Maven Central](https://mvnrepository.com/artifact/com.wavesplatform/wavesj)

## Basic Usage
Create an account from a private key ('T' for testnet):
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
