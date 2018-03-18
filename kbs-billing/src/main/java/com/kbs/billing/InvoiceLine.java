package com.kbs.billing;

public class InvoiceLine {
	private Long id;
	private Integer sequence;
	private String description;
	private Long amount;
	
	public InvoiceLine() {}
	public InvoiceLine(Integer sequence, String description, Long amount) {
		this.sequence = sequence;
		this.description = description;
		this.amount = amount;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getAmount() {
		return amount;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
	}
}
