package com.k1.graphcode.block.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.text.TextUtils;

import com.k1.graphcode.block.Block;
import com.k1.graphcode.block.control.BlockControl;
import com.k1.graphcode.block.control.BlockMain;
import com.k1.graphcode.block.control.BlockSubProgram;
import com.k1.graphcode.block.control.BlockTrigger;
import com.k1.graphcode.block.sub.BlockCall;
import com.k1.graphcode.block.sub.BlockSublock;
import com.k1.graphcode.block.variable.BlockVar;
import com.k1.graphcode.block.variable.BlockVarControl;
import com.k1.graphcode.constant.Const;
import com.k1.graphcode.utils.FileManager;

public class Project {

	private File mXmlFile;
	private String mName;
	private Map<String, Block> mMainMap;
	private Map<String, Block> mSubMap;
	private Map<String, Block> mTriggerMap;
	private Map<String, Block> mVarMap;

	private final static float[] SCALE = { 0.6f, 0.8f, 1.0f, 1.2F, 1.4F, 1.6F,
			1.8F, 2.0F, 2.2F, 2.4F };
	private int mCurrent = 3;

	private Project() {

	}

	public Project(File xml) {
		this(xml.getName().replaceAll(".xml", ""));
		mXmlFile = xml;
	}

	public Project(String name) {
		this();
		mName = name;
		mMainMap = new LinkedHashMap<String, Block>();
		mSubMap = new LinkedHashMap<String, Block>();
		mTriggerMap = new LinkedHashMap<String, Block>();
		mVarMap = new LinkedHashMap<String, Block>();

		BlockVar var = new BlockVar();
		var.setName("Variable");
		mVarMap.put("Variable", var);

		for (int i = 0; i < SCALE.length; i++) {
			if (SCALE[i] == Block.SCALE) {
				mCurrent = i;
			}
		}
	}

	public void zoomIn() {
		mCurrent++;
		if (mCurrent > SCALE.length - 1) {
			mCurrent = SCALE.length - 1;
		}
		zoom();
	}

	public void zoomOut() {
		mCurrent--;
		mCurrent = mCurrent < 0 ? 0 : mCurrent;
		for (Block block : getMainScript()) {
			block.recycleIncludeChild();
		}
		for (Block block : getSubScript()) {
			block.recycleIncludeChild();
		}
		for (Block block : getTriggerScript()) {
			block.recycleIncludeChild();
		}
		zoom();
	}

	public void zoom() {
		float scale = SCALE[mCurrent];
		Block.SCALE = scale;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			Element objectlist = document.createElement("objectList");
			for (Block block : getMainScript()) {
				block.toXmlNodeIncludeChilds(document, objectlist);
			}
			for (Block block : getSubScript()) {
				block.toXmlNodeIncludeChilds(document, objectlist);
			}
			for (Block block : getTriggerScript()) {
				block.toXmlNodeIncludeChilds(document, objectlist);
			}
			NodeList objectNodes = objectlist.getElementsByTagName("object");
			for (int i = 0; i < objectNodes.getLength(); i++) {
				Element objectElement = (Element) objectNodes.item(i);
				Block b = Block.newFromXml(objectElement);
				if (b != null) {
					if (b instanceof BlockMain) {
						saveBlock(Const.EditType.MAIN, b);
					} else if (b instanceof BlockSubProgram) {
						saveBlock(Const.EditType.SUB, b);
					} else if (b instanceof BlockTrigger) {
						saveBlock(Const.EditType.TRIGGER, b);
					}
				}
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void initWidthFile(final CompleteListener completeListener) {
		if (mXmlFile != null) {
			new Thread() {
				public void run() {
					initWidthXml(mXmlFile);
					if (completeListener != null) {
						completeListener.complete(Project.this);
					}
					mXmlFile = null;
				}
			}.start();
		}
	}

	public void saveBlock(int type, Block block) {
		switch (type) {
		case Const.EditType.MAIN:
			mMainMap.put(block.getName(), block);
			break;
		case Const.EditType.SUB:
			mSubMap.put(block.getName(), block);
			break;
		case Const.EditType.TRIGGER:
			mTriggerMap.put(block.getName(), block);
			break;
		case Const.EditType.VARIABLE:
			mVarMap.put(block.getName(), block);
			break;
		}
//		 save();
	}

	public void removeBlock(int type, Block block) {
		switch (type) {
		case Const.EditType.MAIN:
			mMainMap.remove(block.getName());
			break;
		case Const.EditType.SUB:
			mSubMap.remove(block.getName());
			break;
		case Const.EditType.TRIGGER:
			mTriggerMap.remove(block.getName());
			break;
		case Const.EditType.VARIABLE:
			mVarMap.remove(block.getName());
			break;
		}
		// save();
	}

	public void remaneBlock(Block block, String newName) {
		String old = block.getName();
		block.setName(newName);
		if (block instanceof BlockMain) {
			mMainMap.remove(old);
			mMainMap.put(newName, block);
		} else if (block instanceof BlockSubProgram) {
			mSubMap.remove(old);
			mSubMap.put(newName, block);
			findTheCallByName(old, newName);
		} else if (block instanceof BlockTrigger) {
			mTriggerMap.remove(old);
			mTriggerMap.put(newName, block);
		}
	}

	public void findTheCallByName(String old, String newName) {
		for (Block block : getMainScript()) {
			renameSubBlock(block, old, newName);
			block.requestComputeRect();
			block.requestLayout();
		}
		for (Block block : getSubScript()) {
			renameSubBlock(block, old, newName);
		}
		for (Block block : getTriggerScript()) {
			renameSubBlock(block, old, newName);
		}
	}

	public void renameSubBlock(Block block, String old, String newName) {
		for (Block childBlock : block.getChilds()) {
			if (childBlock instanceof BlockSublock) {
				String name = childBlock.getName();
				if (!TextUtils.isEmpty(name) && name.equals(old)) {
					childBlock.setName(newName);
					childBlock.setUseCacheOnlySelf(false);
					BlockCall call = (BlockCall) childBlock.getParent();
					call.rebuildCcache();
					call.requestComputeRect();
					call.requestLayout();
				}
			} else {
				renameSubBlock(childBlock, old, newName);
			}
		}
	}

	public void findTheVariableByName(String old, String newName) {
		Block var = mVarMap.get(old);
		if (var != null) {
			mVarMap.remove(old);
			BlockVar newVar = new BlockVar();
			newVar.setName(newName);
			mVarMap.put(newName, newVar);
		}
		for (Block block : getMainScript()) {
			renameVar(block, old, newName);
			block.requestComputeRect();
			block.requestLayout();
			block.setUseCacheIncludeChilds(false);
		}
		for (Block block : getSubScript()) {
			renameVar(block, old, newName);
			block.requestComputeRect();
			block.requestLayout();
			block.setUseCacheIncludeChilds(false);
		}
		for (Block block : getTriggerScript()) {
			renameVar(block, old, newName);
			block.requestComputeRect();
			block.requestLayout();
			block.setUseCacheIncludeChilds(false);
		}
	}

	public void renameVar(Block block, String old, String newName) {
		for (Block childBlock : block.getChilds()) {
			if (childBlock instanceof BlockVar) {
				String name = childBlock.getName();
				if (!TextUtils.isEmpty(name) && name.equals(old)) {
					childBlock.setName(newName);
				}
			} else if (childBlock instanceof BlockVarControl) {
				BlockVarControl control = (BlockVarControl) childBlock;
				String name = control.getVarName();
				if (!TextUtils.isEmpty(name) && name.equals(old)) {
					control.setVar(newName);
					block.requestComputeRect();
					block.requestLayout();
					block.setUseCacheIncludeChilds(false);
				}
			} else {
				renameVar(childBlock, old, newName);
			}
			if (childBlock instanceof BlockControl) {
				BlockControl control = (BlockControl) childBlock;
				control.rebuildCcache();
			}
		}
	}

	public List<Block> getMainScript() {
		return new ArrayList<Block>(mMainMap.values());
	}

	public List<Block> getSubScript() {
		return new ArrayList<Block>(mSubMap.values());
	}

	public List<Block> getTriggerScript() {
		return new ArrayList<Block>(mTriggerMap.values());
	}

	public List<Block> getVariableScript() {
		return new ArrayList<Block>(mVarMap.values());
	}

	public Block getBlock(String name) {
		if (mMainMap.containsKey(name)) {
			return mMainMap.get(name);
		}
		if (mSubMap.containsKey(name)) {
			return mSubMap.get(name);
		}
		if (mTriggerMap.containsKey(name)) {
			return mTriggerMap.get(name);
		}
		return null;
	}

	public boolean containsName(String name) {
		return mMainMap.containsKey(name) || mSubMap.containsKey(name)
				|| mTriggerMap.containsKey(name);
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public void initWidthXml(final File xml) {
		// new Thread() {
		// public void run() {
		try {
			InputStream is = null;
			is = new FileInputStream(xml);
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(is);
			Element root = dom.getDocumentElement();
			String tag = root.getTagName();

			// 文档内容错误
			if (tag == null || !tag.equals("program")) {
				return;
			}

			Element objectList = (Element) root.getElementsByTagName(
					"objectList").item(0);
			NodeList objectNodes = objectList.getElementsByTagName("object");
			for (int i = 0; i < objectNodes.getLength(); i++) {
				Element objectElement = (Element) objectNodes.item(i);
				Block b = Block.newFromXml(objectElement);
				if (b != null) {
					if (b instanceof BlockMain) {
						saveBlock(Const.EditType.MAIN, b);
					} else if (b instanceof BlockSubProgram) {
						saveBlock(Const.EditType.SUB, b);
					} else if (b instanceof BlockTrigger) {
						saveBlock(Const.EditType.TRIGGER, b);
					}
				}
			}
			List<String> list = Saver.getInstence().getVariavles(getName());
			if (list.size() == 0) {
				for (Block var : getVariableScript()) {
					list.add(var.getName());
				}
				Saver.getInstence().saveVariable(getName(), list);
				list = Saver.getInstence().getVariavles(getName());
			}
			for (String varName : list) {
				BlockVar block = new BlockVar();
				block.setName(varName);
				saveBlock(Const.EditType.VARIABLE, block);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		// }
		// }.start();
	}

	public void save(final CompleteListener completeListener) {
		new Thread() {
			public void run() {
				try {
					createXml();
					List<String> list = new ArrayList<String>();
					for (Block var : getVariableScript()) {
						list.add(var.getName());
					}
					Saver.getInstence().saveVariable(mName, list);
					if (completeListener != null) {
						completeListener.complete(Project.this);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public String createXml() throws Exception {
		Project project = this;
		String name = project.getName();
		String fileName = FileManager.getXmlPath() + "/" + name + ".xml";

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();

		Element root = document.createElement("program");
		document.appendChild(root);
		Element header = document.createElement("header");
		root.appendChild(header);
		Element appname = document.createElement("applicationName");
		appname.appendChild(document.createTextNode(name));
		header.appendChild(appname);
		Element appVersion = document.createElement("applicationVersion");
		appVersion.appendChild(document.createTextNode("1.0"));
		header.appendChild(appVersion);
		Element programName = document.createElement("programName");
		programName.appendChild(document.createTextNode(name));
		header.appendChild(programName);

		Element objectlist = document.createElement("objectList");
		root.appendChild(objectlist);

		for (Block block : project.getMainScript()) {
			block.toXmlNodeIncludeChilds(document, objectlist);
		}

		for (Block block : project.getSubScript()) {
			block.toXmlNodeIncludeChilds(document, objectlist);
		}

		for (Block block : project.getTriggerScript()) {
			block.toXmlNodeIncludeChilds(document, objectlist);
		}

		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		PrintWriter pw = new PrintWriter(new FileOutputStream(fileName));
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(pw);
		transformer.transform(source, result);

		return fileName;
	}

	public interface CompleteListener {
		public void complete(Project project);
	}
}
