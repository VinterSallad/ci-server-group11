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

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import org.mockito.ArgumentCaptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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
        String log = "This is a log";
        int result = History.updateBuildHistory(date, sha, log);
        assertEquals(ERRNONE, result);
    }

    @Test   
    public void testGetBuildHistory() throws IOException {
        String date = "2021-10-10";
        String sha = "123456";
        String log = "This is a log";
        History.clearBuildHistory();
        History.updateBuildHistory(date, sha, log);
        String result = History.getBuildHistory();
        String expected = date + " " + sha + " " + log + "\n";
        assertEquals(expected, result);
    }
    @Test
    public void testNotifyStatus() throws Exception {
        // Mocking parameters
        String state = "success";
        String description = "Compilation and tests passed";
        String token = "dummyToken";
        String encodedToken = "ZHVtbXlUb2tlbg==";
        String url = "dummyUrl";

        // Mocking HTTP client and response
        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse httpResponse = mock(CloseableHttpResponse.class);

        // Mocking HTTP status line and entity
        StatusLine statusLine = mock(StatusLine.class);
        HttpEntity httpEntity = mock(HttpEntity.class);

        // Mocking response code
        when(statusLine.getStatusCode()).thenReturn(201);

        // Mocking HTTP client execution
        when(httpClient.execute(any())).thenReturn(httpResponse);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpResponse.getEntity()).thenReturn(httpEntity);
        when(httpEntity.getContent()).thenReturn(null);

        // Creating Notification instance with mocked HTTP client
        Notification notification = new Notification();
        notification.setHttpClient(httpClient);

        // Invoking the method under test
        String result = notification.notifyStatus(state, description, encodedToken, url);

        // Verifying the method was called with correct parameters
        ArgumentCaptor<HttpPost> argument = ArgumentCaptor.forClass(HttpPost.class);
        verify(httpClient).execute(argument.capture());
        HttpPost capturedHttpPost = argument.getValue();

        assertEquals("Bearer dummyToken", capturedHttpPost.getFirstHeader("Authorization").getValue());
        assertEquals("2022-11-28", capturedHttpPost.getFirstHeader("X-GitHub-API-Version").getValue());
        assertNotNull(capturedHttpPost.getEntity());

        // Verifying the result
        assertEquals("Success", result);

    }

}
