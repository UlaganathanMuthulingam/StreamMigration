package com.maveric.streamChange;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Streamchange {
  public Map<String, Map<String, String>> userStramValues = new LinkedHashMap<>();
  
  public Map<String, String> userStreamOutputFlagsIndividual;
  
  public Map<String, String> userStreamSelectedIndividual;
  
  public Map<String, Map<String, String>> userStreamSelectedAll = new LinkedHashMap<>();
  
  public Map<String, Map<String, String>> userStreamOutputValuesAll = new LinkedHashMap<>();
  
  public XSSFWorkbook excelWorkBook = null;
  
  public FileOutputStream outputStream = null;
  
  public FileInputStream inputStream = null;
  
  public XSSFSheet excelSheet;
  
  public void updateExcelValuesInMap(Map<String, Map<String, String>> inputMap, String inputSheetLocation) {
    FileInputStream input = readInputStream(inputSheetLocation);
    XSSFWorkbook workbook = loadExcelValuefromInputStream(input);
    XSSFSheet sheet = getsheetname(workbook, "Sheet1");
    String employeeId = "";
    XSSFRow row1 = sheet.getRow(0);
    int rowCount = sheet.getLastRowNum();
    int colcount = row1.getLastCellNum();
    for (int row = 1; row <= rowCount; row++) {
      Map<String, String> tempMap = new LinkedHashMap<>();
      XSSFRow row2 = sheet.getRow(row);
      for (int col = 0; col < colcount; col++) {
        String key = row1.getCell(col).toString().trim();
        String value = row2.getCell(col).toString().trim();
        employeeId = row2.getCell(5).toString().trim();
        tempMap.put(key, value);
      } 
      inputMap.put(employeeId, tempMap);
    } 
  }
  
  public XSSFSheet getsheetname(XSSFWorkbook workbook, String sheetName) {
    XSSFSheet sheet = null;
    sheet = workbook.getSheet(sheetName);
    if (sheet == null) {
      System.out.println("Sheet name 'Sheet1' not available in the input excel " + System.getProperty("user.dir"));
      popupMessage("Sheet name 'Sheet1' not available in the input excel " + System.getProperty("user.dir"));
      System.exit(1);
    } 
    return sheet;
  }
  
  public String getValuefromMap(Map<String, String> mapname, String key) {
    String value = "";
    value = mapname.get(key);
    if (value != null)
      return value; 
    System.out.println(String.valueOf(key) + " question not avilable in the in the input sheet ");
    popupMessage("' " + key + " ' question not avilable in the in the input sheet ");
    System.exit(1);
    return value;
  }
  
  public List<String> splitValue(String value) {
    List<String> arrayList = new ArrayList<>();
    String[] spliedArray = value.split(";");
    arrayList.addAll(Arrays.asList(spliedArray));
    arrayList.replaceAll(String::trim);
    return arrayList;
  }
  
  public String flagChecker(String selectedAns, String advancedValue, String intermediateValue, String basicValue, int expectedAdvancedSize, int expectedIntermediateSize, int expectedBasicSize) {
    List<String> advancedArray = new ArrayList<>();
    List<String> intermediateArray = new ArrayList<>();
    List<String> basicArray = new ArrayList<>();
    List<String> selectedValuesArray = new ArrayList<>();
    advancedArray = splitValue(advancedValue);
    intermediateArray = splitValue(intermediateValue);
    basicArray = splitValue(basicValue);
    selectedValuesArray = splitValue(selectedAns);
    basicArray.retainAll(selectedValuesArray);
    intermediateArray.retainAll(selectedValuesArray);
    advancedArray.retainAll(selectedValuesArray);
    if (selectedValuesArray.size() == 1 && ((String)selectedValuesArray.get(0)).equals("NA"))
      return "Not Applicable"; 
    if (advancedArray.size() == expectedAdvancedSize) {
      System.out.println("Advanced");
      return "Advanced";
    } 
    if (intermediateArray.size() == expectedIntermediateSize) {
      if (intermediateArray.size() == 0)
        return "Not Applicable"; 
      return "Intermediate";
    } 
    if (basicArray.size() == expectedBasicSize)
      return "Basic"; 
    return "Not Applicable";
  }
  
  public String replaceTwosemicolons(String inputString) {
    String output = "";
    if (!inputString.isEmpty())
      output = inputString.replaceAll(";;", ";"); 
    return output;
  }
  
  public void verifyFlags(Map<String, String> values) {
    this.userStreamOutputFlagsIndividual = new LinkedHashMap<>();
    this.userStreamSelectedIndividual = new LinkedHashMap<>();
    this.userStreamOutputFlagsIndividual.put("Reporting Manager Name", getValuefromMap(values, "Name"));
    this.userStreamOutputFlagsIndividual.put("Reporting Manager Email", getValuefromMap(values, "Email"));
    this.userStreamOutputFlagsIndividual.put("Nominee - Employee ID", getValuefromMap(values, "Nominee - Employee ID"));
    this.userStreamOutputFlagsIndividual.put("Nominee - Employee Name", getValuefromMap(values, "Nominee - Employee Name"));
    this.userStreamOutputFlagsIndividual.put("Current Designation - Proposed Designation", getValuefromMap(values, "Current Designation - Proposed Designation"));
    this.userStreamSelectedIndividual.put("Reporting Manager Name", getValuefromMap(values, "Name"));
    this.userStreamSelectedIndividual.put("Reporting Manager Email", getValuefromMap(values, "Email"));
    this.userStreamSelectedIndividual.put("Nominee - Employee ID", getValuefromMap(values, "Nominee - Employee ID"));
    this.userStreamSelectedIndividual.put("Nominee - Employee Name", getValuefromMap(values, "Nominee - Employee Name"));
    this.userStreamSelectedIndividual.put("Current Designation - Proposed Designation", getValuefromMap(values, "Current Designation - Proposed Designation"));
    String testCaseDesignTopic = getValuefromMap(values, "Test Case Design");
    testCaseDesignTopic = replaceTwosemicolons(testCaseDesignTopic);
    flagAssessment("Test Case Design", testCaseDesignTopic);
    this.userStreamSelectedIndividual.put("Test Case Design", testCaseDesignTopic);
    String testExecutionTopic = String.valueOf(getValuefromMap(values, "End to End Testing")) + ";" + getValuefromMap(values, "Exploratory Testing") + ";" + getValuefromMap(values, "Compliance") + ";" + getValuefromMap(values, "Test Execution");
    testExecutionTopic = replaceTwosemicolons(testExecutionTopic);
    flagAssessment("Test Execution", testExecutionTopic);
    this.userStreamSelectedIndividual.put("Test Execution", testExecutionTopic);
    String stlcToolsTopic = getValuefromMap(values, "STLC Tools");
    stlcToolsTopic = replaceTwosemicolons(stlcToolsTopic);
    flagAssessment("STLC Tools", stlcToolsTopic);
    this.userStreamSelectedIndividual.put("STLC Tools", stlcToolsTopic);
    String testAutomationDesignTopic = String.valueOf(getValuefromMap(values, "Automation Development")) + ";" + getValuefromMap(values, "Test Automation Design");
    testAutomationDesignTopic = replaceTwosemicolons(testAutomationDesignTopic);
    flagAssessment("Test Automation Design", testAutomationDesignTopic);
    this.userStreamSelectedIndividual.put("Test Automation Design", testAutomationDesignTopic);
    String testAutomationExecutionTopic = String.valueOf(getValuefromMap(values, "Test Automation Execution")) + ";" + getValuefromMap(values, "Automation Execution") + ";" + getValuefromMap(values, "Sustenance/Maintenance");
    testAutomationExecutionTopic = replaceTwosemicolons(testAutomationExecutionTopic);
    flagAssessment("Test Automation Execution", testAutomationExecutionTopic);
    this.userStreamSelectedIndividual.put("Test Automation Execution", testAutomationExecutionTopic);
    String domainKnowledgeTopic = String.valueOf(getValuefromMap(values, "Backlog Prioritization")) + ";" + getValuefromMap(values, "Domain/Product Knowledge");
    domainKnowledgeTopic = replaceTwosemicolons(domainKnowledgeTopic);
    flagAssessment("Domain/Product Knowledge", domainKnowledgeTopic);
    this.userStreamSelectedIndividual.put("Domain/Product Knowledge", domainKnowledgeTopic);
    String defectLoggingAndReportingTopic = String.valueOf(getValuefromMap(values, "Defect Logging and Reporting")) + ";" + getValuefromMap(values, "Defect Management Reports");
    System.out.println("------------" + getValuefromMap(values, "Defect Management"));
    defectLoggingAndReportingTopic = replaceTwosemicolons(defectLoggingAndReportingTopic);
    flagAssessment("Defect Logging and Reporting", defectLoggingAndReportingTopic);
    this.userStreamSelectedIndividual.put("Defect Logging and Reporting", defectLoggingAndReportingTopic);
    String specializedTestingTopic = getValuefromMap(values, "Specialized Testing  [ API, Performance Testing, Mobile Testing ]");
    specializedTestingTopic = replaceTwosemicolons(specializedTestingTopic);
    flagAssessment("Specialized Testing  [ API, Performance Testing, Mobile Testing ]", specializedTestingTopic);
    this.userStreamSelectedIndividual.put("Specialized Testing  [ API, Performance Testing, Mobile Testing ]", specializedTestingTopic);
    String defectManagementTopic = getValuefromMap(values, "Defect Management");
    defectManagementTopic = replaceTwosemicolons(defectManagementTopic);
    flagAssessment("Defect Management", defectManagementTopic);
    this.userStreamSelectedIndividual.put("Defect Management", defectManagementTopic);
    String teamManagementTopic = String.valueOf(getValuefromMap(values, "Collaboration")) + ";" + getValuefromMap(values, "Stake holder Engagement") + ";" + getValuefromMap(values, "Vision & Goals") + ";" + getValuefromMap(values, "Team Management");
    teamManagementTopic = replaceTwosemicolons(teamManagementTopic);
    flagAssessment("Team Management", teamManagementTopic);
    this.userStreamSelectedIndividual.put("Team Management", teamManagementTopic);
    String testManagementTopic = getValuefromMap(values, "Test Management");
    testManagementTopic = replaceTwosemicolons(testManagementTopic);
    flagAssessment("Test Management", testManagementTopic);
    this.userStreamSelectedIndividual.put("Test Management", testManagementTopic);
    String projectAndDeliveryManagementTopic = String.valueOf(getValuefromMap(values, "Project and Delivery Management")) + ";" + getValuefromMap(values, "Progress monitoring") + ";" + getValuefromMap(values, "Governance");
    projectAndDeliveryManagementTopic = replaceTwosemicolons(projectAndDeliveryManagementTopic);
    flagAssessment("Project and Delivery Management", projectAndDeliveryManagementTopic);
    this.userStreamSelectedIndividual.put("Project and Delivery Management", projectAndDeliveryManagementTopic);
    String testStrategyAndPlanningTopic = String.valueOf(getValuefromMap(values, "Planning")) + ";" + getValuefromMap(values, "Test Strategy and Planning");
    testStrategyAndPlanningTopic = replaceTwosemicolons(testStrategyAndPlanningTopic);
    flagAssessment("Test Strategy and Planning", testStrategyAndPlanningTopic);
    this.userStreamSelectedIndividual.put("Test Strategy and Planning", testStrategyAndPlanningTopic);
    String testSolutionsTopic = String.valueOf(getValuefromMap(values, "Test Solutions (Presales)")) + ";" + getValuefromMap(values, "Estimation");
    testSolutionsTopic = replaceTwosemicolons(testSolutionsTopic);
    flagAssessment("Test Solutions (Presales)", testSolutionsTopic);
    this.userStreamSelectedIndividual.put("Test Solutions (Presales)", testSolutionsTopic);
    String programManagementTopic = String.valueOf(getValuefromMap(values, "Process modeling and Change Management Planning")) + ";" + getValuefromMap(values, "Project Management");
    programManagementTopic = replaceTwosemicolons(programManagementTopic);
    flagAssessment("Program Management", programManagementTopic);
    this.userStreamSelectedIndividual.put("Program Management", programManagementTopic);
    String oOProgrammingTopic = String.valueOf(getValuefromMap(values, "OO Programming")) + ";" + getValuefromMap(values, "Test scripting") + ";" + getValuefromMap(values, "Application Coding");
    oOProgrammingTopic = replaceTwosemicolons(oOProgrammingTopic);
    flagAssessment("OO Programming", oOProgrammingTopic);
    this.userStreamSelectedIndividual.put("OO Programming", oOProgrammingTopic);
    String softwareDesignTopic = String.valueOf(getValuefromMap(values, "Application Design")) + ";" + getValuefromMap(values, "Software Design");
    softwareDesignTopic = replaceTwosemicolons(softwareDesignTopic);
    flagAssessment("Software Design", softwareDesignTopic);
    this.userStreamSelectedIndividual.put("Software Design", softwareDesignTopic);
    String knowledgeofMultipleToolsTopic = String.valueOf(getValuefromMap(values, "Test Architect")) + ";" + getValuefromMap(values, "Knowledge of Multiple Tools");
    knowledgeofMultipleToolsTopic = replaceTwosemicolons(knowledgeofMultipleToolsTopic);
    flagAssessment("Knowledge of Multiple Tools (Build tools, Test frameworks)", knowledgeofMultipleToolsTopic);
    this.userStreamSelectedIndividual.put("Knowledge of Multiple Tools (Build tools, Test frameworks)", knowledgeofMultipleToolsTopic);
    String solutionDevelopmentTopic = String.valueOf(getValuefromMap(values, "Test Automation Specialist")) + ";" + getValuefromMap(values, "Solution Development");
    solutionDevelopmentTopic = replaceTwosemicolons(solutionDevelopmentTopic);
    flagAssessment("Solution Development", solutionDevelopmentTopic);
    this.userStreamSelectedIndividual.put("Solution Development", solutionDevelopmentTopic);
    String testDataManagementandDatabaseSkillsTopics = String.valueOf(getValuefromMap(values, "Database Test Engineer")) + ";" + getValuefromMap(values, "Test Data Management and Database Skills");
    testDataManagementandDatabaseSkillsTopics = replaceTwosemicolons(testDataManagementandDatabaseSkillsTopics);
    flagAssessment("Test Data Management and Database Skills", testDataManagementandDatabaseSkillsTopics);
    this.userStreamSelectedIndividual.put("Test Data Management and Database Skills", testDataManagementandDatabaseSkillsTopics);
    String levelsofAutomationTopic = getValuefromMap(values, "Levels of Automation");
    levelsofAutomationTopic = replaceTwosemicolons(levelsofAutomationTopic);
    flagAssessment("Levels of Automation", levelsofAutomationTopic);
    this.userStreamSelectedIndividual.put("Levels of Automation", levelsofAutomationTopic);
    String reportingSkillsTopic = String.valueOf(getValuefromMap(values, "Reporting Analyst")) + ";" + getValuefromMap(values, "Reporting Skills");
    reportingSkillsTopic = replaceTwosemicolons(reportingSkillsTopic);
    flagAssessment("Reporting Skills", reportingSkillsTopic);
    this.userStreamSelectedIndividual.put("Reporting Skills", reportingSkillsTopic);
    String serviceEngineeringTopic = String.valueOf(getValuefromMap(values, "API Engineering")) + ";" + getValuefromMap(values, "Service Engineering");
    serviceEngineeringTopic = replaceTwosemicolons(serviceEngineeringTopic);
    flagAssessment("Service Engineering", serviceEngineeringTopic);
    this.userStreamSelectedIndividual.put("Service Engineering", serviceEngineeringTopic);
    String performanceEngineeringTopic = String.valueOf(getValuefromMap(values, "Performance Testing")) + ";" + getValuefromMap(values, "Performance Engineering");
    performanceEngineeringTopic = replaceTwosemicolons(performanceEngineeringTopic);
    flagAssessment("Performance Engineering", performanceEngineeringTopic);
    this.userStreamSelectedIndividual.put("Performance Engineering", performanceEngineeringTopic);
    String dataScienceAndTestingPredictionsTopic = String.valueOf(getValuefromMap(values, "Data Scientist")) + ";" + getValuefromMap(values, "Data Visualization") + ";" + getValuefromMap(values, "Data Science and Testing Predictions");
    dataScienceAndTestingPredictionsTopic = replaceTwosemicolons(dataScienceAndTestingPredictionsTopic);
    flagAssessment("Data Science and Testing Predictions", dataScienceAndTestingPredictionsTopic);
    this.userStreamSelectedIndividual.put("Data Science and Testing Predictions", dataScienceAndTestingPredictionsTopic);
    String businessProcessKnowledgeTopic = getValuefromMap(values, "Process Owner");
    businessProcessKnowledgeTopic = replaceTwosemicolons(businessProcessKnowledgeTopic);
    flagAssessment("Business Process Knowledge (Client specific/Product Specific)", businessProcessKnowledgeTopic);
    this.userStreamSelectedIndividual.put("Business Process Knowledge (Client specific/Product Specific)", businessProcessKnowledgeTopic);
    String conceptualViewofDomainTopic = String.valueOf(getValuefromMap(values, "Functional / Domain Consultant")) + ";" + getValuefromMap(values, "Conceptual View of Domain");
    conceptualViewofDomainTopic = replaceTwosemicolons(conceptualViewofDomainTopic);
    flagAssessment("Conceptual View of Domain", conceptualViewofDomainTopic);
    this.userStreamSelectedIndividual.put("Conceptual View of Domain", conceptualViewofDomainTopic);
    String requirementAnalysisandInfluencingCRUMTopic = String.valueOf(getValuefromMap(values, "Requirement Analysis and Influencing SCRUM")) + ";" + getValuefromMap(values, "Business Analyst");
    requirementAnalysisandInfluencingCRUMTopic = replaceTwosemicolons(requirementAnalysisandInfluencingCRUMTopic);
    flagAssessment("Requirement Analysis and Influencing SCRUM", requirementAnalysisandInfluencingCRUMTopic);
    this.userStreamSelectedIndividual.put("Requirement Analysis and Influencing SCRUM", requirementAnalysisandInfluencingCRUMTopic);
    String modelBasedTestDesignTopic = getValuefromMap(values, "Model Based Test Design");
    modelBasedTestDesignTopic = replaceTwosemicolons(modelBasedTestDesignTopic);
    flagAssessment("Model Based Test Design", modelBasedTestDesignTopic);
    this.userStreamSelectedIndividual.put("Model Based Test Design", modelBasedTestDesignTopic);
  }
  
  public void flagAssessment(String topic, String selectedAnswers) {
    String testcaseDesignAdvanced;
    String testcaseDesignintermediate;
    String testcaseDesignBasic;
    String testcaseDesignFlag;
    String testExecutionAdvanced;
    String testExecutionintermediate;
    String testExecutionBasic;
    String testExecutionFlag;
    String STLCToolsAdvanced;
    String STLCToolsintermediate;
    String STLCToolsBasic;
    String STLCToolsFlag;
    String testAutomationDesignAdvanced;
    String testAutomationDesignintermediate;
    String testAutomationDesignBasic;
    String testAutomationDesignFlag;
    String testAutomationExecutionAdvanced;
    String testAutomationExecutionintermediate;
    String testAutomationExecutionBasic;
    String testAutomationExecutionFlag;
    String domainKnowledgeAdvanced;
    String domainKnowledgeintermediate;
    String domainKnowledgeBasic;
    String domainKnowledgeFlag;
    String defectLoggingAdvanced;
    String defectLoggingintermediate;
    String defectLoggingBasic;
    String defectLoggingFlag;
    String specializedTestingAdvanced;
    String specializedTestingintermediate;
    String specializedTestingBasic;
    String specializedTestingFlag;
    String defectManagementAdvanced;
    String defectManagementintermediate;
    String defectManagementBasic;
    String defectManagementFlag;
    String teamManagementAdvanced;
    String teamManagementintermediate;
    String teamManagementBasic;
    String teamManagementFlag;
    String testManagementAdvanced;
    String testManagementintermediate;
    String testManagementBasic;
    String testManagementFlag;
    String projectAndDeliveryManagementAdvanced;
    String projectAndDeliveryManagementintermediate;
    String projectAndDeliveryManagementBasic;
    String projectAndDeliveryManagementFlag;
    String testStrategyAndPlanningAdvanced;
    String testStrategyAndPlanningintermediate;
    String testStrategyAndPlanningBasic;
    String testStrategyAndPlanningFlag;
    String testSolutionsAdvanced;
    String testSolutionsintermediate;
    String testSolutionsBasic;
    String testSolutionsFlag;
    String programManagementAdvanced;
    String programManagementintermediate;
    String programManagementBasic;
    String programManagementFlag;
    String oOProgrammingAdvanced;
    String oOProgrammingintermediate;
    String oOProgrammingBasic;
    String oOProgrammingFlag;
    String softwareDesignAdvanced;
    String softwareDesignintermediate;
    String softwareDesignBasic;
    String softwareDesignFlag;
    String knowledgeofMultipleToolsAdvanced;
    String knowledgeofMultipleToolsintermediate;
    String knowledgeofMultipleToolsBasic;
    String knowledgeofMultipleToolsFlag;
    String solutionDevelopmentAdvanced;
    String solutionDevelopmentintermediate;
    String solutionDevelopmentBasic;
    String solutionDevelopmentFlag;
    String testDataManagementAndDatabaseSkillsAdvanced;
    String testDataManagementAndDatabaseSkillsintermediate;
    String testDataManagementAndDatabaseSkillsBasic;
    String testDataManagementAndDatabaseSkillsFlag;
    String levelsofAutomationAdvanced;
    String levelsofAutomationintermediate;
    String levelsofAutomationBasic;
    String levelsofAutomationFlag;
    String reportingSkillsAdvanced;
    String reportingSkillsintermediate;
    String reportingSkillsBasic;
    String reportingSkillsFlag;
    String serviceEngineeringAdvanced;
    String serviceEngineeringintermediate;
    String serviceEngineeringBasic;
    String serviceEngineeringFlag;
    String performanceEngineeringAdvanced;
    String performanceEngineeringintermediate;
    String performanceEngineeringBasic;
    String performanceEngineeringFlag;
    String dataScienceandTestingPredictionsAdvanced;
    String dataScienceandTestingPredictionsintermediate;
    String dataScienceandTestingPredictionsBasic;
    String dataScienceandTestingPredictionsFlag;
    String businessProcessKnowledgeAdvanced;
    String businessProcessKnowledgeintermediate;
    String businessProcessKnowledgeBasic;
    String businessProcessKnowledgeFlag;
    String conceptualViewofDomainAdvanced;
    String conceptualViewofDomainintermediate;
    String conceptualViewofDomainBasic;
    String conceptualViewofDomainFlag;
    String requirementAnalysisandInfluencingSCRUMAdvanced;
    String requirementAnalysisandInfluencingSCRUMintermediate;
    String requirementAnalysisandInfluencingSCRUMBasic;
    String requirementAnalysisandInfluencingSCRUMFlag;
    String modelBasedTestDesignAdvanced;
    String modelBasedTestDesignintermediate;
    String modelBasedTestDesignBasic;
    String modelBasedTestDesignFlag;
    String str1;
    switch ((str1 = topic).hashCode()) {
      case -2029914475:
        if (!str1.equals("Defect Logging and Reporting"))
          break; 
        defectLoggingAdvanced = "Demonstrated ability to perform Defect Triage and run Defect Triage and RCA meetings effectively with Stakeholders;Aware of the blocked test cases (Test Funnel) due to defects;Ability to analyse and report the data extracted from a Defect Tracking tool";
        defectLoggingintermediate = "Aware of the blocked test cases (Test Funnel) due to defects;Demonstrated ability to review defects independently and filter out invalid defects";
        defectLoggingBasic = "Ability to adopt a project tailored defect Tracking tool, maintains hygiene in entering details of defects with clarity and in accordance with defect life cycle and standards of the project.;Aware of the blocked test cases (Test Funnel) due to defects;Produces defect reports and knows status of defects of own work";
        defectLoggingFlag = flagChecker(selectedAnswers, defectLoggingAdvanced, defectLoggingintermediate, defectLoggingBasic, 3, 2, 3);
        this.userStreamOutputFlagsIndividual.put(topic, defectLoggingFlag);
        System.out.println("Topic - " + topic + "Selected Ans - " + selectedAnswers + "Category - " + defectLoggingFlag);
        break;
      case -1631379031:
        if (!str1.equals("Test Data Management and Database Skills"))
          break; 
        testDataManagementAndDatabaseSkillsAdvanced = "Ability to integrate with test data messaging and other sources that can be integrated through automation / RPA (e.g. reset a flag or balance on Mainframe system);Expert in SQL with database design skills";
        testDataManagementAndDatabaseSkillsintermediate = "Automation of Test Data Management and planning to provision test data;Expert in SQL with database design skills;Has strong Data Management/Database expertise combined with test data definition and specification techniques and data privacy ability";
        testDataManagementAndDatabaseSkillsBasic = "Knowledge of basic database and SQL skills with no or limited experience on Test Data Management;Expert in SQL with database design skills";
        testDataManagementAndDatabaseSkillsFlag = flagChecker(selectedAnswers, testDataManagementAndDatabaseSkillsAdvanced, testDataManagementAndDatabaseSkillsintermediate, testDataManagementAndDatabaseSkillsBasic, 2, 3, 2);
        this.userStreamOutputFlagsIndividual.put(topic, testDataManagementAndDatabaseSkillsFlag);
        System.out.println("Topic - " + topic + "Selected Ans - " + selectedAnswers + "Category - " + testDataManagementAndDatabaseSkillsFlag);
        break;
      case -1280507316:
        if (!str1.equals("Test Solutions (Presales)"))
          break; 
        testSolutionsAdvanced = "Ability to individually handle Test Solutions and responses for proposals for a single project or multiple projects;Ability to state assumptions and dependencies clearly;Ensures that assets for proposal building are managed and kept up to date;Ability to conduct effective and compelling proposal walkthroughs leading towards conversion;Provides costing, schedule and optimization solutions for proposals and supports in proposal defence";
        testSolutionsintermediate = "Ability to support development of solution for RFP response;Ability to individually handle Test Solutions and responses for proposals for a single project or multiple projects;Ability to state assumptions and dependencies clearly;Ensures that assets for proposal building are managed and kept up to date;Ability to  sequence test cycles/phases in line with customer schedule, plan effort and schedule along with scope boundaries";
        testSolutionsBasic = "Ability to state assumptions and dependencies clearly;Ensures that assets for proposal building are managed and kept up to date;Ability to provide test scoping through functional breakdown, test cases sizing and test cycles";
        testSolutionsFlag = flagChecker(selectedAnswers, testSolutionsAdvanced, testSolutionsintermediate, testSolutionsBasic, 5, 5, 3);
        this.userStreamOutputFlagsIndividual.put(topic, testSolutionsFlag);
        System.out.println("Topic - " + topic + "Selected Ans - " + selectedAnswers + "Category - " + testSolutionsFlag);
        break;
      case -1226735022:
        if (!str1.equals("Defect Management"))
          break; 
        defectManagementAdvanced = "Establish and publish Defect Metrics supported with evidence from project execution;Perform Root Cause Analysis with successful outcomes;Demonstrated ability to manage Defect Review meetings effectively;Ability to recommend and document best practices in Defect Management;Ability to automate defect metrics collection and plot various defect based process improvements;Ability to tie down high defect areas to project risk and plan for adequate risk coverage through increase of test coverage";
        defectManagementintermediate = "Establish and publish Defect Metrics supported with evidence from project execution;Perform Root Cause Analysis with successful outcomes;Demonstrated ability to manage Defect Review meetings effectively";
        defectManagementBasic = "Has knowledge of Defect Management with no or limited experience of handling Defect Review calls and collating Metrics";
        defectManagementFlag = flagChecker(selectedAnswers, defectManagementAdvanced, defectManagementintermediate, defectManagementBasic, 6, 3, 1);
        this.userStreamOutputFlagsIndividual.put(topic, defectManagementFlag);
        System.out.println("Topic - " + topic + "Selected Ans - " + selectedAnswers + "Category - " + defectManagementFlag);
        break;
      case -1199725900:
        if (!str1.equals("Reporting Skills"))
          break; 
        reportingSkillsAdvanced = "Ability to coach team on best practices on new and efficient ways for building reports - such as AI based reporting, Graphana, Kibana, MATLAB etc;Ability to create highly intuitive visual and data reports that do not require human interpretation";
        reportingSkillsintermediate = "Demonstrated ability to generate reports using HTML, Extent or any fit for purpose tools;Ability to create highly intuitive visual and data reports that do not require human interpretation";
        reportingSkillsBasic = "Trained in HTML reporting and SQL based data querying";
        reportingSkillsFlag = flagChecker(selectedAnswers, reportingSkillsAdvanced, reportingSkillsintermediate, reportingSkillsBasic, 2, 2, 1);
        this.userStreamOutputFlagsIndividual.put(topic, reportingSkillsFlag);
        System.out.println("Topic - " + topic + "Selected Ans - " + selectedAnswers + "Category - " + reportingSkillsFlag);
        break;
      case -1126265265:
        if (!str1.equals("Levels of Automation"))
          break; 
        levelsofAutomationAdvanced = "Demonstrated ability to automate GUI, API and Database layers;Demonstrated ability to automate at multiple layers outside of GUI and enable multi technology integration for the framework";
        levelsofAutomationintermediate = "Demonstrated ability to automate GUI, API and Database layers";
        levelsofAutomationBasic = "";
        levelsofAutomationFlag = flagChecker(selectedAnswers, levelsofAutomationAdvanced, levelsofAutomationintermediate, levelsofAutomationBasic, 2, 1, 0);
        this.userStreamOutputFlagsIndividual.put(topic, levelsofAutomationFlag);
        System.out.println("Topic - " + topic + "Selected Ans - " + selectedAnswers + "Category - " + levelsofAutomationFlag);
        break;
      case -1098141697:
        if (!str1.equals("Project and Delivery Management"))
          break; 
        projectAndDeliveryManagementAdvanced = "Ability to handle Resource Planning and Scheduling;Ability to manage multiple projects;Ability to upfront predict, commit and then define metrics of interest and show committed improvements on metrics and monitoring;Ability to bring in insights and industry best practices through collaborative Stakeholder Governance forums with multi-level Governance meetings";
        projectAndDeliveryManagementintermediate = "Ability to apply Frameworks and Methodologies (e.g. Agile);Ability to handle Resource Planning and Scheduling;Ability to actively address internal and external risks, issues and dependencies including where ownership exists outside the team;Ability to manage multiple projects;Ability to define and set up metrics of interest and show improvements on metrics and monitoring;Ability to define and execute Project Governance with senior internal and external Stakeholders - with regular meetings";
        projectAndDeliveryManagementBasic = "Trained in Project Management concepts;Ability to support a Lead or Manager to perform Delivery Management;Ability to handle Resource Planning and Scheduling;Ability to review and track projects to completion;Ability to actively address internal and external risks, issues and dependencies including where ownership exists outside the team;Ability to measure, report and trend metrics on project over releases/milestones;Ability to provide Project Governance reports effectively and on time";
        projectAndDeliveryManagementFlag = flagChecker(selectedAnswers, projectAndDeliveryManagementAdvanced, projectAndDeliveryManagementintermediate, projectAndDeliveryManagementBasic, 4, 6, 7);
        this.userStreamOutputFlagsIndividual.put(topic, projectAndDeliveryManagementFlag);
        System.out.println("Topic - " + topic + "Selected Ans - " + selectedAnswers + "Category - " + projectAndDeliveryManagementFlag);
        break;
      case -993141965:
        if (!str1.equals("STLC Tools"))
          break; 
        STLCToolsAdvanced = "Knowledge of tools;Shares and publishes short notes on various tools across test life cycle;Uses tools for various activities such as test case writing, test execution, test management, reporting;Knowledge of the limitation of test execution and test management tools;Competent in identifying improvements to the tool based on the analysis of the limitations;Demonstrated proficiency in bringing tool adoption into the program along with process improvements;Competent in independently scripting/adapting tool for current environment";
        STLCToolsintermediate = "Knowledge of tools;Shares and publishes short notes on various tools across test life cycle;Uses tools for various activities such as test case writing, test execution, test management, reporting;Knowledge of the limitation of test execution and test management tools";
        STLCToolsBasic = "Knowledge of tools;Uses tools for various activities such as test case writing, test execution, test management, reporting";
        STLCToolsFlag = flagChecker(selectedAnswers, STLCToolsAdvanced, STLCToolsintermediate, STLCToolsBasic, 7, 4, 2);
        this.userStreamOutputFlagsIndividual.put(topic, STLCToolsFlag);
        System.out.println("Topic - " + topic + "Selected Ans - " + selectedAnswers + "Category - " + STLCToolsFlag);
        break;
      case -860326340:
        if (!str1.equals("Requirement Analysis and Influencing SCRUM"))
          break; 
        requirementAnalysisandInfluencingSCRUMAdvanced = "Ability to trace down from requirements to implementation cycles;Ability to establish minimum acceptance criteria for requirements and development cycles and orchestrate test & development teams towards consistent velocity of team;Expert in Requirements Engineering and Requirements Impact assessment leading to Change management";
        requirementAnalysisandInfluencingSCRUMintermediate = "Ability to establish minimum acceptance criteria for requirements and development cycles and orchestrate test & development teams towards consistent velocity of team;Demonstrated ability to influence Scrum team in the areas of test estimation, test prioritization and sequencing";
        requirementAnalysisandInfluencingSCRUMBasic = "Trained in Requirements Analysis and Agile/Scrum Concepts;Ability to analyse requirements for a single project and translate to the Project Team";
        requirementAnalysisandInfluencingSCRUMFlag = flagChecker(selectedAnswers, requirementAnalysisandInfluencingSCRUMAdvanced, requirementAnalysisandInfluencingSCRUMintermediate, requirementAnalysisandInfluencingSCRUMBasic, 3, 2, 2);
        this.userStreamOutputFlagsIndividual.put(topic, requirementAnalysisandInfluencingSCRUMFlag);
        System.out.println("Topic - " + topic + "\tSelected Ans - " + selectedAnswers + "Category - " + requirementAnalysisandInfluencingSCRUMFlag);
        break;
      case -828517613:
        if (!str1.equals("Specialized Testing  [ API, Performance Testing, Mobile Testing ]"))
          break; 
        specializedTestingAdvanced = "Ability to perform testing for at least one type or multiple types of specialised testing using a tool or automate and ensure that all defects that can potentially arise in a given problem state can be adequately covered through various techniques;Advises or supports specialised testing in opportunities";
        specializedTestingintermediate = "Ability to perform testing for at least one type or multiple types of specialised testing using a tool or automate and ensure that all defects that can potentially arise in a given problem state can be adequately covered through various techniques";
        specializedTestingBasic = "Knowledge of at least one specialized testing type, and ability to define test types for that testing type";
        specializedTestingFlag = flagChecker(selectedAnswers, specializedTestingAdvanced, specializedTestingintermediate, specializedTestingBasic, 2, 1, 1);
        this.userStreamOutputFlagsIndividual.put(topic, specializedTestingFlag);
        System.out.println("Topic - " + topic + "Selected Ans - " + selectedAnswers + "Category - " + specializedTestingFlag);
        break;
      case -695328259:
        if (!str1.equals("Test Automation Execution"))
          break; 
        testAutomationExecutionAdvanced = "Ability to demonstrate productivity improvements in test execution by reducing execution time windows;Ability to schedule and run hands-free and parallel tests and troubleshoot issues related to the same;Proficiency in creating, modifying and executing parallel test scripts based on changes to run plan with parallel execution;Ability to independently manage test execution for self and others managing data, script maintenance, batch and other functional/automation dependencies";
        testAutomationExecutionintermediate = "Ability to schedule and run hands-free and parallel tests and troubleshoot issues related to the same;Proficiency in modifying and executing test scripts based on changes to run plan;Ability to set up automation execution and ability to collect results as well as evidences and report them effectively";
        testAutomationExecutionBasic = "Ability to launch and execute automation test cases and prepare and feed test data for the same;Proficiency in using the run plan and executing test script supported with evidence from projects;Demonstrated ability to make minor maintenance changes to the automation cases and execute based on the test progress and test results";
        testAutomationExecutionFlag = flagChecker(selectedAnswers, testAutomationExecutionAdvanced, testAutomationExecutionintermediate, testAutomationExecutionBasic, 4, 3, 3);
        this.userStreamOutputFlagsIndividual.put(topic, testAutomationExecutionFlag);
        System.out.println("Topic - " + topic + "Selected Ans - " + selectedAnswers + "Category - " + testAutomationExecutionFlag);
        break;
      case -632486636:
        if (!str1.equals("Solution Development"))
          break; 
        solutionDevelopmentAdvanced = "Ability to implement best design/coding standards/practices for Solution Development and ability to coach other team members;Ability to debug all failures independently without any help;Ability to conceptualise solutions to support custom tools or hybrid tools as well as to support Release Management";
        solutionDevelopmentintermediate = "Contributes individually to development of Automation Framework;Ability to debug all failures independently without any help";
        solutionDevelopmentBasic = "Ability to make limited contribution to Framework Development with support";
        solutionDevelopmentFlag = flagChecker(selectedAnswers, solutionDevelopmentAdvanced, solutionDevelopmentintermediate, solutionDevelopmentBasic, 3, 2, 1);
        this.userStreamOutputFlagsIndividual.put(topic, solutionDevelopmentFlag);
        System.out.println("Topic - " + topic + "Selected Ans - " + selectedAnswers + "Category - " + solutionDevelopmentFlag);
        break;
      case -503779183:
        if (!str1.equals("Test Management"))
          break; 
        testManagementAdvanced = "Test Planning, Estimation, Monitoring and Control;Ability to perform Test Management for a small project or independently for a large single project or multiple projects;Defines a run plan given a set of tests and a go-live target;Ability to share and implement best practices in Test Management;Ability to coach and set up test dashboards and bring insights to stakeholders at CXO levels";
        testManagementintermediate = "Test Planning, Estimation, Monitoring and Control;Ability to perform Test Management for a small project or independently for a large single project or multiple projects;Defines a run plan given a set of tests and a go-live target";
        testManagementBasic = "";
        testManagementFlag = flagChecker(selectedAnswers, testManagementAdvanced, testManagementintermediate, testManagementBasic, 5, 3, 0);
        this.userStreamOutputFlagsIndividual.put(topic, testManagementFlag);
        System.out.println("Topic - " + topic + "Selected Ans - " + selectedAnswers + "Category - " + testManagementFlag);
        break;
      case -494972662:
        if (!str1.equals("Test Execution"))
          break; 
        testExecutionAdvanced = "Ability to manage complex execution of test cases based on dependencies and liaise with automation/developers/BA etc. to expedite test case execution;Perform extensive exploratory Adhoc/random testing based on experience, help peers and also implement execution optimizations;Is compliant with test evidence documentation requirements for self & audit evidences for peers or other team members and provide root cause analysis;Demonstrated ability to drive process improvements in test case execution by spotting and improving bottleneck activities";
        testExecutionintermediate = "Execute end to end testing of the complete work flow;Perform extensive exploratory Adhoc/random testing based on experience and also help peers;Is compliant with test evidence documentation requirements for self & audit evidences for peers or other team members";
        testExecutionBasic = "Execute testing based on Test Case specification;Perform limited testing based on experience;Is compliant with test evidence documentation requirements";
        testExecutionFlag = flagChecker(selectedAnswers, testExecutionAdvanced, testExecutionintermediate, testExecutionBasic, 4, 3, 3);
        this.userStreamOutputFlagsIndividual.put(topic, testExecutionFlag);
        System.out.println("Topic - " + topic + "Selected Ans - " + selectedAnswers + "Category - " + testExecutionFlag);
        break;
      case -473567623:
        if (!str1.equals("OO Programming"))
          break; 
        oOProgrammingAdvanced = "Uses code configuration management and tools (git/stash/vss etc.) in the most efficient way possible - No code on personal devices;Ability to implement best practices in programming;Ability to perform Code Optimisation and demonstrate through code metrics;Demonstrated ability to code for complex logic/algorithms";
        oOProgrammingintermediate = "Uses code configuration management and tools (git/stash/vss etc.) in the most efficient way possible - No code on personal devices;Sets up standards and accelerators for development;Demonstrated ability to perform Code Reviews;Ability to implement best practices in programming;Ability to design and write code independently with code metrics and coding practices intact;Ability to build code aligned to OO concepts and layered to support business and data layers";
        oOProgrammingBasic = "Trained or has knowledge in OO Programming and Design;Uses code configuration management and tools (git/stash/vss etc.) in the most efficient way possible - No code on personal devices;Understands Test Code and ability to contribute with help of Low design with coding practices intact;Creates and understands class diagrams and implements the same in code";
        oOProgrammingFlag = flagChecker(selectedAnswers, oOProgrammingAdvanced, oOProgrammingintermediate, oOProgrammingBasic, 4, 6, 4);
        this.userStreamOutputFlagsIndividual.put(topic, oOProgrammingFlag);
        System.out.println("Topic - " + topic + "Selected Ans - " + selectedAnswers + "Category - " + oOProgrammingFlag);
        break;
      case -421071386:
        if (!str1.equals("Team Management"))
          break; 
        teamManagementAdvanced = "Proactively and reactively identifies problems or issues in team dynamics and rectifies them;Ability to engage in varying types of feedback, choosing the right type at the appropriate time and ensuring the discussion and decision sticks;Define team vision and goals and guide team using quantitative techniques on the progress towards vision / goal;Ability to bring people together to form a motivated team;Ability to help create the right environment for a team to work in;Recognizes and deals with issues and conflicts;Structure the team in an optimal way depending on the situation;Ability to identify issues through health-checks with the team and remedy the same;Ability to accelerate Team Development";
        teamManagementintermediate = "Understands importance of Team Dynamics. Creates a collaborative culture and empowers Delivery Teams;Ability to work on constructive actions from feedback in a manner that positively impact Stakeholders;Define team vision and goals and motivate team to progress towards the same;Ability to bring people together to form a motivated team;Ability to help create the right environment for a team to work in;Recognizes and deals with issues and conflicts;Structure the team in an optimal way depending on the situation";
        teamManagementBasic = "Understands Team dynamics and works harmoniously with any team;Gathers feedback from team and Stakeholders at regular intervals and works on action items;Aware of team vision and motivates team to progress towards the same";
        teamManagementFlag = flagChecker(selectedAnswers, teamManagementAdvanced, teamManagementintermediate, teamManagementBasic, 9, 7, 3);
        this.userStreamOutputFlagsIndividual.put(topic, teamManagementFlag);
        System.out.println("Topic - " + topic + "Selected Ans - " + selectedAnswers + "Category - " + teamManagementFlag);
        break;
      case 342875685:
        if (!str1.equals("Test Strategy and Planning"))
          break; 
        testStrategyAndPlanningAdvanced = "Coaches other program (non-testing) teams as the central point of expertise;Ability to lead a continual planning process in a very complex environment";
        testStrategyAndPlanningintermediate = "Recommend multiple strategies based on needs and complexities of the project;Understands the environment and is able to prioritise the most important or highest value tasks;Ability to manage complex internal and external dependencies;Ability to eliminate blockers that affect the plan and develop a plan for difficult situations";
        testStrategyAndPlanningBasic = "Understand Test Strategy and define the Test Strategy independently and plan the testing for a project";
        testStrategyAndPlanningFlag = flagChecker(selectedAnswers, testStrategyAndPlanningAdvanced, testStrategyAndPlanningintermediate, testStrategyAndPlanningBasic, 2, 4, 1);
        this.userStreamOutputFlagsIndividual.put(topic, testStrategyAndPlanningFlag);
        System.out.println("Topic - " + topic + "Selected Ans - " + selectedAnswers + "Category - " + testStrategyAndPlanningFlag);
        break;
      case 504884601:
        if (!str1.equals("Data Science and Testing Predictions"))
          break; 
        dataScienceandTestingPredictionsAdvanced = "Ability to create or tweak predictive analytics models (test and improve them using the lifecycle data to arrive at testing predictions);Ability to create visuals to derive interesting insights and formulate various hypothesis based data testing;Coaches team to sustain and scale this capability";
        dataScienceandTestingPredictionsintermediate = "Ability to leverage Data Analytics, Machine Learning and build an adaptive framework dynamically to optimizes these assets based on lifecycle data such as: build failure trends, defect detection and escape patterns,causes of defects;Ability to create various engineering dashboards from data that is sourced real time or in batch mode";
        dataScienceandTestingPredictionsBasic = "Trained in data/predictive analysis with limited or no practical experience;Ability to pull out data for various measures in a hands-free and repeatability manner and present visuals";
        dataScienceandTestingPredictionsFlag = flagChecker(selectedAnswers, dataScienceandTestingPredictionsAdvanced, dataScienceandTestingPredictionsintermediate, dataScienceandTestingPredictionsBasic, 3, 2, 2);
        this.userStreamOutputFlagsIndividual.put(topic, dataScienceandTestingPredictionsFlag);
        System.out.println("Topic - " + topic + "Selected Ans - " + selectedAnswers + "Category - " + dataScienceandTestingPredictionsFlag);
        break;
      case 616881929:
        if (!str1.equals("Business Process Knowledge (Client specific/Product Specific)"))
          break; 
        businessProcessKnowledgeAdvanced = "Provides expertise for most of the operations of an entity (e.g. Bank, Insurance) integrated with data needs and story proofing/user story analysis etc";
        businessProcessKnowledgeintermediate = "Knowledge of complete operations of a particular division (e.g. Cards, Wealth Management, Corporate Banking, Retail Banking) and can apply the knowledge for testing";
        businessProcessKnowledgeBasic = "Knowledge of workflows limited to one function in the domain (e.g. Retail/Cards)";
        businessProcessKnowledgeFlag = flagChecker(selectedAnswers, businessProcessKnowledgeAdvanced, businessProcessKnowledgeintermediate, businessProcessKnowledgeBasic, 1, 1, 1);
        this.userStreamOutputFlagsIndividual.put(topic, businessProcessKnowledgeFlag);
        System.out.println("Topic - " + topic + "Selected Ans - " + selectedAnswers + "Category - " + businessProcessKnowledgeFlag);
        break;
      case 679697064:
        if (!str1.equals("Model Based Test Design"))
          break; 
        modelBasedTestDesignAdvanced = "Ability to create models for different levels of requirements (business process level, feature or story level) linked to each other;Ability to describe models at different levels of abstraction with high level flows;Practitioner for Test basis and Consultant for various automated test design models such as API, services etc;Expert in designing test cases through test basis model and re-use strategies;Ability to analyse, validate and manage requirements for a single project and provide assistance to the Project Team";
        modelBasedTestDesignintermediate = "Ability to analyse and specify requirements in a structured manner and model the behavioural flow using the model based technique or tool;Ability to apply and drive Model Based test design;Ability to prepare Test Basis, Transaction Matrix and Scenario Matrix with help";
        modelBasedTestDesignBasic = "Knowledge of model based testing and development of requirements in the form of models (e.g. Behavioural model, Test Basis etc.);Knowledge on Test Basis Methodology and design test cases from the Scenario Matrix and Transaction Matrix";
        modelBasedTestDesignFlag = flagChecker(selectedAnswers, modelBasedTestDesignAdvanced, modelBasedTestDesignintermediate, modelBasedTestDesignBasic, 5, 3, 2);
        this.userStreamOutputFlagsIndividual.put(topic, modelBasedTestDesignFlag);
        System.out.println("Topic - " + topic + "Selected Ans - " + selectedAnswers + "Category - " + modelBasedTestDesignFlag);
        break;
      case 746337856:
        if (!str1.equals("Test Case Design"))
          break; 
        testcaseDesignAdvanced = "Design quality test cases with help from the available specification, user documentation, interviews/discussions, functional flows & operational flows;Demonstrated competency in reviewing test cases of peers and ensuring consistency and quality of test cases;Demonstrated proficiency in establishing test coverage and traceability;Competent to tag and mark test cases for various suites and plan for test data and run scheduling for test cases;Proficiency in subsetting test cases based on regression and impact";
        testcaseDesignintermediate = "Design quality test cases with help from the available specification, user documentation, interviews/discussions, functional flows & operational flows;Demonstrated competency in reviewing test cases of peers and ensuring consistency and quality of test cases;Demonstrated proficiency in establishing test coverage and traceability";
        testcaseDesignBasic = "Design quality test cases with help from the available specification, user documentation, interviews/discussions, functional flows & operational flows";
        testcaseDesignFlag = flagChecker(selectedAnswers, testcaseDesignAdvanced, testcaseDesignintermediate, testcaseDesignBasic, 5, 3, 1);
        this.userStreamOutputFlagsIndividual.put(topic, testcaseDesignFlag);
        System.out.println("Topic - " + topic + "  Selected Ans - " + selectedAnswers + " Category - " + testcaseDesignFlag);
        break;
      case 762923496:
        if (!str1.equals("Service Engineering"))
          break; 
        serviceEngineeringAdvanced = "Ability to apply Model based, coverage led and automated test case design for services and hands-free execution with fully sourced synthetic and provisioned test data;Ability to contribute to service level tests - through automated test design from specifications;Ability to integrate hands free Test Automation in CI/CD pipeline;Builds automation framework from ground up, integrating with CI tools and Test Case Management tools";
        serviceEngineeringintermediate = "Knowledge of service/micro-service/API and Application Architecture, Test infrastructures, configurations and environments;Ability to contribute to service level tests - through automated test design from specifications;Ability to integrate hands free Test Automation in CI/CD pipeline";
        serviceEngineeringBasic = "Knowledge of  SWAGGER/ WSDL, XML concepts and file passing (JSON/XML );Knowledge of Service Automation concepts";
        serviceEngineeringFlag = flagChecker(selectedAnswers, serviceEngineeringAdvanced, serviceEngineeringintermediate, serviceEngineeringBasic, 4, 3, 2);
        this.userStreamOutputFlagsIndividual.put(topic, serviceEngineeringFlag);
        System.out.println("Topic - " + topic + "Selected Ans - " + selectedAnswers + "Category - " + serviceEngineeringFlag);
        break;
      case 837668767:
        if (!str1.equals("Program Management"))
          break; 
        programManagementAdvanced = "Ability to drive a change control board to approve and percolate changes through the appropriate processes. Control changes at customer level;Ability to apply superior analytical skills;Good knowledge of techniques for planning, monitoring and controlling programmes;Good knowledge of budgeting and resource allocation procedures;Ability to continuously focus on Risk Management and include contingency & mitigation planning;Ability to focus on Governance to achieve desired results in the program;Ability to think Big Picture and set the vision;Has sufficient seniority and credibility to advise project teams on their projects in relation to the program;Ability to build effective relationships with Stakeholders;Ability to perform Resource forecasting and fulfilment";
        programManagementintermediate = "Ability to setup uniform processes to monitor and handle change. Ensure no scope creep and monetize changes;Ability to apply superior analytical skills;Good knowledge of techniques for planning, monitoring and controlling programmes;Good knowledge of budgeting and resource allocation procedures;Ability to continuously focus on Risk Management and include contingency & mitigation planning;Ability to focus on Governance to achieve desired results in the program;Ability to think Big Picture and set the vision";
        programManagementBasic = "Ability to impose a common set of processes to report progress, change and uncertainty;Ability to adjust plans based on performance changes;Ensures Project Managers use the correct Testing life-cycle for the characteristics of the products they have to test;Good knowledge of techniques for planning, monitoring and controlling programmes;Good knowledge of budgeting and resource allocation procedures";
        programManagementFlag = flagChecker(selectedAnswers, programManagementAdvanced, programManagementintermediate, programManagementBasic, 10, 7, 5);
        this.userStreamOutputFlagsIndividual.put(topic, programManagementFlag);
        System.out.println("Topic - " + topic + "Selected Ans - " + selectedAnswers + "Category - " + programManagementFlag);
        break;
      case 1228402146:
        if (!str1.equals("Domain/Product Knowledge"))
          break; 
        domainKnowledgeAdvanced = "Ability to direct test and functional prioritization in scrums and / or test strategy on the basis of complexities and dependencies of each area within the product / domain of expertise;Ability to recast knowledge into forms conducive for model based design - such as domain taxonomies, function/sub function listings etc.";
        domainKnowledgeintermediate = "Is seen as a practitioner in the domain / product area and can work independently with no guidance, leading teams towards optimized test design and coverage independently";
        domainKnowledgeBasic = "Fully trained on a Domain/Product area and can articulate concepts with clarity";
        domainKnowledgeFlag = flagChecker(selectedAnswers, domainKnowledgeAdvanced, domainKnowledgeintermediate, domainKnowledgeBasic, 2, 1, 1);
        this.userStreamOutputFlagsIndividual.put(topic, domainKnowledgeFlag);
        System.out.println("Topic - " + topic + "Selected Ans - " + selectedAnswers + "Category - " + domainKnowledgeFlag);
        break;
      case 1268620537:
        if (!str1.equals("Test Automation Design"))
          break; 
        testAutomationDesignAdvanced = "Ability to design a new automation framework and / or significantly upgrade an existing framework;Ability to adopt TDD, BDD, methods and integrate data, manual + automated testing in test case design;Ability to package test automation suites for execution;Ability to apply best practices such as audit trail, data independence, multithreading and reporting;Ability to create hands-free test execution methods that can be handed over for execution and maintenance to manual or entry level automation Test Engineers;Uses industry standard design standards to bring in overall productivity improvement, test case execution windows;Ability to create modular test case designs that can be fully re-used and integrated with a domain model;Demonstrated ability to write optimized scripts and having knowledge to integrate with other tools";
        testAutomationDesignintermediate = "Demonstrated ability to use Test Automation Framework for writing new test cases and minor upgrades to the framework to support new features;Ability to adopt TDD, BDD, methods and integrate data, manual + automated testing in test case design;Ability to package test automation suites for execution;Ability to apply best practices such as audit trail, data independence, multithreading and reporting;Uses industry standard design standards to bring in overall productivity improvement, test case execution windows;Demonstrated ability to write optimized scripts and having knowledge to integrate with other tools";
        testAutomationDesignBasic = "Familiar with using the Test Automation Framework for writing test cases;Demonstrated ability to independently maintain test cases for minor changes";
        testAutomationDesignFlag = flagChecker(selectedAnswers, testAutomationDesignAdvanced, testAutomationDesignintermediate, testAutomationDesignBasic, 8, 6, 2);
        this.userStreamOutputFlagsIndividual.put(topic, testAutomationDesignFlag);
        System.out.println("Topic - " + topic + "Selected Ans - " + selectedAnswers + "Category - " + testAutomationDesignFlag);
        break;
      case 1376416954:
        if (!str1.equals("Conceptual View of Domain"))
          break; 
        conceptualViewofDomainAdvanced = "Ability to provide a functional/ domain taxonomy that will enable automation teams to build out automation units from business concepts and flows;Ability to segregate scenarios and isolate data needs, PIIs and key actors in a Domain;Ability to provide consulting on data model needs for the domain to tie in to Test data planning and provisioning for test cases";
        conceptualViewofDomainintermediate = "Ability to represent the domain as interacting sequence of activities and actors, such as a swim lane, conceptual map, function matrix etc.;Ability to segregate scenarios and isolate data needs, PIIs and key actors in a Domain;Ability to guide prioritization of testing within a domain";
        conceptualViewofDomainBasic = "Understands the domain as interacting sequence of activities and actors, such as a swim lane, conceptual map, function matrix etc;Ability to guide prioritization of testing within a domain";
        conceptualViewofDomainFlag = flagChecker(selectedAnswers, conceptualViewofDomainAdvanced, conceptualViewofDomainintermediate, conceptualViewofDomainBasic, 3, 3, 2);
        this.userStreamOutputFlagsIndividual.put(topic, conceptualViewofDomainFlag);
        System.out.println("Topic - " + topic + "Selected Ans - " + selectedAnswers + "Category - " + conceptualViewofDomainFlag);
        break;
      case 1529787817:
        if (!str1.equals("Knowledge of Multiple Tools (Build tools, Test frameworks)"))
          break; 
        knowledgeofMultipleToolsAdvanced = "Ability to add Plugins/Add Ons to the tools to overcome limitations;Ability to optimise the use of tools;Ability to conceptualize an integrated platform using the tools";
        knowledgeofMultipleToolsintermediate = "Ability to understand the limitations of the tools";
        knowledgeofMultipleToolsBasic = "Has knowledge of what tools are used for Frameworks, Build and Scripting";
        knowledgeofMultipleToolsFlag = flagChecker(selectedAnswers, knowledgeofMultipleToolsAdvanced, knowledgeofMultipleToolsintermediate, knowledgeofMultipleToolsBasic, 3, 1, 1);
        this.userStreamOutputFlagsIndividual.put(topic, knowledgeofMultipleToolsFlag);
        System.out.println("Topic - " + topic + "Selected Ans - " + selectedAnswers + "Category - " + knowledgeofMultipleToolsFlag);
        break;
      case 1963627095:
        if (!str1.equals("Software Design"))
          break; 
        softwareDesignAdvanced = "Ability to conceptualise and design using design patterns and optimise the design;Ability to create portability software that can be called in an API - with no dependency on OS, Text files, human intervention etc;Ability to create and package re-usability software assets for more than one problem / program and promote such re-use;Coaches team on best practices for software design";
        softwareDesignintermediate = "Ability to create DB designs, Class diagrams and other UML designs to represent flow of information;Ability to create and package re-usability software assets for more than one problem / program and promote such re-use;Coaches team on best practices for software design";
        softwareDesignBasic = "Ability to understand UML concepts, design diagrams and code as per design specifications";
        softwareDesignFlag = flagChecker(selectedAnswers, softwareDesignAdvanced, softwareDesignintermediate, softwareDesignBasic, 4, 3, 1);
        this.userStreamOutputFlagsIndividual.put(topic, softwareDesignFlag);
        System.out.println("Topic - " + topic + "Selected Ans - " + selectedAnswers + "Category - " + softwareDesignFlag);
        break;
      case 2103460579:
        if (!str1.equals("Performance Engineering"))
          break; 
        performanceEngineeringAdvanced = "Expert in performance engineering;Ability to gain and contribute knowledge on new performance engineering skills using new tools and technologies";
        performanceEngineeringintermediate = "Ability to analyse performance test result to identify bottlenecks (use advanced SQL skills);Ability to identify transactions and workflows and calculate workload TPS goals and rates;Ability to use and customize monitoring tools helps in identifying the server side bottlenecks in performance testing";
        performanceEngineeringBasic = "Knowledge of Performance test and Engineering and ability to apply Scripting skills to build load tests";
        performanceEngineeringFlag = flagChecker(selectedAnswers, performanceEngineeringAdvanced, performanceEngineeringintermediate, performanceEngineeringBasic, 2, 3, 1);
        this.userStreamOutputFlagsIndividual.put(topic, performanceEngineeringFlag);
        System.out.println("Topic - " + topic + "Selected Ans - " + selectedAnswers + "Category - " + performanceEngineeringFlag);
        break;
    } 
  }
  
  public void createAndWriteValuesInExcel(Map<String, Map<String, String>> result, String sheetNameDetails, String location) {
    this.excelWorkBook = new XSSFWorkbook();
    this.excelSheet = createsheet(this.excelWorkBook, sheetNameDetails);
    Set<String> headervalues = new LinkedHashSet<>();
    headervalues = this.userStreamOutputFlagsIndividual.keySet();
    XSSFRow row0 = this.excelSheet.createRow(0);
    writeExcelHeaderValues(this.excelWorkBook, row0, headervalues);
    writeExcelvalues(result, this.excelSheet);
    writeinOutputStream(this.outputStream, this.excelWorkBook, location);
  }
  
  public void updatingValuesInExistingExcel(Map<String, Map<String, String>> result, String sheetNameDetails, String location) {
    this.inputStream = readInputStream(location);
    this.excelWorkBook = loadExcelValuefromInputStream(this.inputStream);
    this.excelSheet = createsheet(this.excelWorkBook, sheetNameDetails);
    Set<String> headervalues = new LinkedHashSet<>();
    headervalues = this.userStreamSelectedIndividual.keySet();
    XSSFRow row0 = this.excelSheet.createRow(0);
    writeExcelHeaderValues(this.excelWorkBook, row0, headervalues);
    writeExcelvalues(result, this.excelSheet);
    writeinOutputStream(this.outputStream, this.excelWorkBook, location);
  }
  
  public void createIndividualRecords(Map<String, Map<String, String>> result, String sheetNameDetails, String location) {
    this.inputStream = readInputStream(location);
    this.excelWorkBook = loadExcelValuefromInputStream(this.inputStream);
    this.excelSheet = createsheet(this.excelWorkBook, sheetNameDetails);
    Set<String> headervalues = new LinkedHashSet<>();
    headervalues = this.userStreamSelectedIndividual.keySet();
    XSSFRow row0 = this.excelSheet.createRow(0);
    writeExcelHeaderValues(this.excelWorkBook, row0, headervalues);
    writeExcelvalues(result, this.excelSheet);
    writeinOutputStream(this.outputStream, this.excelWorkBook, location);
  }
  
  public XSSFWorkbook loadExcelValuefromInputStream(FileInputStream inputStream) {
    XSSFWorkbook excelworkbook = null;
    try {
      excelworkbook = new XSSFWorkbook(inputStream);
    } catch (IOException e) {
      System.out.println("Issue while loading existing excel sheet " + System.getProperty("user.dir"));
      popupMessage("Issue while loading existing excel sheet " + System.getProperty("user.dir"));
      e.printStackTrace();
      System.exit(1);
    } 
    return excelworkbook;
  }
  
  public FileInputStream readInputStream(String location) {
    FileInputStream inputstream = null;
    try {
      inputstream = new FileInputStream(new File(location));
    } catch (FileNotFoundException e) {
      System.out.println("output excel not available in the location,make sure output excel closed already " + System.getProperty("user.dir"));
      popupMessage("output excel not available in the location,make sure output excel closed already " + System.getProperty("user.dir"));
      e.printStackTrace();
      System.exit(1);
    } 
    return inputstream;
  }
  
  public void writeinOutputStream(FileOutputStream output, XSSFWorkbook book, String location) {
    try {
      output = new FileOutputStream(new File(location));
      book.write(output);
      output.close();
    } catch (FileNotFoundException e) {
      System.out.println("output excel not available in the location,make sure output excel closed already " + System.getProperty("user.dir"));
      popupMessage("output excel not available in the location,make sure output excel closed already " + System.getProperty("user.dir"));
      e.printStackTrace();
      System.exit(1);
    } catch (IOException e) {
      System.out.println("Issue while writing values in output excel " + System.getProperty("user.dir"));
      popupMessage("Issue while writing values in output excel " + System.getProperty("user.dir"));
      e.printStackTrace();
      System.exit(1);
    } 
  }
  
  public XSSFSheet createsheet(XSSFWorkbook workbook, String sheetName) {
    XSSFSheet excelsheetName = workbook.createSheet(sheetName);
    return excelsheetName;
  }
  
  public void writeExcelHeaderValues(XSSFWorkbook workbook, XSSFRow row, Set<String> headervalues) {
    int headercellNumber = 0;
    XSSFCellStyle style = workbook.createCellStyle();
    XSSFFont font = workbook.createFont();
    font.setBold(true);
    style.setFont((Font)font);
    for (String headerVal : headervalues) {
      XSSFCell cell = row.createCell(headercellNumber);
      cell.setCellValue(headerVal);
      cell.setCellStyle((CellStyle)style);
      headercellNumber++;
    } 
  }
  
  public void writeExcelvalues(Map<String, Map<String, String>> result, XSSFSheet sheet) {
    int index = 1;
    int detailsCellNumber = 0;
    for (Map.Entry<String, Map<String, String>> enterset : result.entrySet()) {
      Map<String, String> tempExeMap = new LinkedHashMap<>();
      tempExeMap = enterset.getValue();
      XSSFRow row1 = sheet.createRow(index);
      for (Map.Entry<String, String> individual : tempExeMap.entrySet())
        row1.createCell(detailsCellNumber++).setCellValue(individual.getValue()); 
      detailsCellNumber = 0;
      index++;
    } 
  }
  
  public void individualRecord(Map<String, Map<String, String>> allrecords) {
    for (Map.Entry<String, Map<String, String>> allrec : allrecords.entrySet()) {
      FileInputStream inputstreamIndividual = null;
      FileOutputStream outputstreamIndividual = null;
      XSSFWorkbook excelWorkBookIndividual = null;
      Map<String, String> individualRecord = new LinkedHashMap<>();
      individualRecord = allrec.getValue();
      String empId = individualRecord.get("Nominee - Employee ID");
      String employeeName = individualRecord.get("Nominee - Employee Name");
      String reportingManager = individualRecord.get("Reporting Manager Name");
      String propdesignation = individualRecord.get("Current Designation - Proposed Designation");
      String[] currentAndProposeddes = propdesignation.split("-");
      String currentDesignation = currentAndProposeddes[0];
      String targetDesignation = currentAndProposeddes[1];
      String excelpath = pickupInputSheet(propdesignation);
      if (!excelpath.equalsIgnoreCase("File Not Available")) {
        inputstreamIndividual = readInputStream(excelpath);
      } else {
        popupMessage("Excel sheet not available in the path" + System.getProperty("user.dir") + "//destination");
        System.exit(0);
      } 
      excelWorkBookIndividual = loadExcelValuefromInputStream(inputstreamIndividual);
      XSSFSheet sheet = excelWorkBookIndividual.getSheetAt(1);
      int enamerow = findRow((Sheet)sheet, "Name");
      int curDes = findRownum((Sheet)sheet, "Current Designation");
      int targetdes = findRownum((Sheet)sheet, "Target Designation");
      int repManager = findRow((Sheet)sheet, "Reporting Manager");
      sheet.getRow(enamerow).getCell(5).setCellValue(employeeName);
      sheet.getRow(curDes).getCell(5).setCellValue(currentDesignation);
      sheet.getRow(targetdes).getCell(5).setCellValue(targetDesignation);
      sheet.getRow(repManager).getCell(5).setCellValue(reportingManager);
      for (Map.Entry<String, String> indivual : individualRecord.entrySet()) {
        String key = indivual.getKey();
        String value = indivual.getValue();
        int rownumForupdate = findRow((Sheet)sheet, key);
        if (rownumForupdate != 0) {
          String fieldName = sheet.getRow(rownumForupdate).getCell(1).toString();
          if (!fieldName.isEmpty() && fieldName.matches("[0-9].*"))
            sheet.getRow(rownumForupdate).getCell(7).setCellValue(value); 
        } 
      } 
      String foldername = "IndividualRecords";
      File file = new File(FileLocation.individualRecordsFolder + "\\" + foldername);
      deletedir(file);
      file.mkdir();
      String folderpath = file.getAbsolutePath();
      folderpath = String.valueOf(folderpath) + "\\" + empId + "-" + employeeName + ".xlsx";
      System.out.println("Individual Record folderpath - " + folderpath);
      writeinOutputStream(outputstreamIndividual, excelWorkBookIndividual, folderpath);
    } 
  }
  
  public void deletedir(File file) {
    try {
      FileUtils.deleteDirectory(file);
    } catch (IOException e) {
      System.out.println("Issue while reading folder");
      e.printStackTrace();
    } 
  }
  
  public int findRow(Sheet sheet, String cellContent) {
    for (Row row : sheet) {
      for (Cell cell : row) {
        String str = new String();
        str = cell.getRichStringCellValue().getString().trim();
        str.matches("(.*)" + cellContent);
        if (str.matches("(.*)" + cellContent))
          return row.getRowNum(); 
      } 
    } 
    return 0;
  }
  
  public int findRownum(Sheet sheet, String cellContent) {
    for (Row row : sheet) {
      for (Cell cell : row) {
        if (cell.getRichStringCellValue().getString().trim().contains(cellContent))
          return row.getRowNum(); 
      } 
    } 
    return 0;
  }
  
  public String pickupInputSheet(String filename) {
    String filePath = FileLocation.DesignationFolder;
    File inputFile = new File(filePath);
    if (inputFile.exists()) {
      File[] files = inputFile.listFiles();
      byte b;
      int i;
      File[] arrayOfFile1;
      for (i = (arrayOfFile1 = files).length, b = 0; b < i; ) {
        File file = arrayOfFile1[b];
        if (file.getName().contains(filename))
          return file.getAbsolutePath(); 
        b++;
      } 
    } 
    return "File Not Available";
  }
  
  public void popupMessage(String message) {
    JFrame frame = new JFrame();
    JOptionPane.showMessageDialog(frame, message);
  }
  
  public static void main(String[] args) throws Exception {
    Streamchange excel = new Streamchange();
    excel.updateExcelValuesInMap(excel.userStramValues, FileLocation.inputexcel);
    for (Map.Entry<String, Map<String, String>> mapValues : excel.userStramValues.entrySet()) {
      String name = String.valueOf(((Map)mapValues.getValue()).get("Nominee - Employee Name")) + (String)((Map)mapValues.getValue()).get("Nominee - Employee ID");
      excel.verifyFlags(mapValues.getValue());
      excel.userStreamOutputValuesAll.put(name, excel.userStreamOutputFlagsIndividual);
      excel.userStreamSelectedAll.put(name, excel.userStreamSelectedIndividual);
    } 
    excel.individualRecord(excel.userStreamOutputValuesAll);
    System.out.println(excel.userStreamOutputValuesAll.size());
    excel.createAndWriteValuesInExcel(excel.userStreamOutputValuesAll, "StreamChange", FileLocation.outputexcel);
    excel.updatingValuesInExistingExcel(excel.userStreamSelectedAll, "SelectedValues", FileLocation.outputexcel);
    System.out.println(excel.userStreamOutputValuesAll);
  
    excel.popupMessage("Assessment completed , Please check output excel sheet");
    System.exit(0);
  }
}
