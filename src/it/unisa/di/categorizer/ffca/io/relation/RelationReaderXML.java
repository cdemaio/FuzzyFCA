package it.unisa.di.categorizer.ffca.io.relation;

import it.unisa.di.categorizer.ffca.lib.Relation;
import it.unisa.di.categorizer.ffca.lib.TreeRelation;
import it.unisa.di.categorizer.ffca.utils.FuzzyFCAProperties;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;




import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;




/**
 * Class for importing a binary relation from an .xml file.
 * @author Daniel N. Goetzmann
 * @version 1.0
 */
public class RelationReaderXML extends DefaultHandler {
	public enum State{
		Membership,
		Other;
	}
	
	private State activeState;
	private String currentContext = null;
	private String currentObject = null;
	private String currentAttribute = null;
	private double currentMembership = 0.0;
	private Relation relation = null;
	private FuzzyFCAProperties properties;
	private String content = "";
	private Map<Comparable, Map<Comparable, Double>> memObjs;
	private Map<Comparable, Map<Comparable, Double>> memAttrs;
	
	
	public Map<Comparable, Map<Comparable, Double>> getMembershipsObjs() {
		return memObjs;
	}
	public Map<Comparable, Map<Comparable, Double>> getMembershipsAttrs() {
		return memAttrs;
	}
	
	public RelationReaderXML () {
		super();
		try {
			memObjs = new HashMap<Comparable, Map<Comparable,Double>>();
			memAttrs = new HashMap<Comparable, Map<Comparable,Double>>();
			activeState = State.Other;
			properties = FuzzyFCAProperties.getInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * reads an .xml file
	 * @param fileName the name of the file to be read
	 * @return a relation containing exactly those objects, attributes and pairs contained in the file
	 * @throws SAXException
	 * @throws IOException
	 */
	public Relation read(String fileName) throws SAXException, IOException {
		Relation relation = new TreeRelation();
		read(fileName, relation);
		return relation;
	}
	
	
	/**
	 * reads an .xml file
	 * @param fileName the name of the file to be read
	 * @param relation the relation to which the elements contained in the file will be added
	 */
	public void read(String fileName, Relation relation) throws SAXException, IOException {
		this.relation = relation;
		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setContentHandler(this);
		reader.setErrorHandler(this);
		
		FileReader fileReader = new FileReader(fileName);
		try {
			reader.parse(new InputSource(fileReader));
		} finally {
			fileReader.close();
		}
		System.out.println("# Relations: " + j);
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
		if (activeState == State.Membership)
			content += new String(ch, start, length);
	}
	
	public void startElement (String uri, String name, String qName, Attributes atts) {
		//if an object is read that has no ancestor that is an object add it to the relation
		if (qName.equals("context")) {
			currentContext = atts.getValue("uri");
		}
		
		if (qName.equals("object") && currentObject == null) {
			currentObject = atts.getValue("name");
			//if an attribute has already been read, the pair will be added
			//else attribute is null and only the object will be added
//			relation.add(currentObject, currentAttribute);
		}
		
		//if an attribute is read that has no ancestor that is an attribute add it to the relation
		if (qName.equals("attribute") && currentAttribute == null) {
			currentAttribute = atts.getValue("name");
			//if an object has already been read, the pair will be added
			//else object is null and only the attribute will be added
//			relation.add(currentObject, currentAttribute);
		}
		
		if (qName.equals("membership")){
			activeState = State.Membership;
		}
	}
	
	int i=0;
	int j=0;
	public void endElement (String uri, String name, String qName) {
		double threshold = properties.getThreshold();
		
		if (qName.equals("membership")){
			currentMembership = Double.parseDouble((content.equalsIgnoreCase(""))? "0.0":content);
			if(currentMembership > threshold) {
				relation.add(currentObject, currentAttribute);
				Map<Comparable, Double> attMap = memObjs.get(currentObject);
				if (attMap == null) 
					attMap = new HashMap<Comparable, Double>();
				attMap.put(currentAttribute, currentMembership);
				memObjs.put(currentObject, attMap);

				Map<Comparable, Double> objMap = memAttrs.get(currentAttribute);
				if (objMap == null) 
					objMap = new HashMap<Comparable, Double>();
				objMap.put(currentObject, currentMembership);
				memAttrs.put(currentAttribute, objMap);

				j++;
			}		
//			else 
//				relation.remove(currentObject, currentAttribute);
		}else{
			content = "";
			activeState = State.Other;
		}
		if (qName.equals("object")){
			if((relation.getAttributeSet(currentObject)==null)||(relation.getAttributeSet(currentObject).size() == 0)){
//				relation.remove(currentObject);
				System.err.println(currentObject);
				i++;
			}
		}
		if (qName.equals("object"))
			currentObject = null;
		if (qName.equals("attribute"))
			currentAttribute = null;
		if (qName.equals("membership"))
			currentMembership = 0.0;
		
		if (qName.equals("context")){// || qName.equals("lattice")){
			System.out.println(Integer.toString(i) + " unclussifiable objects!");
		}
	}
	
	public String getCurrentContext() {
		return currentContext;
	}


	public void setCurrentContext(String currentContext) {
		this.currentContext = currentContext;
	}
}
