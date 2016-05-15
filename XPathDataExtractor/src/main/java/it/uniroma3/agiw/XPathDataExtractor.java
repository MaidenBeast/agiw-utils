package it.uniroma3.agiw;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

public class XPathDataExtractor implements XPathProgram {
	private String sourceJson;
	private String inJson;
	private String outJson;
	private XPathExecutor executor;
	private String[] strFunctions;
	
	public XPathDataExtractor(String sourceJson, String inJson, String outJson, String[] strFunctions) {
		this.sourceJson = sourceJson;
		this.inJson = inJson;
		this.outJson = outJson;
		this.executor = new XPathExecutor();
		this.strFunctions = strFunctions;
	}

	public void execute() {
		JSONParser parser = new JSONParser();
		
		try {
			JSONObject srcJsonObj = (JSONObject)parser.parse(new FileReader(sourceJson));
			JSONObject inJsonObj = (JSONObject)parser.parse(new FileReader(inJson));
			
			JSONObject siteOutObj = new JSONObject();
			
			for (Object key1 : srcJsonObj.keySet()) { //itera per sito
				String keyStr1 = (String)key1;
				
				JSONObject siteSrcObj = (JSONObject) srcJsonObj.get(keyStr1);
				JSONObject siteInObj = (JSONObject) inJsonObj.get(keyStr1);
				
				JSONObject productOutObj = new JSONObject();
				
				for (Object key2 : siteSrcObj.keySet()) { //itera per prodotto
					String keyStr2 = (String)key2;
					String productKey = keyStr2.substring(keyStr2.indexOf("-")+1);
					
					JSONArray productSrcArr = (JSONArray) siteSrcObj.get(keyStr2);
					JSONArray productInArr = (JSONArray) siteInObj.get(productKey);
					
					JSONObject idTypeJsonObj = new JSONObject();
					
					for (Object ruleObj : productInArr) { //itera per ogni regola XPath
						JSONObject jsonRuleObj = (JSONObject)ruleObj;
						String rule = (String)jsonRuleObj.get("rule");
						String attr_name = (String)jsonRuleObj.get("attribute_name");
						
						System.out.println("================ GETTING "+attr_name+" INFOS ================");
						
						JSONArray jsonArr = this.executeSingleXPathRule(rule, productSrcArr);
						
						idTypeJsonObj.put(attr_name, jsonArr);
					}
					productOutObj.put(productKey, idTypeJsonObj);
					System.out.println(idTypeJsonObj);
				}
				siteOutObj.put(keyStr1, productOutObj);
				System.out.println(productOutObj);
			}
			
			FileWriter file = new FileWriter(this.outJson);
			file.write(siteOutObj.toJSONString());
			file.flush();
			file.close();
			
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private JSONArray executeSingleXPathRule(String rule, JSONArray urlArr) {
		JSONArray outArr = new JSONArray();
		for(Object urlObj : urlArr) {
			JSONObject jsonObj = new JSONObject();
			
			String url = (String)urlObj;
			//String result = "";
			Object result = "";
			Document doc;
			try {
				doc = Jsoup.connect(url).get();
				String html = doc.html();
				List<String> resultList = this.executor.executeXPath(html, rule, strFunctions);
				if (resultList.size() == 1)
					result = resultList.get(0);
				else if (resultList.size() > 1)  {
					JSONArray resultArr = new JSONArray();
					for(String res : resultList)
						resultArr.add(res);
					result = resultArr;
				}
			} catch (IOException | XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			jsonObj.put(url.replace("\\", ""), result);
			outArr.add(jsonObj);
			System.out.println(jsonObj);
		}
		return outArr;
	}
}
