package org.example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import org.json.*;

public class Main extends AbstractHandler {

    /**
     * converts the http request object into a string to be parseable by json library
     * @param request http request from github
     * @return http request as a string
     * @throws IOException
     */
    public String httpRequestToString(HttpServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = request.getReader();

        String line;
        while((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }

        return stringBuilder.toString();
    }

    /**
     * takes a string and converts it into a json object to extract the needed data to clone a github repo
     * and swap to its relevant branch
     * @param request post request as a string
     * @return return the repo url and the relevant push branch as an array in that order
     */
    public String[] getGitHubRepo(String request) {
        JSONObject jsonRequest = new JSONObject(request);

        String repo = jsonRequest.getJSONObject("repository").getString("url");
        String branch = jsonRequest.getString("ref");

        String[] result = new String[2];

        result[0] = repo;
        result[1] = branch;

        return result;
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
        if(githubEvent == "ping") {
            System.out.println("GitHub successfully communicated with the server!");
        }

        //TODO
        if(githubEvent == "push") {
            String[] test = getGitHubRepo(httpRequestToString(request));

            System.out.println(test[0]);
            System.out.println(test[1]);
        }


        System.out.println(target);

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code

        response.getWriter().println("CI job done");
    }

    // used to start the CI server in command line
    public static void main(String[] args) throws Exception {
        System.out.println("Helllo World");

        Server server = new Server(8011);
        server.setHandler(new Main());
        server.start();
        server.join();
    }
}