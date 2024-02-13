package org.example;

import org.example.Main.*;

import org.json.JSONObject;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import static org.junit.Assert.*;

public class ContinuousIntegrationServerTest {
    public static final int ERROR = -1;
    public static final int ERRNONE = 0; 

    //This is all setup
    Main main = new Main();
    CompileTest compileTest = new CompileTest(); 
    BufferedReader inputReaderPush;
    BufferedReader inputReaderPing;
    {
        try {
            inputReaderPush = new BufferedReader(new FileReader("src/test/java/org/example/gitPushPayload.json"));
            inputReaderPing = new BufferedReader(new FileReader("src/test/java/org/example/gitPingPayload.json"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    //This is an actual payload that was sent for a push to the main branch
    JSONObject gitPushPayload = null;

    //This is a payload that is sent when GitHub pings the server
    JSONObject gitPingPayload = null;

    {
        try {
            gitPushPayload = main.readerToJSON(inputReaderPush);
            gitPingPayload = main.readerToJSON(inputReaderPing);
            inputReaderPush.close();
            inputReaderPing.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Testing
    @Test
    public void testReaderToJSON() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("src/test/java/org/example/smallExample.json"));

        JSONObject functionResult = main.readerToJSON(reader);
        JSONObject expectedResult = new JSONObject();

        expectedResult.putOpt("ref", "refs/heads/main");

        JSONObject repository = new JSONObject();
        repository.put("url", "https://github.com/VinterSallad/ci-server-group11");
        expectedResult.putOpt("repository", repository);

        JSONAssert.assertEquals(expectedResult, functionResult, JSONCompareMode.STRICT);
    }

    @Test
    public void testGetGitHubRepoRef() {
        String expected = "refs/heads/main";
        String actual = main.getGitHubRepoRef(gitPushPayload);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetGitHubRepoURL() {
        String expected = "https://github.com/VinterSallad/ci-server-group11";
        String actual = main.getGitHubRepoURL(gitPushPayload);

        assertEquals(expected, actual);
    }

    @Test
    public void testCloneGoodRepo() {
        String URL = "https://github.com/VinterSallad/ci-server-group11";
        int cloning_result = main.cloneRepo(URL); 

        assertEquals(ERRNONE, cloning_result);
    }

    @Test
    public void testCloneInexistantRepo() {
        String URL = "https://github.com/VinterSallad/ci-server-group1111111";
        int cloning_result = main.cloneRepo(URL); 

        assertEquals(ERROR, cloning_result);
    }

    @Test
    public void testCompileAndTest(){
        String URL = "https://github.com/VinterSallad/ci-server-group11";
        main.cloneRepo(URL); 
        String out = compileTest.compileAndTest(); 
        assertEquals(CompileTest.PASSED, out); 
    }

    @Test 
    public void testUpdateBuildHistory() throws IOException {
        String date = "2021-10-10";
        String sha = "123456";
        String log = "log";
        int result = History.updateBuildHistory(date, sha, log);
        assertEquals(ERRNONE, result);
    }

    @Test   
    public void testGetBuildHistory() throws IOException {
        String date = "2021-10-10";
        String sha = "123456";
        String log = "some code";
        History.clearBuildHistory();
        History.updateBuildHistory(date, sha, log);
        String result = History.getBuildHistory();
        String expected = date + " " + sha + " " + log + "\n";
        assertEquals(expected, result);
    }
    @Test
    public void testGetBuildHistoryHTML() throws IOException {
        String date = "2021-10-10";
        String sha = "123456";
        String log = "some code";
        History.clearBuildHistory();
        History.updateBuildHistory(date, sha, log);
        int result = History.getBuildHistoryHTML();
        assertEquals(ERRNONE, result);
    }

}
