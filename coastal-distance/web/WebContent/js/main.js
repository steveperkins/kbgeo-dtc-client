jQuery(function($) {

	$(function(){
		$('#main-slider.carousel').carousel({
			interval: 3000,
			pause: false
		});
	});

	//Ajax contact
	$('.contact-form').submit(function (event) {
		event.preventDefault();
		$this = $(this);
		
		// Validate input
		var contactNameControl = $("#contactName");
		var contactEmailControl = $("#contactEmail");
		var contactMessageControl = $("#contactMessage");
		
		contactNameControl.removeClass("has-error");
		contactEmailControl.removeClass("has-error");
		contactMessageControl.removeClass("has-error");
		
		var error = false;
		
		var contactName = contactNameControl.val();
		if(!contactName) {
			error = true;
			contactNameControl.addClass("has-error");
		}
		var contactEmail = contactEmailControl.val();
		var re = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
		if(!contactEmail || !re.test(contactEmail)) {
			error = true;
			contactEmailControl.addClass("has-error");
		}
		var contactMessage = contactMessageControl.val();
		if(!contactMessage) {
			error = true;
			contactMessageControl.addClass("has-error");
		}
		if(error) return;
		
		var contactPhone = $("#contactPhone").val();
		var contactCompany = $("#contactCompany").val();
		var contactFreeTrialKey = document.getElementById("contactFreeTrialKey").checked;
		// Send the message
		$.ajax({
			type: 'POST',
			url: 'http://biz.kbgeo.com/contact-us',
			//url: 'http://localhost:8080/biz/contact-us',
			//headers: { "kb-auth-token": "gaA34o32" },
			data: JSON.stringify({
					name: contactName,
					company: contactCompany,
					email: contactEmail,
					phone: contactPhone,
					message: contactMessage,
					requestFreeTrial: contactFreeTrialKey
				}),
			success: function(data) {
				$("#main-contact-form").prev().text(data.message).fadeIn().delay(3000).fadeOut();
				$("#contactUsSubmit").attr("disabled", true);
			},
			contentType: 'application/json',
			dataType: 'json'
		});
		
		return false;
	});

	//smooth scroll
	$('.navbar-nav > li').click(function(event) {
		event.preventDefault();
		var target = $(this).find('>a').prop('hash');
		$('html, body').animate({
			scrollTop: $(target).offset().top - 100
		}, 500);
	});

	//scrollspy
	$('[data-spy="scroll"]').each(function () {
//		var $spy = $(this).scrollspy('refresh')
		var $spy = $(this).scrollspy({
			offset: $(window).height() * 0.35
		});
	})

});