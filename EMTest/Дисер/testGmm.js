'use strict';
const intersectionClusters=[{mean:-50, cov:20, size:20000},{mean:-20, cov:20, size:20000},{mean:0, cov:10, size:20000},{mean:20, cov:15, size:20000}];
const diffweightClusters=[{mean:-50, cov:20, size:200000},{mean:-20, cov:20, size:20000},{mean:0, cov:10, size:20000},{mean:20, cov:15, size:20000}];
class TestGmm {
	constructor(params) {
		this.points = [];
		var sumelem = 0;
		
		this.params = [];
		for (var k = 0; k < params.length; k++) {
			sumelem = 0;
			var tmpPoints = [];
			for (var i = 0; i < params[k].size; i++) {
				var elem = TestGmm.normal(params[k].mean, params[k].cov);
				sumelem += elem;
				tmpPoints.push([elem]);
			}
			var mean = sumelem / params[k].size;
			var cov = tmpPoints.reduce((acc, cur) => (parseFloat(acc) + (parseFloat(cur[0]) - mean) * (parseFloat(cur[0]) - mean)), 0) / (params[k].size-1);
			console.log(k + " mean " + mean + " cov " + cov);
			this.params[k] = { mean, cov };
			this.points = this.points.concat(tmpPoints);
		}
	}

	static generateEqual(clusters, initMean, equalClustersSize, equalClustersCov) {
		let mean = initMean;
		const params = [];
		for (var k = 0; k < clusters; k++) {
			params[k]={mean:mean, size:equalClustersSize, cov: equalClustersCov}
			mean += 40;
		}
		return params;
	}

	print(gmm, str){
		for (var i=0; i<gmm.clusters; i++){
			console.log(str+i + " " + gmm.means[i] + " " + gmm.covariances[i] + " " + gmm.weights[i]);
		}
	}

	transformKMeansToGMM(model){
				const n = model.observations.length;
        const k = model.centroids.length;
        const assignments = model.assignments;
				const clusters = [];
				
        for(let i = 0; i < k; i += 1) {
            clusters.push({mean:0,cov:0,size:0, sum:0, elements:[], weight:0})
				}
				
        for(let i = 0; i < n; i += 1) {
						clusters[assignments[i]].sum+=model.observations[i][0];
						clusters[assignments[i]].elements.push(model.observations[i][0]);
				}
				
				for(let i = 0; i < k; i += 1) {
					clusters[i].size = clusters[i].elements.length;
					clusters[i].mean = clusters[i].sum/clusters[i].size;
					clusters[i].cov = clusters[i].elements.reduce((acc,x)=>acc+(x-clusters[i].mean)*(x-clusters[i].mean),0)/(clusters[i].size-1);
					clusters[i].weight = clusters[i].size/n;
				}
 
        return clusters;
	}

	diffParams(gmm){
		var newMeans=[...gmm.means].sort((a,b)=>a[0]>b[0]);
		var newCovs=[...gmm.covariances];
		for (var k=0; k<gmm.means.length; k++){
			newCovs[newMeans.indexOf(gmm.means[k])]=gmm.covariances[k];
		}
		const diffMeans=newMeans.reduce((acc, curr, i)=>acc+Math.abs(curr[0] - this.params[i].mean),0)/gmm.clusters;
		const diffCovs=newCovs.reduce((acc, curr, i)=>acc+Math.abs(curr[0][0] - this.params[i].cov),0)/gmm.clusters;
		return {diffMeans, diffCovs};  
	}

	static gaussianRand() {
		var rand = 0;

		for (var i = 0; i < 6; i += 1) {
			rand += Math.random();
		}

		return rand / 6;
	}

	static gaussianRandom(start, end) {
		return Math.floor(start + gaussianRand() * (end - start + 1));
	}

	static normal(mu, sigma, nsamples) {
		if (!nsamples) nsamples = 6
		if (!sigma) sigma = 1
		if (!mu) mu = 0

		var run_total = 0
		for (var i = 0; i < nsamples; i++) {
			run_total += Math.random()
		}

		return sigma * (run_total - nsamples / 2) / (nsamples / 2) + mu
	}
}
