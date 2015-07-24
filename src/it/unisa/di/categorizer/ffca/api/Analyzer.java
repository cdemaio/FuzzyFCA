package it.unisa.di.categorizer.ffca.api;

import it.unisa.di.categorizer.ffca.io.relation.RelationReaderXML;
import it.unisa.di.categorizer.ffca.lib.Concept;
import it.unisa.di.categorizer.ffca.lib.Edge;
import it.unisa.di.categorizer.ffca.lib.HybridLattice;
import it.unisa.di.categorizer.ffca.lib.Lattice;
import it.unisa.di.categorizer.ffca.lib.Relation;
import it.unisa.di.categorizer.ffca.lib.Traversal;
import it.unisa.di.categorizer.ffca.lib.TreeRelation;
import it.unisa.di.categorizer.ffca.utils.FuzzyFCAProperties;
import it.unisa.di.datasets.xml.ffca.lattice.Concepts;
import it.unisa.di.datasets.xml.ffca.lattice.Edges;
import it.unisa.di.datasets.xml.ffca.lattice.ObjectFactory;
import it.unisa.di.datasets.xml.wrappers.WrapperXMLFuzzyFCAOutput;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

/**
 * Imports a binary relation from a .con or .xml file and outputs the edges of
 * the corresponding lattice or the edges returned by the violation iterator.
 * 
 * @author Daniel N. Goetzmann
 * @version 1.0
 */
public class Analyzer {
	private FuzzyFCAProperties properties;

	public Analyzer() throws Exception {
		properties = FuzzyFCAProperties.getInstance();
	}

	/**
	 * @param propertiesRoot root where to find the configuration
	 * file (FuzzyFCA_properties.xml)
	 * @throws Exception
	 */
	public Analyzer(String propertiesRoot, String prefFile) throws Exception {
		FuzzyFCAProperties.BASE_DIR = propertiesRoot;
		FuzzyFCAProperties.PREFERENCES_FILE = prefFile;
		properties = FuzzyFCAProperties.getInstance();
	}
	
	public void toXML() throws Exception {
		String inputFile = properties.getInputFile();
		String outputFile = properties.getOutputFile();
		toXML(inputFile, outputFile);
	}

	public it.unisa.di.datasets.xml.ffca.lattice.Lattice getFuzzyLatticeXML(
			String inputFile) throws Exception {
		it.unisa.di.datasets.xml.ffca.lattice.Lattice l = null;
		long inizio = System.currentTimeMillis();

		Traversal traversal = Traversal.TOP_ATTRSIZE;

		if ((inputFile == null)) {
			System.err
					.println("Please specify the file name of the input and input file.");
			return null;
		}

		Relation relation = new TreeRelation();

		RelationReaderXML xmlReader = new RelationReaderXML();
		try {
			xmlReader = new RelationReaderXML();
			xmlReader.read(inputFile, relation);
		} catch (SAXException e) {
			System.err.println("Reading xml-file failed.");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			System.err.println("Reading xml-file failed.");
			e.printStackTrace();
			return null;
		}

		Lattice lattice = new HybridLattice(relation);
		
		System.out.println(relation.getSizeAttributes() + " attributes");
		System.out.println(relation.getSizeObjects() + " objects");

		System.out.println("Tempo Prima di Scrivere: "
				+ (System.currentTimeMillis() - inizio));

		l = getFuzzyLattice(traversal, xmlReader, lattice, relation);

		return l;
	}

	public void toXML(String inputFile, String outputFile) throws Exception {

		String overwrite = properties.getOverwrite();
		it.unisa.di.datasets.xml.ffca.lattice.Lattice l = getFuzzyLatticeXML(inputFile);
		File file = null;
		file = new File(outputFile);
		if (file.exists()) {
			if (overwrite == null || !overwrite.equals("yes")
					|| !file.canWrite()) {
				System.err
						.println("Unable to write to the specified file. The file already exists.");
				return;
			} else {
				try {
					file.createNewFile();
				} catch (IOException e) {
					System.err
							.println("Unable to write to the specified file.");
					e.printStackTrace();
					return;
				}
			}
		} else {
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.err.println("Unable to write to the specified file.");
				e.printStackTrace();
				return;
			}
		}

		/* da commentare se non si vuole xml */
		long inizioScritturaXML = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream(outputFile);
		WrapperXMLFuzzyFCAOutput ioLattice = new WrapperXMLFuzzyFCAOutput();
		try {

			ioLattice.write(l, fos);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Tempo Scrittura XML: "
				+ (System.currentTimeMillis() - inizioScritturaXML));
	}

	public static it.unisa.di.datasets.xml.ffca.lattice.Lattice getFuzzyLattice(
			Traversal trav, RelationReaderXML relationXml, Lattice lattice,
			Relation relation) {

		it.unisa.di.datasets.xml.ffca.lattice.Lattice latticeXml;

		ObjectFactory of = new ObjectFactory();
		latticeXml = new it.unisa.di.datasets.xml.ffca.lattice.Lattice();
		latticeXml.setContextUri(relationXml.getCurrentContext());
		Concepts concepts = of.createConcepts();
		Edges edges = of.createEdges();
		
		Map<Concept, it.unisa.di.datasets.xml.ffca.lattice.Concept> idsConceptsAlreadyAdded = new HashMap<Concept, it.unisa.di.datasets.xml.ffca.lattice.Concept>();
		Iterator<Edge> edgeIterator = lattice.edgeIterator(trav);

		Concept root = lattice.top();
		Concept target = lattice.bottom();
		root.setId(0);
		target.setId(1);
		latticeXml.setObjectSize(lattice.top().getObjects().size());
		latticeXml.setAttributeSize(lattice.bottom().getAttributes().size());

		TreeMap<Integer, Map<String, Double>> conceptMap = new TreeMap<Integer, Map<String, Double>>();
		// LabelUpper, mappa <lowerConcept, label upperConcept>
		Map<Concept, String> labelsUpper = new HashMap<Concept, String>();

		int i = 0;
		int id = 2;
		for (; edgeIterator.hasNext();) {
			Map<String, Double> upperObjMem = new TreeMap<String, Double>();
			Map<String, Double> lowerObjMem = new TreeMap<String, Double>();

			Edge edge = (Edge) edgeIterator.next();
			// System.err.println("Arco: " + edge.getUpper().getId() + " --> "+
			// edge.getLower().getId());

			// Upper
			Concept upperConcept = (Concept) edge.getUpper();
			// Lower
			Concept lowerConcept = (Concept) edge.getLower();

			// Creo l'Upper Concept
			it.unisa.di.datasets.xml.ffca.lattice.Concept xmlUpperConcept = idsConceptsAlreadyAdded
					.get(upperConcept);
			if (xmlUpperConcept == null) {
				// Creo un nuovo upper concept
				xmlUpperConcept = of.createConcept();
				double supp = (double) upperConcept.getObjects().size()
						/ (double) latticeXml.getObjectSize();
				xmlUpperConcept.setSupport(arrotondaPerEccesso(supp, 3));

				List<it.unisa.di.datasets.xml.ffca.lattice.Object> objs = xmlUpperConcept
						.getObject();
				Iterator<Comparable> itObjs;
				itObjs = upperConcept.getObjects().iterator();
				while (itObjs.hasNext()) {
					Comparable entryob = itObjs.next();
					it.unisa.di.datasets.xml.ffca.lattice.Object o = of.createObject();
					o.setName("" + entryob.toString());

					Iterator<Comparable> itAttr;
					itAttr = upperConcept.getAttributes().iterator();
					double min = Double.MAX_VALUE;
					if (upperConcept.getAttributes().size() == 0)
						min = 0.0;
					while (itAttr.hasNext()) {
						Comparable entryattr = itAttr.next();
						if ((relationXml.getMembershipsObjs().get(entryob) != null)
								&& (relationXml.getMembershipsObjs().get(
										entryob).get(entryattr) != null)
								&& (relationXml.getMembershipsObjs().get(
										entryob).get(entryattr) < min))
							min = relationXml.getMembershipsObjs().get(entryob)
									.get(entryattr);
					}

					o.setMembership(min);

					if (!upperObjMem.containsKey(entryob))
						upperObjMem.put(entryob.toString(), min);
					else
						System.out
								.println("oggetto duplicato nello stesso concetto");

					if (!lowerConcept.getObjects().contains(entryob)) {
						o.setOwn(true);
					} else
						o.setOwn(false);

					objs.add(o);
				}
				List<it.unisa.di.datasets.xml.ffca.lattice.Attribute> attrs = xmlUpperConcept
						.getAttribute();
				Iterator<Comparable> itAttr;
				itAttr = upperConcept.getAttributes().iterator();
				List<it.unisa.di.datasets.xml.ffca.lattice.Attribute> aMaxList = new ArrayList<it.unisa.di.datasets.xml.ffca.lattice.Attribute>();
				double max = Double.MIN_VALUE;
				while (itAttr.hasNext()) {
					Comparable entryattr = itAttr.next();
					it.unisa.di.datasets.xml.ffca.lattice.Attribute a = of.createAttribute();
					a.setName(entryattr.toString());

					Iterator<Comparable> itObjs2;
					itObjs2 = upperConcept.getObjects().iterator();
					double min = Double.MAX_VALUE;
					if (upperConcept.getObjects().size() == 0)
						min = 0.0;
					while (itObjs2.hasNext()) {
						Comparable entryobj = itObjs2.next();
						if ((relationXml.getMembershipsAttrs().get(entryattr) != null)
								&& (relationXml.getMembershipsAttrs().get(
										entryattr).get(entryobj) != null)
								&& (relationXml.getMembershipsAttrs().get(
										entryattr).get(entryobj) < min))
							min = relationXml.getMembershipsAttrs().get(
									entryattr).get(entryobj);
					}
					a.setOwn(true);
					a.setMembership(min);
					attrs.add(a);
					if (min > max) {
						max = min;
						aMaxList.clear();
						aMaxList.add(a);
					} else if (min == max) {
						aMaxList.add(a);
					}
				}
				if (i != 0) {
					for (Iterator iterator = aMaxList.iterator(); iterator
							.hasNext();) {
						it.unisa.di.datasets.xml.ffca.lattice.Attribute attribute = (it.unisa.di.datasets.xml.ffca.lattice.Attribute) iterator.next();
						xmlUpperConcept.setLabel(xmlUpperConcept.getLabel()
								+ "#" + attribute.getName());
					}
				} else {
					if (aMaxList.size() != 0) {
						for (Iterator iterator = aMaxList.iterator(); iterator
								.hasNext();) {
							it.unisa.di.datasets.xml.ffca.lattice.Attribute attribute = (it.unisa.di.datasets.xml.ffca.lattice.Attribute) iterator.next();
							xmlUpperConcept.setLabel(xmlUpperConcept.getLabel()
									+ "#" + attribute.getName());
						}
					} else{
						xmlUpperConcept.setLabel("#root");
					}
					i++;
				}

				if(root.equals(upperConcept))
					xmlUpperConcept.setId(0 + "");
				else{ 
					xmlUpperConcept.setId(id + "");
					upperConcept.setId(id);
					id++;
				}
				concepts.getConcept().add(xmlUpperConcept);
				idsConceptsAlreadyAdded.put(upperConcept, xmlUpperConcept);

				conceptMap.put(upperConcept.getId(), upperObjMem);
			} else {
				// Modifico oggetti propri se concetto già è stato considerato
				// in altre occasioni
				upperConcept.setId(Integer.parseInt(xmlUpperConcept.getId()));

				List<it.unisa.di.datasets.xml.ffca.lattice.Object> objs = xmlUpperConcept
						.getObject();
				Iterator<Comparable> itObjs = upperConcept.getObjects()
						.iterator();
				while (itObjs.hasNext()) {
					Comparable entryob = itObjs.next();
					for (Iterator iterator = objs.iterator(); iterator
							.hasNext();) {
						it.unisa.di.datasets.xml.ffca.lattice.Object object = (it.unisa.di.datasets.xml.ffca.lattice.Object) iterator.next();
						if (object.getName().equalsIgnoreCase(
								entryob.toString())) {
							if (lowerConcept.getObjects().contains(entryob)) {
								object.setOwn(false);
								break;
							}
						}
					}
				}

				idsConceptsAlreadyAdded.remove(upperConcept);
				idsConceptsAlreadyAdded.put(upperConcept, xmlUpperConcept);
				concepts.getConcept().remove(xmlUpperConcept);
				concepts.getConcept().add(xmlUpperConcept);

				upperObjMem = conceptMap.get(upperConcept.getId());
			}

			// Crea il Lower Concept
			it.unisa.di.datasets.xml.ffca.lattice.Concept xmlLowerConcept = idsConceptsAlreadyAdded
					.get(lowerConcept);
			if (xmlLowerConcept == null) {
				labelsUpper.put(lowerConcept, xmlUpperConcept.getLabel());

				xmlLowerConcept = of.createConcept();
				double supp = (double) lowerConcept.getObjects().size()
						/ (double) latticeXml.getObjectSize();
				xmlLowerConcept.setSupport(arrotondaPerEccesso(supp, 3));

				List<it.unisa.di.datasets.xml.ffca.lattice.Object> objs = xmlLowerConcept.getObject();
				Iterator<Comparable> itObjs = lowerConcept.getObjects()
						.iterator();
				Iterator<Comparable> itAttr;
				while (itObjs.hasNext()) {
					Comparable entryob = itObjs.next();
					it.unisa.di.datasets.xml.ffca.lattice.Object o = of.createObject();
					o.setName("" + entryob.toString());

					itAttr = lowerConcept.getAttributes().iterator();
					double min = Double.MAX_VALUE;
					while (itAttr.hasNext()) {
						Comparable entryattr = itAttr.next();
						if ((relationXml.getMembershipsObjs().get(entryob) != null)
								&& (relationXml.getMembershipsObjs().get(
										entryob).get(entryattr) != null)
								&& (relationXml.getMembershipsObjs().get(
										entryob).get(entryattr) < min))
							min = relationXml.getMembershipsObjs().get(entryob)
									.get(entryattr);
					}
					o.setOwn(true);
					o.setMembership(min);
					objs.add(o);

					if (!lowerObjMem.containsKey(entryob))
						lowerObjMem.put(entryob.toString(), min);
					else
						System.out
								.println("oggetto duplicato nello stesso concetto");

				}
				List<it.unisa.di.datasets.xml.ffca.lattice.Attribute> attrs = xmlLowerConcept.getAttribute();
				itAttr = lowerConcept.getAttributes().iterator();
				ArrayList<it.unisa.di.datasets.xml.ffca.lattice.Attribute> aMaxList = new ArrayList<it.unisa.di.datasets.xml.ffca.lattice.Attribute>();
				double max = Double.MIN_VALUE;
				while (itAttr.hasNext()) {
					Comparable entryattr = itAttr.next();
					it.unisa.di.datasets.xml.ffca.lattice.Attribute a = of.createAttribute();
					a.setName(entryattr.toString());

					Iterator<Comparable> itObjs2;
					itObjs2 = lowerConcept.getObjects().iterator();
					double min = Double.MAX_VALUE;
					while (itObjs2.hasNext()) {
						Comparable entryobj = itObjs2.next();
						if ((relationXml.getMembershipsAttrs().get(entryattr) != null)
								&& (relationXml.getMembershipsAttrs().get(
										entryattr).get(entryobj) != null)
								&& (relationXml.getMembershipsAttrs().get(
										entryattr).get(entryobj) < min))
							min = relationXml.getMembershipsAttrs().get(
									entryattr).get(entryobj);
					}
					if (lowerConcept.getObjects().size() == 0) {
						a.setMembership(0.0);
					} else
						a.setMembership(min);

					if (!upperConcept.getAttributes().contains(entryattr)) {
						a.setOwn(true);
					} else
						a.setOwn(false);
					attrs.add(a);
					if ((min > max) && a.isOwn()) {
						max = min;
						aMaxList.clear();
						aMaxList.add(a);
					} else if ((min == max) && a.isOwn()) {
						aMaxList.add(a);
					}

				}
				if (lowerConcept.getObjects().size() != 0) {
					if (aMaxList.size() == 0) {
						xmlLowerConcept.setLabel(labelsUpper.get(lowerConcept));
					} else {
						for (Iterator iterator = aMaxList.iterator(); iterator
								.hasNext();) {
							it.unisa.di.datasets.xml.ffca.lattice.Attribute attribute = (it.unisa.di.datasets.xml.ffca.lattice.Attribute) iterator.next();
							xmlLowerConcept.setLabel(xmlLowerConcept.getLabel()
									+ "#" + attribute.getName());
						}
					}
				} else {
					xmlLowerConcept.setLabel("#target");
				}

				if(target.equals(lowerConcept))
					xmlLowerConcept.setId(1 + "");
				else{ 
					xmlLowerConcept.setId(id + "");
					lowerConcept.setId(id);
					id++;
				}
				
				concepts.getConcept().add(xmlLowerConcept);
				if (idsConceptsAlreadyAdded.get(lowerConcept) != null)
					idsConceptsAlreadyAdded.remove(lowerConcept);
				idsConceptsAlreadyAdded.put(lowerConcept, xmlLowerConcept);

				conceptMap.put(lowerConcept.getId(), lowerObjMem);

			} else {
				String labels = labelsUpper.get(lowerConcept);
				String[] arrayLabels = labels.split("#");
				String newLabel = "#";
				String[] arrayLabelsNew = xmlUpperConcept.getLabel().split("#");
				List<String> notDuplicated = new ArrayList<String>();
				for (int j = 1; j < arrayLabelsNew.length; j++) {
					if (!notDuplicated.contains(arrayLabelsNew[j])) {
						newLabel += arrayLabelsNew[j] + "#";
						notDuplicated.add(arrayLabelsNew[j]);
					}
				}
				for (int j = 1; j < arrayLabels.length; j++) {
					if (!notDuplicated.contains(arrayLabels[j])) {
						newLabel += arrayLabels[j] + "#";
						notDuplicated.add(arrayLabels[j]);
					}
				}

				labelsUpper.remove(lowerConcept);
				labelsUpper.put(lowerConcept, newLabel);

				lowerConcept.setId(Integer.parseInt(xmlLowerConcept.getId()));
				List<it.unisa.di.datasets.xml.ffca.lattice.Attribute> attrs = xmlLowerConcept
						.getAttribute();
				Iterator<Comparable> itAttr;
				itAttr = lowerConcept.getAttributes().iterator();
				while (itAttr.hasNext()) {
					Comparable entryAttr = itAttr.next();

					for (Iterator iterator = attrs.iterator(); iterator
							.hasNext();) {
						it.unisa.di.datasets.xml.ffca.lattice.Attribute a = (it.unisa.di.datasets.xml.ffca.lattice.Attribute) iterator.next();
						if (a.getName().equalsIgnoreCase(entryAttr.toString())) {
							if ((upperConcept.getAttributes()
									.contains(entryAttr))
									&& a.isOwn())
								a.setOwn(false);
						}
					}
				}
				// Aggiusto la label del lower se il concetto già è stato
				// considerato
				Iterator<it.unisa.di.datasets.xml.ffca.lattice.Attribute> itAttr2 = xmlLowerConcept.getAttribute()
						.iterator();
				ArrayList<it.unisa.di.datasets.xml.ffca.lattice.Attribute> aMaxList = new ArrayList<it.unisa.di.datasets.xml.ffca.lattice.Attribute>();
				double max = Double.MIN_VALUE;
				while (itAttr2.hasNext()) {
					it.unisa.di.datasets.xml.ffca.lattice.Attribute a = itAttr2.next();

					if ((a.getMembership() > max) && (a.isOwn())) {
						max = a.getMembership();
						aMaxList.clear();
						aMaxList.add(a);
					} else if ((a.getMembership() == max) && a.isOwn()) {
						aMaxList.add(a);
					}

				}
				if (lowerConcept.getObjects().size() != 0) {
					if (aMaxList.size() == 0) {
						xmlLowerConcept.setLabel(labelsUpper.get(lowerConcept));
					} else {
						xmlLowerConcept.setLabel("");
						for (Iterator iterator = aMaxList.iterator(); iterator
								.hasNext();) {
							it.unisa.di.datasets.xml.ffca.lattice.Attribute attribute = (it.unisa.di.datasets.xml.ffca.lattice.Attribute) iterator.next();
							xmlLowerConcept.setLabel(xmlLowerConcept.getLabel()
									+ "#" + attribute.getName());
						}
					}
				} else
					xmlLowerConcept.setLabel("#target");
				lowerObjMem = conceptMap.get(lowerConcept.getId());
			}

			// similarity of a formal concept E(k(1), k(2));
			// calcolo dell'intersezione e unione

			Iterator<Entry<String, Double>> itU;

			Iterator<Entry<String, Double>> itL;
			Map<String, Double> allObjMemMax = new HashMap<String, Double>();
			double intersection = 0.0;
			double union = 0.0;
			double conceptSimilarity = 0.0;
			itU = upperObjMem.entrySet().iterator();
			while (itU.hasNext()) {
				Map.Entry<String, java.lang.Double> entryU = (Map.Entry<String, java.lang.Double>) itU
						.next();
				allObjMemMax.put(entryU.getKey(), entryU.getValue());
				itL = lowerObjMem.entrySet().iterator();
				while (itL.hasNext()) {
					Map.Entry<String, java.lang.Double> entryL = (Map.Entry<String, java.lang.Double>) itL
							.next();
					if (entryU.getKey().equals(entryL.getKey())) {
						if (entryU.getValue() < entryL.getValue())
							intersection += entryU.getValue();
						else
							intersection += entryL.getValue();
						break;
					}
				}
			}
			itL = lowerObjMem.entrySet().iterator();
			while (itL.hasNext()) {
				Map.Entry<String, java.lang.Double> entryL = (Map.Entry<String, java.lang.Double>) itL
						.next();
				if (!allObjMemMax.containsKey(entryL.getKey())) {
					allObjMemMax.put(entryL.getKey(), entryL.getValue());
				} else {
					if (entryL.getValue() > allObjMemMax.get(entryL.getKey())) {
						allObjMemMax.remove(entryL.getKey());
						allObjMemMax.put(entryL.getKey(), entryL.getValue());
					}
				}
			}

			for (Iterator<Entry<String, Double>> itValueMax = allObjMemMax
					.entrySet().iterator(); itValueMax.hasNext();) {
				Entry<String, Double> entry = (Entry<String, Double>) itValueMax
						.next();
				union = union + entry.getValue();
			}

			if (union == 0.0)
				conceptSimilarity = 0.0;
			else
				conceptSimilarity = intersection / union;

			conceptSimilarity = arrotondaPerEccesso(conceptSimilarity, 2);

			// fine calcolo similarità tra concetti

			it.unisa.di.datasets.xml.ffca.lattice.Edge e = of.createEdge();
			e.setLowerConceptId("" + edge.getLower().getId());
			e.setUpperConceptId("" + edge.getUpper().getId());
			e.setMembership(conceptSimilarity);
			edges.getEdge().add(e);

		}
		latticeXml.setEdgeSize(edges.getEdge().size());
		latticeXml.setConceptSize(concepts.getConcept().size());
		latticeXml.setEdges(edges);
		latticeXml.setConcepts(concepts);
		latticeXml.setContextUri(relationXml.getCurrentContext());
		System.out.println("# Concepts: "
				+ latticeXml.getConcepts().getConcept().size());
		System.out
				.println("# Edges: " + latticeXml.getEdges().getEdge().size());

		return latticeXml;
	}

	public static it.unisa.di.datasets.xml.ffca.lattice.Lattice getIcebergLattice(
			it.unisa.di.datasets.xml.ffca.lattice.Lattice l, double support) {
		it.unisa.di.datasets.xml.ffca.lattice.Lattice newL = new it.unisa.di.datasets.xml.ffca.lattice.Lattice();
		List<it.unisa.di.datasets.xml.ffca.lattice.Concept> concepts = l.getConcepts()
				.getConcept();
		ArrayList<String> removedConcept = new ArrayList<String>();
		Concepts c = new Concepts();
		it.unisa.di.datasets.xml.ffca.lattice.Concept c1 = new it.unisa.di.datasets.xml.ffca.lattice.Concept();

		for (Iterator iterator = concepts.iterator(); iterator.hasNext();) {
			it.unisa.di.datasets.xml.ffca.lattice.Concept concept = (it.unisa.di.datasets.xml.ffca.lattice.Concept) iterator
					.next();

			if (concept.getSupport() < support) {
				removedConcept.add(concept.getId());
			} else {
				c.getConcept().add(concept);
			}
			if (concept.getId().equals("1")) {
				c1 = concept;
				c.getConcept().add(c1);
			}

		}
		newL.setContextUri(l.getContextUri());
		newL.setConcepts(c);

		Edges e = new Edges();
		// @mapConcept usata per verificare se un concetto è solo lower (boolean
		// value = false)
		int countEdges = 1;
		Map<String, Boolean> mapConcept = new HashMap<String, Boolean>();
		for (Iterator iterator2 = l.getEdges().getEdge().iterator(); iterator2
				.hasNext();) {
			it.unisa.di.datasets.xml.ffca.lattice.Edge edge = (it.unisa.di.datasets.xml.ffca.lattice.Edge) iterator2
					.next();

			if (!removedConcept.contains(edge.getLowerConceptId())
					&& !removedConcept.contains(edge.getUpperConceptId())) {
				e.getEdge().add(edge);
				countEdges++;
				if (!mapConcept.containsKey(edge.getLowerConceptId())) {
					mapConcept.put(edge.getLowerConceptId(), false);
				}
				if (!mapConcept.containsKey(edge.getUpperConceptId())) {
					mapConcept.put(edge.getUpperConceptId(), true);
				} else if (mapConcept.get(edge.getUpperConceptId()) == false) {
					mapConcept.remove(edge.getUpperConceptId());
					mapConcept.put(edge.getUpperConceptId(), true);
				}
			}
		}
		Iterator<Entry<String, Boolean>> it = mapConcept.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<java.lang.String, java.lang.Boolean> entry = (Map.Entry<java.lang.String, java.lang.Boolean>) it
					.next();
			it.unisa.di.datasets.xml.ffca.lattice.Edge edge;
			if (entry.getValue() == false) {
				edge = new it.unisa.di.datasets.xml.ffca.lattice.Edge();
				edge.setLowerConceptId(c1.getId());
				edge.setUpperConceptId(entry.getKey());
				e.getEdge().add(edge);
				countEdges++;
			}
		}
		newL.setEdges(e);
		newL.setAttributeSize(l.getAttributeSize());
		newL.setConceptSize(l.getConceptSize() - removedConcept.size());
		newL.setEdgeSize(countEdges);
		newL.setObjectSize(l.getObjectSize());
		System.out.println("Iceberg Lattice:");
		System.out.println("# Concepts: "
				+ newL.getConcepts().getConcept().size());
		System.out.println("# Edges: " + newL.getEdges().getEdge().size());

		return newL;
	}

	public it.unisa.di.datasets.xml.ffca.lattice.Lattice getIcebergLattice(
			String inputFile, double support) throws Exception {

		it.unisa.di.datasets.xml.ffca.lattice.Lattice l = getFuzzyLatticeXML(inputFile);
		return getIcebergLattice(l, support);
	}
	
	public it.unisa.di.datasets.xml.ffca.lattice.Lattice getIcebergLattice(double support) throws Exception{
		String inputFile = properties.getInputFile();
		return getIcebergLattice(inputFile, support);
	}
	
	public it.unisa.di.datasets.xml.ffca.lattice.Lattice getIcebergLattice() throws Exception{
		double minSupp = properties.getSupp();
		return getIcebergLattice(minSupp);
	}
	
	
	private static double arrotondaPerEccesso(double valore, int cifreDecimali) {
		double fattore = Math.pow(10, cifreDecimali);
		return Math.ceil(valore * fattore) / fattore;
	}


}
