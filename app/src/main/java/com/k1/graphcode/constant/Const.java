package com.k1.graphcode.constant;

import java.lang.reflect.Field;

import android.content.Context;
import android.support.v4.view.ViewConfigurationCompat;
import android.view.ViewConfiguration;

import com.k1.graphcode.block.BlockType;
import com.k1.graphcode.block.compute.BlockAdd;
import com.k1.graphcode.block.compute.BlockDiv;
import com.k1.graphcode.block.compute.BlockMul;
import com.k1.graphcode.block.compute.BlockSub;
import com.k1.graphcode.block.control.BlockDelay;
import com.k1.graphcode.block.control.BlockForever;
import com.k1.graphcode.block.control.BlockIf;
import com.k1.graphcode.block.control.BlockIfElse;
import com.k1.graphcode.block.control.BlockRepeat;
import com.k1.graphcode.block.control.BlockWhile;
import com.k1.graphcode.block.input.HexagonBlockInfraredtube;
import com.k1.graphcode.block.input.HexagonBlockInfraredtube1;
import com.k1.graphcode.block.input.HexagonBlockInfraredtube2;
import com.k1.graphcode.block.input.HexagonBlockInfraredtube3;
import com.k1.graphcode.block.input.HexagonBlockLight;
import com.k1.graphcode.block.input.HexagonBlockSound;
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
import com.k1.graphcode.block.output.BlockMotor;
import com.k1.graphcode.block.output.BlockSetColor;
import com.k1.graphcode.block.output.BlockSetLED;
import com.k1.graphcode.constant.Const.Colors.BlockDefault;
import com.k1.graphcode.utils.Density;

public class Const {

	public static int TOUCH_SLOP = 10;
	public static int TOUCH_SLOP_VELOCITY = 10;
	public static int BITMAP_MAX = 500;

	public static void init(Context context) {
		TOUCH_SLOP = ViewConfigurationCompat
				.getScaledPagingTouchSlop(ViewConfiguration.get(context));
		TOUCH_SLOP_VELOCITY = TOUCH_SLOP * 40;
		DpConst.init();
		BITMAP_MAX = Density.screenHeight();
	}

	/**
	 * 
	 * @author BinGe 根据分辨率不同的一些DP类型常量
	 */
	public static class DpConst {

		public static int TOUCH_ = 10;
		public static int DP_2 = 20;

		private static void init() {
			Field[] fields = DpConst.class.getDeclaredFields();
			for (Field field : fields) {
				try {
					int value = field.getInt(null);
					field.set(null, Density.dip2px(value));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	public static class Colors {
		
		public static class BlockDefault {
			public static final int COMPUTE = 0xffc474bb;
			public static final int LOGIC = 0xff87b438;
			public static final int CONTROL = 0xff3ba681;
			public static final int VARIABLE = 0xffd8a209;
			public static final int VARIABLE_CONTROL = 0xffd8B209;
			public static final int INPUT_AI = 0xffd6795d;
			public static final int INPUT_DI = 0xffFF69B4;
			public static final int OUTPUT = 0xff628fe2;
			public static final int SUB = 0xffB03060;
		}
		
		public static final int COLOR_BACKGROUND = 0XFF383838;
		public static final int COLOR_LINE = 0XFF282828;
		public static final int COLOR_ADD_BACKGROUND = 0XFF3F3F3F;
	}

	public static final BlockType[] BLOCK_TYPE = new BlockType[] {
			new BlockType("Compute", 
					BlockDefault.COMPUTE, 
					new Class<?>[] {
						BlockAdd.class, 
						BlockSub.class, 
						BlockMul.class,
						BlockDiv.class 
					}),
			new BlockType("Logic", 
					BlockDefault.LOGIC, 
					new Class<?>[] {
						BlockAbove.class, 
						BlockBelow.class, 
						BlockEquels.class,
						BlockNotEquels.class,
						BlockAnd.class, 
						BlockNot.class, 
						BlockOr.class 
					}),
			new BlockType("Control", 
					BlockDefault.CONTROL, 
					new Class<?>[] {
//						BlockMain.class, 
//						BlockSubProgram.class, 
//						BlockTrigger.class,	
						BlockForever.class, 
						BlockRepeat.class, 
						BlockIf.class,
						BlockIfElse.class, 
						BlockWhile.class, 
						BlockDelay.class 
					}),
			new BlockType("Variable", 
					BlockDefault.VARIABLE, 
					new Class<?>[] {
//						BlockVar.class, 
//						BlockVarControl.class 
					}),
			new BlockType("Input", 
					BlockDefault.INPUT_AI,
					new Class<?>[] { 
						RoundBlockLight.class,
						RoundBlockSound.class, 
						RoundBlockUltrasonic.class,
						RoundBlockTemperature.class,
						HexagonBlockLight.class, 
						HexagonBlockSound.class,
						HexagonBlockInfraredtube.class,
						HexagonBlockInfraredtube1.class,
						HexagonBlockInfraredtube2.class,
						HexagonBlockInfraredtube3.class,
						HexagonBlockTouch.class 
					}),
			new BlockType("Output", 
					BlockDefault.OUTPUT, 
					new Class<?>[] {
						BlockMotor.class, 
						BlockSetColor.class, 
						BlockSetLED.class 
					}),
			new BlockType("Sub", BlockDefault.SUB, 
					new Class<?>[] { 
//						BlockCall.class 
					})
		};

	public static class EditType {
		public static final int MAIN = 10086;
		public static final int SUB = 10010;
		public static final int TRIGGER = 10000;
		public static final int VARIABLE = 20000;
	}
	
	public static String[] PART = {
		"Port 1", 
		"Port 2", 
		"Port 3",
		"Port 4",
		"Port 5",
		"Port 6",
		"Port 7",
		"Port 8"
	};

}
