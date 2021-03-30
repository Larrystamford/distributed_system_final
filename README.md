### Client (Java)
```
cd client
javac -d . -cp .:lib/* *.java
java -cp .:lib/* client.UDPClient -h <HOST NAME> -p <PORT> [-al] [-am] [-fr <FAILURE RATE>] [-to <TIMEOUT>] [-mt <MAX TIMEOUT COUNT>] [-v]
```