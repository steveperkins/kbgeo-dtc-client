package com.kbs.geo.coastal.validator;

import org.apache.commons.lang3.StringUtils;

import com.kbs.geo.coastal.http.exception.InvalidAddressException;

public class AddressValidator {
	
	public void validate(String address) {
		if(StringUtils.isBlank(address)) throw new InvalidAddressException("Address is required");
		if(address.length() <= 3) throw new InvalidAddressException("Address too short");
	}
}
