package com.k1.graphcode.block.variable;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.k1.graphcode.block.Block;
import com.k1.graphcode.block.BlockInput;
import com.k1.graphcode.block.compute.BlockCompute;
import com.k1.graphcode.block.control.BlockControl;
import com.k1.graphcode.block.input.RoundBlockInput;
import com.k1.graphcode.constant.Const;

public class BlockVarControl extends BlockControl {

	private String mVarName;
	private String mText;

	public BlockVarControl() {
		super(0, new int[] { 0 });
		setType(TYPE_CHILD);
		setVarType(TYPE_VAR_ROUND);
		setVar(new BlockVar());
		getInput(0).setValue("0");
		setBackground(Const.Colors.BlockDefault.VARIABLE_CONTROL);
	}
	
	@Override
	protected boolean isMyVariableBlock(Block b) {
		return b instanceof BlockVar || b instanceof RoundBlockInput
				|| b instanceof BlockCompute;
	}

	public void setVar(BlockVar var) {
		setId(var.getId());
		mVarName = var.getName();
		mText = var.getName() + "  =  ";
		setText(0, mText);
	}
	
	public void setVar(String var) {
		mVarName = var;
		mText = var + "  =  ";
		setText(0, mText);
		requestComputeRect();
		requestLayout();
	}
	
	public String getVarName() {
		return mVarName;
	}
	
	@Override
	public RectF computeRect() {
//		RectF rf = super.computeRect();
//		LogUtils.d("rf : "+rf.width());
		return super.computeRect();
	}
	
	@Override
	protected void draw(Canvas canvas) {
		super.draw(canvas);
	}

	@Override
	public Block clone() {
		BlockVarControl b = (BlockVarControl) super.clone();
		b.setVar(mVarName);
		b.computeRect();
		return b;
	}

	@Override
	public void toXmlNodeIncludeChilds(Document document, Element parent) {
		Element p = toXmlNode(document, parent);
		Block varBlock = getVariableBlock(0);
		if (varBlock == null) {
			Element number = document.createElement("type");
			number.setTextContent("NUMBER");
			p.appendChild(number);

			Element value = document.createElement("value");
			BlockInput input = getInput(0);
			if (input != null) {
				value.setTextContent(input.getValue());
			} else {
				value.setTextContent("0");
			}
			p.appendChild(value);
		} else {
			varBlock.toXmlNodeIncludeChilds(document, p);
		}
	}

	@Override
	public Element toXmlNode(Document document, Element parent) {
		Element object = document.createElement("brick");
		parent.appendChild(object);
		object.setAttribute("type", "SetVariableBrick");

		Element formulaList = document.createElement("formulaList");
		object.appendChild(formulaList);

		Element formula = document.createElement("formula");
		formulaList.appendChild(formula);
		formula.setAttribute("category", "VARIABLE");

		Element useBrick = document.createElement("inUserBrick");
		object.appendChild(useBrick);
		useBrick.setTextContent("false");

		Element variable = document.createElement("userVariable");
		object.appendChild(variable);
		variable.setTextContent(mVarName);

		return formula;
	}

	@Override
	public JSONObject toJsonIncludeChilds() {
		JSONObject parent = toJson();
		try {
			parent.put("variable", mVarName);
			Block varBlock = getVariableBlock(0);
			if (varBlock == null) {
				BlockInput input = getInput(0);
				if (input != null) {
					parent.put("value", input.getValue());
				} else {
					parent.put("value", "0");
				}
			} else {
				parent.put("value", varBlock.toJsonIncludeChilds());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return parent;
	}
	
	@Override
	public void json2Block(JSONObject json) {
		try {
			String name = json.getString("variable");
			mVarName = name;
			mVarName = name;
			mText = name + "  =  ";
			setText(0, mText);
			JSONObject valueJson = null;
			try {
				valueJson = json.getJSONObject("value");
			} catch (JSONException e) {
				valueJson = null;
			}
			BlockInput input = getInput(0);
			if (valueJson == null) {
				input.setValue(json.getString("value"));
			} else {
				Block varBlock = Block.newFromJson(valueJson);
				setVariableNoAnimation(varBlock, 0);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

}
