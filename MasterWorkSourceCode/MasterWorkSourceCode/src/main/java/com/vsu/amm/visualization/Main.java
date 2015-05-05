package com.vsu.amm.visualization;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.vsu.amm.command.DataSetPlayer;
import com.vsu.amm.command.xmlgen.SourceGenerator;
import com.vsu.amm.data.storage.BTree;
import com.vsu.amm.data.storage.BinaryTree;
import com.vsu.amm.data.storage.CacheArray;
import com.vsu.amm.data.storage.HashTable;
import com.vsu.amm.data.storage.IDataStorage;
import com.vsu.amm.data.storage.SimpleArray;
import com.vsu.amm.data.storage.SimpleList;
import com.vsu.amm.data.storage.SortedList;
import com.vsu.amm.data.stream.LogFileWriter;
import com.vsu.amm.stat.SimpleCounterSet;
import com.vsu.amm.stat.ICounterSet.OperationType;
import com.vsu.amm.visualization.coordinate.CoordinanateTranslator;
import com.vsu.amm.visualization.coordinate.Point3DInIRSCoords;
import com.vsu.amm.visualization.data.DataGenerator;

public class Main {
	public static void main(String[] args) {
		List<IDataStorage> stor = new ArrayList<>();
		IDataStorage s = new SortedList();
		//s.setCacheSize(128);
		s.setCounterSet(new SimpleCounterSet());
		stor.add(s);
//		List<Integer> counts=DataGenerator.getContersForStorages(stor, 5, 3, 0);
//		System.out.println(counts);
		CoordinanateTranslator tr=new CoordinanateTranslator(10);
		Point2D p=new Point2D.Double(10, 0);
		Point3DInIRSCoords r=tr.translate(p);
		//System.out.println(r.getInsertCoord()+"    "+r.getRemoveCoord()+"    "+r.getSelectCoord() );
		Vizualizator.Draw(256,"test",stor);
	}
}
