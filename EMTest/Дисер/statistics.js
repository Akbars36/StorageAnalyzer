class Statistics{
    constructor(){
        this.diffMean = 0;
        this.diffCov = 0;
        this.sumtime = 0;
    }

    update(time, diffMeans, diffCovs){
        console.log("time " + time);
		console.log("diff"+diffMeans+" "+diffCovs);
        this.sumtime += time;
		this.diffMean += diffMeans;
        this.diffCov += diffCovs;
        if (diffMeans>1||diffCovs>10){
            console.error("error, diffMean: "+diffMeans+" diffCovs "+diffCovs);
        }
    }

    calculate(testsCount){
        console.log("average time " +  this.sumtime / testsCount);
		console.log("average diff "+this.diffMean/testsCount+" "+this.diffCov/testsCount);
    }
}