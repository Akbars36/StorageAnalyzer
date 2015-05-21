package com.vsu.amm.visualization.classification;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public class Classificator {
	public static double[] classification(List<Point> classificationPoints) {
		svm_parameter param = new svm_parameter();

		// default values
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.LINEAR;
		param.degree = 3;
		param.gamma = 0;
		param.coef0 = 0;
		param.nu = 0.5;
		param.cache_size = 40;
		param.C = 100;
		param.eps = 1e-3;
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
		for (Point point : classificationPoints) {
			//System.out.println("x="+ point.getX()+"y="+ point.getY());
			problem.x[i][0] = new svm_node();
			problem.x[i][0].index = 1;
			problem.x[i][0].value = point.getX();
			problem.x[i][1] = new svm_node();
			problem.x[i][1].index = 2;
			problem.x[i][1].value = point.getY();
			problem.y[i] = point.value;
			i++;
		}
		svm_model model = svm.svm_train(problem, param);
		svm_node[] x = new svm_node[2];
		double[] w = new double[3];
		for (i = 0; i < model.SV[0].length; i++) {
			for (int j = 0; j < model.SV.length; j++) {
				w[i] += model.SV[j][i].value * model.sv_coef[0][j];
			}
		}
		w[2]=-model.rho[0];
		System.out.println(w[0] + "         " + w[1] + "          "
				+ (-model.rho[0]));
		return w;

	}
}
