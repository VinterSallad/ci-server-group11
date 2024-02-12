package org.example;

import org.apache.http.impl.client.HttpClients;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import java.util.Base64;

public class Notification {

    private CloseableHttpClient httpClient = null;

    // Setter method for httpClient
    public void setHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Notifies the status of the compilation and testing process to the GitHub API.
     * 
     * @param state sucess or failure depending on the test and compile result
     * @param description the description of the state
     * @param token the token to access the github api
     * @param url the url to send the status to
     * @return a string indicating the result of the notification process
     */
    public String notifyStatus (String state, String description, String token, String url) 
    {
        byte[] decodedBytes = Base64.getDecoder().decode(token);
        String decodedString = new String(decodedBytes);
        try {
            if(httpClient == null)
                httpClient = HttpClients.createDefault(); 
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Accept", "application/vnd.github.v3+json");
            httpPost.setHeader("Authorization", "Bearer " + decodedString);
            httpPost.setHeader("X-GitHub-API-Version", "2022-11-28");
            StringEntity entity = new StringEntity("{\n" +
                    "  \"state\": \"" + state + "\",\n" +
                    "  \"description\": \"" + description + "\",\n" +
                    "  \"context\": \"CI-Server\"\n}");

            httpPost.setEntity(entity);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            int responseCode = response.getStatusLine().getStatusCode();
            httpClient.close();
            response.close();
            if (responseCode != 201) {
                return "Failure with response code: " + responseCode;
            }
            return "Success";

        } catch (Exception e) {
            return "Failure due to exception: " + e.getMessage();
        }
    }
}
