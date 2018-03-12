package com.k1.graphcode.block.control;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.k1.graphcode.block.Block;
import com.k1.graphcode.block.input.HexagonBlockInput;
import com.k1.graphcode.block.logic.BlockLogic;

public class BlockIf extends BlockControl {

	public BlockIf() {
		super(1, new int[] { 0 });
		setType(TYPE_CHILD);
		setVarType(TYPE_VAR_HEXAGON);
		setText(0, "If");
	}
	
	@Override
	protected boolean isMyVariableBlock(Block b) {
		return b instanceof BlockLogic || b instanceof HexagonBlockInput;
	}
	
	/**
	 * <brick type="IfLogicBeginBrick">
              <formulaList>
                <formula category="IF_CONDITION">
					。
					。
					。
                </formula>
              </formulaList>
            </brick>
					。
					。
					。
            <brick type="IfLogicElseBrick"/>
            <brick type="IfLogicEndBrick"/>
	 */
	@Override
	public void toXmlNodeIncludeChilds(Document document, Element parent) {
		Element p = toXmlNode(document, parent);
		Block varBlock = getVariableBlock(0);
		if (varBlock == null) {
			//<type>NUMBER</type>
            //<value>{常数值}</value>
			
			
//			Element number = document.createElement("type");
//			number.setTextContent("NUMBER");
//			p.appendChild(number);
//			
//			Element value = document.createElement("value");
//			BlockInput input = getInput(0);
//			if (input != null) {
//				value.setTextContent(input.getValue());
//			} else {
//				value.setTextContent("0");
//			}
//			p.appendChild(value);
			
		} else {
			varBlock.toXmlNodeIncludeChilds(document, p);
		}
		
		if (getChildControls(0).size() > 0) {
			for (Block b : getChildControls(0)) {
				b.toXmlNodeIncludeChilds(document, parent);
			}
		}

		
		Element elseBrick = document.createElement("brick");
		parent.appendChild(elseBrick);
		elseBrick.setAttribute("type", "IfLogicElseBrick");
		Element endBrick = document.createElement("brick");
		parent.appendChild(endBrick);
		endBrick.setAttribute("type", "IfLogicEndBrick");
	}

	@Override
	public Element toXmlNode(Document document, Element parent) {
		Element object = document.createElement("brick");
		parent.appendChild(object);
		object.setAttribute("type", "IfLogicBeginBrick");
		
		Element formulaList = document.createElement("formulaList");
		object.appendChild(formulaList);
		
		Element formula = document.createElement("formula");
		formulaList.appendChild(formula);
		formula.setAttribute("category", "IF_CONDITION");
		
		return formula;
	}
	
	
}
