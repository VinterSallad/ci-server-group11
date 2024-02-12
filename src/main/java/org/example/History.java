package org.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.text.html.HTML;

import org.json.JSONObject;

public class History {

    
    
    
    //Property (CI feature): the CI server keeps the history of the past builds. This history persists even if the server is rebooted. Each build is given a unique URL, that is accessible to get the build information (commit identifier, build date, build logs). One URL exists to list all builds.
    public static int updateBuildHistory(String date, String sha, String log) throws IOException {
        File file = new File("buildHistory.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        String content = Files.readString(file.toPath());
        content = date + " " + sha + " " + log + "\n" + content;
        Files.writeString(file.toPath(), content);
        return 0;
    }
    public static String getBuildHistory() throws IOException {
        File file = new File("buildHistory.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        return Files.readString(file.toPath());
    }
    


     
}

    

