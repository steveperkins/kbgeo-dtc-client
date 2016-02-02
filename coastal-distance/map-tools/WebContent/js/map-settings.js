function MapSettings() {}

var defaultClearPointsBeforeDrawing = true;
var defaultShowLines = true;
var defaultAlternateLines = false;
var defaultMarkerIcon = 'img/dot-red.png';
var defaultMarkerLabelOffsetHorizontal = 20;
var defaultMarkerLabelOffsetVertical = 0;
var defaultMarkerLabelOpacity = 0.5;
var defaultLineColor = '#0084B8';
var defaultLineOpacity =  0.8;
var defaultLineWeight = 1;

MapSettings.prototype.setClearPointsBeforeDrawing = function(clearPointsBeforeDrawing) {
	this.clearPointsBeforeDrawing = clearPointsBeforeDrawing;
};
MapSettings.prototype.getClearPointsBeforeDrawing = function() {
	if(null == this.clearPointsBeforeDrawing) this.clearPointsBeforeDrawing = defaultClearPointsBeforeDrawing;
	return this.clearPointsBeforeDrawing;
};
//Must be a boolean
MapSettings.prototype.setShowLines = function(showLines) {
	this.showLines = showLines;
};
MapSettings.prototype.getShowLines = function() {
	if(null == this.showLines) this.showLines = defaultShowLines;
	return this.showLines;
};
//Must be a boolean
MapSettings.prototype.setAlternateLines = function(alternateLines) {
	this.alternateLines = alternateLines;
};
MapSettings.prototype.getAlternateLines = function() {
	if(null == this.alternateLines) this.alternateLines = defaultAlternateLines;
	return this.alternateLines;
};
// Can be a URL to an image or an icon object
MapSettings.prototype.setMarkerIcon = function(markerIcon) {
	this.markerIcon = markerIcon;
};
MapSettings.prototype.getMarkerIcon = function() {
	if(!this.markerIcon) this.markerIcon = defaultMarkerIcon;
	return this.markerIcon;
};
// Must be a number 0 or greater
MapSettings.prototype.setMarkerLabelOffsetHorizontal = function(markerLabelOffsetHorizontal) {
	this.markerLabelOffsetHorizontal = markerLabelOffsetHorizontal;
};
MapSettings.prototype.getMarkerLabelOffsetHorizontal = function() {
	if(!this.markerLabelOffsetHorizontal) this.markerLabelOffsetHorizontal = defaultMarkerLabelOffsetHorizontal;
	return this.markerLabelOffsetHorizontal;
};
// Must be a number 0 or greater
MapSettings.prototype.setMarkerLabelOffsetVertical = function(markerLabelOffsetVertical) {
	this.markerLabelOffsetVertical = markerLabelOffsetVertical;
};
MapSettings.prototype.getMarkerLabelOffsetVertical = function() {
	if(!this.markerLabelOffsetVertical) this.markerLabelOffsetVertical = defaultMarkerLabelOffsetVertical;
	return this.markerLabelOffsetVertical;
};
// Must be a number between 0 and 1
MapSettings.prototype.setMarkerLabelOpacity = function(markerLabelOpacity) {
	this.markerLabelOpacity = markerLabelOpacity;
};
MapSettings.prototype.getMarkerLabelOpacity = function() {
	if(!this.markerLabelOpacity) this.markerLabelOpacity = defaultMarkerLabelOpacity;
	return this.markerLabelOpacity;
};
// Can be any HTML-valid color string
MapSettings.prototype.setLineColor = function(lineColor) {
	this.lineColor = lineColor;
};
MapSettings.prototype.getLineColor = function() {
	if(!this.lineColor) this.lineColor = defaultLineColor;
	return this.lineColor;
};
// Must be a number between 0 and 1
MapSettings.prototype.setLineOpacity = function(lineOpacity) {
	this.lineOpacity = lineOpacity;
};
MapSettings.prototype.getLineOpacity = function() {
	if(!this.lineOpacity) this.lineOpacity = defaultLineOpacity;
	return this.lineOpacity;
};
// Must be a number 0 or greater
MapSettings.prototype.setLineWeight = function(lineWeight) {
	this.lineWeight = lineWeight;
};
MapSettings.prototype.getLineWeight = function() {
	if(!this.lineWeight) this.lineWeight = defaultLineWeight;
	return this.lineWeight;
};

MapSettings.prototype.loadPersistedSettings = function() {
	var settingsStr = localStorage.getItem("kb-maptools-mapsettings");
	if(settingsStr == null || "null" == settingsStr || "undefined" == settingsStr) {
		return new MapSettings();
	}
	
	var settings = JSON.parse(settingsStr);
	this.setMarkerIcon(settings.markerIcon);
	this.setMarkerLabelOffsetHorizontal(settings.markerLabelOffsetHorizontal);
	this.setMarkerLabelOffsetVertical(settings.markerLabelOffsetVertical);
	this.setMarkerLabelOpacity(settings.markerLabelOpacity);
	this.setShowLines(settings.showLines);
	this.setLineColor(settings.lineColor);
	this.setLineOpacity(settings.lineOpacity);
	this.setLineWeight(settings.lineWeight);
	
	return this;
};

MapSettings.prototype.persist = function() {
	localStorage.setItem("kb-maptools-mapsettings", JSON.stringify(this));
};