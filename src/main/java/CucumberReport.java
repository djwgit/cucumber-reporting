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
        System.out.println("\nneed json file, or path to json files !");
        System.out.println("e.g.  \njava -jar cucumber-reporting.jar -output ./report -input ./jsons");
        System.out.println("java -jar cucumber-reporting.jar -json ./report1.json ./report2.json");
        System.out.println("if -output not specified, it writes to current dir.");
        System.out.println("\noptional infos to show in the table above result:");
        System.out.println("-project FieldMapsSF");
//        System.out.println("-buildnum 121");
        System.out.println("\noptional notes to show in top-right corner of report page: e.g.");
        System.out.println("-note branch:signin254 build:daily#232");
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

        ///////////////////////////////////////////////////////////
        // REQUIRED parameters
        ///////////////////////////////////////////////////////////
        //  (json file or dir): -f or -d

        // need -f or -d, not both
        if ((params.containsKey("input") && params.containsKey("json")) || (!params.containsKey("input") && !params.containsKey("json"))) {
            printUsage();
        }

        // -f: json files array
        List<String> jsonFiles = new ArrayList<>();
        if (params.containsKey("json")) {
            for (String file : params.get("json")){
                System.out.println("json:" + file);
                jsonFiles.add(file);
            }
        }
        // -d: a json file folder
        else if (params.containsKey("input")) {
            String jsonFilesPath = params.get("input").get(0);
            System.out.println("input:" + jsonFilesPath);
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


        //////////////////////////////////////////////////////////
        // OPTIONAL parameters:
        //////////////////////////////////////////////////////////

        // table above result table:
        // -------------------------------------------
        // | Project   | Number | Date               |
        // | FieldMaps | 232    | 10 Jun 2020, 08:36 |
        // -------------------------------------------

        // -output
        // report output dir. if not specified, use current dir
        String outputdir;
        if (params.containsKey("output")){
            outputdir = params.get("output").get(0);
        } else {
            outputdir= FileSystems.getDefault().getPath(".").toAbsolutePath().toString();
        }
        System.out.println("outputdir:" + outputdir);
        File reportOutputDirectory = new File(outputdir);

        // -project
        String projectName = "";
        if (params.containsKey("project")) {
            projectName = params.get("project").get(0);
        }
        System.out.println("project:" + projectName);
        Configuration configuration = new Configuration(reportOutputDirectory, projectName);

        // -buildnum
        if (params.containsKey("buildnum")) {
            String buildNumber = params.get("buildnum").get(0);
            System.out.println("buildNumber:" + buildNumber);
            configuration.setBuildNumber(buildNumber);
        }


        // notes table in the top-right corner
        // could add more as needed
        // ---------------------------
        // | Browser | Firefox        |
        // | Branch  | release/20.2.0 |
        // | build   | daily          |
        // | ...     | ...            |
        // ---------------------------

        // -note
        // -note Browser:Firefox
        if (params.containsKey("note")) {
            for (String classification : params.get("note")){
                String[] arr = classification.split(":");
                System.out.println("note:" + classification);
                if (arr.length < 2) {
                    continue;
                }
//                System.out.println("- " + arr[0] + ": " + arr[1]);
                configuration.addClassifications(arr[0], arr[1]);
            }
        }


        configuration.setSortingMethod(SortingMethod.NATURAL);
        //configuration.addPresentationModes(PresentationMode.EXPAND_ALL_STEPS);
        configuration.addPresentationModes(PresentationMode.PARALLEL_TESTING);
        configuration.addReducingMethod(ReducingMethod.HIDE_EMPTY_HOOKS);
        // points to the demo trends which is not used for other tests
        // configuration.setTrendsStatsFile(new File("target/test-classes/demo-trends.json"));

        ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration);
        reportBuilder.generateReports();
    }
}


