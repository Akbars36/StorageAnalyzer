package com.vsu.amm.visualization.classification;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_print_interface;
import libsvm.svm_problem;

public class Classificator2 {

	public static double[] classification(List<Point> classificationPoints) {
		Problem problem = new Problem();
		problem.l = classificationPoints.size(); // number of training examples
		problem.n = 3; // number of features
		problem.y = new double[problem.l];
		problem.x = new FeatureNode[problem.l][3];
		//problem.bias=1;
		SolverType solver = SolverType.L1R_LR; // -s 0
		double C = 100.0; // cost of constraints violation
		double eps = 0.01; // stopping criteria

		Parameter parameter = new Parameter(solver, C, eps);

		int i = 0;
		List<Integer> vals = new ArrayList<>();
		for (Point point : classificationPoints) {
			// System.out.println("x="+ point.getX()+"y="+ point.getY());
			problem.x[i][0] = new FeatureNode(1, point.getX());
			problem.x[i][1] = new FeatureNode(2, point.getY());
			problem.x[i][2] = new FeatureNode(3,1);
			problem.y[i] = point.value;
			vals.add(point.value);
			// System.out.println(point);
			if (point.value == 1) {
				// System.out.println(point);
			}
			i++;
		}
		Model model = Linear.train(problem, parameter);
		File modelFile = new File("model");
		try {
			model.save(modelFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double[] w = new double[3];
		for (i = 0; i < model.getFeatureWeights().length; i++) {
			w[i] += model.getFeatureWeights()[i];
		}
		if (model.getLabels()[1] == 0){
			w[0]=-w[0];
			w[1]=-w[1];
			w[2]=-w[2];
		}
		//w[2] = model.getBias();
		System.out.println(w[0] + "         " + w[1] + "          " + w[2]);
		// do_cross_validation(problem,param);
		return w;

	}

	private static void do_cross_validation(svm_problem prob,
			svm_parameter param) {
		int i;
		int total_correct = 0;
		double total_error = 0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
		double[] target = new double[prob.l];

		svm.svm_cross_validation(prob, param, 2, target);
		if (param.svm_type == svm_parameter.EPSILON_SVR
				|| param.svm_type == svm_parameter.NU_SVR) {
			for (i = 0; i < prob.l; i++) {
				double y = prob.y[i];
				double v = target[i];
				total_error += (v - y) * (v - y);
				sumv += v;
				sumy += y;
				sumvv += v * v;
				sumyy += y * y;
				sumvy += v * y;
			}
			System.out.print("Cross Validation Mean squared error = "
					+ total_error / prob.l + "\n");
			System.out
					.print("Cross Validation Squared correlation coefficient = "
							+ ((prob.l * sumvy - sumv * sumy) * (prob.l * sumvy - sumv
									* sumy))
							/ ((prob.l * sumvv - sumv * sumv) * (prob.l * sumyy - sumy
									* sumy)) + "\n");
		} else
			for (i = 0; i < prob.l; i++)
				if (target[i] == prob.y[i])
					++total_correct;
		System.out.print("Cross Validation Accuracy = " + 100.0 * total_correct
				/ prob.l + "%\n");
	}
}
