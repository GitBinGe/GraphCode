package com.k1.graphcode.block.input;

import com.k1.graphcode.utils.Density;

public class RoundBlockUltrasonic extends RoundBlockInput {

	public RoundBlockUltrasonic() {
		super("Ultrasonic", "ULTRASONIC_DIST", Density.dip2px(125), Density
				.dip2px(30));
	}

}
