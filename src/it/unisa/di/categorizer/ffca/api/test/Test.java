package it.unisa.di.categorizer.ffca.api.test;
import it.unisa.di.categorizer.ffca.api.Analyzer;
import it.unisa.di.categorizer.ffca.utils.FuzzyFCAProperties;
import it.unisa.di.datasets.xml.ffca.lattice.Lattice;
import it.unisa.di.datasets.xml.wrappers.WrapperXMLFuzzyFCAOutput;

import java.io.FileOutputStream;

import javax.xml.bind.JAXBException;




public class Test {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String fuzzyFormalContextInputFileName = "";
		String fuzzyLatticeOutput = "";
		
		if(args.length==0){
			fuzzyFormalContextInputFileName = FuzzyFCAProperties.getInstance().getInputFile();
			fuzzyLatticeOutput = FuzzyFCAProperties.getInstance().getOutputFile();
		}
		else{
			if(args.length<2){
				System.out.println("Parametri Mancanti: 1 - Formal Context FileName; 2 - Fuzzy Lattice File Name");
			}
			else
			{
				fuzzyFormalContextInputFileName = args[0];
				fuzzyLatticeOutput = args[1];
			}
		}
		
		System.out.println("Formal Context FileName: " + fuzzyFormalContextInputFileName);
		System.out.println("Fuzzy Lattice File Name: " + fuzzyLatticeOutput);
			
		
		Analyzer analyzer = new Analyzer();
		Lattice l;
		
		l = analyzer.getFuzzyLatticeXML(fuzzyFormalContextInputFileName);
		long inizioScritturaXML = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream(fuzzyLatticeOutput);
		WrapperXMLFuzzyFCAOutput ioLattice = new WrapperXMLFuzzyFCAOutput();
		try {
			ioLattice.write(l, fos);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Tempo Scrittura XML (sec): "
				+ ((System.currentTimeMillis() - inizioScritturaXML)/1000));

	}

}
