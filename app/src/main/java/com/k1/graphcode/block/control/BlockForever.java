package com.k1.graphcode.block.control;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.k1.graphcode.block.Block;

public class BlockForever extends BlockControl {

	public BlockForever() {
		super(1, new int[] { 0 });
		setType(TYPE_CHILD);
		setVarType(TYPE_VAR_NULL);
		setText(0, "Forever");
	}
	@Override
	protected boolean isMyVariableBlock(Block b) {
		return false;
	}

	@Override
	public void toXmlNodeIncludeChilds(Document document, Element parent) {
		Element brickStart = document.createElement("brick");
		parent.appendChild(brickStart);
		brickStart.setAttribute("type", "ForeverBrick");

		if (getChildControls(0).size() > 0) {
			for (Block b : getChildControls(0)) {
				b.toXmlNodeIncludeChilds(document, parent);
			}
		}

		Element brickEnd = document.createElement("brick");
		parent.appendChild(brickEnd);
		brickEnd.setAttribute("type", "LoopEndlessBrick");
	}

	@Override
	public Element toXmlNode(Document document, Element parent) {
		return null;
	}
	
	@Override
	public Node element2Block(Element list, Node node) {
		
		
		
		return null;
	}

}
