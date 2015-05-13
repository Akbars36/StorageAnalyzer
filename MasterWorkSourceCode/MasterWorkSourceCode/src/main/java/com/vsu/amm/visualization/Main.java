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
import com.vsu.amm.data.storage.SortedArray;
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
		s.setCounterSet(new SimpleCounterSet());
		stor.add(s);

		s = new SimpleList();
		s.setCounterSet(new SimpleCounterSet());
		stor.add(s);
		
		s = new SimpleArray();
		s.setCounterSet(new SimpleCounterSet());
		stor.add(s);
		
		s = new SortedArray();
		s.setCounterSet(new SimpleCounterSet());
		stor.add(s);
		
		s = new SimpleList();
		s.setCounterSet(new SimpleCounterSet());
		stor.add(s);
		
		s = new BTree();
		s.setCounterSet(new SimpleCounterSet());
		stor.add(s);
		
		Vizualizator.Draw(500, "test", stor); // red - sortedlist(lfu_cache);
												// blue - btree(mru_cache);
												// green - simpleListl; yellow -
												// simpleArray
	}
}
