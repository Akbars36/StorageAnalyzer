
const clusterColors = [
	'rgb(228,  26,  28)',
	'rgb( 55, 126, 184)',
	'rgb( 77, 175,  74)',
	'rgb(152,  78, 163)',
	'rgb(255, 127,   0)',
	'rgb(255, 255,  51)',
	'rgb(166,  86,  40)',
];


const xMin = -100, xMax = 100;
const yMin = -100, yMax = 100;

class DrawGmm {
	constructor(canvas) {
		this.draw = new Draw(canvas, xMin, xMax, yMin, yMax);
	}

	drawClusters(gmm) {
		for (let i = 0; i < gmm.clusters; i++) {
			this.draw.circle(gmm.means[i], gmm.covariances[i], clusterColors[i]);
		}
	}

	drawPoints(gmm, points) {
		var pointColors = points
			.map(x => gmm.predict(x))
			.map(probs => probs.reduce(
				(iMax, x, i, arr) => x > arr[iMax] ? i : iMax, 0
			))
			.map(i => clusterColors[i]);

		this.draw.points(points, pointColors);
	}

	drawGmm(gmm, points){
		this.draw.canvas.width = this.draw.canvas.width; // clean
		this.drawClusters(gmm);
		this.drawPoints(gmm, points);
	}
}

