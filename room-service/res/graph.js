let barColor = "blue";
let hours = [];

for (var i = 0; i < 24; i++) {
	hours.push(i);
}

const lightChart = new Chart(id_graph, {
	type: "bar",
	data: {
		labels: hours,
		datasets: [{ 
			backgroundColor: barColor,
		}]
	},
	options: {
		legend: {display: false},
		title: {
			display: true,
			text: "Minutes per hour of light usage"
		}
	}
});

function updateGraphData(activeTime) {
	lightChart.data.datasets[0].data = activeTime;
	lightChart.update();
}