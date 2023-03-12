let id_left = "pleft";
let id_right = "pright"
let id_value = "pvalue";
let id_led = "pled";
let id_bar = "pbar";
let id_time = "time";
let id_status = "status";

let clock = 1000;

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
	} else {
		document.getElementById(id_led).style.backgroundColor = "#86C900";
		document.getElementById(id_led).style.boxShadow = "none";
	}
}
	
function updateLightLvl(value) {
	if (value != null) {
		document.getElementById(id_bar).style.width = value + "%";
		document.getElementById(id_bar).innerHTML = Math.round(value) + "%";
	}
}

function getDataFromServer() {
	let req = new XMLHttpRequest();
	req.onload = function() {
		document.getElementById(id_status).innerHTML = "Status: " + this.responseText.split(";")[0];
		document.getElementById(id_time).innerHTML = "Time: " + this.responseText.split(";")[1] + ":" + this.responseText.split(";")[2];
		updateLight(this.responseText.split(";")[3]);
		updateRollerBlind(this.responseText.split(";")[4]);
		updateLightLvl(this.responseText.split(";")[5]);
	};

	req.open("GET", "requestTelemetry", true);
    req.send();
	setTimeout(getDataFromServer, clock);
}