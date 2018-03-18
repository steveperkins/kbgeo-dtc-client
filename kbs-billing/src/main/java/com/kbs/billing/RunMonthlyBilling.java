package com.kbs.billing;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunMonthlyBilling {
	public static void main(String[] args) {
		RunMonthlyBilling instance = new RunMonthlyBilling();
		instance.run(Calendar.getInstance().get(Calendar.MONTH));
	}
	
	private final Logger LOG = LoggerFactory.getLogger(RunMonthlyBilling.class);
	private final Integer FLAT_FEE_REQUEST_THRESHOLD = 500;
	private final Long CUSTOMER_FEE = 50000L;
	private final Long PRICE_PER_CALL = 25L;
	
	private BillingDataProvider dataProvider = new BillingDataProvider();
	
	public void run(Integer month) {
		// TODO get a list of clients for which to run monthly billing
		// Right now there is only one client
		Long clientId = 1L;
		
		// First of last month
		Calendar startCal = Calendar.getInstance();
		startCal.roll(Calendar.MONTH, -1);
		startCal.set(Calendar.DATE, 1);
		startCal.set(Calendar.HOUR, 0);
		startCal.set(Calendar.MINUTE, 0);
		startCal.set(Calendar.SECOND, 0);
		startCal.set(Calendar.MILLISECOND, 0);
		
		// First of the current month
		Calendar endCal = Calendar.getInstance();
		endCal.set(Calendar.DATE, 1);
		
		run(clientId, startCal.getTime(), endCal.getTime());
		
		dataProvider.shutdown();
	}
	
	public void run(Long clientId, Date startDate, Date endDate) {
		LOG.debug("Running monthly billing for client ID " + clientId + ", startDate '" + startDate.toString() + "', endDate '" + endDate + "'");
		try {
			String clientName = dataProvider.getClientName(clientId);
			
			Invoice invoice = new Invoice(new Date(), clientName);
			invoice.setClientId(clientId);
			invoice.setCreatedDate(new Date());
			invoice.setInvoiceDate(new Date());
			
			Long billableRequestCount = dataProvider.getSuccessRequestCount(clientId, startDate, endDate);
			if(billableRequestCount <= 0) return;
			
			List<InvoiceLine> lineItems = new ArrayList<InvoiceLine>();
			lineItems.add(new InvoiceLine(1, "KBGeo.com Distance to Coast - fixed price under threshold", CUSTOMER_FEE));
			if(billableRequestCount > FLAT_FEE_REQUEST_THRESHOLD) {
				lineItems.add(new InvoiceLine(2, "KBGeo.com Distance to Coast - Super Saver Tier", PRICE_PER_CALL * (billableRequestCount - FLAT_FEE_REQUEST_THRESHOLD)));
			}
			
			Long subtotal = 0L;
			for(InvoiceLine line: lineItems) {
				subtotal += line.getAmount();
			}
			invoice.setSubtotal(subtotal);
			invoice.setGrandTotal(subtotal);
			
			// Save to billing database
			
			ResultSet requests = dataProvider.getMonthRequests(clientId, startDate, endDate);
			// TODO Send email notification with request log attached
		} catch(Exception e) {
			LOG.error("COULD NOT COMPLETE BILLING FOR client ID " + clientId + ", startDate '" + startDate.toString() + "', endDate '" + endDate + "'!", e);
			// Send error email notification
			
		}
	}
}
