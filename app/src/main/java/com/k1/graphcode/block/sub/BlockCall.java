package com.k1.graphcode.block.sub;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.k1.graphcode.block.Block;
import com.k1.graphcode.block.BlockInput;
import com.k1.graphcode.block.control.BlockControl;
import com.k1.graphcode.constant.Const;

public class BlockCall extends BlockControl {

	public BlockCall() {
		super(0, new int[] { 0 });
		setType(TYPE_CHILD);
		setVarType(TYPE_VAR_ROUND);
		setText(0, "Call");
		setBackground(Const.Colors.BlockDefault.SUB);
	}
	
	@Override
	protected boolean isMyVariableBlock(Block b) {
		return b instanceof BlockSublock;
	}
	
	@Override
	public BlockInput getBlocInput(float x, float y) {
		return null;
	}
	
	@Override
	public void toXmlNodeIncludeChilds(Document document, Element parent) {
		Element brick = document.createElement("brick");
		parent.appendChild(brick);
		brick.setAttribute("type", "BroadcastWaitBrick");
		
		Element receivedMessage = document.createElement("broadcastMessage");
		brick.appendChild(receivedMessage);
		Block sub = getVariableBlock(0);
		if (sub != null) {
			receivedMessage.setTextContent(sub.getName());
		}
	}
	
}
