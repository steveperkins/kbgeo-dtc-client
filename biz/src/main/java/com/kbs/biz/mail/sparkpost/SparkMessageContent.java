package com.kbs.biz.mail.sparkpost;

import java.lang.reflect.Type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class SparkMessageContent {
	String from;
	String subject;
	String text;
	String templateId;
	Map<String, Object> substitutionData = new HashMap<String, Object>();
	Map<String, GsonList> substitutionListData = new HashMap<String, GsonList>();
	
	public SparkMessageContent from(String from) {
		this.from = from;
		return this;
	}
	
	public SparkMessageContent withSubject(String subject) {
		this.subject = subject;
		return this;
	}
	
	public SparkMessageContent withText(String text) {
		this.text = text;
		return this;
	}
	
	public SparkMessageContent withTemplate(String templateId) {
		this.templateId = templateId;
		return this;
	}
	
	public SparkMessageContent withSubstitutionData(Map<String, Object> substitutionData) {
		this.substitutionData = substitutionData;
		return this;
	}
	
	public SparkMessageContent addSubstitutionData(String key, Object value) {
		if(null == substitutionData) substitutionData = new HashMap<>();
		this.substitutionData.put(key, value);
		return this;
	}
	
	public SparkMessageContent addSubstitutionList(String key, List value, Type listType) {
		if(null == substitutionListData) substitutionListData = new HashMap<String, GsonList>();
		this.substitutionListData.put(key, new GsonList(value, listType));
		return this;
	}
	
	public String toJson() {
		if(StringUtils.isNotBlank(templateId)) return toTemplateJson();
		return toInlineJson();
	}
	
	private String toTemplateJson() {
		return String.format("\"template_id\":\"%s\"", templateId);
	}
	private String toInlineJson() {
		return String.format("\"from\":\"%s\", \"subject\":\"%s\", \"text\":\"%s\"", 
				from,
				subject,
				text);
	}
	
	class GsonList {
		private List value;
		private Type type;
		
		public GsonList(List value, Type type) {
			this.value = value;
			this.type = type;
		}
		public List getValue() {
			return value;
		}
		public void setValue(List value) {
			this.value = value;
		}
		public Type getType() {
			return type;
		}
		public void setType(Type type) {
			this.type = type;
		}
	}
}
