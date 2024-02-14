package org.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.text.html.HTML;

import org.json.JSONObject;

public class History {

    /**
     * Updates the build history with the date, sha, and log of the build
     * @param date the date of the build
     * @param sha the sha of the build
     * @param log the log of the build
     * @return 0 if successful
     * @throws IOException
     */
    public int updateBuildHistory(String date, String sha, String log) throws IOException {
        File file = new File("buildHistory.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        String content = Files.readString(file.toPath());
        content = date + " " + sha + " " + log + "\n" + content;
        Files.writeString(file.toPath(), content);
        return 0;
    }

    /**
     * Returns the build history
     * @return the build history in a string
     * @throws IOException
     */

    public String getBuildHistory() throws IOException {
        File file = new File("buildHistory.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        return Files.readString(file.toPath());
    }

    /**
     * Creates an HTML file with the build history
     * @return 0 if successful
     * @throws IOException
     */
    public int getBuildHistoryHTML() throws IOException {
        File file = new File("buildHistory.html");
        if (!file.exists()) {
            file.createNewFile();
        }
        String content = "<!DOCTYPE html>\n<html>\n<head>\n<title>Build History</title>\n</head>\n<body>\n";
        content += "<h1>Build History</h1>\n";
        String history = getBuildHistory();
        String[] builds = history.split("\n");  
        content += "<p>Format : Date SHA Log</p>\n";
        for (String build : builds) {
            String[] buildInfo = build.split(" ", 3);

            content += "<p><a href=\"log/" + buildInfo[1] + ".txt\">" + buildInfo[0] + " " + buildInfo[1] + "</a> " +"</p>\n";

            // Create a log file for each build
            // Create a log folder if it doesn't exist
            File logFolder = new File("log");
            if (!logFolder.exists()) {
                logFolder.mkdir();
            }
            File logFile = new File("log/" + buildInfo[1] + ".txt");
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            // Add "Date" and "SHA" to the log file
            Files.writeString(logFile.toPath(), "Date : "+buildInfo[0] + " " +"SHA : " +buildInfo[1] + "\n"+buildInfo[2]);

        }
        content += "</body>\n</html>";
        Files.writeString(file.toPath(), content);
        return 0;
    }

    /**
     * Clears the build history
     * @return 0 if successful
     * @throws IOException
     */
    public int clearBuildHistory() throws IOException {
        File file = new File("buildHistory.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        Files.writeString(file.toPath(), "");

        // Delete the log folder
        File logFolder = new File("log");
        if (logFolder.exists()) {
            File[] logs = logFolder.listFiles();
            for (File log : logs) {
                log.delete();
            }
            logFolder.delete();
        }
        return 0;
    }
}

    

