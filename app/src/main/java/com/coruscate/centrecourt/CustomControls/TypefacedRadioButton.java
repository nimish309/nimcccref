package com.coruscate.centrecourt.CustomControls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RadioButton;

public class TypefacedRadioButton extends RadioButton {
	public TypefacedRadioButton(Context context, AttributeSet attrs,
								int defStyle) {
		super(context, attrs, defStyle);

	}

	@SuppressLint("NewApi")
	public TypefacedRadioButton(Context context, AttributeSet attrs) {
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

	public TypefacedRadioButton(Context context) {
		super(context);

	}
}
