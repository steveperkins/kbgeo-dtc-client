package com.kbs.biz.mail.sparkpost;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;

import com.google.gson.Gson;
import com.kbs.biz.mail.sparkpost.SparkMessageContent.GsonList;

public class SparkMailMessage {
	String[] recipients;
	SparkMessageContent content;
	
	public SparkMailMessage() {}
	public SparkMailMessage(SparkMessageContent content) {
		this.content = content;
	}
	public String toJson() {
		StringBuilder recipientsBuilder = new StringBuilder();
		for(int x = 0; x < recipients.length; x++) {
			if(x > 0) recipientsBuilder.append(",");
			recipientsBuilder.append(String.format("{\"address\":\"%s\"}", recipients[x]));
		}
		
		String json = String.format("{\"content\": {%s}, \"recipients\":[%s]", 
				content.toJson(),
				recipientsBuilder.toString());
		if(MapUtils.isNotEmpty(content.substitutionData)) {
			StringBuilder sb = new StringBuilder();
			sb.append(", \"substitution_data\":{");
			
			Boolean needsComma = Boolean.FALSE;
			for(Map.Entry<String, Object> entry: content.substitutionData.entrySet()) {
				if(needsComma) sb.append(",");
				needsComma = Boolean.TRUE;
				if(entry.getValue() instanceof String) sb.append(String.format("\"%s\":\"%s\"", entry.getKey(), entry.getValue()));
				else sb.append(String.format("\"%s\":%s", entry.getKey(), new Gson().toJson(entry.getValue())));
			}
			for(Map.Entry<String, GsonList> entry: content.substitutionListData.entrySet()) {
				if(needsComma) sb.append(",");
				needsComma = Boolean.TRUE;
				sb.append(String.format("\"%s\":%s", entry.getKey(), new Gson().toJson(entry.getValue().getValue(), entry.getValue().getType())));
			}
			sb.append("}");
			json += sb.toString();
		}
		
		return json + "}";
	}
	
	public SparkMailMessage withContent(SparkMessageContent content) {
		this.content = content;
		return this;
	}
	
	public SparkMailMessage withRecipients(String... recipients) {
		this.recipients = recipients;
		return this;
	}
	
}
