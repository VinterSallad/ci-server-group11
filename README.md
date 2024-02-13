# ci-server-group11 DD2480 Assignment 2

## How to Install and Run Server (Users)
The server requires Apache Maven 3.9.6 and JDK17. You also need ngrok to allow for GitHub to communicate with the server. The CI server is only capable of working with Maven based projects.

Package the server into a .jar file.
```bash
$ mvn package
```

Start ngrok on port 8011.
```bash
$ ngrok http 8011
```

Run the .jar folder in the target/ folder.
```bash
$ java -jar server-ci-group11-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Add the webhook to the GitHub repository that the server should work with. Whenever a push happens, the server should clone, mvn package and assign a status to the commit whether the command was successful or not.

## How to Run Server (Legacy/Internal)
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

## ngrok

For GitHub to be able to communicate with the CI server, you should use a tool like ngrok. You will have to download ngrok, register to get an authtoken and configure your ngrok with this token before being able to run ngrok.

## Detaching SSH Clients

For the server to be able to run in the background on the SSH you wanna make use of the screen commands. The following command creates a separate "screen" where you can start the process:
```bash
$ screen
```
You can then detach from the screen with CTRL+D or equivalent.

To return to your screen to terminate the server for example, you can use the following command:
```bash
$ screen -r
```