package com.kbs.biz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kbs.biz.model.ContactUs;
import com.kbs.biz.model.JsonResponse;
import com.kbs.biz.service.ContactUsService;

//@CrossOrigin(origins = "*", allowedHeaders={"kb-auth-token"}, methods={RequestMethod.OPTIONS, RequestMethod.POST})
@RestController
@RequestMapping(value="contact-us")
public class ContactUsController {
	@Autowired
	ContactUsService contactUsService;
	
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody JsonResponse submitContactUs(@RequestBody ContactUs contactUs) {
		JsonResponse response = new JsonResponse();
		contactUs = contactUsService.save(contactUs);
		response.setObject(contactUs);
		response.setMessage("Thank you! We look forward to speaking with you soon.");
		return response;
	}
}
