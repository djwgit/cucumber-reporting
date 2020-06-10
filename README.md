
# Publish pretty [cucumber](https://cucumber.io/) reports

This is a Java report publisher primarily created to publish cucumber reports on the Jenkins build server.
It publishes pretty html reports with charts showing the results of cucumber runs. It has been split out into a standalone package so it can be used for Jenkins and maven command line as well as any other packaging that might be useful. Generated report has no dependency so can be viewed offline.

## Background

Cucumber is a test automation tool following the principles of Behavioural Driven Design and living documentation. Specifications are written in a concise human readable form and executed in continuous integration.

This project allows you to publish the results of a cucumber run as pretty html reports. In order for this to work you must generate a cucumber json report. The project converts the json report into an overview html linking to separate feature files with stats and results.


## Standalone CLI
java -jar cucumber-reporting.jar 

- required parameters
  - -json : cucumber test result json file
  - -input : a path that has multiple json files
  
- optional parameters
  - -output : test report output folder
  - -project : project name to show in the table above the report result table.
  - -note : infos to show in the info table (top-right corner on the page) 
  
 e.g.
 ```
  java -jar cucumber-reporting.jar -json ./result.json -project 'AppName Daily Build Test` -note buildNum:233 ios_version:13.2
```

## Install

Add a maven dependency to your pom
```xml
<dependency>
    <groupId>net.masterthought</groupId>
    <artifactId>cucumber-reporting</artifactId>
    <version>(check version above)</version>
</dependency>
```

Read this if you need further [detailed configuration](https://github.com/jenkinsci/cucumber-reports-plugin/wiki/Detailed-Configuration) instructions for using the Jenkins version of this project

## Usage
```Java
File reportOutputDirectory = new File("target");
List<String> jsonFiles = new ArrayList<>();
jsonFiles.add("cucumber-report-1.json");
jsonFiles.add("cucumber-report-2.json");

String buildNumber = "1";
String projectName = "cucumberProject";

Configuration configuration = new Configuration(reportOutputDirectory, projectName);
// optional configuration - check javadoc for details
configuration.addPresentationModes(PresentationMode.RUN_WITH_JENKINS);
// do not make scenario failed when step has status SKIPPED
configuration.setNotFailingStatuses(Collections.singleton(Status.SKIPPED));
configuration.setBuildNumber(buildNumber);
// addidtional metadata presented on main page
configuration.addClassifications("Platform", "Windows");
configuration.addClassifications("Browser", "Firefox");
configuration.addClassifications("Branch", "release/1.0");

// optionally add metadata presented on main page via properties file
List<String> classificationFiles = new ArrayList<>();
classificationFiles.add("properties-1.properties");
classificationFiles.add("properties-2.properties");
configuration.addClassificationFiles(classificationFiles);

ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration);
Reportable result = reportBuilder.generateReports();
// and here validate 'result' to decide what to do if report has failed
```
There is a feature overview page:

![feature overview page](./.README/feature-overview.png)

And there are also feature specific results pages:

![feature specific page passing](./.README/feature-passed.png)

And useful information for failures:

![feature specific page passing](./.README/feature-failed.png)

If you have tags in your cucumber features you can see a tag overview:

![Tag overview](./.README/tag-overview.png)

And you can drill down into tag specific reports:

![Tag report](./.README/tag-report.png)

![Trends report](./.README/trends.png)

## Continuous delivery and live demo

You can play with the [live demo](http://damianszczepanik.github.io/cucumber-html-reports/overview-features.html) report before you decide if this is worth to use. Report is generated every time new change is merged into the main development branch so it always refers to the most recent version of this project. Sample configuration is provided by [sample code](./src/test/java/LiveDemoTest.java).

## Code quality

Once you developed your new feature or improvement you should test it by providing several unit or integration tests.

![codecov.io](https://codecov.io/gh/damianszczepanik/cucumber-reporting/branch/master/graphs/tree.svg)

## Contribution

Interested in contributing to the cucumber-reporting?  Great!  Start [here](https://github.com/damianszczepanik/cucumber-reporting).
