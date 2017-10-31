package com.kbs.biz.mail.sparkpost;

public class FreeTrialApiKeyMessage extends SparkMessageContent {

	public FreeTrialApiKeyMessage(String firstName, String apiKey) {
		super();
		
		super.withTemplate("free-trial-api-key");
		super.addSubstitutionData("firstName", firstName);
		super.addSubstitutionData("apiKey", apiKey);
	}
	
}
