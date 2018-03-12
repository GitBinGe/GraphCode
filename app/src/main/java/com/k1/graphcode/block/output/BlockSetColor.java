package com.k1.graphcode.block.output;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.k1.graphcode.block.Block;
import com.k1.graphcode.block.BlockInput;
import com.k1.graphcode.block.control.BlockControl;
import com.k1.graphcode.constant.Const;

public class BlockSetColor extends BlockControl {

	private BlockInput mPortInput = new BlockInput(BlockInput.TYPE_INPUT_SELECT);
	private BlockInput[] mInput = new BlockInput[3];

	public BlockSetColor() {
		super(0, null);
		setType(TYPE_CHILD);
		setVarType(TYPE_VAR_NULL);

		setText(0, "SetColor");
		mPortInput.setDialogTitle("Port");
		mPortInput.setAdapter(Const.PART);
		for (int i = 0; i < mInput.length; i++) {
			mInput[i] = new BlockInput();
			mInput[i].setDialogTitle("Color值（0～255）");
			mInput[i].setValue("255");
		}
		insertBlockVar(new BlockInput[] { mPortInput, mInput[0], mInput[1],
				mInput[2] });
		setBackground(Const.Colors.BlockDefault.OUTPUT);
	}

	@Override
	public void toXmlNodeIncludeChilds(Document document, Element parent) {
		Element brick = document.createElement("brick");
		brick.setAttribute("type", "IBrickRGBLedBrick");
		parent.appendChild(brick);

		Element formulaList = document.createElement("formulaList");
		brick.appendChild(formulaList);

		// 端口
		Element formula = document.createElement("formula");
		formula.setAttribute("category", "IBRICK_PORT");
		formulaList.appendChild(formula);

		Element type = document.createElement("type");
		type.setTextContent("STRING");
		formula.appendChild(type);

		BlockInput input = mPortInput;
		String port = input.getValue();
		port = port.substring(port.length() - 1);
		Element value = document.createElement("value");
		value.setTextContent(port);
		formula.appendChild(value);

		// RED
		formula = document.createElement("formula");
		formula.setAttribute("category", "IBRICK_LED_RED");
		formulaList.appendChild(formula);

		Block block = getInsertBlock(mInput[0]);
		if (block == null) {
			type = document.createElement("type");
			type.setTextContent("NUMBER");
			formula.appendChild(type);

			input = mInput[0];
			value = document.createElement("value");
			value.setTextContent(input.getValue());
			formula.appendChild(value);
		} else {
			block.toXmlNodeIncludeChilds(document, formula);
		}

		// GREEN
		formula = document.createElement("formula");
		formula.setAttribute("category", "IBRICK_LED_GREEN");
		formulaList.appendChild(formula);
		block = getInsertBlock(mInput[1]);
		if (block == null) {
			type = document.createElement("type");
			type.setTextContent("NUMBER");
			formula.appendChild(type);

			input = mInput[1];
			value = document.createElement("value");
			value.setTextContent(input.getValue());
			formula.appendChild(value);
		} else {
			block.toXmlNodeIncludeChilds(document, formula);
		}

		// BLUE
		formula = document.createElement("formula");
		formula.setAttribute("category", "IBRICK_LED_BLUE");
		formulaList.appendChild(formula);
		block = getInsertBlock(mInput[2]);
		if (block == null) {
			type = document.createElement("type");
			type.setTextContent("NUMBER");
			formula.appendChild(type);

			input = mInput[2];
			value = document.createElement("value");
			value.setTextContent(input.getValue());
			formula.appendChild(value);
		} else {
			block.toXmlNodeIncludeChilds(document, formula);
		}
	}

	@Override
	public void element2Variable(Block parent, Element element, Node node) {
		Element formulaList = getFirstElementByTagName(element, "formulaList");
		if (formulaList != null) {
			Element formula = getFirstElementByTagName(formulaList, "formula");
			while (formula != null) {
				Element value = getFirstElementByTagName(formula, "value");
				String val = value.getTextContent();
				String category = formula.getAttribute("category");
				if (category.contains("IBRICK_PORT")) {
					mPortInput.setValue("Port " + val);
				} else if (category.contains("IBRICK_LED_RED")) {
//					mInput[0].setValue(val);
//					mInput.setValue(val);
//					element2Variable(formula);
					Block var = getTypeBlockWidthElement(formula);
					if (var != null) {
						var.element2Variable(var, formula, null);
						insertBlockVariable(var, 1, false);
					} else {
						Element typeElement = getFirstElementByTagName(formula, "type");
						String type = typeElement.getTextContent();
						if (type.equals("NUMBER")) {
							mInput[0].setValue(val);
						}
					}
				} else if (category.contains("IBRICK_LED_GREEN")) {
//					mInput[1].setValue(val);
					Block var = getTypeBlockWidthElement(formula);
					if (var != null) {
						var.element2Variable(var, formula, null);
						insertBlockVariable(var, 2, false);
					} else {
						Element typeElement = getFirstElementByTagName(formula, "type");
						String type = typeElement.getTextContent();
						if (type.equals("NUMBER")) {
							mInput[1].setValue(val);
						}
					}
				} else if (category.contains("IBRICK_LED_BLUE")) {
//					mInput[2].setValue(val);
					Block var = getTypeBlockWidthElement(formula);
					if (var != null) {
						var.element2Variable(var, formula, null);
						insertBlockVariable(var, 3, false);
					} else {
						Element typeElement = getFirstElementByTagName(formula, "type");
						String type = typeElement.getTextContent();
						if (type.equals("NUMBER")) {
							mInput[2].setValue(val);
						}
					}
				}
				formula = getNextElementByTagName(formula, "formula");
			}
		}
	}

}
