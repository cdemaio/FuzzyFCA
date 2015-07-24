package it.unisa.di.categorizer.ffca.utils.rules.apriori;

/* -------------------------------------------------------------------------- */
/*                                                                            */
/*                              RULE LIST                                     */
/*                                                                            */
/*                            Frans Coenen                                    */
/*                                                                            */
/*                         Tuesday 2 March 2004                               */
/*                                                                            */
/*                    Department of Computer Science                          */
/*                     The University of Liverpool                            */
/*                                                                            */
/* -------------------------------------------------------------------------- */

/* Class structure

 AssocRuleMining
 |
 +-- RuleList			*/

// Java packages
import it.unisa.di.categorizer.ffca.utils.FuzzyFCAProperties;
import it.unisa.di.categorizer.ffca.utils.XmlRulesWR;
import it.unisa.di.datasets.xml.ffca.rules.Rule;
import it.unisa.di.datasets.xml.ffca.rules.Rules;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

/**
 * Set of utilities to support various Association Rule Mining (ARM) algorithms
 * included in the LUCS-KDD suite of ARM programs.
 * 
 * @author Frans Coenen
 * @version 2 March 2004
 */

public class RuleList extends AssocRuleMining {

	/* ------ FIELDS ------ */

	// --- Data structures ---
	/** Rule node in linked list of rules (either ARs or CRs). */

	protected class RuleNode {
		/** Antecedent of AR. */
		protected short[] antecedent;
		/** Consequent of AR. */
		protected short[] consequent;
		/**
		 * The confidence value associate with the rule represented by this
		 * node.
		 */
		double confidenceForRule = 0.0;
		/** Link to next node */
		RuleNode next = null;

		/**
		 * Three argument constructor
		 * 
		 * @param antecedent
		 *            the antecedent (LHS) of the AR.
		 * @param consequent
		 *            the consequent (RHS) of the AR.
		 * @param support
		 *            the associated confidence value.
		 */

		private RuleNode(short[] ante, short[] cons, double confValue) {
			antecedent = ante;
			consequent = cons;
			confidenceForRule = confValue;
		}
	}

	/** The reference to start of the rule list. */
	protected RuleNode startRulelist = null;

	/* ------ CONSTRUCTORS ------ */

	/** Default constructor to create an instance of the class RuleList */

	public RuleList() {
	}

	/* ------ METHODS ------ */

	/* -------------------------------------------------------------- */
	/*                                                                */
	/* RULE LINKED LIST ORDERED ACCORDING TO CONFIDENCE */
	/*                                                                */
	/* -------------------------------------------------------------- */

	/*
	 * Methods for inserting rules into a linked list of rules ordered according
	 * to confidence (most confident first). Each rule described in terms of 3
	 * fields: 1) Antecedent (an item set), 2) a consequent (an item set), 3) a
	 * confidence value (double). <P> The support field is not used.
	 */

	/*
	 * INSERT (ASSOCIATION/CLASSIFICATION) RULE INTO RULE LINKED LIST (ORDERED
	 * ACCORDING CONFIDENCE).
	 */

	/**
	 * Inserts an (association/classification) rule into the linkedlist of rules
	 * pointed at by <TT>startRulelist</TT>.
	 * <P>
	 * The list is ordered so that rules with highest confidence are listed
	 * first. If two rules have the same confidence the new rule will be placed
	 * after the existing rule. Thus, if using an Apriori approach to generating
	 * rules, more general rules will appear first in the list with more
	 * specific rules (i.e. rules with a larger antecedent) appearing later as
	 * the more general rules will be generated first.
	 * 
	 * @param antecedent
	 *            the antecedent (LHS) of the rule.
	 * @param consequent
	 *            the consequent (RHS) of the rule.
	 * @param confidenceForRule
	 *            the associated confidence value.
	 */

	protected void insertRuleintoRulelist(short[] antecedent,
			short[] consequent, double confidenceForRule) {

		// Create new node
		RuleNode newNode = new RuleNode(antecedent, consequent,
				confidenceForRule);

		// Empty list situation
		if (startRulelist == null) {
			startRulelist = newNode;
			return;
		}

		// Add new node to start
		if (confidenceForRule > startRulelist.confidenceForRule) {
			newNode.next = startRulelist;
			startRulelist = newNode;
			return;
		}

		// Add new node to middle
		RuleNode markerNode = startRulelist;
		RuleNode linkRuleNode = startRulelist.next;
		while (linkRuleNode != null) {
			if (confidenceForRule > linkRuleNode.confidenceForRule) {
				markerNode.next = newNode;
				newNode.next = linkRuleNode;
				return;
			}
			markerNode = linkRuleNode;
			linkRuleNode = linkRuleNode.next;
		}

		// Add new node to end
		markerNode.next = newNode;
	}

	/* ----------------------------------- */
	/*                                     */
	/* GET METHODS */
	/*                                     */
	/* ----------------------------------- */

	/* GET NUMBER OF RULES */

	/**
	 * Returns the number of generated rules.
	 * 
	 * @return the number of rules.
	 */

	public int getNumRules() {
		int number = 0;
		RuleNode linkRuleNode = startRulelist;

		// Loop through linked list
		while (linkRuleNode != null) {
			number++;
			linkRuleNode = linkRuleNode.next;
		}

		// Return
		return (number);
	}

	/* ----------------------------------- */
	/*                                     */
	/* SET METHODS */
	/*                                     */
	/* ----------------------------------- */

	/* SET RECONVERSION ARRAYS */

	/**
	 * Sets the reconversion array reference values.
	 * 
	 * @param conversionArrayRef
	 *            the reference to the 2-D array used to renumber columns for
	 *            input data in terms of frequency of single attributes
	 *            (reordering will enhance performance for some ARM and CARM
	 *            algorithms).
	 * @param reconversionArrayRef
	 *            the reference to the 1-D array used to reconvert input data
	 *            column numbers to their original numbering where the input
	 *            data has been ordered to enhance computational efficiency.
	 */

	protected void setReconversionArrayRefs(int[][] conversionArrayRef,
			short[] reconversionArrayRef) {
		conversionArray = conversionArrayRef;
		reconversionArray = reconversionArrayRef;
	}

	/* ------------------------------ */
	/*                                */
	/* OUTPUT */
	/*                                */
	/* ------------------------------ */

	/* OUTPUT RULE LINKED LIST */
	/**
	 * Outputs contents of rule linked list (if any)
	 * 
	 * @throws FileNotFoundException
	 * @throws JAXBException
	 */

	public List<Rule> outputRules() throws FileNotFoundException, JAXBException {
		FuzzyFCAProperties prop;
		FileOutputStream fos;

		try {
			prop = FuzzyFCAProperties.getInstance();
			fos = new FileOutputStream(prop
					.getFcaRulesXmlFileName());
			return outputRules(startRulelist, fos);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Outputs given rule list.
	 * 
	 * @param ruleList
	 *            the given rule list.
	 * @throws JAXBException
	 */

	public List<Rule> outputRules(RuleNode ruleList, FileOutputStream fos)
			throws JAXBException {
		// Contenitore della regola e delle regole
		Rule r = null;
		Rules rules = new Rules();

		// Setto xml writer and reader
		XmlRulesWR xmlrw = new XmlRulesWR(fos);

		// Mie modifiche
		try {
			FuzzyFCAProperties prop = FuzzyFCAProperties.getInstance();

			// Check for empty rule list
			if (ruleList == null)
				System.out.println("No rules generated!");
			else
				System.out.println("Generating file: "
						+ prop.getFcaRulesTxtFileName());

			FileWriter writer = new FileWriter(prop.getFcaRulesTxtFileName());

			// Loop through rule list
			int number = 1;
			RuleNode linkRuleNode = ruleList;
			while (linkRuleNode != null) {
				r = new Rule();
				writer.write("(" + number + ") ");

				r.setId("" + number);

				outputRule(linkRuleNode, writer, r);
				writer.write(" " + twoDecPlaces(linkRuleNode.confidenceForRule)
						+ "%\n");

				r.setConfidence("" + linkRuleNode.confidenceForRule);

				rules.getRule().add(number - 1, r);
				number++;
				linkRuleNode = linkRuleNode.next;
			}

			writer.close();

			System.out.println("Generating file: "
					+ prop.getFcaRulesXmlFileName());
			xmlrw.write(rules);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rules.getRule();

	}

	/**
	 * Outputs a rule.
	 * 
	 * @param rule
	 *            the rule to be output.
	 * @throws IOException
	 */

	private void outputRule(RuleNode rule, FileWriter writer, Rule r)
			throws IOException {
		String value = new String();

		value = outputItemSet(rule.antecedent);
		r.setAntecedent(value);
		writer.write(value);

		writer.write(" -> ");

		value = outputItemSet(rule.consequent);
		r.setConsequent(value);
		writer.write(value);
	}

	/* OUTPUT RULE LINKED LIST WITH RECONVERSION */
	/** Outputs contents of rule linked list (if any) with reconversion. */

	public void outputRulesWithReconversion() {
		// Check for empty rule list
		if (startRulelist == null)
			System.out.println("No rules generated!");

		// Loop through rule list
		int number = 1;
		RuleNode linkRuleNode = startRulelist;
		while (linkRuleNode != null) {
			System.out.print("(" + number + ") ");
			outputItemSetWithReconversion(linkRuleNode.antecedent);
			System.out.print(" -> ");
			outputItemSetWithReconversion(linkRuleNode.consequent);
			System.out.println(" "
					+ twoDecPlaces(linkRuleNode.confidenceForRule) + "%");
			number++;
			linkRuleNode = linkRuleNode.next;
		}
	}

	/* OUTPUT NUMBER OF RULES */

	/** Outputs number of generated rules (ARs or CARS). */

	public void outputNumRules() {
		System.out.println("Number of rules         = " + getNumRules());
	}
}
