/**
 * 
 */
package it.unisa.di.categorizer.ffca.utils.rules.fcabased;

import it.unisa.di.categorizer.ffca.utils.XmlRulesWR;
import it.unisa.di.datasets.xml.ffca.context.Context;
import it.unisa.di.datasets.xml.ffca.lattice.Attribute;
import it.unisa.di.datasets.xml.ffca.lattice.Concept;
import it.unisa.di.datasets.xml.ffca.lattice.Concepts;
import it.unisa.di.datasets.xml.ffca.lattice.Edge;
import it.unisa.di.datasets.xml.ffca.lattice.Edges;
import it.unisa.di.datasets.xml.ffca.lattice.Lattice;
import it.unisa.di.datasets.xml.ffca.rules.Rule;
import it.unisa.di.datasets.xml.ffca.rules.Rules;
import it.unisa.di.datasets.xml.wrappers.WrapperXMLFuzzyFCAInput;
import it.unisa.di.datasets.xml.wrappers.WrapperXMLFuzzyFCAOutput;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

/**
 * @version 1.0 
 * @author Luca Liscio 
 * @author Marco Lettieri
 */
public class FcaBasedRouleMining {

	/**
	 * 
	 */
	public FcaBasedRouleMining() {
		// TODO Auto-generated constructor stub
	}

	public List<Rule> getRules(String lattice, String context, double confidence, double support) throws JAXBException, IOException {
		
		//Variabili--------------------------------->
		Rule r = null;
    	Rules rules = new Rules();
    	FileOutputStream fos = new FileOutputStream("rules.xml");
    	FileWriter  txt = new FileWriter("rules.txt");
    	FileInputStream fis = new FileInputStream(lattice);
    	FileInputStream fis_context = new FileInputStream(context);
    	XmlRulesWR xmlrw = new XmlRulesWR(fos);
    	Lattice lat = new Lattice();
    	WrapperXMLFuzzyFCAOutput lat_reader = new WrapperXMLFuzzyFCAOutput();
    	WrapperXMLFuzzyFCAInput cont_reader = new WrapperXMLFuzzyFCAInput();
    	Context cont = new Context();
    	Concepts con = new Concepts();
    	Edges ed = new Edges();
    	Concept co = new Concept();
    	Edge e = new Edge();
    	List<Integer> parents = new ArrayList<Integer>();
    	int attribNum, objectNum, id = 0;
    	double lsup, lconf;
    	    	
    	//Leggo il lattice e contesto -------------->
    	lat = lat_reader.read(fis);
    	cont = cont_reader.read(fis_context);
    	con = lat.getConcepts();
    	ed = lat.getEdges();
    	
    	/*
    	 * Ottengo il numero totale degli attributi contando 
    	 * il numero di attributi presenti nel concetto 1 che è
    	 * quello che ha tutti gli attributi ma nessun oggetto 
    	 */
    	attribNum = con.getConcept().get(0).getAttribute().size();
    	objectNum = cont.getObject().size();
    	
    	//Genero le regole ------------------------->
    	for(int i = 0; i<con.getConcept().size();i++){
    		co = con.getConcept().get(i);
    		parents = getParents(co,ed);
    		
    		lsup = (co.getObject().size()*100)/objectNum;
    		System.out.println("Supporto: "+ lsup);
    		System.out.println("Parente: 0");
    		if((parents.size()!=0)&&(lsup >= support)){
    			if(parents.size()==1){
    				id++;
    				r = new Rule();
    				
    				r.setId(""+id);
    				txt.write("(" + id + ") ");
    				int idp = getIndex(parents.get(0),con);
    				
    				String antecedent = getCoplement(co.getAttribute(),con.getConcept().get(idp).getAttribute());
    				String consequent = getString(con.getConcept().get(idp).getAttribute());
    				
    				r.setAntecedent(antecedent);
    				r.setConsequent(consequent);
    				
    				txt.write(antecedent+" -> "+consequent);
    				
    				r.setConfidence("100.0");
    				
    				r.setSupport(""+lsup);
    				
    				txt.write(" "+lsup+"% ");
    				
    				txt.write(" 100.0%\n");
    				
    				System.out.println(antecedent+" -> "+consequent+" 100%");
    				
    				rules.getRule().add(id-1, r);
    			}
    			
    			System.out.println("Parenti: "+parents.size());
    			for(int y = 0;y<parents.size();y++){
    				System.out.println("Parente: "+y);
    				int index = getIndex(parents.get(y),con);
    				Concept cop = con.getConcept().get(index);
    				lconf = (co.getObject().size()*100)/cop.getObject().size();
    				System.out.println("Confidenza con il parente: "+lconf);
    				if((cop.getAttribute().size()!=0)&&(lconf>=confidence)){
    					id++;
    	    			r = new Rule();
    	    			
    	    			r.setId(""+id);
    	    			txt.write("(" + id + ") ");
    	    			int idp = index;
    	    			
    	    			String consequent = getCoplement(co.getAttribute(),con.getConcept().get(idp).getAttribute());
    	    			String antecedent = getString(con.getConcept().get(idp).getAttribute());
    	    				
    	    			r.setAntecedent(antecedent);
    	    			r.setConsequent(consequent);
    	    				
    	    			txt.write(antecedent+" -> "+consequent);
    	    				
    	    			r.setConfidence(""+lconf);
    	    			
    	    			r.setSupport(""+lsup);
        				
        				txt.write(" "+lsup+"% ");
    	    				
    	    			txt.write(" "+lconf+"%\n");
    	    			
    	    			System.out.println(antecedent+" -> "+consequent+" "+lconf+"%");
    	    			
    	    			
    	    			rules.getRule().add(id-1, r);
    				}
    			}
    		}
    	}
    	txt.close();
    	System.out.println("Generating file: rules.xml");
		xmlrw.write(rules);
    	return rules.getRule();
	}

	private int getIndex(Integer integer, Concepts con) {
		for(int k = 0; k < con.getConcept().size();k++){
			if(con.getConcept().get(k).getId().equals(String.valueOf(integer))){
				System.out.println("Posizione :" +k);
				return k;
			}
		}
		
		return -1;
	}

	private String getString(List<Attribute> attribute) {
		String s="{ ";
		for(int x = 0; x < attribute.size();x++){
			s+=attribute.get(x).getName()+" ";
		}
		s+="}";
		return s;
	}

	private String getCoplement(List<Attribute> attribute, List<Attribute> list) {
		String s="{ ";
		for(int k = 0; k < attribute.size();k++){
			if(!isIn(list,attribute.get(k).getName())){
				s+=attribute.get(k).getName()+" ";
			}
		}
		s+="}";
		return s;
	}

	private boolean isIn(List<Attribute> list, String name) {
		for(int k = 0; k < list.size();k++){
			if(list.get(k).getName().equals(name)){
				return true;
			}
		}
		return false;
	}

	private List<Integer> getParents(Concept co, Edges ed) {
		List<Integer> p = new ArrayList<Integer>();
		System.out.println("Concetto numero: "+co.getId());
		for (int j=0;j<ed.getEdge().size();j++){
			if(ed.getEdge().get(j).getLowerConceptId().equals(co.getId())){
				p.add(Integer.valueOf(ed.getEdge().get(j).getUpperConceptId()));
				System.out.println("Inserito il parente: "+ed.getEdge().get(j).getUpperConceptId()+" -->  "+p.toString());
			}
		}
		
		return p;
	}
	
	

}