package com.coruscate.centrecourt.CustomControls;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.CheckBox;

public class TypefacedCheckBox extends CheckBox {

	public TypefacedCheckBox(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public TypefacedCheckBox(Context context, AttributeSet attrs) {

		super(context, attrs);

		if (attrs != null) {
			if (isInEditMode()) {
				return;
			}
			Typeface typeface = Typeface.createFromAsset(context.getAssets(),
					"fonts/AvenirNext-Regular.ttf");
			setTypeface(typeface);
		}
	}

}
