package com.vsu.amm.visualization;

import com.vsu.amm.data.cache.AbstractCache;
import com.vsu.amm.data.cache.LFUCache;
import com.vsu.amm.data.cache.LRUCache;
import com.vsu.amm.data.cache.MRUCache;
import com.vsu.amm.data.storage.BTree;
import com.vsu.amm.data.storage.BinaryTree;
import com.vsu.amm.data.storage.IDataStorage;
import com.vsu.amm.data.storage.SimpleArray;
import com.vsu.amm.data.storage.SimpleList;
import com.vsu.amm.data.storage.SortedList;
import com.vsu.amm.stat.SimpleCounterSet;
import com.vsu.amm.visualization.Vizualizator;
import com.vsu.amm.visualization.coordinate.CoordinanateTranslator;
import com.vsu.amm.visualization.coordinate.Point3DInIRSCoords;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		List<IDataStorage> stor = new ArrayList<>();
		IDataStorage s = new SortedList();
		//s.setCacheSize(128);
		s.setCounterSet(new SimpleCounterSet());
		s.setCache(new LFUCache("30", "75", null));
		
		stor.add(s);
		s = new BTree();
		//s.setCacheSize(128);
		s.setCounterSet(new SimpleCounterSet());
		s.setCache(new MRUCache("25", "75", null));
		
		stor.add(s);
		
		s = new SimpleList();
		AbstractCache ac = new MRUCache("20","75", null);
		s.setCache(ac);
		s.setCounterSet(new SimpleCounterSet());
		stor.add(s);
		
		s = new SimpleArray();
		s.setCounterSet(new SimpleCounterSet());
		s.setCache(new LRUCache("30", "50", null));
		stor.add(s);
//		List<Integer> counts=DataGenerator.getContersForStorages(stor, 5, 3, 0);
//		System.out.println(counts);
		CoordinanateTranslator tr = new CoordinanateTranslator(10);
		Point2D p=new Point2D.Double(10, 0);
		Point3DInIRSCoords r=tr.translate(p);
		//System.out.println(r.getInsertCoord()+"    "+r.getRemoveCoord()+"    "+r.getSelectCoord() );
		Vizualizator.Draw(1024,"test",stor); //red - sortedlist(lfu_cache); blue - btree(mru_cache); green - simpleListl; yellow - simpleArray 
	}
}
