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
     * @return An array of strings (result) indicating : 
     *  1) the result of the compilation and testing process (result[0]).
     *         Possible return values  for result[0] are:
     *         - "PASSED" if the project compiled and passed all tests successfully.
     *         - "CFAILED" if there were compilation errors in the project.
     *         - "TFAILED" if there were test failures in the project.
     *         - "OTHER" if there was an unexpected error during the process.
     *  2) the log of the build (result[1])
     *  3) the date of the build (result[2])
     */
    public String[] compileAndTest() {
        File project_folder = new File(Main.REPO_FOLDER);
        String[] result = new String[3]; //result, log, date

        // Default value for result[0] is OTHER 
        result[0] = OTHER; 

        if (project_folder.exists() && project_folder.isDirectory()) {
            try {
                ProcessBuilder futureBuild = new ProcessBuilder("mvn", "package"); //CHANGED
                futureBuild.redirectErrorStream(true);

                // Set location of process 
                System.out.println(project_folder.getAbsolutePath());
                futureBuild.directory(project_folder); 

                // Start process 
                Process building = futureBuild.start(); 

                // Wait for building process to end
                building.waitFor();

                // Create structures to get the console output
                BufferedReader output =  new BufferedReader(new InputStreamReader(building.getInputStream()));
                String line = null;
                StringBuilder sb = new StringBuilder();
                Boolean done = false; 
                result[0] = PASSED;
                
                // Read console output and check for errors
                while ((line = output.readLine()) != null ) {
                    System.out.println(line);
                    sb.append(line);
                    if (line.contains("COMPILATION ERROR" ) && !done) {
                        result[0] = CFAILED;
                        done = true; 
                    } else if (line.contains("BUILD FAILURE") && !done) {
                        result[0] = TFAILED;
                        done = true; 
                    } else if (line.contains("Finished at:")) {
                        result[1] = line.split(": ")[1];
                    }
                }
                // Delete repository
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
