package org.example;

import java.io.*;

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
    public String[] compileAndTest() {
        File project_folder = new File(Main.REPO_FOLDER);
        String[] result = new String[3]; //result, log, date

        if(project_folder.exists() && project_folder.isDirectory()){
            try {
                ProcessBuilder futureBuild = new ProcessBuilder("mvn", "package"); //CHANGED
                futureBuild.redirectErrorStream(true);
                //set location of process 
                System.out.println(project_folder.getAbsolutePath());
                futureBuild.directory(project_folder); 
                //start process 
                Process building = futureBuild.start(); 
                //wait for building process to end
                building.waitFor();
                //create structures to get the console output
                BufferedReader output =  new BufferedReader(new InputStreamReader(building.getInputStream()));
                String line = null;
                StringBuilder sb = new StringBuilder();
                Boolean done = false; 
                result[0] = PASSED;
                while ((line = output.readLine()) != null ) {
                    System.out.println(line);
                    sb.append(line);
                    if(line.contains("COMPILATION ERROR" ) && !done){
                        result[0] = CFAILED;
                        done = true; 
                    }else if(line.contains("BUILD FAILURE") && !done){
                        result[0] = TFAILED;
                        done = true; 
                    }else if(line.contains("Finished at:")) {
                        result[1] = line.split(": ")[1];
                    }
                }
                //delete repository
                Process remove = Runtime.getRuntime().exec("rm -rf " + Main.REPO_FOLDER); 

                result[2] = sb.toString();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                System.out.print("Could not build.");

            }

        }
        return result;
    }


    
}
