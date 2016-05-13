package it.uniroma3.agiw;

import java.io.IOException;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class XPathTester implements XPathProgram {
	private String htmlPage;
	private String xpath;
	
	public XPathTester(String htmlPage, String xpath) {
		this.htmlPage = htmlPage;
		this.xpath = xpath;
	}

	public void execute() {
		XPathExecutor executor = new XPathExecutor();
		try {
			Document doc = Jsoup.connect(htmlPage).get();
			String html = doc.html();
			List<String> xpathResult = executor.executeXPath(html, this.xpath);
			System.out.println(xpathResult);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
