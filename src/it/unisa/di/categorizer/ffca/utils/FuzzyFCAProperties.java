package it.unisa.di.categorizer.ffca.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

public class FuzzyFCAProperties {
	
	public enum PropertiesNames{ 
		pNameLog4j,
		pNameThreshold,
		pNameInputFile,
		pNameOutputFile,
		pNameOutputFileLabeling,
		pNameOverwrite,
		pNameSupp,
		pNameConf,
		pNameDiff,
		pNameCEX,
		pNameRulesTxtFileName,
		pNameRulesXmlFileName
	}

	/**
	 * @uml.property  name="properties"
	 */
	private Properties properties;
/***
 * fuzzy.fca.log4j.properties.file - file name of the log4j setting properties;
 * fuzzy.fca.threshold - pruning threshold;
 * fuzzy.fca.input.format - the format of the input file;
 * fuzzy.fca.input.file - the file name of the input ;
 * fuzzy.fca.output.file - the file name of the output ;
 * fuzzy.fca.output.file.labeling - the file name of the outputLabeling ;
 * fuzzy.fca.output.format - the format of the output file;
 * fuzzy.fca.overwrite - true, enable overwriting of the generated data file; false, disable overwriting;
 * fuzzy.fca.traversal - Enumeration of supported traversals for concept and edge iterators;
 * fuzzy.fca.rtype - Representation of a context;
 * fuzzy.fca.ltypee - Representation of a concept lattice;
 * fuzzy.fca.supp - the minimal support, i.e.&nbsp;the minimal number of objects contained in the lower neighbor.
 * fuzzy.fca.conf - the minimal confidence, i.e.&nbsp;the minimal fraction l/u, where l is the number of objects in the lower neighbor and u is the number of objects in the upper neighbor. Must be a value between 0 and 1.
 * fuzzy.fca.diff - the maximal difference between the number of attributes in the lower neighbor and the number of attributes in the upper neighbor.
 * fuzzy.fca.cex.file - the file name of the output cex file;
 * fuzzy.fca.rules.txt.fileName - the file name of the rules file .txt;
 * fuzzy.fca.rules.xml.fileName - the file name of the rules file .xml;
 *  
 */
	
	private static final String pNames[] = new String[]{"fuzzy.fca.log4j.properties.file",
														"fuzzy.fca.threshold",
														"fuzzy.fca.input.file",
														"fuzzy.fca.output.file",
														"fuzzy.fca.output.file.labeling",
														"fuzzy.fca.overwrite",
														"fuzzy.fca.supp",
														"fuzzy.fca.conf",
														"fuzzy.fca.diff",
														"fuzzy.fca.cex.file",
														"fuzzy.fca.rules.txt.fileName",
														"fuzzy.fca.rules.xml.fileName"}; 

	public static String BASE_DIR = "./config/";
	public static String PREFERENCES_FILE = "FuzzyFCA_properties.xml";
	private static final String DEFAULT_FUZZY_FCA_LOG4J_PROPERTIES_FILENAME = "./log4j.properties";
	private static final String DEFAULT_FUZZY_FCA_THRESHOLD = "0.0";
//	private static final String DEFAULT_FUZZY_FCA_INPUT_FORMAT = "xml";
	private static final String DEFAULT_FUZZY_FCA_INPUT_FILE = "inputpeppe.xml";
	private static final String DEFAULT_FUZZY_FCA_OUTPUT_FILE= "outputpeppe.xml";
	private static final String DEFAULT_FUZZY_FCA_OUTPUT_FILE_LABELING= "outputpeppeLabeling.xml";
	private static final String DEFAULT_FUZZY_FCA_OUTPUT_CEX_FILE= "outputpeppe.cex";
	private static final String DEFAULT_FUZZY_FCA_OUTPUT_RULES_TXT_FILE= "rules.txt";
	private static final String DEFAULT_FUZZY_FCA_OUTPUT_RULES_XML_FILE= "rules.xml";
	private static final String DEFAULT_FUZZY_FCA_OUTPUT_FORMAT = "xml";
	private static final String DEFAULT_FUZZY_FCA_OVERWRITE = "yes";
//	private static final String DEFAULT_FUZZY_FCA_TRAVERSAL = "bo";
//	private static final String DEFAULT_FUZZY_FCA_RTYPE = "tree";
//	private static final String DEFAULT_FUZZY_FCA_LTYPE = "set";
	private static final String DEFAULT_FUZZY_FCA_SUPP = "0.0";
	private static final String DEFAULT_FUZZY_FCA_CONF = "0.9";
	private static final String DEFAULT_FUZZY_FCA_DIFF = "2";
		
	
	protected static Log log = LogFactory.getLog(FuzzyFCAProperties.class);

	/**
	 * @uml.property   name="singleton"
	 */
	private static FuzzyFCAProperties singleton;

	/**
	 * @throws Exception 
	 */
	public static FuzzyFCAProperties getInstance() throws Exception {
		if (singleton == null) {
			synchronized (FuzzyFCAProperties.class) {
				if (singleton == null) {
					singleton = new FuzzyFCAProperties();
				}
			}
		}
		return singleton;
	}
	
	/**
	 * Constructor
	 * 
	 * @throws Exception 
	 */
	private FuzzyFCAProperties(){
		FileInputStream fis;
		
		try {
			fis = new FileInputStream(BASE_DIR + PREFERENCES_FILE);
			properties = new Properties();

			properties.loadFromXML( fis );
			
			fis.close();
			
			log4j = properties.getProperty(pNames[PropertiesNames.pNameLog4j.ordinal()], DEFAULT_FUZZY_FCA_LOG4J_PROPERTIES_FILENAME).trim();
			
			
			threshold = Double.parseDouble(properties.getProperty(pNames[PropertiesNames.pNameThreshold.ordinal()], DEFAULT_FUZZY_FCA_THRESHOLD).trim());
//			inputFormat = properties.getProperty(pNames[PropertiesNames.pNameInputFormat.ordinal()], DEFAULT_FUZZY_FCA_INPUT_FORMAT).trim();
			inputFile = properties.getProperty(pNames[PropertiesNames.pNameInputFile.ordinal()], DEFAULT_FUZZY_FCA_INPUT_FILE).trim();
//			outputFormat = properties.getProperty(pNames[PropertiesNames.pNameOutputFormat.ordinal()], DEFAULT_FUZZY_FCA_OUTPUT_FORMAT).trim();
			outputFile = properties.getProperty(pNames[PropertiesNames.pNameOutputFile.ordinal()], DEFAULT_FUZZY_FCA_OUTPUT_FILE).trim();
			outputFileLabeling = properties.getProperty(pNames[PropertiesNames.pNameOutputFileLabeling.ordinal()], DEFAULT_FUZZY_FCA_OUTPUT_FILE_LABELING).trim();
			overwrite = properties.getProperty(pNames[PropertiesNames.pNameOverwrite.ordinal()], DEFAULT_FUZZY_FCA_OVERWRITE).trim();
//			trav = properties.getProperty(pNames[PropertiesNames.pNameTraversal.ordinal()], DEFAULT_FUZZY_FCA_TRAVERSAL).trim();
//			relationType = properties.getProperty(pNames[PropertiesNames.pNameRtype.ordinal()], DEFAULT_FUZZY_FCA_RTYPE).trim();
//			latticeType = properties.getProperty(pNames[PropertiesNames.pNameLtype.ordinal()], DEFAULT_FUZZY_FCA_LTYPE).trim();
			supp = Double.parseDouble(properties.getProperty(pNames[PropertiesNames.pNameSupp.ordinal()], DEFAULT_FUZZY_FCA_SUPP).trim());
			conf = (float) Double.parseDouble(properties.getProperty(pNames[PropertiesNames.pNameConf.ordinal()], DEFAULT_FUZZY_FCA_CONF).trim());
			diff = Integer.parseInt(properties.getProperty(pNames[PropertiesNames.pNameDiff.ordinal()], DEFAULT_FUZZY_FCA_DIFF).trim());
			cex = properties.getProperty(pNames[PropertiesNames.pNameCEX.ordinal()], DEFAULT_FUZZY_FCA_OUTPUT_CEX_FILE).trim();
			
			fcaRulesXmlFileName = properties.getProperty(pNames[PropertiesNames.pNameRulesXmlFileName.ordinal()], DEFAULT_FUZZY_FCA_OUTPUT_RULES_XML_FILE).trim();
			fcaRulesTxtFileName = properties.getProperty(pNames[PropertiesNames.pNameRulesTxtFileName.ordinal()], DEFAULT_FUZZY_FCA_OUTPUT_RULES_TXT_FILE).trim();
			
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	double threshold;
	
	public double getThreshold(){
		return threshold;
	}
	
	public void setThreshold(double threshold){
		this.threshold = threshold;
		properties.setProperty(pNames[PropertiesNames.pNameThreshold.ordinal()], ""+threshold);
	}
	
	
	private String log4j = "";
	
	public String getLog4j(){
		return log4j;
	}
	
	public void setLog4j(String log4j){
		this.log4j = log4j;
		properties.setProperty(pNames[PropertiesNames.pNameLog4j.ordinal()], log4j);
		PropertyConfigurator.configure(log4j);
	}
	
	public void saveChanges(){
		String comment;
		FileOutputStream fos;

		try {
			comment = "	This file specifies properties of clustering rules generator module.";
			fos = new FileOutputStream(PREFERENCES_FILE);
			properties.storeToXML(fos, comment);
     	    if(log.isInfoEnabled()){
     	    	log.info("Changes Saved!");
    	    }
     	    fos.close();
	    } catch (FileNotFoundException e) {
			log.error("FileNotFoundException Occurs. Error On Save Properties Changes: " + e.getMessage());
	    	e.printStackTrace();
		} catch (IOException e) {
			log.error("IOException Occurs. Error On Save Properties Changes: " + e.getMessage());
	    	e.printStackTrace();
		}
	}
	
//	String inputFormat;
//	
//	public String getInputFormat() {
//		return inputFormat;
//	}
//
//	public void setInputFormat(String inputFormat) {
//		this.inputFormat = inputFormat;
//		properties.setProperty(pNames[PropertiesNames.pNameInputFormat.ordinal()], ""+ inputFormat);
//	}
	
	String inputFile;

	public String getInputFile() {
		return inputFile;
	}

	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
		properties.setProperty(pNames[PropertiesNames.pNameInputFile.ordinal()], ""+ inputFile);
	}


//	String outputFormat;
//
//	public String getOutputFormat() {
//		return outputFormat;
//	}
//
//	public void setOutputFormat(String outputFormat) {
//		this.outputFormat = outputFormat;
//		properties.setProperty(pNames[PropertiesNames.pNameOutputFormat.ordinal()], ""+ outputFormat);
//	}
	
	String outputFile;

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
		properties.setProperty(pNames[PropertiesNames.pNameOutputFile.ordinal()], ""+ outputFile);
	}
	
	String outputFileLabeling;

	public String getOutputFileLabeling() {
		return outputFileLabeling;
	}

	public void setOutputFileLabeling(String outputFileLabeling) {
		this.outputFileLabeling = outputFileLabeling;
		properties.setProperty(pNames[PropertiesNames.pNameOutputFileLabeling.ordinal()], ""+ outputFileLabeling);
	}
	
	String cex;

	public String getCEXFileName() {
		return cex;
	}

	public void setCEXFileName(String cex) {
		this.cex = cex;
		properties.setProperty(pNames[PropertiesNames.pNameCEX.ordinal()], ""+ cex);
	}
	
	String overwrite;

	public String getOverwrite() {
		return overwrite;
	}

	public void setOverwrite(String overwrite) {
		this.overwrite = overwrite;
		properties.setProperty(pNames[PropertiesNames.pNameOverwrite.ordinal()], ""+ overwrite);
	}
	
//	String trav;
//	
//	public String getTrav() {
//		return trav;
//	}
//
//	public void setTrav(String trav) {
//		this.trav = trav;
//		properties.setProperty(pNames[PropertiesNames.pNameTraversal.ordinal()], ""+ trav);
//	}

//	String relationType;
//
//	public String getRelationType() {
//		return relationType;
//	}
//
//	public void setRelationType(String relationType) {
//		this.relationType = relationType;
//		properties.setProperty(pNames[PropertiesNames.pNameRtype.ordinal()], ""+ relationType);
//	}
//	
//	String latticeType;
//	
//	public String getLatticeType() {
//		return latticeType;
//	}
//
//	public void setLatticeType(String latticeType) {
//		this.latticeType = latticeType;
//		properties.setProperty(pNames[PropertiesNames.pNameLtype.ordinal()], ""+ latticeType);
//	}


	String fcaRulesTxtFileName;

	public String getFcaRulesTxtFileName() {
		return fcaRulesTxtFileName;
	}

	public void setFcaRulesTxtFileName(String fcaRulesTxtFileName) {
		this.fcaRulesTxtFileName = fcaRulesTxtFileName;
		properties.setProperty(pNames[PropertiesNames.pNameRulesTxtFileName.ordinal()], ""+ fcaRulesTxtFileName);
	}

	public String getFcaRulesXmlFileName() {
		return fcaRulesXmlFileName;
	}

	public void setFcaRulesXmlFileName(String fcaRulesXmlFileName) {
		this.fcaRulesXmlFileName = fcaRulesXmlFileName;
		properties.setProperty(pNames[PropertiesNames.pNameRulesXmlFileName.ordinal()], ""+ fcaRulesXmlFileName);
	}

	String fcaRulesXmlFileName;
	
	
	double supp;

	public double getSupp() {
		return supp;
	}

	public void setSupp(double supp) {
		this.supp = supp;
		properties.setProperty(pNames[PropertiesNames.pNameSupp.ordinal()], ""+ supp);
	}
	
	float conf;

	public float getConf() {
		return conf;
	}

	public void setConf(float conf) {
		this.conf = conf;
		properties.setProperty(pNames[PropertiesNames.pNameConf.ordinal()], ""+ conf);
	}
	
	int diff;

	public int getDiff() {
		return diff;
	}

	public void setDiff(int diff) {
		this.diff = diff;
		properties.setProperty(pNames[PropertiesNames.pNameDiff.ordinal()], ""+ diff);
	}

}
