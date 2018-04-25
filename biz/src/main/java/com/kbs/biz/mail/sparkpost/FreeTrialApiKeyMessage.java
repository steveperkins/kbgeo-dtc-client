package com.kbs.biz.mail.sparkpost;

public class FreeTrialApiKeyMessage extends SparkMessageContent {

	public FreeTrialApiKeyMessage(String firstName, String apiKey, String contractType) {
		super();
		
		super.withTemplate("free-" + contractType + "-trial-api-key");
		super.addSubstitutionData("firstName", firstName);
		super.addSubstitutionData("apiKey", apiKey);
	}
	
}
