/**
 * 
 */
package it.unisa.di.categorizer.ffca.io.lattice;



import it.unisa.di.categorizer.ffca.utils.FuzzyFCAProperties;
import it.unisa.di.categorizer.ffca.utils.QuickSort;
import it.unisa.di.datasets.xml.ffca.context.Attribute;
import it.unisa.di.datasets.xml.ffca.context.Context;
import it.unisa.di.datasets.xml.wrappers.WrapperXMLFuzzyFCAInput;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.JAXBException;

/**
 * @author  
 *
 */
public class XmlLatticeDrawingWriter {
	
	private String contextIn;
	private FileInputStream fisContext;
	private String fos;
	
	WrapperXMLFuzzyFCAInput serializerContext;
	List<Attribute> attributes;
	List<it.unisa.di.datasets.xml.ffca.context.Object> objects;
	
	public XmlLatticeDrawingWriter() {
		this.fisContext = null;
		serializerContext = new WrapperXMLFuzzyFCAInput();
	}

	public void evaluate(String conIn, String fosOut) throws Exception {
	
		fos = fosOut;
		FileWriter writer = new FileWriter(fos);
		try{
			
			contextIn = conIn;
			fisContext = new FileInputStream(contextIn);
			Context context = serializerContext.read(fisContext);
			objects = context.getObject();
			Iterator<it.unisa.di.datasets.xml.ffca.context.Object> obj = objects.iterator();
			// passo 1: creiamo il contesto da dare in input a ConExp
			
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<ConceptualSystem xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
					"<Version MajorNumber=\"1\" MinorNumber=\"0\"/>\n<Contexts>	\n<Context Identifier=\"0\" Type=\"Binary\">\n<Attributes>\n");
			int i=0;
			Vector<String> Allattr = new Vector<String>();
					
			
			while (obj.hasNext()) {
				it.unisa.di.datasets.xml.ffca.context.Object object = obj.next();
				attributes= object.getAttribute();
			
				Iterator<Attribute> attr = attributes.iterator();
				
				while (attr.hasNext()) {
					Attribute entry = attr.next();
					String attribute = entry.getName();
					if((!Allattr.contains(attribute))&&(entry.getMembership()>FuzzyFCAProperties.getInstance().getThreshold())){
						
						writer.write("<Attribute Identifier=\""+ i + "\">\n <Name>"+ attribute.replaceAll("><", ";").replaceAll("<", "").replaceAll(">", "")+"</Name>\n</Attribute>" );
						Allattr.add(i, attribute);
						i++;
					}
				}
			}
			writer.write("</Attributes>\n<Objects>\n");
			
		    
			Iterator<it.unisa.di.datasets.xml.ffca.context.Object> objs = objects.iterator();
			while (objs.hasNext()) {
				it.unisa.di.datasets.xml.ffca.context.Object object = objs.next();
				attributes= object.getAttribute();
				writer.write("<Object>\n<Name>"+object.getName()+"</Name>\n<Intent>\n");
				
				Iterator<Attribute> attr = attributes.iterator();
				
				while (attr.hasNext()) {
					Attribute entry = attr.next();
					String attribute = entry.getName();
					int id = Allattr.indexOf(attribute);
					if(entry.getMembership()>FuzzyFCAProperties.getInstance().getThreshold())
						writer.write("<HasAttribute AttributeIdentifier=\""+id+"\"/>\n");
				}
				writer.write("</Intent>\n</Object>\n");
			}
			writer.write("</Objects>\n</Context>\n</Contexts>\n<RecalculationPolicy Value=\"Clear\"/>\n<Lattices/>\n</ConceptualSystem>\n");
		}
		catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.close();
    }

	private boolean isArrayEquals(String a[], String b[]){
		boolean esito = false;
		
		if( a.length == b.length ){
			for (int i = 0; i < a.length; i++) {
				int j = 0;
				
				for (; (j < b.length) && (!a[i].trim().equals(b[j].trim())); j++) ;
				
				if( j >= a.length ){
					esito = false;
					return esito;
				}
				else{
					esito = true;
				}
			}
		}
	
		return esito;
	}
	
	
	//Crea il file usato per la generazione delle regole
	public void numgen(String conIn, String fosOut) throws Exception {
		
		fos = fosOut;
		FileWriter writer = new FileWriter(fos);
		QuickSort qs = new QuickSort();
				
		try{
			
			contextIn = conIn;
			fisContext = new FileInputStream(contextIn);
			
			// Scorro i primi elementi del documento 
			Context context = serializerContext.read(fisContext);
			objects = context.getObject();
			Iterator<it.unisa.di.datasets.xml.ffca.context.Object> obj = objects.iterator();
			// passo 1: creiamo il contesto da dare in input a ConExp
			
			int i=0;
			Vector<String> Allattr = new Vector<String>();
					
			
			while (obj.hasNext()) {
				it.unisa.di.datasets.xml.ffca.context.Object object = obj.next();
				attributes= object.getAttribute();
			
				Iterator<Attribute> attr = attributes.iterator();
				
				while (attr.hasNext()) {
					Attribute entry = attr.next();
					String attribute = entry.getName();
					if((!Allattr.contains(attribute))&&(entry.getMembership()>FuzzyFCAProperties.getInstance().getThreshold())){
						
						Allattr.add(i, attribute);
						i++;
					}
				}
			}
//			System.out.println(""+Allattr.size());
			double[] attrib = new double[Allattr.size()+1];
		    
			Iterator<it.unisa.di.datasets.xml.ffca.context.Object> objs = objects.iterator();
			
			int index = -1;
			
			while (objs.hasNext()) {
				it.unisa.di.datasets.xml.ffca.context.Object object = objs.next();
				attributes= object.getAttribute();
				
				Iterator<Attribute> attr = attributes.iterator();
				
				while (attr.hasNext()) {
					Attribute entry = attr.next();
					String attribute = entry.getName();
					int id = Allattr.indexOf(attribute)+1;
					if(entry.getMembership()>FuzzyFCAProperties.getInstance().getThreshold()){
						index++;
						attrib[index]=id;
					}
					
					
				}
				
				index = -1;
				
				qs.quicksort(attrib);
				
				//variabile di servizio usata per correggere un bug del quicksort
				double prev = 0.0;
				
				for (int f = 0; f<Allattr.size()+1;f++){
					int id;
					if((attrib[f]!= 0)&&(attrib[f]!=prev)){
						id = (int) attrib[f];
						// System.out.print("["+f+"]->"+id+"<-"+prev+" ");
						writer.write(id+" ");
						prev = attrib[f];
						attrib[f]=0;
					}
				}
//				System.out.println("");
				writer.write("\n");
			}
			
		}
		catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.close();
    }

}
