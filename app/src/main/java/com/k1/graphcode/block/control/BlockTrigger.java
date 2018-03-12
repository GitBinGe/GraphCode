package com.k1.graphcode.block.control;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.k1.graphcode.block.Block;
import com.k1.graphcode.block.input.HexagonBlockInput;
import com.k1.graphcode.block.logic.BlockLogic;

public class BlockTrigger extends BlockControl {

	public BlockTrigger() {
		super(1, new int[] { 0 });
		setType(TYPE_ROOT);
		setVarType(TYPE_VAR_HEXAGON);
		setText(0, "Trigger");
	}

	protected boolean isMyVariableBlock(Block b) {
		return b instanceof BlockLogic || b instanceof HexagonBlockInput;
	}

	@Override
	public void toXmlNodeIncludeChilds(Document document, Element parent) {
		Element object = document.createElement("object");
		parent.appendChild(object);
		object.setAttribute("name", getName());

		Element scriptList = document.createElement("scriptList");
		object.appendChild(scriptList);

		Element script = document.createElement("script");
		scriptList.appendChild(script);
		script.setAttribute("type", "TriggerScript");

		Element brickList = document.createElement("brickList");
		script.appendChild(brickList);

		if (!getChilds().get(0).isEmptyChilds()) {
			for (Block b : getChilds().get(0).getChilds()) {
				b.toXmlNodeIncludeChilds(document, brickList);
			}
		}

		Element formulaList = document.createElement("formulaList");
		script.appendChild(formulaList);

		Element formula = document.createElement("formula");
		formulaList.appendChild(formula);
		formula.setAttribute("category", "TRIGGER_CONDITION");
		Block varBlock = getVariableBlock(0);
		if (varBlock == null) {
		} else {
			varBlock.toXmlNodeIncludeChilds(document, formula);
		}

	}

	@Override
	public Element toXmlNode(Document document, Element parent) {
		return parent;
	}

	@Override
	public Node element2Block(Element e, Node node) {
		String name = e.getAttribute("name");
		setName(name);

		Element scriptList = (Element) e.getElementsByTagName("scriptList")
				.item(0);
		Element script = (Element) scriptList.getElementsByTagName("script")
				.item(0);
		Element brickList = (Element) script.getElementsByTagName("brickList")
				.item(0);
		if (brickList.hasChildNodes()) {
			node = brickList.getFirstChild();
			while (node != null) {
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element brick = (Element) node;
					super.element2Block(brickList, brick);
					break;
				}
				node = node.getNextSibling();
			}
		}
		
		requestComputeRect();
		requestLayout();
		return node;
	}

}
