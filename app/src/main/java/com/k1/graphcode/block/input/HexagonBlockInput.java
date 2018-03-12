package com.k1.graphcode.block.input;

import com.k1.graphcode.constant.Const;

public class HexagonBlockInput extends RoundBlockInput {

	public HexagonBlockInput(String inputType, String xmlValue, int w, int h) {
		super(inputType, xmlValue, w, h);
		setBackground(Const.Colors.BlockDefault.INPUT_DI);
	}

}
