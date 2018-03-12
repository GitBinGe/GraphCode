package com.k1.graphcode.block.control;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.k1.graphcode.block.Block;

public class BlockWhile extends BlockControl {

	public BlockWhile() {
		super(1, new int[] { 0 });
		setType(TYPE_CHILD);
		setVarType(TYPE_VAR_HEXAGON);
		setText(0, "While");
	}
	/**
	 * <brick type="DoWhileBrick">
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
            <brick type="DoWhileEndBrick"/>
	 */
	@Override
	public void toXmlNodeIncludeChilds(Document document, Element parent) {
		Element p = toXmlNode(document, parent);
		Block varBlock = getVariableBlock(0);
		if (varBlock == null) {
			
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
		endBrick.setAttribute("type", "DoWhileEndBrick");
	}

	@Override
	public Element toXmlNode(Document document, Element parent) {
		Element object = document.createElement("brick");
		parent.appendChild(object);
		object.setAttribute("type", "DoWhileBrick");
		
		Element formulaList = document.createElement("formulaList");
		object.appendChild(formulaList);
		
		Element formula = document.createElement("formula");
		formulaList.appendChild(formula);
		formula.setAttribute("category", "IF_CONDITION");
		
		return formula;
	}

	
	
}
