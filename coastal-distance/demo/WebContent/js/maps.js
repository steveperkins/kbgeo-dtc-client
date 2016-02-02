function getCurrentUserLocation() {
	if (google.loader && google.loader.ClientLocation) {
		// Reportedly this functionality no longer exists. I looked at the source code for this mechanism and google.loader.ClientLocation is declared null and never touched again.
//        var city = google.loader.ClientLocation.address.city;
//        var country = google.loader.ClientLocation.address.country;
//        var country_code = google.loader.ClientLocation.address.country_code;
//        var region = google.loader.ClientLocation.address.region;
        return {lat: google.loader.ClientLocation.latitude, lng: google.loader.ClientLocation.longitude};
    }
	return null;
}


// centerPoint must be an object in the form {lat: -34.397, lng: 150.644}
function initMap(elementId, centerPoint) {
	// First try the point passed in
	if(!centerPoint) {
		// Oops, none specified. Try to get the user's lat/lng
		centerPoint = getCurrentUserLocation();
		// Default to an arbitrary lat/lng
		if(!centerPoint) centerPoint = {lat: 39.809877, lng: -98.555213};
	}
	// Create a map object and specify the DOM element for display.
	var map = new google.maps.Map(document.getElementById(elementId), {
	    center: centerPoint,
	    // Set mapTypeId to google.maps.MapTypeId.SATELLITE in order
	    // to activate satellite imagery.
	    mapTypeId: google.maps.MapTypeId.HYBRID,
	    zoom: 5
	});
		
  return map;
}

function drawLineBetween(originPoint, coastalPoint, map) {
	var lineSymbol = {
	    path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW
	};
	
	// Create the polyline and add the symbol via the 'icons' property.
	var line = new google.maps.Polyline({
		strokeColor: '#0084B8',
	    strokeOpacity: 0.7,
	    strokeWeight: 2,
	    path: [originPoint, coastalPoint],
	    icons: [{
	      icon: lineSymbol,
	      // Draw arrow at end of line
	      offset: '100%'
	    }],
	    map: map
	  });
}

function createOriginPointMarker(map, originPoint, distanceInMiles) {
	var marker = new MarkerWithLabel({
		map: map,
		position: originPoint,
		animation:google.maps.Animation.DROP,
	    labelAnchor: new google.maps.Point(30, 0),
	    labelClass: "map-label map-label-origin", // the CSS class for the label
	    labelStyle: {opacity: 0.75},
	    labelContent: round(distanceInMiles) + ' mi'
	});
	return marker;
}

function createCoastalPointMarker(map, coastalPoint) {
	var marker = new MarkerWithLabel({
		map: map,
		position: coastalPoint,
		label: 'C',
		animation:google.maps.Animation.DROP,
		labelAnchor: new google.maps.Point(55, 0),
	    labelClass: "map-label map-label-coastal", // the CSS class for the label
	    labelStyle: {opacity: 0.75},
	    labelContent: roundToThreePlaces(coastalPoint.lat) + ", " + roundToThreePlaces(coastalPoint.lng)
	});
	return marker;
}