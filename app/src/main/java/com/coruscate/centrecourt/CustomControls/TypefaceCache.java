package com.coruscate.centrecourt.CustomControls;

import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.util.Hashtable;

public class TypefaceCache {

	private static final Hashtable<String, Typeface> CACHE = new Hashtable<String, Typeface>();

	private static final String ROBOTO = "fonts/AvenirNext-Regular.ttf";
	private static final String ROBOTOLIGHT = "fonts/AvenirNext-UltraLight.ttf";
	private static final String ROBOTOBOLD = "fonts/AvenirNext-Bold.ttf";
	
	public static Typeface get(AssetManager manager, int typefaceCode) {
		synchronized (CACHE) {

			String typefaceName = getTypefaceName(typefaceCode);

			if (!CACHE.containsKey(typefaceName)) {
				Typeface t = Typeface.createFromAsset(manager, typefaceName);
				CACHE.put(typefaceName, t);
			}
			return CACHE.get(typefaceName);
		}
	}

	private static String getTypefaceName(int typefaceCode) {
		switch (typefaceCode) {
		case 0:
			return ROBOTO;
		case 1:
			return ROBOTOLIGHT;
		case 2:
			return ROBOTOBOLD;
		default:
			return null;
		}
	}

}