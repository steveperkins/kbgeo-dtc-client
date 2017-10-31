package com.kbs.geo.crime;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.kbs.geo.crime.preprocessing.ZipLookupService;
import com.kbs.geo.crime.preprocessing.model.ZipCodeLookupModel;

public class Main {
	public static void main(String[] args) {
		ZipLookupService zipLookupService = new ZipLookupService();
		ZipCodeLookupModel result;
		try {
//			result = zipLookupService.lookupCityAndState("Sheridan", "WY");
			result = zipLookupService.parseResponseXml("<?xml version=\"1.0\" encoding=\"utf-8\"?><NewDataSet>  <Table>    <CITY>Sheridan</CITY>    <STATE>AR</STATE>    <ZIP>72150</ZIP>    <AREA_CODE>870</AREA_CODE>    <TIME_ZONE>C</TIME_ZONE>  </Table>  <Table>    <CITY>Sheridan</CITY>    <STATE>CA</STATE>    <ZIP>95681</ZIP>    <AREA_CODE>530</AREA_CODE>    <TIME_ZONE>P</TIME_ZONE>  </Table>  <Table>    <CITY>Sheridan</CITY>    <STATE>IL</STATE>    <ZIP>60551</ZIP>    <AREA_CODE>815</AREA_CODE>    <TIME_ZONE>C</TIME_ZONE>  </Table>  <Table>    <CITY>Sheridan</CITY>    <STATE>IN</STATE>    <ZIP>46069</ZIP>    <AREA_CODE>317</AREA_CODE>    <TIME_ZONE>E</TIME_ZONE>  </Table>  <Table>    <CITY>Sheridan</CITY>    <STATE>ME</STATE>    <ZIP>04775</ZIP>    <AREA_CODE>207</AREA_CODE>    <TIME_ZONE>E</TIME_ZONE>  </Table>  <Table>    <CITY>Sheridan</CITY>    <STATE>MI</STATE>    <ZIP>48884</ZIP>    <AREA_CODE>517</AREA_CODE>    <TIME_ZONE>E</TIME_ZONE>  </Table>  <Table>    <CITY>Sheridan</CITY>    <STATE>MO</STATE>    <ZIP>64486</ZIP>    <AREA_CODE>660</AREA_CODE>    <TIME_ZONE>C</TIME_ZONE>  </Table>  <Table>    <CITY>Sheridan</CITY>    <STATE>MT</STATE>    <ZIP>59749</ZIP>    <AREA_CODE>406</AREA_CODE>    <TIME_ZONE>M</TIME_ZONE>  </Table>  <Table>    <CITY>Sheridan</CITY>    <STATE>NY</STATE>    <ZIP>14135</ZIP>    <AREA_CODE>716</AREA_CODE>    <TIME_ZONE>E</TIME_ZONE>  </Table>  <Table>    <CITY>Sheridan</CITY>    <STATE>OR</STATE>    <ZIP>97378</ZIP>    <AREA_CODE>503</AREA_CODE>    <TIME_ZONE>P</TIME_ZONE>  </Table>  <Table>    <CITY>Sheridan</CITY>    <STATE>TX</STATE>    <ZIP>77475</ZIP>    <AREA_CODE>409</AREA_CODE>    <TIME_ZONE>C</TIME_ZONE>  </Table>  <Table>    <CITY>Sheridan</CITY>    <STATE>WY</STATE>    <ZIP>82801</ZIP>    <AREA_CODE>307</AREA_CODE>    <TIME_ZONE>M</TIME_ZONE>  </Table></NewDataSet>", "WY");
			System.out.println("Result:");
			System.out.println("City: " + result.getCity());
			System.out.println("State: " + result.getState());
			System.out.println("Zip: " + result.getZip());
			System.out.println("Area code: " + result.getAreaCode());
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
