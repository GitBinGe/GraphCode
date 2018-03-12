package com.k1.graphcode.block.control;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.k1.graphcode.block.Block;
import com.k1.graphcode.block.BlockInput;
import com.k1.graphcode.block.compute.BlockCompute;
import com.k1.graphcode.block.input.RoundBlockInput;
import com.k1.graphcode.block.variable.BlockVar;

public class BlockRepeat extends BlockControl {

	public BlockRepeat() {
		super(1, new int[] { 0 });
		setType(TYPE_CHILD);
		setVarType(TYPE_VAR_ROUND);
		setText(0, "Repeat");
		getInput(0).setValue("0");
	}
	
	protected boolean isMyVariableBlock(Block b) {
		return b instanceof BlockVar || b instanceof RoundBlockInput
				|| b instanceof BlockCompute;
	}
	
	/**
	 * <brick type="RepeatBrick">
              <formulaList>
                <formula category="TIMES_TO_REPEAT ">
					。
                  {重复次数}
					。
                </formula>
              </formulaList>
            </brick>
					。
					。
					。
            <brick type="LoopEndBrick"/>
	 */
	@Override
	public void toXmlNodeIncludeChilds(Document document, Element parent) {
		Element p = toXmlNode(document, parent);
		Block varBlock = getVariableBlock(0);
		if (varBlock == null) {
			//<type>NUMBER</type>
            //<value>{常数值}</value>
			
			
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
		
		if (getChildControls(0).size() > 0) {
			for (Block b : getChildControls(0)) {
				b.toXmlNodeIncludeChilds(document, parent);
			}
		}

		
		Element endBrick = document.createElement("brick");
		parent.appendChild(endBrick);
		endBrick.setAttribute("type", "LoopEndBrick");
	}

	@Override
	public Element toXmlNode(Document document, Element parent) {
		Element object = document.createElement("brick");
		parent.appendChild(object);
		object.setAttribute("type", "RepeatBrick");
		
		Element formulaList = document.createElement("formulaList");
		object.appendChild(formulaList);
		
		Element formula = document.createElement("formula");
		formulaList.appendChild(formula);
		formula.setAttribute("category", "TIMES_TO_REPEAT");
		
		return formula;
	}

	
}
