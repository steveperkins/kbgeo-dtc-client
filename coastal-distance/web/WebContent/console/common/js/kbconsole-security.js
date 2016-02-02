var ROOT = "http://localhost:8080/api";
// var ROOT = "https://api.kbgeo.com";
var loginUrl = window.location + "/../../login";
var userData = localStorage.getItem("kb-userData");

function redirectToLogin() {
	window.location.replace(loginUrl);
}

function displayError(error) {
	console.error(error);
}

function checkCredentials() {
	console.log("Redirect url is " + loginUrl);
	console.log("Userdata is " + userData);
	if(userData == null || "null" == userData || "undefined" == userData) {
		redirectToLogin();
		return;
	}
	userData = JSON.parse(userData);
	if(!userData.kbWebToken) redirectToLogin();
	
	$.ajax({
		type: 'GET',
		url: ROOT + "/console/verify-token",
		headers: {
			"kb-web-token": userData.kbWebToken
		},
		contentType: 'application/json',
	success: function(data) {
		console.log(data);
		localStorage.setItem("kb-userData", JSON.stringify(data));
	},
	error: function(jqXHR, textStatus, errorThrown) {
		localStorage.setItem("kb-userData", null);
		displayError("Could not authenticate: " + errorThrown);
		
		if(jqXHR.responseJSON && jqXHR.responseJSON.error) {
			displayError(jqXHR.responseJSON.error);
		}
		redirectToLogin();
		return;
	}
	});
}

checkCredentials();