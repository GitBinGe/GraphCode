package com.k1.graphcode.block.output;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import android.text.TextUtils;

import com.k1.graphcode.block.Block;
import com.k1.graphcode.block.BlockInput;
import com.k1.graphcode.block.BlockInput.ValueChangedListener;
import com.k1.graphcode.block.control.BlockControl;
import com.k1.graphcode.constant.Const;

public class BlockMotor extends BlockControl implements ValueChangedListener {

	private BlockInput mPortInput = new BlockInput(BlockInput.TYPE_INPUT_SELECT);
	private BlockInput mDirectionInput = new BlockInput(
			BlockInput.TYPE_INPUT_SELECT);
	private BlockInput mInput = new BlockInput();
	private String mPwmValue;
	private boolean mChangedValue = true;

	public BlockMotor() {
		super(0, null);
		setType(TYPE_CHILD);
		setVarType(TYPE_VAR_NULL);
		setText(0, "MOTOR");

		mPortInput.setDialogTitle("Port");
		mPortInput.setAdapter(Const.PART);
		mDirectionInput.setValueChangedListener(this);
		mDirectionInput.setDialogTitle("Direction");
		mDirectionInput
				.setAdapter(new String[] { "Forward", "Backward", "Stop" });
		mInput.setDialogTitle("PWM值（0~255）");
		mInput.setValueChangedListener(new ValueChangedListener() {
			public void valueChcanged(String value) {
				if (mChangedValue) {
					mPwmValue = value;
				}
			}
		});
		mInput.setValue("255");
		insertBlockVar(new BlockInput[] { mPortInput, mDirectionInput, mInput });

		setBackground(Const.Colors.BlockDefault.OUTPUT);
	}

	@Override
	public void toXmlNodeIncludeChilds(Document document, Element parent) {
		Element brick = document.createElement("brick");
		brick.setAttribute("type", "IBrickMotorBrick");
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

		String port = mPortInput.getValue();
		port = port.substring(port.length() - 1);
		Element value = document.createElement("value");
		value.setTextContent(port);
		formula.appendChild(value);

		// 方向
		formula = document.createElement("formula");
		formula.setAttribute("category", "IBRICK_MOTOR_DIR");
		formulaList.appendChild(formula);

		type = document.createElement("type");
		type.setTextContent("NUMBER");
		formula.appendChild(type);

		port = mDirectionInput.getValue();
		value = document.createElement("value");
		String dir = "0";
		if (port.equals("Stop")) {
			dir = "0";
		} else if (port.equals("Forward")) {
			dir = "1";
		} else if (port.equals("Backward")) {
			dir = "2";
		}
		value.setTextContent(dir);
		formula.appendChild(value);

		// PWM
		formula = document.createElement("formula");
		formula.setAttribute("category", "IBRICK_MOTOR_PWM");
		formulaList.appendChild(formula);

		Block block = getInsertBlock(mInput);
		if (block == null) {
			Element number = document.createElement("type");
			number.setTextContent("NUMBER");
			formula.appendChild(number);

			value = document.createElement("value");
			value.setTextContent(mInput.getValue());
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
				} else if (category.contains("IBRICK_MOTOR_DIR")) {
					if (val.equals("0")) {
						mDirectionInput.setValue("Stop");
					} else if (val.equals("1")) {
						mDirectionInput.setValue("Forward");
					} else if (val.equals("2")) {
						mDirectionInput.setValue("Backward");
					}
				} else if (category.contains("IBRICK_MOTOR_PWM")) {
					// mInput.setValue(val);
					// element2Variable(formula);
					Block var = getTypeBlockWidthElement(formula);
					if (var != null) {
						var.element2Variable(var, formula, null);
						insertBlockVariable(var, 2, false);
					} else {
						Element typeElement = getFirstElementByTagName(formula,
								"type");
						String type = typeElement.getTextContent();
						if (type.equals("NUMBER")) {
							mInput.setValue(val);
						}
					}
				}
				formula = getNextElementByTagName(formula, "formula");
			}
		}
	}

	@Override
	public void valueChcanged(String value) {
		if (value != null) {
			if (value.toLowerCase().equals("stop")) {
				mChangedValue = false;
				mInput.setValue("0");
				mChangedValue = true;
			} else {
				mInput.setValue(TextUtils.isEmpty(mPwmValue) ? "255"
						: mPwmValue);
			}
		}
	}
}
