jQuery(function($) {

	$(function(){
		$('#main-slider.carousel').carousel({
			interval: 3000,
			pause: false
		});
	});

	//Ajax contact
	$('.contact-form').submit(function () {
		$this = $(this);
		
		// Validate input
		var emailNameControl = $("#emailName");
		var emailAddressControl = $("#emailAddress");
		
		emailNameControl.removeClass("has-error");
		emailAddressControl.removeClass("has-error");
		
		var error = false;
		
		var emailName = emailNameControl.val();
		if(!emailName) {
			error = true;
			emailNameControl.addClass("has-error");
		}
		var emailAddress = emailNameControl.val();
		var re = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
		if(!emailAddress || !re.test(emailAddress)) {
			error = true;
			emailAddressControl.addClass("has-error");
		}
		if(error) return;
		
		// Send the message
		$.post($(this).attr('action'), function(data) {
			$this.prev().text(data.message).fadeIn().delay(3000).fadeOut();
		},'json');
		
		
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