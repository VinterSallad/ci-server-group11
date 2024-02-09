package org.example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.Objects;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import org.json.*;

public class Main extends AbstractHandler {
    public static final int ERROR = -1;
    public static final int ERRNONE = 0; 

    /**
     * converts the bufferedreader optimally that comes from either a http request or json file into a json object
     * @param reader http request from github or a json file
     * @return json object
     * @throws IOException
     */
    public JSONObject readerToJSON(BufferedReader reader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        String line;
        while((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }

        JSONObject jsonRequest = new JSONObject(stringBuilder.toString());

        return jsonRequest;
    }

    /**
     * takes a json object and returns the "repository" object to be used to clone the repo
     * @param jsonRequest the jsonObject to extract info from
     * @return the repository url
     */
    public String getGitHubRepoURL(JSONObject jsonRequest) {
        return jsonRequest.getJSONObject("repository").getString("url");
    }

    /**
     * takes a json object and returns the "ref" object to be used to swap to the correct branch of the request
     * @param jsonRequest the jsonObject to extract info from
     * @return the ref info
     */
    public String getGitHubRepoRef(JSONObject jsonRequest) {
        return jsonRequest.getString("ref");
    }


    /**
     * Clones a Git repository into a local directory.
     * 
     * @param repo the URL of the Git repository to clone
     * @return ERRNONE if the repository is cloned successfully, ERROR otherwise
     */
    public static int cloneRepo(String repo){
        System.out.println("Cloning repository "+ repo);
        try {
            String currentDir = System.getProperty("user.dir");
            System.out.println("Current working directory: " + currentDir);
            Process cloning = Runtime.getRuntime().exec("git clone " + repo + " ./repo");
            
            // Wait for the process to finish
            int exitValue = cloning.waitFor();
            
            // Check the exit value to determine if the command was successful
            return (exitValue == ERRNONE) ? ERRNONE : ERROR ; 
        } catch (Throwable t) {
            System.err.println("An unexpected error occurred during cloning : " + t.getMessage());
            return  ERROR;
        }
    }

    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        //GitHub sends HTTP POST request and we extract the type of event it is and act accordingly
        //we are interested in "push" https://docs.github.com/en/webhooks/webhook-events-and-payloads#push
        String githubEvent = request.getHeader("X-GitHub-Event");

        //Example
        if(Objects.equals(githubEvent, "ping")) {
            System.out.println("GitHub successfully communicated with the server!");
        }

        //TODO
        if(Objects.equals(githubEvent, "push")) {
            BufferedReader bufferedReader = request.getReader();
            JSONObject payload = readerToJSON(bufferedReader);
            bufferedReader.close();

            String repo = getGitHubRepoURL(payload);
            String ref = getGitHubRepoRef(payload);

            System.out.println(repo);
            System.out.println(ref);

            // here you do all the continuous integration tasks
            // for example
            int error = cloneRepo(repo); 
            if(error == ERRNONE){
                System.out.println("cloned without any issues"); 
            }
            // 2nd compile the code
        
        }


        System.out.println(target);

        

        response.getWriter().println("CI job done");

        System.out.println("This is the end of handle");
    }

    // used to start the CI server in command line
    public static void main(String[] args) throws Exception {
        System.out.println("Hello World");

        Server server = new Server(8011);
        server.setHandler(new Main());
        server.start();
        server.join();
    }
}