package org.example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.Buffer;
import java.util.Objects;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import org.json.*;

public class Main extends AbstractHandler {

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
        }


        System.out.println(target);

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code

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