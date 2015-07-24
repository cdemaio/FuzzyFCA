/**
 * 
 */
package it.unisa.di.categorizer.ffca.utils;

import java.io.*;

import javax.xml.bind.JAXBException;

import it.unisa.di.datasets.xml.ffca.rules.Rules;
import it.unisa.di.datasets.xml.wrappers.WrapperXMLFuzzyFCAAssociationRules;


/**
 * @author luca
 *
 */
public class XmlRulesWR {

	private	WrapperXMLFuzzyFCAAssociationRules wrapper;
	private FileInputStream fis = null;
	private FileOutputStream fos = null;
	
	
	//------------------ Costruttori -----------------------
	public XmlRulesWR(FileInputStream fis, FileOutputStream fos) {
		super();
		this.wrapper = new WrapperXMLFuzzyFCAAssociationRules();
		this.fis = fis;
		this.fos = fos;
	}
	
	public XmlRulesWR(FileInputStream fis) {
		super();
		this.wrapper = new WrapperXMLFuzzyFCAAssociationRules();
		this.fis = fis;
	}

	public XmlRulesWR(FileOutputStream fos) {
		super();
		this.wrapper = new WrapperXMLFuzzyFCAAssociationRules();
		this.fos = fos;
	}
	//-----------------------------------------------------------
	
	public Rules readRulesIn() throws JAXBException{
		return wrapper.read(fis);
	}
	
	public void write(Rules data) throws JAXBException, IOException{
		wrapper.write(data, fos);
	}


	
	
	//----------------------- set e get ----------------------
	
	/**
	 * @return the fis
	 */
	public FileInputStream getFis() {
		return fis;
	}

	/**
	 * @param fis the fis to set
	 */
	public void setFis(FileInputStream fis) {
		this.fis = fis;
	}

	/**
	 * @return the fos
	 */
	public FileOutputStream getFos() {
		return fos;
	}

	/**
	 * @param fos the fos to set
	 */
	public void setFos(FileOutputStream fos) {
		this.fos = fos;
	}
}

