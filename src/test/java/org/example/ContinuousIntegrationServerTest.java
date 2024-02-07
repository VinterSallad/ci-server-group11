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

    //This is all setup
    Main main = new Main();
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

}
