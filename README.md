# DD2480 Assignment 2 (Group 11)

This project was created for "Assignment 2: Continuous Integration" of the course VT24 DD2480 - Software Engineering Fundamentals at KTH Royal Institute of Technology. 

## Lite Continuous Integration (CI) Server

The focus of this project was to develop a small continuous integration (CI) server containing some basic, core features of continuous integration which were; #1 Compilation, #2 Testing, #3 Notification and a bonus feature of retaining logs of the historic builds.

To go more in-depth for each of the features:
1. Compilation: the CI server supports compiling the group project, a static syntax check is to be performed for languages without a compiler. Compilation is triggered as a webhook, the CI server compiles the branch where the change has been made, as specified in the HTTP payload.

2. Testing - the CI server supports executing the automated tests of the group project. Testing is triggered as a webhook, on the branch where the change has been made, as specified in the HTTP payload.
   
3. Notification - the CI server supports notification of CI results through GitHub API.

4. Past Builds - the CI server keeps the history of the past builds. This history persists even if the server is rebooted. 

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

## Compilation and Testing Implementation

The CI Features; Compilation and Testing, were implemented within the "CompileTest.java" file as the following method:
```java
public String[] compileAndTest()
```
First, the `compileTest` class contains the following string constants to indicate the state of compilation and testing:
```java
public static final String PASSED = "Compilation and test passed"; 
public static final String TFAILED = "Tests failed"; 
public static final String CFAILED = "Compilation failed"; 
public static final String OTHER = "Could not find repo"; 
```
The method then initializes a File object with the path specified by `Main.REPO_FOLDER`. It creates a String array result to store the result, log, and date of the compilation and testing process. It then checks for the existence of the project folder and directory, where it initializes a `ProcessBuilder` object to execute the Mave command `mvn package`. Redirects error stream to standard output, and it then sets directory of the process to the project folder, where it starts the process and waits for the process to finish to get the exit value to see if the build and test were successful. It then reads the output line by line to check if the line contains specific keywords indicating compilation error or test failure, where it updates the `result` array accordingly. It stores the timestamp when the compilation and testing are finished and deletes the repository folder using the `rm -rf` command. Finally, it catches IOException and InterruptedException, prints stack trace, and logs an error message if the compilation and testing process encounters any issues.

For the testing of the two features, the "assessment" branch was first created and within it, the branch was able to compile and ensure that every test in the branch passed. In the "main" branch containing the test cases for the `compileAndTest()` method, the return value was ensured to be 'PASSED'. Within the "assessment" branch, a dummy test was created that returns "FAIL" and returns to the "main" branch to ensure the return value was "TFAILED" for "Test Failed". For the final test, return to the "assessment" branch and choose something to fail to compile, and then return to "main" and change the return value to "CFAILED" for "Compilation Failed".

## Notification Feature Implementation

The Notification feature was developed within the "Notification.java" file, the main method `notifyStatus` contains the following parameters:
```java
public String notifyStatus (String state, String description, String token, String URL)
```
- `state`: Represents the success or failure state of the compilation and testing process.
- `description`: Description of the state
- `token`: Token to access the GitHub API. This token is expected to be base64 encoded.
- `URL`: The URL to which the notification will be sent.

`token` is first decoded from base64 to obtain the actual token string. It will initialize `httpClient` if it is not null using the `.createDefault()` method. Then an HTTP Post is created for the `URL` specified beforehand where headers include Accept, Authorization, and X-GitHub-API-Version. A JSON payload containing state, description, and context is created which is assigned to the `entity` of the request. The POST Request is then executed and we then retrieve the response status code and the method returns the appropriate String according to the status code we received.

## Past Build History

To check on the history of the past builds, ensure that the server is currently running, and then input "buildHistory.html" into the directory to bring you to a webpage containing the Data SHA Log of each past build. Clicking into each log will bring up the mvn log.

## Statement of Contribution

Alexander: Setting up the repo and server skeleton (Initial Meeting). Running and testing the server. Entrypoint for the server, i.e. handling the GitHub JSON POST request. Created and initialised test file and JSON tests.

Iley: I added support for notifications of CI results and tests for it, by using the REST API for Github. I also reviewed one of Roxanne's pull requests.

Rached: Wrote History class which stores the pushed code's information in HTML and .txt file along with test cases for the History class.

Roxanne: Cloning of a repo, and then the TestCompile class which checks if a "push" compiles and if the tests of that "push" pass, accompanied by the development of test cases for the stated checks. Reviewed pull requests from Iley and Rached.

Marcus: Wrote README Documentation File with implementation/unit-testing of CI features and Team Evaluation, reviewed pull request from Roxanne.

## Team Evaluation

Upon careful assessment of our team's activities, we have determined that the team is currently placed in the "Performing" state. This is demonstrated through several instances, such as when a new issue was detected during development, our team was able to quickly assign new members to tackle the issue and resolve it quickly without incurring any complications. Every team member was assigned a dedicated role before the project began, and all of them were able to meet their assigned objectives and perform what was expected of them. All issues were resolved within the team, and any issues a member discovered were forwarded to the rest of the team who were able to help solve the issue together. The team was able to constantly make progress in this project, and no setbacks occurred to halt the development as each member had clearly defined goals and did not interfere with other's assignments. Wasted works rarely cropped up, but were always immediately checked by various members in pull requests to ensure they were eliminated. However, one aspect the team could improve upon is more constant communication from certain members to ensure everyone is cognizant of each other's progress as sometimes, developments seem to be slightly slowed as we were sometimes unaware of how far each other's progress was, as some parts of the codebase being worked on by multiple members were dependent on one another. 
