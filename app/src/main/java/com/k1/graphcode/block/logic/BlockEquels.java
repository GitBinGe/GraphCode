package com.k1.graphcode.block.logic;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BlockEquels extends BlockLogic {

	public BlockEquels() {
		super(CHILD_TYPE_ROUND, "=");
	}
	
	@Override
	public void toXmlNodeIncludeChilds(Document document, Element parent) {
		super.toXmlNodeIncludeChilds(document, parent);
		Element value = document.createElement("value");
		value.setTextContent("EQUAL");
		parent.appendChild(value);
	}
	
}
