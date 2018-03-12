package com.k1.graphcode.block;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;

import com.k1.graphcode.block.compute.BlockAdd;
import com.k1.graphcode.block.compute.BlockDiv;
import com.k1.graphcode.block.compute.BlockMul;
import com.k1.graphcode.block.compute.BlockSub;
import com.k1.graphcode.block.control.BlockControl;
import com.k1.graphcode.block.control.BlockMain;
import com.k1.graphcode.block.control.BlockSubProgram;
import com.k1.graphcode.block.control.BlockTrigger;
import com.k1.graphcode.block.input.HexagonBlockInfraredtube;
import com.k1.graphcode.block.input.HexagonBlockInfraredtube1;
import com.k1.graphcode.block.input.HexagonBlockInfraredtube2;
import com.k1.graphcode.block.input.HexagonBlockInfraredtube3;
import com.k1.graphcode.block.input.HexagonBlockLight;
import com.k1.graphcode.block.input.HexagonBlockTouch;
import com.k1.graphcode.block.input.RoundBlockLight;
import com.k1.graphcode.block.input.RoundBlockSound;
import com.k1.graphcode.block.input.RoundBlockTemperature;
import com.k1.graphcode.block.input.RoundBlockUltrasonic;
import com.k1.graphcode.block.logic.BlockAbove;
import com.k1.graphcode.block.logic.BlockAnd;
import com.k1.graphcode.block.logic.BlockBelow;
import com.k1.graphcode.block.logic.BlockEquels;
import com.k1.graphcode.block.logic.BlockNot;
import com.k1.graphcode.block.logic.BlockNotEquels;
import com.k1.graphcode.block.logic.BlockOr;
import com.k1.graphcode.block.project.ProjectCache;
import com.k1.graphcode.block.variable.BlockVar;
import com.k1.graphcode.constant.Const;
import com.k1.graphcode.utils.Density;

/**
 * 
 * @author BinGe
 * 所有图形块的基类
 */
public class Block implements Cloneable {

	public static float ANIMATION_TIME = 150f;

	public static int DEFAULT_COLOR = 0x22FFFFFF & Color.BLACK;

	public static int VAR_COLOR = 0x88FFFFFF & Color.WHITE;

	public static int LIGHT_COLOR = 0xaaFFFFFF & Color.WHITE;
	
	//当前图片编辑的放大缩小倍数，默认为1.2倍
	public static float SCALE = 1.2F;

	//图形中使用的一些单量，根据分辨率不同重新初始化。
	public int DP_1 = 1;
	public int BLOCK_ROUND = 10;
	public int BLOCK_PADDING_LEFT = 4;
	public int BLOCK_PADDING_RIGHT = 4;
	public int BLOCK_PADDING_TOP = 4;
	public int BLOCK_PADDING_BOTTOM = 4;
	public int BLOCK_WIDTH = 40;
	public int BLOCK_HEIGHT = 25;

	public int CONTROL_WIDTH = 100;
	public int CONTROL_HEIGHT = 30;
	public int CONTROL_PADDING = 20;
	public int CONTROL_GEAR_ROUND = 6;
	public int CONTROL_GEAR_PADDING = 35;
	
	private void initContast(float scale) {
		DP_1 = Density.dip2px(1);
		BLOCK_ROUND = (int) (Density.dip2px(10) * scale);
		BLOCK_WIDTH = (int) (Density.dip2px(40) * scale);
		BLOCK_HEIGHT = (int) (Density.dip2px(25) * scale);
		BLOCK_PADDING_LEFT = (int) (Density.dip2px(4) * scale);
		BLOCK_PADDING_RIGHT = (int) (Density.dip2px(4) * scale);
		BLOCK_PADDING_TOP = (int) (Density.dip2px(4) * scale);
		BLOCK_PADDING_BOTTOM = (int) (Density.dip2px(4) * scale);

		CONTROL_WIDTH = (int) (Density.dip2px(100) * scale);
		CONTROL_HEIGHT = (int) (Density.dip2px(30) * scale);
		CONTROL_PADDING = (int) (Density.dip2px(20) * scale);
		CONTROL_GEAR_ROUND = (int) (Density.dip2px(6) * scale);
		CONTROL_GEAR_PADDING = (int) (Density.dip2px(35) * scale);
	}

	private long mId;
	private String mName;
	private Block mParent;
	private RectF mRectF;
	private Paint mPaint, mStrokePaint;
	private List<Block> mChilds;
	private boolean isDebug;

	private boolean isPayAnimation;
	private RectF mLayoutStart = new RectF();
	private RectF mLayoutEnd = new RectF();
	private RectF mLayoutRect = new RectF();
	private RectF mSizeStart = new RectF();
	private RectF mSizeEnd = new RectF();
	private RectF mSizeRect = new RectF();
	private long mTime;
	private float mAnimationTimes;
	private boolean mUseCache;
	private Bitmap mCache;

	private List<BlockInput> mInputList;

	public Block() {
		initContast(SCALE);
		create(BLOCK_WIDTH, BLOCK_HEIGHT);
	}

	public void setDebug() {
		isDebug = true;
	}

	public boolean isDebug() {
		return isDebug;
	}

	public Block(float w, float h) {
		initContast(SCALE);
		create(w, h);
	}

	public void create(float w, float h) {
		mInputList = new ArrayList<BlockInput>();
		mChilds = new ArrayList<Block>();
		mRectF = new RectF(0, 0, w, h);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);

		mStrokePaint = new Paint(mPaint);
		mStrokePaint.setStyle(Style.STROKE);
		mStrokePaint.setStrokeWidth(DP_1);
		mStrokePaint.setColor(DEFAULT_COLOR);
	}

	public Paint getStrokePaint() {
		return mStrokePaint;
	}

	//把自己和所有子block设置为是否使用图形缓存绘制图形，可以大大提高UI的流畅度
	public void setUseCacheIncludeChilds(boolean useCache) {
		setUseCacheOnlySelf(useCache);
		if (!isEmptyChilds()) {
			for (Block child : getChilds()) {
				child.setUseCacheIncludeChilds(useCache);
			}
		}
	}

	//把自己设置为是否使用图形缓存绘制图形
	public void setUseCacheOnlySelf(boolean useCache) {
		mUseCache = useCache;
		if (!useCache) {
			if (mCache != null) {
				mCache.recycle();
				mCache = null;
			}
		}
	}

	//判断是否使用缓存
	public boolean isUseCache() {
		if (getHeight() > Const.BITMAP_MAX) {
			if (mCache != null) {
				mCache.recycle();
				mCache = null;
			}
			return false;
		}
		return mUseCache;
	}

	//获取所有的一级子block
	public Class<?>[] getChildKinds() {
		return new Class<?>[0];
	}

	public void setBackground(int background) {
		mPaint.setColor(background);
	}

	public void setName(String name) {
		mName = name;
	}

	public String getName() {
		return mName;
	}

	/**
	 * 添加一个子块
	 */
	public void addBlock(Block block) {
		addBlock(block, mChilds.size());
	}

	//根据index添加子block
	public void addBlock(Block block, int index) {
		if (!mChilds.contains(block)) {
			mChilds.add(index, block);
		}
		block.setParent(this);
		block.offsetTo(getLeft(), getTop());
		requestComputeRect();
		requestLayout();
	}

	//添加带动画的block
	public void addBlockAnimation(Block block) {
		addBlockAnimation(block, mChilds.size());
	}

	public void addBlockAnimation(Block block, int index) {
		RectF start = new RectF(block.getRect());
		addBlock(block, index);
		RectF end = block.getRect();
		block.startAnimation(start, end);
	}

	//删除block
	public void remove(Block block) {
		mChilds.remove(block);
		block.setParent(null);
		requestComputeRect();
		requestLayout();
	}

	// 获取父控制block
	public BlockControl getRootControl(Block b) {
		Block parent = b.getParent();
		if (parent != null) {
			if (parent instanceof BlockControl) {
				return (BlockControl) parent;
			}
			return getRootControl(parent);
		}
		return null;
	}

	// 回收
	public void recycleIncludeChild() {
		recycle();
		if (!isEmptyChilds()) {
			for (Block child : getChilds()) {
				child.recycleIncludeChild();
			}
		}
	}

	// 回收
	public void recycle() {
		if (mCache != null) {
			mCache.recycle();
			mCache = null;
		}
	}

	// 删除bock时播放动画
	public void removeBlockAnimation(Block block) {
	}

	
	public void backBlockAnimation(Block block) {
	}

	
	public void startAnimation(RectF start, RectF end) {
		isPayAnimation = true;
		mLayoutStart.set(start);
		mLayoutEnd.set(end);
		mTime = System.currentTimeMillis();
		mAnimationTimes = 0;
		requestComputeRect();
		requestLayout();
	}

	public void computeAnimation() {
		float t = (System.currentTimeMillis() - mTime) / Block.ANIMATION_TIME;
		if (t < 1) {

		} else {
			if (t > 1.2) {
				endAnimation();
			}
			t = 1;
		}
		requestComputeRect();
		requestLayout();
		mAnimationTimes = t;
	}

	public float getAnimationTimes() {
		return mAnimationTimes;
	}

	public RectF getAnimationLayoutRect() {
		float t = mAnimationTimes;
		mLayoutRect.left = mLayoutStart.left
				+ (mLayoutEnd.left - mLayoutStart.left) * t;
		mLayoutRect.top = mLayoutStart.top
				+ (mLayoutEnd.top - mLayoutStart.top) * t;
		mLayoutRect.right = mLayoutStart.right
				+ (mLayoutEnd.right - mLayoutStart.right) * t;
		mLayoutRect.bottom = mLayoutStart.bottom
				+ (mLayoutEnd.bottom - mLayoutStart.bottom) * t;
		return mLayoutRect;
	}

	public RectF getAnimationSizeRect() {
		float t = mAnimationTimes;
		mSizeRect.left = mSizeStart.left + (mSizeEnd.left - mSizeStart.left)
				* t;
		mSizeRect.top = mSizeStart.top + (mSizeEnd.top - mSizeStart.top) * t;
		mSizeRect.right = mSizeStart.right
				+ (mSizeEnd.right - mSizeStart.right) * t;
		mSizeRect.bottom = mSizeStart.bottom
				+ (mSizeEnd.bottom - mSizeStart.bottom) * t;
		return mSizeRect;
	}

	public void endAnimation() {
		mAnimationTimes = 0;
		isPayAnimation = false;
	}

	public boolean isPayingAnimation() {
		return isPayAnimation;
	}

	public void requestComputeRect() {
		Block root = this;
		while (root != null) {
			Block parent = root.getParent();
			if (parent == null) {
				root.computeRect();
				break;
			}
			root = parent;
		}
	}

	public void removeAll() {
		for (Block b : mChilds) {
			b.setParent(null);
		}
		mChilds.clear();
	}

	public void setParent(Block block) {
		mParent = block;
	}

	public Block getParent() {
		return mParent;
	}

	public boolean isEmptyChilds() {
		return mChilds.size() == 0;
	}

	public List<Block> getChilds() {
		return mChilds;
	}

	public RectF getRect() {
		return mRectF;
	}

	public float getHeight() {
		return mRectF.height();
	}

	public float getWidth() {
		return mRectF.width();
	}

	public float getLeft() {
		return mRectF.left;
	}

	public float getRight() {
		return mRectF.right;
	}

	public float getTop() {
		return mRectF.top;
	}

	public float getBottom() {
		return mRectF.bottom;
	}

	public Paint getPaint() {
		return mPaint;
	}

	public void addInput(BlockInput bi) {
		if (!mInputList.contains(bi)) {
			mInputList.add(bi);
		}
	}

	public void removeInput(BlockInput bi) {
		if (mInputList.contains(bi)) {
			mInputList.remove(bi);
		}
	}

	public BlockInput getInput(int index) {
		if (mInputList.size() > index) {
			return mInputList.get(index);
		}
		return null;
	}

	public List<BlockInput> getInputs() {
		return mInputList;
	}

	public void offsetTo(float left, float top) {
		mRectF.offsetTo(left, top);
		layout(left, top);
	}

	public void offset(float left, float top) {
		left += mRectF.left;
		top += mRectF.top;
		offsetTo(left, top);
	}

	public BlockInput getBlocInput(float x, float y) {
		for (BlockInput bi : mInputList) {
			if (bi.getRect().contains(x, y)) {
				return bi;
			}
		}
		return null;
	}

	/**
	 * 计算block所占区域
	 * 
	 * @return
	 */
	public RectF computeRect() {
		float height = 0;
		float width = 0;
		for (Block b : mChilds) {
			RectF childRectF = b.computeRect();
			height += childRectF.height();
			width = childRectF.width() > width ? childRectF.width() : width;
		}
		if (height != 0) {
			mRectF.bottom = mRectF.top + height;
			mRectF.right = mRectF.left + width;
		}
		return mRectF;
	}

	public Bitmap getCache() {
		return mCache;
	}

	//ui层调用的方法，把block绘制到ui上面
	public void dispatchDraw(Canvas canvas, RectF view) {
		if (getTop() > view.bottom || getBottom() < view.top) {
			return;
		}
		float l = getLeft();
		float t = getTop();
		if (isUseCache()) {
			if (mCache == null) {
				offsetTo(0, 0);
				int widht = Math.round(getWidth());
				int height = Math.round(getHeight());
				if (this instanceof BlockControl) {
					height += CONTROL_GEAR_ROUND;
				}
				mCache = Bitmap.createBitmap(widht, height, Config.ARGB_4444);
				Canvas c = new Canvas(mCache);
				draw(c);
				offsetTo(l, t);
			}
		}

		if (mCache != null) {
			canvas.drawBitmap(mCache, l, t, null);
		} else {
			draw(canvas);
		}

		for (BlockInput bi : mInputList) {
			if (bi != null) {
				bi.draw(canvas);
			}
		}

		Block anim = null;
		for (Block b : getChilds()) {
			if (b.isPayingAnimation()) {
				anim = b;
			} else {
				b.dispatchDraw(canvas, view);
			}
		}
		if (anim != null) {
			anim.dispatchDraw(canvas, view);
		}
	}

	protected void draw(Canvas canvas) {
		// canvas.drawRect(getRect(), mPaint);
	}

	public void layout(float left, float top) {

	}

	// 从根block重新排版
	public void requestLayout() {
		Block root = this;
		while (root != null) {
			Block parent = root.getParent();
			if (parent == null) {
				// root.computeRect();
				root.layout(root.getLeft(), root.getTop());
				break;
			}
			root = parent;
		}
		// layout(mRectF.left, mRectF.top);
	}

	public void stroke(Path p, Canvas canvas) {
		canvas.drawPath(p, mStrokePaint);
	}

	public boolean contains(float x, float y) {
		return mRectF.contains(x, y);
	}

	public void inset(float dx, float dy) {
		mRectF.inset(dx, dy);
	}

	// 点击事件的抬手事件，判断是否选中了该block
	public boolean onBlockDragUp(Block b, float x, float y) {
		return false;
	}
	// 点击事件的移动事件，判断是否选中了该block
	public boolean onBlockDragMove(Block b, float x, float y, Path out) {
		return false;
	}

	public boolean isMyChilds(Block b) {
		return true;
	}

	public float dragX() {
		return mRectF.centerX();
	}

	public float dragY() {
		return mRectF.centerY();
	}

	public void setId(long id) {
		mId = id;
	}

	public long getId() {
		return mId;
	}

	public Block findBlockById(long id) {
		if (id == 0) {
			return null;
		}
		if (mId == id) {
			return this;
		}
		if (isEmptyChilds()) {
			return null;
		}
		for (Block child : getChilds()) {
			Block block = child.findBlockById(id);
			if (block != null) {
				return block;
			}
		}
		return null;
	}

	// 克隆对像，在从侧边栏拖出block的时候使用
	public Block clone() {
		Class<?> name = this.getClass();
		Block clone = null;
		try {
			clone = (Block) name.newInstance();
			clone.offsetTo(getLeft(), getTop());
			// clone.setBackground(mPaint.getColor());
			clone.computeRect();
			clone.requestLayout();
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
		return clone;
	}

	public Block cloneRoot() {
		try {
			return (Block) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void drawCenterText(Canvas canvas, RectF rect, String text) {
		Paint t = new Paint();
		t.setColor(Color.WHITE);
		t.setTextSize(rect.width() / text.length());
		FontMetrics fm = t.getFontMetrics();
		float x = rect.centerX() - t.measureText(text) / 2;
		float y = rect.centerY() + fm.descent;
		canvas.drawText(text, x, y, t);

	}

	public void drawCenterText(Canvas canvas, Paint p, RectF rect, String text) {
		FontMetrics fm = p.getFontMetrics();
		float x = rect.centerX() - p.measureText(text) / 2;
		float y = rect.centerY() + fm.descent;
		canvas.drawText(text, x, y, p);

	}

	public void drawLeftText(Canvas canvas, RectF rect, String text) {
		Paint t = new Paint();
		t.setColor(Color.WHITE);
		t.setTextSize(BLOCK_ROUND);
		FontMetrics fm = t.getFontMetrics();
		float x = rect.left;
		float y = rect.centerY() + fm.descent;
		canvas.drawText(text, x, y, t);

	}

	// 把block转成xml
	public void toXmlNodeIncludeChilds(Document document, Element parent) {
		Element p = toXmlNode(document, parent);
		if (!isEmptyChilds()) {
			for (Block b : getChilds()) {
				b.toXmlNodeIncludeChilds(document, p);
			}
		}
	}

	public Element toXmlNode(Document document, Element parent) {
		return null;
	}

	// 把xml转成block
	public Node element2Block(Element list, Node node) {
		return null;
	}

	// 把xml转成变量block
	public void element2Variable(Block parent, Element element, Node node) {

	}

	// 把block转成json，之前用的，现在不用了，只用xml方式存储
	public JSONObject toJsonIncludeChilds() {
		JSONObject parent = toJson();
		JSONObject childs = new JSONObject();
		try {
			if (!isEmptyChilds()) {
				for (Block b : getChilds()) {
					String name = String.valueOf(System.currentTimeMillis());
					childs.put(name, b.toJsonIncludeChilds());
				}
				parent.put("childs", childs);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return parent;
	}

	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		try {
			json.put("class", this.getClass().getName());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	public void json2Block(JSONObject json) {

	}

	public static Block newFromJson(JSONObject json) {

		try {
			String clsName = json.getString("class");
			Class<?> cls = Class.forName(clsName);
			Block block = (Block) cls.newInstance();
			block.json2Block(json);
			return block;
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}

	// 从xml文件中初始化成一个scripts
	public static Block newFromXml(Element root) {
		Block block = null;
		Element scriptList = (Element) root.getElementsByTagName("scriptList")
				.item(0);
		Element script = (Element) scriptList.getElementsByTagName("script")
				.item(0);
		String objectType = script.getAttribute("type");
		if (objectType != null) {
			if (objectType.equals("StartScript")) {
				block = new BlockMain();
			} else if (objectType.equals("TriggerScript")) {
				block = new BlockTrigger();
			} else if (objectType.equals("BroadcastScript")) {
				block = new BlockSubProgram();
			}
			block.element2Block(root, null);
		}
		return block;
	}

	// 根据xml的类型获取不同的block
	public Block getTypeBlockWidthElement(Element root) {
		Node current = root.getFirstChild();
		Node node = current;
		while (node != null) {
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (element.getTagName().equals("type")) {
					String type = element.getTextContent();
					if (type.contains("USER_VARIABLE")) {
						Element valueElement = getNextElementByTagName(element,
								"value");
						String varName = valueElement.getTextContent();
						if (varName != null && !varName.equals("var")) {
							BlockVar var = new BlockVar();
							var.setName(varName);
							var.setUseCacheIncludeChilds(true);
							ProjectCache.getInstence().getCurrentProject()
									.saveBlock(Const.EditType.VARIABLE, var);
						}
						BlockVar var = new BlockVar();
						var.setName(varName);
						return var;
					}
				}
				if (element.getTagName().equals("value")) {
					String type = element.getTextContent();
					if (type.contains("PLUS")) {
						return new BlockAdd();
					} else if (type.contains("MINUS")) {
						return new BlockSub();
					} else if (type.contains("MULT")) {
						return new BlockMul();
					} else if (type.contains("DIVIDE")) {
						return new BlockDiv();
					} else if (type.contains("GREATER_THAN")) {
						return new BlockAbove();
					} else if (type.contains("SMALLER_THAN")) {
						return new BlockBelow();
					} else if (type.contains("NOT_EQUAL")) {
						return new BlockNotEquels();
					} else if (type.contains("EQUAL")) {
						return new BlockEquels();
					} else if (type.contains("LOGICAL_NOT")) {
						return new BlockNot();
					} else if (type.contains("LOGICAL_AND")) {
						return new BlockAnd();
					} else if (type.contains("LOGICAL_OR")) {
						return new BlockOr();
					} else if (type.contains("LIGHT_AI")) {
						return new RoundBlockLight();
					} else if (type.contains("LIGHT_DI")) {
						return new HexagonBlockLight();
					} else if (type.contains("SOUND_AI")) {
						return new RoundBlockSound();
					} else if (type.contains("SOUND_DI")) {
						return new HexagonBlockLight();
					} else if (type.contains("TOUCH_DI")) {
						return new HexagonBlockTouch();
					} else if (type.contains("ULTRASONIC_DIST")) {
						return new RoundBlockUltrasonic();
					} else if (type.contains("INFRAREDTUDE_DI3")) {
						return new HexagonBlockInfraredtube3();
					} else if (type.contains("INFRAREDTUDE_DI2")) {
						return new HexagonBlockInfraredtube2();
					} else if (type.contains("INFRAREDTUDE_DI1")) {
						return new HexagonBlockInfraredtube1();
					} else if (type.contains("INFRAREDTUDE_DI")) {
						return new HexagonBlockInfraredtube();
					} else if (type.contains("TEMPERATURE")) {
						return new RoundBlockTemperature();
					}
				}
			}
			node = getNextElementNode(node);
		}
		return null;
	}

	public Element getNextElementByTagName(Node node, String name) {
		node = node.getNextSibling();
		while (node != null) {
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) node;
				String tag = e.getTagName();
				if (tag.equals(name)) {
					return e;
				}
			}
			node = getNextElementNode(node);
		}
		return null;
	}

	public Node getNextElementNode(Node node) {
		node = node.getNextSibling();
		while (node != null) {
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				return node;
			}
			node = node.getNextSibling();
		}
		return node;
	}

	public Element getFirstElementByTagName(Node node, String name) {
		Node n = node.getFirstChild();
		while (n != null) {
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) n;
				String tag = e.getTagName();
				if (tag.equals(name)) {
					return e;
				}
			}
			n = getNextElementNode(n);
		}
		return null;
	}
}
