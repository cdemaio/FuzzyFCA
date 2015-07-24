package it.unisa.di.categorizer.ffca.utils;

import it.unisa.di.categorizer.ffca.io.relation.RelationWriterCON;
import it.unisa.di.categorizer.ffca.io.relation.RelationWriterXML;
import it.unisa.di.categorizer.ffca.lib.Relation;

import java.io.File;
import java.io.IOException;






/**
 * Creates a random binary relation and exports it to
 * a .con or .xml file.
 * @author Daniel N. Goetzmann
 * @version 1.0
 */
public class RandomRelationWriter {
	public static void main (String args[]) throws IOException {
		try {
			String xmlFileName = System.getProperty("xml_file");
			String conFileName = System.getProperty("con_file");
			Integer objects = Integer.valueOf(System.getProperty("objects"));
			Integer attributes = Integer.valueOf(System.getProperty("attributes"));
			Float density = Float.valueOf(System.getProperty("density"));
			
			RandomRelationGenerator generator = new RandomRelationGenerator();
			
			Relation relation = generator.generate(objects.intValue(), attributes.intValue(), density.floatValue(), true);
			
			if (conFileName != null) {
				File file = new File (conFileName);
				
				RelationWriterCON conWriter = new RelationWriterCON();
				conWriter.write(relation, file);
				
				System.out.println("con file written to " + conFileName);
			}
			
			if (xmlFileName != null) {
				File file = new File (xmlFileName);
				
				RelationWriterXML xmlWriter = new RelationWriterXML();
				xmlWriter.write(relation, file);
				
				System.out.println("xml file written to " + xmlFileName);
			}
			
		} catch (RuntimeException e)  {
			System.out.println(e.toString());
		}
	}
}
