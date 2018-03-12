package com.k1.graphcode.block.compute;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BlockAdd extends BlockCompute {

	public BlockAdd() {
		super("+");
	}
	
	
	@Override
	public void toXmlNodeIncludeChilds(Document document, Element parent) {
		super.toXmlNodeIncludeChilds(document, parent);
		Element value = document.createElement("value");
		value.setTextContent("PLUS");
		parent.appendChild(value);
	}
	
}
