# ci-server-group11 DD2480 Assignment 2

## How to Run Server (Preliminary)
The project uses Apache Maven 3.9.6 with JDK17.

Package into a .jar with:
```bash
$ mvn package
```

Go to target/ subfolder and run:
```bash
$ java -jar server-ci-group11-1.0-SNAPSHOT-jar-with-dependencies.jar
```

If the intention is to run the server through an SSH client, SCP the .jar file to the SSH client. Make sure the SSH client uses JDK17 in $PATH and run the above command.