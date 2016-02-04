package com.coruscate.centrecourt.CustomControls;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class TypedfacedButton extends Button {

	public TypedfacedButton(Context context, AttributeSet attrs) {

		super(context, attrs);

		if (attrs != null) {
			// Get Custom Attribute Name and value
			// TypedArray styledAttrs = context.obtainStyledAttributes(attrs,
			// R.styleable.TypefacedTextView);
			// int typefaceCode = styledAttrs.getInt(
			// R.styleable.TypefacedTextView_fontStyle, -1);
			// styledAttrs.recycle();
			// Typeface.createFromAsset doesn't work in the layout editor.
			// Skipping...
			if (isInEditMode()) {
				return;
			}
			Typeface typeface = Typeface.createFromAsset(context.getAssets(),
					"fonts/AvenirNext-Regular.ttf");
			setTypeface(typeface);
		}
	}

	public TypedfacedButton(Context context) {
		super(context);
	}

}