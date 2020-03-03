import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.*;

import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import net.masterthought.cucumber.presentation.PresentationMode;
import net.masterthought.cucumber.reducers.ReducingMethod;
import net.masterthought.cucumber.sorting.SortingMethod;


/*
generate html report for cucumber json files
-output: optional, use current dir if not given
-f     : a list of json files,
-d     : a dir that contains json files

e.g.
java -jar CucumberReport -output ./html-report -f 1.json 2.json
java -jar CucumberReport -d ./jsons

 */


public class CucumberReport {

    // usage
    public static void printUsage() {
        System.out.println("need json file, or path to json files !");
        System.out.println("e.g.  java -jar CucumberReport.jar -output ./ -d ./jsons");
        System.out.println("e.g.  java -jar CucumberReport.jar -output ./ -f ./report1.json ./report2.json");
        System.out.println("if -output not specified, it write to current dir.");
        System.exit(1);
    }

    // parse out command-line arguments
    public static Map<String, List<String>> getCommandLineArgs(String[] args){
        final Map<String, List<String>> params = new HashMap<>();
        List<String> options = null;
        for (int i = 0; i < args.length; i++) {
            final String a = args[i];

            if (a.charAt(0) == '-') {
                if (a.length() < 2) {
                    System.err.println("Error at argument " + a);
                    return params;
                }
                options = new ArrayList<>();
                params.put(a.substring(1), options);
            }
            else if (options != null) {
                options.add(a);
            }
            else {
                System.err.println("Illegal parameter usage");
                return params;
            }
        }

        return params;
    }


    public static void main(String[] args) throws IOException {
        System.out.println("Convert cucumber json into html report.");

        if (args.length < 2) {
            printUsage();
        }

        // parse out the cli arguments
        Map<String, List<String>> params = getCommandLineArgs(args);

        // check output given, if not, use current dir
        String outputdir;
        if (params.containsKey("output")){
            outputdir = params.get("output").get(0);
        } else {
            outputdir= FileSystems.getDefault().getPath(".").toAbsolutePath().toString();
        }

        // need -f or -d, not both
        if ((params.containsKey("d") && params.containsKey("f")) || (!params.containsKey("d") && !params.containsKey("f"))) {
            printUsage();
        }

        // json files array
        List<String> jsonFiles = new ArrayList<>();

        // if f
        if (params.containsKey("f")) {
            for (String file : params.get("f")){
                jsonFiles.add(file);
            }
        }
        // if d
        else if (params.containsKey("d")) {
            String jsonFilesPath = params.get("d").get(0);
            System.out.println(jsonFilesPath);
            // list all json files within the folder
            File f = new File(jsonFilesPath);
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File f, String name) {
                    return name.endsWith(".json");
                }
            };

            String[] fileNames = f.list(filter);
            for (String filename : fileNames) {
                System.out.println(filename);
                jsonFiles.add(jsonFilesPath + "/" + filename);
            }
        }
        else {
            printUsage();
        }

        // output html report folder
        File reportOutputDirectory = new File(outputdir);

        String buildNumber = "101";
        String projectName = "Live Demo Project";
        Configuration configuration = new Configuration(reportOutputDirectory, projectName);
        configuration.setBuildNumber(buildNumber);
        configuration.addClassifications("Browser", "Firefox");
        configuration.addClassifications("Branch", "release/1.0");
        configuration.addClassifications("build", "release #2346");
        configuration.setSortingMethod(SortingMethod.NATURAL);
//        configuration.addPresentationModes(PresentationMode.EXPAND_ALL_STEPS);
        configuration.addPresentationModes(PresentationMode.PARALLEL_TESTING);
        configuration.addReducingMethod(ReducingMethod.HIDE_EMPTY_HOOKS);
        // points to the demo trends which is not used for other tests
        // configuration.setTrendsStatsFile(new File("target/test-classes/demo-trends.json"));

        ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration);
        reportBuilder.generateReports();
    }
}


