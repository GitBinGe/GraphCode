package com.k1.graphcode.block.control;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.k1.graphcode.block.Block;
import com.k1.graphcode.block.BlockInput;
import com.k1.graphcode.block.compute.BlockCompute;
import com.k1.graphcode.block.input.RoundBlockInput;
import com.k1.graphcode.block.variable.BlockVar;

public class BlockDelay extends BlockControl {

	public BlockDelay() {
		super(0, new int[] { 0 });
		setType(TYPE_CHILD);
		setVarType(TYPE_VAR_ROUND);
		setText(0, "Delay");
		getInput(0).setValue("0");
	}
	
	@Override
	protected boolean isMyVariableBlock(Block b) {
		return b instanceof BlockVar || b instanceof RoundBlockInput
				|| b instanceof BlockCompute;
	}
	
	/**
	 * <brick type="WaitBrick">
              <formulaList>
                <formula category="TIME_TO_WAIT_IN_SECONDS">
					。
                  {延时秒数}
					。
                </formula>
              </formulaList>
            </brick>
	 */
	@Override
	public void toXmlNodeIncludeChilds(Document document, Element parent) {
		Element p = toXmlNode(document, parent);
		Block varBlock = getVariableBlock(0);
		if (varBlock == null) {
			Element number = document.createElement("type");
			number.setTextContent("NUMBER");
			p.appendChild(number);
			
			Element value = document.createElement("value");
			p.appendChild(value);
			BlockInput input = getInput(0);
			if (input != null) {
				value.setTextContent(input.getValue());
			} else {
				value.setTextContent("0");
			}
		} else {
			varBlock.toXmlNodeIncludeChilds(document, p);
		}
	}

	@Override
	public Element toXmlNode(Document document, Element parent) {
		Element object = document.createElement("brick");
		parent.appendChild(object);
		object.setAttribute("type", "WaitBrick");
		
		Element formulaList = document.createElement("formulaList");
		object.appendChild(formulaList);
		
		Element formula = document.createElement("formula");
		formulaList.appendChild(formula);
		formula.setAttribute("category", "TIME_TO_WAIT_IN_SECONDS");
		
		return formula;
	}

	
}
