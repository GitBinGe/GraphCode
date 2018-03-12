package com.k1.graphcode.block.output;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.k1.graphcode.block.Block;
import com.k1.graphcode.block.BlockInput;
import com.k1.graphcode.block.control.BlockControl;
import com.k1.graphcode.constant.Const;

public class BlockSetLED extends BlockControl {

	private BlockInput mPortInput = new BlockInput(BlockInput.TYPE_INPUT_SELECT);
	private BlockInput mSwitchInput = new BlockInput(
			BlockInput.TYPE_INPUT_SELECT);

	public BlockSetLED() {
		super(0, null);
		setType(TYPE_CHILD);
		setVarType(TYPE_VAR_NULL);
		setText(0, "SetLED");
		mPortInput.setAdapter(Const.PART);
		mPortInput.setDialogTitle("Port");
		mSwitchInput.setAdapter(new String[] { "ON", "OFF" });
		mSwitchInput.setDialogTitle("Switch");
		insertBlockVar(new BlockInput[] { mPortInput, mSwitchInput });
		setBackground(Const.Colors.BlockDefault.OUTPUT);
	}

	@Override
	public void toXmlNodeIncludeChilds(Document document, Element parent) {
		Element brick = document.createElement("brick");
		brick.setAttribute("type", "IBrickLEDBrick");
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

		// 开关
		formula = document.createElement("formula");
		formula.setAttribute("category", "IBRICK_LED");
		formulaList.appendChild(formula);

		type = document.createElement("type");
		type.setTextContent("NUMBER");
		formula.appendChild(type);

		input = mSwitchInput;
		String switchString = input.getValue();
		if (switchString != null && switchString.equals("ON")) {
			switchString = "1";
		} else {
			switchString = "0";
		}
		value = document.createElement("value");
		value.setTextContent(switchString);
		formula.appendChild(value);
	}

	@Override
	public void element2Variable(Block parent, Element element, Node node) {
		Element formulaList = getFirstElementByTagName(element, "formulaList");
		if (formulaList != null) {
			Element formula = getFirstElementByTagName(formulaList, "formula");
			while(formula != null) {
				Element value = getFirstElementByTagName(formula, "value");
				String val = value.getTextContent();
				String category = formula.getAttribute("category");
				if (category.contains("IBRICK_PORT")) {
					mPortInput.setValue("Port "+val);
				} else if (category.contains("IBRICK_LED")) {
					if (val!= null && val.equals("1")) {
						mSwitchInput.setValue("ON");
					} else {
						mSwitchInput.setValue("OFF");
					}
				}
				formula = getNextElementByTagName(formula, "formula");
			}
		}
	}

}
