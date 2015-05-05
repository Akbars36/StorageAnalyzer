package com.vsu.amm.visualization;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import com.vsu.amm.data.storage.IDataStorage;
import com.vsu.amm.stat.ICounterSet;
import com.vsu.amm.stat.SimpleCounterSet;
import com.vsu.amm.stat.ICounterSet.OperationType;
import com.vsu.amm.visualization.coordinate.CoordinanateTranslator;
import com.vsu.amm.visualization.coordinate.Point3DInIRSCoords;
import com.vsu.amm.visualization.data.DataGenerator;
import com.vsu.amm.visualization.data.ImageData;
import com.vsu.amm.visualization.utils.DrawConstants;
import com.vsu.amm.visualization.utils.DrawUtils;

public class Vizualizator {

	public static ImageData getCoeffs(int size, List<IDataStorage> storages) {
		
		Map<Point2D, List<Integer>> coeffs = new HashMap<Point2D, List<Integer>>();
		CoordinanateTranslator transl = new CoordinanateTranslator(size);
		Integer min = null;
		Integer max = null;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (DrawUtils.pointInTriangle(0, 0, size/2, (float) (size*Math.sqrt(3.0) / 2.0f), size, 0, x,
						 size-y)) {
					Point2D p = new Point2D.Double(x, size-y);
					Point3DInIRSCoords res = transl.translate(p);
					System.out.print(x+"     "+(size-y)+"       "+res.getInsertCoord()+"   "+ res.getSelectCoord()+"   "+ res.getRemoveCoord());
					List<Integer> result = DataGenerator.getContersForStorages(
							storages, res.getInsertCoord(),
							res.getSelectCoord(), res.getRemoveCoord());
					System.out.println("    "+result.get(0));
					if (storages.size() == 1) {
						Integer cur = storages.get(0).getCounterSet()
								.get(OperationType.ASSIGN)
								+ storages.get(0).getCounterSet()
										.get(OperationType.COMPARE);
						if (min == null || cur < min)
							min = cur;
						if (max == null || cur > max)
							max = cur;
					}
					for(int i=0;i<storages.size();i++){
						storages.get(i).clear();
						storages.get(i).setCounterSet(new SimpleCounterSet());
					}
					coeffs.put(p, result);
				}
			}
		}
		System.out.println(min+"        "+max);
		ImageData data=new ImageData(min, max, coeffs);
		return data;
	}
	
	public static void Draw(int size,String filename, List<IDataStorage> storages){
		int type = BufferedImage.TYPE_INT_ARGB;
		BufferedImage image = new BufferedImage(size+DrawConstants.OFFSET*2, size+DrawConstants.OFFSET*2, type);
		File outputfile = new File("saved.png");
		ImageData data=getCoeffs(size, storages);
		Map<Point2D, List<Integer>> coeffs=data.getData();
		boolean isSingle=storages.size()==1;
		int colorIndex=0;
		Color curColor=null;
		double curRange=0;
		if(isSingle)
			curRange=data.getMax()-data.getMin();
		for(Entry<Point2D,List<Integer>> entry:coeffs.entrySet()){
			Point2D point =entry.getKey();
			if(isSingle){
				double ratio=(entry.getValue().get(0)-data.getMin())/curRange;
				curColor=DrawUtils.getLinearGradient(ratio, DrawConstants.COLORS[0], DrawConstants.COLORS[1]);
			}else{
				
			}
			image.setRGB((int)point.getX()+DrawConstants.OFFSET, size-(int)point.getY()+DrawConstants.OFFSET, curColor.getRGB());
		}
		Graphics gr=image.getGraphics();
		gr.setColor(Color.RED);
		gr.drawString("Select", (size+DrawConstants.OFFSET)/2,DrawConstants.OFFSET);
		gr.drawString("Insert", 0,size+DrawConstants.OFFSET);
		gr.drawString("Remove", size+DrawConstants.OFFSET,size+DrawConstants.OFFSET);
		try {
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
