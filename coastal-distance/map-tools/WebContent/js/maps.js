function KbMapTools() {
	this.map = null;
	this.markers = [];
	this.lines = [];
	this.mapSettings = null;
}

KbMapTools.prototype.getCurrentUserLocation = function() {
	if (google.loader && google.loader.ClientLocation) {
        return {lat: google.loader.ClientLocation.latitude, lng: google.loader.ClientLocation.longitude};
    }
	return null;
};

// centerPoint must be an object in the form {lat: -34.397, lng: 150.644}
KbMapTools.prototype.initMap = function(elementId, centerPoint, mapSettings) {
	this.mapSettings = mapSettings;
	
	// First try the point passed in
	if(!centerPoint) {
		// Oops, none specified. Try to get the user's lat/lng
		centerPoint = this.getCurrentUserLocation();
		// Default to an arbitrary lat/lng
		if(!centerPoint) centerPoint = {lat: 39.809877, lng: -98.555213};
	}
	// Create a map object and specify the DOM element for display.
	var newMap = new google.maps.Map(document.getElementById(elementId), {
	    center: centerPoint,
	    // Set mapTypeId to google.maps.MapTypeId.SATELLITE in order
	    // to activate satellite imagery.
	    mapTypeId: google.maps.MapTypeId.HYBRID,
	    zoom: 5
	});
	this.map = newMap;
    return newMap;
};

KbMapTools.prototype.drawLineBetween = function(originPoint, destinationPoint) {
	// Create the polyline and add the symbol via the 'icons' property.
	var line = new google.maps.Polyline({
		strokeColor: mapSettings.getLineColor(),
	    strokeOpacity: mapSettings.getLineOpacity(),
	    strokeWeight: mapSettings.getLineWeight(),
	    path: [{lat: originPoint.position.lat(), lng: originPoint.position.lng()}, {lat: destinationPoint.position.lat(), lng: destinationPoint.position.lng()}],
	    //map: map
	  });
	line.setMap(this.map);
	this.lines.push(line);
};

KbMapTools.prototype.createPointMarker = function(point) {
	var marker = new MarkerWithLabel({
		map: this.map,
		position: point,
		animation:google.maps.Animation.DROP,
	    labelAnchor: new google.maps.Point(mapSettings.getMarkerLabelOffsetHorizontal(), mapSettings.getMarkerLabelOffsetVertical()),
	    labelClass: "map-label", // the CSS class for the label
	    labelStyle: {opacity: (point.ord ? mapSettings.getMarkerLabelOpacity() : 0)},
	    labelContent: (point.ord ? point.ord : ''),
	    icon: mapSettings.getMarkerIcon()
	});
	this.lines.push(marker);
	return marker;
};

KbMapTools.prototype.clearMap = function() {
	for(var x = 0; x < markers.length; x++) {
		this.markers[x].setMap(null);
	}
	
	for(var x = 0; x < lines.length; x++) {
		this.lines[x].setMap(null);
	}
	this.markers = [];
	this.lines = [];
};
