const status_auto = 0;
const status_dash = 1;
const status_mobile = 2;

let id_left = "extLeft";
let id_right = "extRight"
let id_value = "extValue";
let id_led = "extLed";
let id_bar = "extBar";
let id_time = "lblTime";
let id_status = "lblStatus";
let id_graph = "lightChart";
let id_switch = "chkLight";
let id_roll = "nmbRoll";
let id_button = "btnApply";
let id_btn_release = "btnRelease";
let id_btn_label = "lblMessage";

let last_det_time = null;
let current_time = null;
let current_status = -1;
let is_light_on = false;
let form_interaction = false;
let first_update = true;
let applyPressed = false;
let system_time_ctrl_obtained = 0
let control_lock_time_mins = 60 * 1000;
let clock = 200;

let time_scheme = [];
for (var i = 0; i < 24; i++) {
	time_scheme.push(0);
}

let close_msg = "You're currently taking control of the room.\nAny unsaved changes will be lost, are you sure you want to leave?";
let oldOnBeforeUnload = window.onbeforeunload;
function onWindowClose(e) {
	if (current_status == status_dash) {
		releaseControlLock();
		e.returnValue = close_msg;
	}
}

//Updates the current status of the room and releted GUI elements
function updateStatus(state) {
	if (state == null) return;
	current_status = state;
	switch(Number(current_status)) {
		case status_auto:
			document.getElementById(id_status).innerHTML = "Status: <b>AUTO</b>";
			system_time_ctrl_obtained = 0;
			document.getElementById(id_btn_release).disabled = true;
			document.getElementById(id_button).disabled = false;
			break;
		case status_dash:
			document.getElementById(id_status).innerHTML = "Status: <b>DASHBOARD CONTROL</b>";
			document.getElementById(id_btn_release).disabled = false;
			document.getElementById(id_button).disabled = false;
			if (applyPressed) {
				system_time_ctrl_obtained = Date.now();
				applyPressed = false;
			}
			break;
		case status_mobile:
			document.getElementById(id_status).innerHTML = "Status: <b>MOBILE APP CONTROL</b>";
			document.getElementById(id_btn_release).disabled = true;
			document.getElementById(id_button).disabled = true;
			system_time_ctrl_obtained = 0;
			break;
		default:
			document.getElementById(id_status).innerHTML = "Status: <b>Unknown</b>";
			document.getElementById(id_btn_release).disabled = true;
			document.getElementById(id_button).disabled = true;
			system_time_ctrl_obtained = 0;
			break;
	}
}

//Updates the current opening of the room rollerblind and releted GUI elements
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

//Updates the current status of the room light and releted GUI elements
function updateLight(is_on) {
	if (is_on == 1) {
		document.getElementById(id_led).style.backgroundColor = "#ABFF00";
		document.getElementById(id_led).style.boxShadow = "rgba(0, 0, 0, 0.2) 0 -1px 7px 1px, inset #304701 0 -1px 9px, #89FF00 0 2px 12px";
		is_light_on = true;
		if (!form_interaction) {
			document.getElementById(id_switch).checked = true;
		}
	} else {
		document.getElementById(id_led).style.backgroundColor = "#86C900";
		document.getElementById(id_led).style.boxShadow = "none";
		is_light_on = false;
		if (!form_interaction) {
			document.getElementById(id_switch).checked = false;
		}
	}
}

//Updates the current amounth of light detected by the photoresister
function updateLightLvl(value) {
	if (value != null) {
		document.getElementById(id_bar).style.width = value + "%";
		document.getElementById(id_bar).innerHTML = Math.round(value) + "%";
	}
}

//Updates the current time of the arduino
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

//Updates the time graph with the current values
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

//Event listener that responds when the form is clicked
function formOnClick() {
	form_interaction = true;
}

//Event listener that responds when the button apply is clicked
function applyOnClick() {
	var confirmation_msg_reset = 3 * 1000;
	var roll = document.getElementById(id_roll).value;
	var light = 0;
	if (document.getElementById(id_switch).checked) {
		light++;
	}
	document.getElementById(id_button).disabled = true;
	document.getElementById(id_btn_release).disabled = true;
	document.getElementById(id_btn_label).innerHTML = "Waiting for server response...";
	form_interaction = false;
	applyPressed = true;
	
	var req = new XMLHttpRequest();
	if (req == null) {
		return;
	}
	req.onreadystatechange = function(){
		document.getElementById(id_button).disabled = false;
		document.getElementById(id_btn_release).disabled = false;
		if (this.readyState == 4 && this.status == 200) {
			document.getElementById(id_btn_label).innerHTML = this.responseText;
		} else {
			document.getElementById(id_btn_label).innerHTML = "An error occured while sending the request.";
		}
		setTimeout(function(){
			document.getElementById(id_btn_label).innerHTML = "";
		}, confirmation_msg_reset);
	};
	req.open("POST", "accessControl", true);
	req.send("status="+status_dash+"&light="+light+"&roll="+roll);
}

//Event listener that responds when the button release is clicked
function releaseOnClick() {
	if (current_status == status_dash) {
		system_time_ctrl_obtained = 1;
	}
}

//Sends to the arduino the signal for releasing the lock and returning to the AUTO status
function releaseControlLock() {
	var req = new XMLHttpRequest();
	if (req == null) {
		return;
	}
	req.open("POST", "accessControl", true);
	req.send("status="+status_auto+"&light=0&roll=0");
}

//Utility function used for calcolate the minutes difference between 2 Date types
function diff_minutes(dt2, dt1) {
	if (dt2 == null || dt1 == null) return 0;
	
	var diff =(dt2.getTime() - dt1.getTime()) / 1000;
	diff /= 60;
	return Math.abs(Math.round(diff));  
}

//Requests the data from the server and updates GUI elements
function getDataFromServer() {
	var req = new XMLHttpRequest();
	if (req == null) {
		return;
	}
	req.onload = function() {
		console.log("Received from server:\n"+this.responseText);
		updateGraph();
		updateStatus(this.responseText.split(";")[0]);
		updateTime(this.responseText.split(";")[1], this.responseText.split(";")[2]);
		updateLight(this.responseText.split(";")[3]);
		updateRollerBlind(this.responseText.split(";")[4]);
		updateLightLvl(this.responseText.split(";")[5]);
		first_update = false;
	};
	req.open("GET", "requestData", true);
    req.send();
}

//Function that handles the runtime behaviour of the page, it executes once every clock tick.
function runtime() {
	getDataFromServer();
	if (system_time_ctrl_obtained > 0 && (Date.now()-system_time_ctrl_obtained) >= control_lock_time_mins) {
		releaseControlLock();
	}
	setTimeout(runtime, clock);
}