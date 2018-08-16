window.onload = function () {

	const clusters = 4;
	const equalClustersSize = 15000;
	const equalClustersCov = 10;
	const testsCount = 10;
	const kmeansInit = true;
	const canvas = document.querySelector('.graph canvas');

	let drawGmm = new DrawGmm(canvas);
	let testGmm = new TestGmm(TestGmm.generateEqual(clusters, -80, equalClustersSize, equalClustersCov));
	let stats= new Statistics();

	let dx = 200;
	let dy = 200;

	console.log("start");

	
	for (let j = 0; j < testsCount; j++) {
		setTimeout( function timer(){
			var gmm = null;

			let means = Array(clusters).fill(0)
				.map(_ => [-100+Math.random()*dx]);

			let covariances = Array(clusters).fill(0)
				.map(_ => [[dx*0.1]]);
			var before = window.performance.now();
			const initialModel = kmeansInit?kmeanspp(testGmm.points, clusters):{centroids:means};
			const results = kmeans.kmeans(initialModel,200,kmeans.assignmentsConverged);
			results.clusters = testGmm.transformKMeansToGMM(results.model);
			var timek = window.performance.now() - before;
			console.log("kmeans++ time " + timek);

			gmm = new window.GMM({
				dimensions: 1,
				bufferSize: 100000000,
				weights: results.clusters.map(x=>x.weight),
				means: initialModel.centroids,
				covariances:results.clusters.map(x=>[[x.cov]])
			});
			testGmm.print(gmm, "init");

			testGmm.points.forEach(p => gmm.addPoint(p));

			var before = window.performance.now();
			// run 5 iterations of EM algorithm
			gmm.runEM(200);
			var time = window.performance.now() - before;
			testGmm.print(gmm);
			const diff=testGmm.diffParams(gmm);
			stats.update(time, diff.diffMeans, diff.diffCovs);
			//gmm=null;
			//points=null;
			drawGmm.drawGmm(gmm, testGmm.points);
			
		}, j*1);

	}
	setTimeout( function timer(){
		stats.calculate(testsCount);
	}, testsCount*10000);


	
	


	/*setTimeout( function timer(){
		var sign=-1;
		for (let i=0; i<500000; i++) {
		//setTimeout( function timer(){
			canvas.width = canvas.width; // clean
			var elem=-50+((i%2)*sign);
			points.push([elem]);
			gmm.addPoint([elem]);
			sumelem+=elem;
		
		
			if(i%2==1)sign*=-1;
		}
		gmm.runEM(20);
		var pointColors = points
					.map(x=>gmm.predict(x))
					.map(probs => probs.reduce(
						(iMax, x, i, arr) => x > arr[iMax] ? i : iMax, 0
					))
					.map(i => clusterColors[i]);
					for(let i=0; i<gmm.clusters; i++) {
						draw.circle(gmm.means[i], gmm.covariances[i], clusterColors[i]);
					}
		draw.points(points, pointColors);
					
		for(let i=0; i<gmm.clusters; i++) {
			console.log(i+" "+ gmm.means[i]+" "+ gmm.covariances[i]+" "+gmm.weights[i])
		}
	
		mean=sumelem/1500000;
		cov=points.reduce((acc,cur)=>(parseFloat(acc)+(parseFloat(cur[0])-mean)*(parseFloat(cur[0])-mean)),0)/1500000;
		console.log("cov2"+cov+"mean2"+mean);
	
	}, 5000 );
	
	
	setTimeout( function timer(){
		var sign=1;
		for (let i=0; i<1000000; i++) {
		//setTimeout( function timer(){
			canvas.width = canvas.width; // clean
			var elem=-50+((i%2)*sign);
			points.push([elem]);
			gmm.addPoint([elem]);
			sumelem+=elem;
		
			if(i%2==1)sign*=-1;
		}
		gmm.runEM(20);
		var pointColors = points
					.map(x=>gmm.predict(x))
					.map(probs => probs.reduce(
						(iMax, x, i, arr) => x > arr[iMax] ? i : iMax, 0
					))
					.map(i => clusterColors[i]);
					for(let i=0; i<gmm.clusters; i++) {
						draw.circle(gmm.means[i], gmm.covariances[i], clusterColors[i]);
					}
		draw.points(points, pointColors);
					
		for(let i=0; i<gmm.clusters; i++) {
			console.log(i+" "+ gmm.means[i]+" "+ gmm.covariances[i]+" "+gmm.weights[i])
		}
	
		mean=sumelem/1500000;
		cov=points.reduce((acc,cur)=>(parseFloat(acc)+(parseFloat(cur[0])-mean)*(parseFloat(cur[0])-mean)),0)/1500000;
		console.log("cov2"+cov+"mean2"+mean);
	
	}, 10000 );
	*/

}