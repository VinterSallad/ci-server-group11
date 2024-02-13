package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CompileTest {

    public static final String PASSED = "Compilation and test passed"; 
    public static final String TFAILED = "Tests failed"; 
    public static final String CFAILED = "Compilation failed"; 
    public static final String OTHER = "Could not find repo"; 

    /**
     * Compiles and tests a project located in the repo folder Main.REPO_FOLDER.
     * This method uses Maven to build the project and checks for compilation errors
     * and test failures. After compilation and testing, it deletes the repository folder.
     * 
     * @return A string indicating the result of the compilation and testing process.
     *         Possible return values are:
     *         - "PASSED" if the project compiled and passed all tests successfully.
     *         - "CFAILED" if there were compilation errors in the project.
     *         - "TFAILED" if there were test failures in the project.
     *         - "OTHER" if there was an unexpected error during the process.
     */
    public String compileAndTest() {
        File project_folder = new File(Main.REPO_FOLDER);
        String result = OTHER; 

        if(project_folder.exists() && project_folder.isDirectory()){
            try {
                ProcessBuilder futureBuild = new ProcessBuilder("mvn", "package", "--log-file", "../mvn.log"); //CHANGED
                futureBuild.redirectErrorStream(true);
                //set location of process 
                System.out.println(project_folder.getAbsolutePath());
                futureBuild.directory(project_folder); 
                //start process 
                Process building = futureBuild.start(); 
                //get the exit value to see if the build and test were successful 
                building.waitFor();
                BufferedReader output =  new BufferedReader(new InputStreamReader(building.getInputStream()));
                String line = null;
                Boolean done = false; 
                result = PASSED; 
                while ((line = output.readLine()) != null ) {
                    System.out.println(line);
                    if(line.contains("COMPILATION ERROR" ) && !done){
                        result = CFAILED; 
                        done = true; 
                    }else if(line.contains("BUILD FAILURE") && !done){
                        done = true; 
                        result = TFAILED; 
                    }    
                }
                //delete repo
                Process remove = Runtime.getRuntime().exec("rm -rf " + Main.REPO_FOLDER); 
    
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                System.out.print("Could not build.");

            }

        }
        return result;
    }


    
}
