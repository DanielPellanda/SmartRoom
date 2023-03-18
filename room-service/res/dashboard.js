let id_left = "pleft";
let id_right = "pright"
let id_value = "pvalue";
let id_led = "pled";
let id_bar = "pbar";
let id_time = "time";
let id_status = "status";
let id_graph = "lightChart";

let last_det_time = null;
let current_time = null;
let is_light_on = false;
let first_update = true;
let clock = 1000;

let time_scheme = [];
for (var i = 0; i < 24; i++) {
	time_scheme.push(0);
}

function updateRollerBlind(value) {
	let percentage = value;
	if (isNaN(value)) {
		percentage = 0;
	}
	
	let loading_time = 3;

	let degrees = percentage * 360 / 100;

	let degrees_right = degrees;
	if (degrees > 180) {
		degrees_right = 180;
	}
	degrees_left = degrees - degrees_right;
	
	document.getElementById(id_left).style.webkitTransform  = "rotate(" + degrees_left + "deg)";
	document.getElementById(id_right).style.webkitTransform  = "rotate(" + degrees_right + "deg)";
	document.getElementById(id_left).style.transform  = "rotate(" + degrees_left + "deg)";
	document.getElementById(id_right).style.transform  = "rotate(" + degrees_right +"deg)";
	document.getElementById(id_value).innerHTML = Math.round(percentage) + "%";
}

function updateLight(is_on) {
	if (is_on == 1) {
		document.getElementById(id_led).style.backgroundColor = "#ABFF00";
		document.getElementById(id_led).style.boxShadow = "rgba(0, 0, 0, 0.2) 0 -1px 7px 1px, inset #304701 0 -1px 9px, #89FF00 0 2px 12px";
		is_light_on = true;
	} else {
		document.getElementById(id_led).style.backgroundColor = "#86C900";
		document.getElementById(id_led).style.boxShadow = "none";
		is_light_on = false;
	}
}
	
function updateLightLvl(value) {
	if (value != null) {
		document.getElementById(id_bar).style.width = value + "%";
		document.getElementById(id_bar).innerHTML = Math.round(value) + "%";
	}
}

function updateTime(hour, min) {
	if (!isNaN(hour) && !isNaN(min)) {
		if (hour < 24 && hour >= 0 && min < 60 && min >= 0) {
			last_det_time = current_time;
			current_time = new Date(min*60*1000+(hour-1)*60*60*1000);
			if (diff_minutes(current_time,last_det_time) < 0) {
				current_time = new Date(min*60*1000+(hour+24-1)*60*60*1000);
			}
			
			if (min < 10) {
				min = "0" + min;
			}
			document.getElementById(id_time).innerHTML = "Time: " + hour + ":" + min;
		}
	}
}

function diff_minutes(dt2, dt1) {
	if (dt2 == null || dt1 == null) return 0;
	
	var diff =(dt2.getTime() - dt1.getTime()) / 1000;
	diff /= 60;
	return Math.abs(Math.round(diff));  
}

function updateGraph() {
	if (!first_update && is_light_on) {
		var mins_passed = diff_minutes(current_time,last_det_time);
		var curr_hour = last_det_time.getHours();
		var limit_hour = current_time.getHours();
		
		while (mins_passed > 0 && curr_hour <= limit_hour) {
			var init = 0;
			var end = 60;
			if (curr_hour == last_det_time.getHours()) {
				init = last_det_time.getMinutes();
			}
			if (curr_hour == current_time.getHours()) {
				end = current_time.getMinutes();
			}
			var mins_in_hour = end - init;
			time_scheme[curr_hour] = time_scheme[curr_hour] + mins_in_hour;
			updateGraphData(time_scheme);
			mins_passed = mins_passed - mins_in_hour;
			curr_hour++;
		}
	}
}

function getDataFromServer() {
	let req = new XMLHttpRequest();
	req.onload = function() {
		document.getElementById(id_status).innerHTML = "Status: " + this.responseText.split(";")[0];
		updateGraph();
		updateTime(this.responseText.split(";")[1], this.responseText.split(";")[2]);
		updateLight(this.responseText.split(";")[3]);
		updateRollerBlind(this.responseText.split(";")[4]);
		updateLightLvl(this.responseText.split(";")[5]);
		first_update = false;
	};
	req.open("GET", "requestTelemetry", true);
    req.send();
	setTimeout(getDataFromServer, clock);
}