package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CompileTest {


   public static String compileAndTest() {
        File project_folder = new File(Main.REPO_FOLDER);

        if(project_folder.exists() && project_folder.isDirectory()){
            try {
                ProcessBuilder futureBuild = new ProcessBuilder("mvn", "package"); //CHANGED
                futureBuild.redirectErrorStream(true);
                //set location of process 
                System.out.println(project_folder.getAbsolutePath());
                futureBuild.directory(project_folder); 
                //start process 
                Process building = futureBuild.start(); 
                //get the exit value to see if the build and test were successful 
                int exitValue = building.waitFor();
                InputStream fis = building.getInputStream();
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader fg = new BufferedReader(isr);
                String line = null;
                while ((line = fg.readLine()) != null) {
                    System.out.println(line);
                }
                Process remove = Runtime.getRuntime().exec("rm -rf " + Main.REPO_FOLDER); //delete repo
                System.out.println(exitValue); 
    
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                System.out.print("Could not build.");
            }

        }
        return "";
    }


    
}
