package com.kbs.geo.crime.preprocessing;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.kbs.geo.crime.preprocessing.model.ZipCodeLookupModel;

@Service
public class ZipLookupService {
	private final String BASE_URL = "http://www.webservicex.net/uszip.asmx/GetInfoByCity";

	public ZipCodeLookupModel lookupCityAndState(String city, String state) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL)
		        .queryParam("USCity", city);
		
		HttpEntity<String> response = new RestTemplate().exchange(
		        builder.build().encode().toUri(), 
		        HttpMethod.GET, 
		        null, 
		        String.class);
		
		return parseResponseXml(response.getBody(), state);
	}
	
	public ZipCodeLookupModel parseResponseXml(String xml, String state) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		System.out.println("Response: " + xml);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    InputSource is = new InputSource(new StringReader(xml));
	    Document doc = builder.parse(is);
	    
	    XPathFactory xpathFactory = XPathFactory.newInstance();
	    XPath xpath = xpathFactory.newXPath();
	    
	    XPathExpression nodesByStateExpression = xpath.compile("//Table[STATE='" + state.toUpperCase() + "']");
	    NodeList entries = (NodeList) nodesByStateExpression.evaluate(doc, XPathConstants.NODESET);
	    if(entries.getLength() == 0) return null;
	    
	    Node table = entries.item(0);
	    ZipCodeLookupModel model = new ZipCodeLookupModel();
	    model.setCity(
				((String)xpath
					.evaluate("CITY", table, XPathConstants.STRING)).toUpperCase());
	    model.setState(
	    		((String)xpath
	    				.evaluate("//STATE", table, XPathConstants.STRING)));
	    model.setZip(
	    		((String)xpath
	    				.evaluate("//ZIP", table, XPathConstants.STRING)));
	    model.setAreaCode(
	    		((String)xpath
	    				.evaluate("//AREA_CODE", table, XPathConstants.STRING)));
	    return model;
	}
}
