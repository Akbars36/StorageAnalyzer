package com.vsu.amm.visualization.classification;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_print_interface;
import libsvm.svm_problem;

public class Classificator3 {
	private static int[] arr={1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1};
							//   [1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 0, 1]
	public static double[] classification(List<Point> classificationPoints) {
		svm_parameter param = new svm_parameter();
		svm.svm_set_print_string_function(new libsvm.svm_print_interface(){
		    @Override public void print(String s) {} // Disables svm output
		});
		// default values
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.LINEAR;
		param.degree = 3;
		param.gamma = 0;
		param.coef0 = 0;
		param.nu = 0.5;
		param.cache_size = 0;
		param.C = 5;
		param.eps = 1e-2;
		param.p = 0.1;
		param.shrinking = 0;
		param.probability = 0;
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];

		svm_problem problem = new svm_problem();

		problem.l = classificationPoints.size(); // number of training examples
		problem.y = new double[problem.l];
		problem.x = new svm_node[problem.l][2];
		int i = 0;
		List<Integer> vals=new ArrayList<>();
		for (Point point : classificationPoints) {
			//System.out.println("x="+ point.getX()+"y="+ point.getY());
			problem.x[i][0] = new svm_node();
			problem.x[i][0].index = 1;
			problem.x[i][0].value = point.getX();
			problem.x[i][1] = new svm_node();
			problem.x[i][1].index = 2;
			problem.x[i][1].value = point.getY();
			problem.y[i] = point.value;
			vals.add(point.value);
			//System.out.println(point);
			if(point.value==1){
				//System.out.println(point);
			}
			i++;
		}
		//System.out.println(vals);
		svm_model model = svm.svm_train(problem, param);
		svm_node[] x = new svm_node[2];
		double[] w = new double[3];
		for (i = 0; i < model.SV[0].length; i++) {
			for (int j = 0; j < model.SV.length; j++) {
				//System.out.println(i+"  "+j+"    "+model.SV[j][i].value);
				w[i] += model.SV[j][i].value * model.sv_coef[0][j];
			}
		}
		w[2]=-model.rho[0];
		if (model.label[1] == 0){
			w[0]=-w[0];
			w[1]=-w[1];
			w[2]=-w[2];
		}
		
//		while(Math.abs(w[0])<1){
//			w[0]*=10;
//			w[1]*=10;
//			w[2]*=10;
//		}
		//w[2]=w[0]*-100;
		System.out.println(w[0] + "         " + w[1] + "          "
				+ (-model.rho[0]));
		do_cross_validation(problem,param);
		return w;

	}
	
	private static void do_cross_validation(svm_problem prob,svm_parameter param)
	{
		int i;
		int total_correct = 0;
		double total_error = 0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
		double[] target = new double[prob.l];

		svm.svm_cross_validation(prob,param,2,target);
		if(param.svm_type == svm_parameter.EPSILON_SVR ||
		   param.svm_type == svm_parameter.NU_SVR)
		{
			for(i=0;i<prob.l;i++)
			{
				double y = prob.y[i];
				double v = target[i];
				total_error += (v-y)*(v-y);
				sumv += v;
				sumy += y;
				sumvv += v*v;
				sumyy += y*y;
				sumvy += v*y;
			}
			System.out.print("Cross Validation Mean squared error = "+total_error/prob.l+"\n");
			System.out.print("Cross Validation Squared correlation coefficient = "+
				((prob.l*sumvy-sumv*sumy)*(prob.l*sumvy-sumv*sumy))/
				((prob.l*sumvv-sumv*sumv)*(prob.l*sumyy-sumy*sumy))+"\n"
				);
		}
		else
			for(i=0;i<prob.l;i++)
				if(target[i] == prob.y[i])
					++total_correct;
			System.out.print("Cross Validation Accuracy = "+100.0*total_correct/prob.l+"%\n");
	}
}
