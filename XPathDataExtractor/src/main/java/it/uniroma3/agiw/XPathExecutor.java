package it.uniroma3.agiw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;

public class XPathExecutor {
	public List<String> executeXPath(String html, String xpath, String[] strFunctions) throws XPathExpressionException {
		List<String> output = new ArrayList<String>();

		TagNode tagNode = new HtmlCleaner().clean(html);
		try {
			Document doc = new DomSerializer(
					new CleanerProperties()).createDOM(tagNode);

			XPath xpathObj = XPathFactory.newInstance().newXPath();


			//se è stata lanciata una funzione che restituisce una stringa
			if (!xpath.startsWith("/") &&
					Arrays.asList(strFunctions).contains(xpath.substring(0, xpath.indexOf("(")))) {
				String xpathRes = (String)xpathObj.evaluate(xpath, 
						doc, XPathConstants.STRING);
				output.add(xpathRes);
			} else {

				NodeList nl = (NodeList) xpathObj.evaluate(xpath, 
						doc, XPathConstants.NODESET);
				for (int i = 0; i < nl.getLength(); i++) {
					Node child = nl.item(i);

					/*if ((child instanceof CharacterData && !(child instanceof Comment))
		            	|| child instanceof EntityReference) {
		            	output.add(child.getNodeValue().trim());
		            }
		            else if (child.getNodeType() == Node.ELEMENT_NODE) {
		            	output.add(child.getTextContent().trim());
		            }*/

					output.add(child.getTextContent().trim());
				}
			}


		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return output;

	}
}
