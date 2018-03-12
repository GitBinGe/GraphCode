package com.k1.graphcode.block.logic;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BlockAnd extends BlockLogic {

	public BlockAnd() {
		super(CHILD_TYPE_HEXAGON, "AND");
	}
	
	@Override
	public void toXmlNodeIncludeChilds(Document document, Element parent) {
		super.toXmlNodeIncludeChilds(document, parent);
		Element value = document.createElement("value");
		value.setTextContent("LOGICAL_AND");
		parent.appendChild(value);
	}
	
	
}
