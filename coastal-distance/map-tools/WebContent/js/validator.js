var MIN_LAT = -90;
var MAX_LAT = 90;
var MIN_LNG = -180;
var MAX_LNG = 180;
var CHAR_PERIOD = String.fromCharCode(190);
var CHAR_HYPHEN = String.fromCharCode(189);
var CHAR_NUMPAD_PERIOD = String.fromCharCode(190);
var CHAR_NUMPAD_HYPHEN = String.fromCharCode(189);
var CHAR_DELETE = String.fromCharCode(46);
var CHAR_BACKSPACE = String.fromCharCode(8);
var CHAR_TAB = String.fromCharCode(9);
var CHAR_CTRL = String.fromCharCode(17);
var CHAR_ENTER = String.fromCharCode(13);
var CHAR_INSERT = String.fromCharCode(45);
var CHAR_ALT = String.fromCharCode(18);
var CHAR_HOME = String.fromCharCode(36);
var CHAR_END = String.fromCharCode(35);
var CHAR_LEFT_ARROW = String.fromCharCode(37);
var CHAR_UP_ARROW = String.fromCharCode(38);
var CHAR_RIGHT_ARROW = String.fromCharCode(39);
var CHAR_DOWN_ARROW = String.fromCharCode(40);


// Verifies lat is a number and within possible latitude range
function validateLat(lat) {
	if(!lat) return false;
	if(typeof lat == "string") {
		if('' === lat.trim()) return false;
		return $.isNumeric(lat) && +lat >= MIN_LAT && +lat <= MAX_LAT;
	} else {
		return lat >= MIN_LAT && lat <= MAX_LAT;
	}
}

//Verifies lng is a number and within possible longitude range
function validateLng(lng) {
	if(!lng) return false;
	if(typeof lng == "string") {
		if('' === lng.trim()) return false;
		return $.isNumeric(lng) && +lng >= MIN_LNG && +lng <= MAX_LNG;
	} else {
		return lng >= MIN_LNG && lng <= MAX_LNG;
	}
}

// returns true if the given character (string or number) is ., -, or numeric
function validateCoordinateCharacter(c) {
	if(
			CHAR_PERIOD == c 
			|| CHAR_NUMPAD_PERIOD == c 
			|| CHAR_HYPHEN == c 
			|| CHAR_NUMPAD_HYPHEN == c 
			|| CHAR_DELETE == c 
			|| CHAR_BACKSPACE == c 
			|| CHAR_TAB == c 
			|| CHAR_CTRL == c 
			|| CHAR_ENTER == c 
			|| CHAR_INSERT == c 
			|| CHAR_ALT == c 
			|| CHAR_HOME == c 
			|| CHAR_END == c 
			|| CHAR_LEFT_ARROW == c 
			|| CHAR_UP_ARROW == c 
			|| CHAR_RIGHT_ARROW == c 
			|| CHAR_DOWN_ARROW == c 
			|| ($.isNumeric(c) 
			)) {
		return true;
	}
	return false;
}
