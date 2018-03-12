package com.k1.graphcode.block.logic;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BlockOr extends BlockLogic {

	public BlockOr() {
		super(CHILD_TYPE_HEXAGON, "OR");
	}
	
	@Override
	public void toXmlNodeIncludeChilds(Document document, Element parent) {
		super.toXmlNodeIncludeChilds(document, parent);
		Element value = document.createElement("value");
		value.setTextContent("LOGICAL_OR");
		parent.appendChild(value);
	}
	
}
