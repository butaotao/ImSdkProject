/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dachen.imsdk.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html.ImageGetter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.URLSpan;
import android.util.Log;

import com.dachen.common.widget.MyImageSpan;
import com.dachen.imsdk.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class for annotating a CharSequence with spans to convert textual emoticons
 * to graphical ones.
 */
public class ExpressionParser {
	private static ExpressionParser sInstance;

	public static ExpressionParser getInstance(Context context) {
		if (sInstance == null) {
			synchronized (ExpressionParser.class) {
				if (sInstance == null) {
					sInstance = new ExpressionParser(context);
				}
			}
		}
		return sInstance;
	}

	private final Context mContext;
	private final Pattern mPattern;
	private final Pattern mHtmlPattern;

	private ExpressionParser(Context context) {
		mContext = context;
		mPattern = buildPattern();
		mHtmlPattern = buildHtmlPattern();
	}

	public static class Smilies {
		public static int[][] getIds() {
			return IDS;
		}

		public static String[][] getTexts() {
			return TEXTS;
		}

		public static int textMapId(String text) {
			if (MAPS.containsKey(text)) {
				return MAPS.get(text);
			} else {
				return -1;
			}
		}

		private static final int[][] IDS = { { R.drawable.emoji_001, R.drawable.emoji_002, R.drawable.emoji_003,
				R.drawable.emoji_004, R.drawable.emoji_005, R.drawable.emoji_006, R.drawable.emoji_007,
				R.drawable.emoji_008, R.drawable.emoji_009, R.drawable.emoji_010, R.drawable.emoji_011,
				R.drawable.emoji_012, R.drawable.emoji_013, R.drawable.emoji_014, R.drawable.emoji_015,
				R.drawable.emoji_016, R.drawable.emoji_017, R.drawable.emoji_018, } };

		private static final String[][] TEXTS = { { "[微笑]", "[得意]", "[害羞]", "[汗]", "[奸笑]", "[惊呆了]", "[开心]", "[哭]",
				"[呕吐]", "[亲亲]", "[色眯眯]", "[生病]", "[生气]", "[爽]", "[委屈]", "[严肃]", "[疑问]", "[晕]" } };

		private static final Map<String, Integer> MAPS = new HashMap<String, Integer>();

		static {
			// 取最小的长度，防止长度不一致出错
			int length = IDS.length > TEXTS.length ? TEXTS.length : IDS.length;
			for (int i = 0; i < length; i++) {
				int[] subIds = IDS[i];
				String[] subTexts = TEXTS[i];
				if (subIds == null || subTexts == null) {
					continue;
				}
				int subLength = subIds.length > subTexts.length ? subTexts.length : subIds.length;
				for (int j = 0; j < subLength; j++) {
					MAPS.put(TEXTS[i][j], IDS[i][j]);
				}
			}
		}
	}

	/*
	 * public static class Gifs { public static int[][] getIds() { return IDS; }
	 * 
	 * public static String[][] getTexts() { return TEXTS; }
	 * 
	 * public static int textMapId(String text) { if (MAPS.containsKey(text)) {
	 * return MAPS.get(text); } else { return -1; } }
	 * 
	 * private static final int[][] IDS = { { R.drawable.gif_001,
	 * R.drawable.gif_002, R.drawable.gif_003, R.drawable.gif_004,
	 * R.drawable.gif_005, R.drawable.gif_006, R.drawable.gif_007,
	 * R.drawable.gif_008 }, { R.drawable.gif_009, R.drawable.gif_010,
	 * R.drawable.gif_011, R.drawable.gif_012, R.drawable.gif_013,
	 * R.drawable.gif_014, R.drawable.gif_015, R.drawable.gif_016 }, {
	 * R.drawable.gif_017, R.drawable.gif_018, R.drawable.gif_019,
	 * R.drawable.gif_020, R.drawable.gif_021, R.drawable.gif_022,
	 * R.drawable.gif_023, R.drawable.gif_024 }, { R.drawable.gif_025,
	 * R.drawable.gif_026, R.drawable.gif_027, R.drawable.gif_028,
	 * R.drawable.gif_029, R.drawable.gif_030, R.drawable.gif_031,
	 * R.drawable.gif_032 }, { R.drawable.gif_033, R.drawable.gif_034,
	 * R.drawable.gif_035, R.drawable.gif_036, R.drawable.gif_037,
	 * R.drawable.gif_038, R.drawable.gif_039, R.drawable.gif_040 }, {
	 * R.drawable.gif_041, R.drawable.gif_042, R.drawable.gif_043,
	 * R.drawable.gif_044, R.drawable.gif_045 } };
	 * 
	 * private static final String[][] TEXTS = { { "bad手势.gif", "come手势.gif",
	 * "fuck手势.gif", "good手势.gif", "nono手势.gif", "ok手势.gif", "yeh手势.gif",
	 * "爱你.gif" }, { "抱抱.gif", "鄙视.gif", "闭嘴.gif", "吃惊.gif", "打哈秋.gif",
	 * "鼓掌.gif", "哈哈.gif", "害羞.gif" }, { "好吃.gif", "呵呵.gif", "哼.gif", "花心.gif",
	 * "奸笑.gif", "见钱开眼.gif", "可爱.gif", "困.gif" }, { "流汗.gif", "流泪.gif", "怒.gif",
	 * "抛媚眼.gif", "亲亲.gif", "傻眼.gif", "生病.gif", "失望.gif" }, { "睡觉.gif",
	 * "思考.gif", "送花.gif", "调皮.gif", "偷笑.gif", "吐白沫.gif", "委屈.gif", "嘻嘻.gif" },
	 * { "喜欢.gif", "兴奋.gif", "嘘！安静.gif", "疑问.gif", "再见.gif" } };
	 * 
	 * private static final Map<String, Integer> MAPS = new HashMap<String,
	 * Integer>();
	 * 
	 * static { // 取最小的长度，防止长度不一致出错 int length = IDS.length > TEXTS.length ?
	 * TEXTS.length : IDS.length; for (int i = 0; i < length; i++) { int[]
	 * subIds = IDS[i]; String[] subTexts = TEXTS[i]; if (subIds == null ||
	 * subTexts == null) { continue; } int subLength = subIds.length >
	 * subTexts.length ? subTexts.length : subIds.length; for (int j = 0; j <
	 * subLength; j++) { MAPS.put(TEXTS[i][j], IDS[i][j]); } } } }
	 */

	/**
	 * 
	 * @author 新版笑脸表情
	 * 
	 */
	public static class New_Smiles {
		// 使用LinkedHashMap保证顺序
		private static final Map<String, Integer> maps = new LinkedHashMap<String, Integer>();

		private static int[][] IDS = { { R.drawable.f_static_000, R.drawable.f_static_001, R.drawable.f_static_002,
				R.drawable.f_static_003, R.drawable.f_static_004, R.drawable.f_static_005, R.drawable.f_static_006,
				R.drawable.f_static_007, R.drawable.f_static_008, R.drawable.f_static_009, R.drawable.f_static_010,
				R.drawable.f_static_011, R.drawable.f_static_012, R.drawable.f_static_013, R.drawable.f_static_014,
				R.drawable.f_static_015, R.drawable.f_static_016, R.drawable.f_static_017, R.drawable.f_static_018,
				R.drawable.f_static_019, R.drawable.f_static_020, R.drawable.f_static_021, R.drawable.f_static_022,
				R.drawable.f_static_023, R.drawable.f_static_024, R.drawable.f_static_025, R.drawable.f_static_026,
				R.drawable.f_static_027, R.drawable.f_static_028, R.drawable.f_static_029, R.drawable.f_static_030,
				R.drawable.f_static_031, R.drawable.f_static_032, R.drawable.f_static_033, R.drawable.f_static_034,
				R.drawable.f_static_035, R.drawable.f_static_036, R.drawable.f_static_037, R.drawable.f_static_038,
				R.drawable.f_static_039, R.drawable.f_static_040, R.drawable.f_static_041, R.drawable.f_static_042,
				R.drawable.f_static_043, R.drawable.f_static_044, R.drawable.f_static_045, R.drawable.f_static_046,
				R.drawable.f_static_047, R.drawable.f_static_048, R.drawable.f_static_049, R.drawable.f_static_050,
				R.drawable.f_static_051, R.drawable.f_static_052, R.drawable.f_static_053, R.drawable.f_static_054,
				R.drawable.f_static_055, R.drawable.f_static_056, R.drawable.f_static_057, R.drawable.f_static_058,
				R.drawable.f_static_059, R.drawable.f_static_060, R.drawable.f_static_061, R.drawable.f_static_062,
				R.drawable.f_static_063, R.drawable.f_static_064, R.drawable.f_static_065, R.drawable.f_static_066,
				R.drawable.f_static_067, R.drawable.f_static_068, R.drawable.f_static_069, R.drawable.f_static_070,
				R.drawable.f_static_071, R.drawable.f_static_072, R.drawable.f_static_073, R.drawable.f_static_074,
				R.drawable.f_static_075, R.drawable.f_static_076, R.drawable.f_static_077, R.drawable.f_static_078,
				R.drawable.f_static_079, R.drawable.f_static_080, R.drawable.f_static_081, R.drawable.f_static_082,
				R.drawable.f_static_083, R.drawable.f_static_084, R.drawable.f_static_085, R.drawable.f_static_086,
				R.drawable.f_static_087, R.drawable.f_static_088, R.drawable.f_static_089, R.drawable.f_static_090,
				R.drawable.f_static_091, R.drawable.f_static_092, R.drawable.f_static_093, R.drawable.f_static_094,
				R.drawable.f_static_095, R.drawable.f_static_096, R.drawable.f_static_097, R.drawable.f_static_098,
				R.drawable.f_static_099, R.drawable.f_static_100, R.drawable.f_static_101, R.drawable.f_static_102,
				R.drawable.f_static_103, R.drawable.f_static_104, R.drawable.f_static_105, R.drawable.f_static_106,

				} };

//		private static String[][] TEXTS = { { "[呲牙]", "[调皮]", "[流汗]", "[偷笑]", "[再见]", "[敲打]", "[擦汗]", "[猪头]", "[玫瑰]",
//				"[流泪]", "[大哭]", "[嘘]", "[酷]", "[抓狂]", "[委屈]", "[便便]", "[炸弹]", "[菜刀]", "[可爱]", "[色]", "[害羞]", "[得意]",
//				"[吐]", "[微笑]", "[发怒]", "[尴尬]", "[惊恐]", "[冷汗]", "[爱心]", "[示爱]", "[白眼]", "[傲慢]", "[难过]", "[惊讶]", "[疑问]",
//				"[睡]", "[亲亲]", "[憨笑]", "[爱情]", "[衰]", "[撇嘴]", "[阴险]", "[奋斗]", "[发呆]", "[右哼哼]", "[拥抱]", "[坏笑]", "[闭嘴]",
//				"[鄙视]", "[晕]", "[大兵]", "[可怜]", "[强]", "[弱]", "[握手]", "[胜利]", "[抱拳]", "[凋谢]", "[饭]", "[蛋糕]", "[西瓜]",
//				"[啤酒]", "[飘虫]", "[勾引]", "[OK]", "[爱你]", "[咖啡]", "[钱]", "[月亮]", "[美女]", "[刀]", "[NO]", "[差劲]", "[拳头]",
//				"[心碎]", "[太阳]", "[礼物]", "[足球]", "[骷髅]", "[乒乓球]", "[闪电]", "[饥饿]", "[困]", "[咒骂]", "[折磨]", "[抠鼻]", "[鼓掌]",
//				"[糗大了]", "[左哼哼]", "[哈欠]", "[快哭了]", "[吓]", "[篮球]", "[乒乓球]", "[NO]", "[跳跳]", "[怄火]", "[转圈]", "[磕头]",
//				"[回头]", "[跳绳]", "[激动]", "[街舞]", "[献吻]", "[左太极]", "[右太极]", "[闭嘴]",
//
//				} };

		static {
			// 第一屏
			maps.put("[呲牙]", R.drawable.f_static_000);
			maps.put("[调皮]", R.drawable.f_static_001);
			maps.put("[流汗]", R.drawable.f_static_002);
			maps.put("[偷笑]", R.drawable.f_static_003);
			maps.put("[再见]", R.drawable.f_static_004);
			maps.put("[敲打]", R.drawable.f_static_005);
			maps.put("[擦汗]", R.drawable.f_static_006);
			maps.put("[猪头]", R.drawable.f_static_007);
			maps.put("[玫瑰]", R.drawable.f_static_008);
			maps.put("[流泪]", R.drawable.f_static_009);
			maps.put("[大哭]", R.drawable.f_static_010);
			maps.put("[嘘]", R.drawable.f_static_011);
			maps.put("[酷]", R.drawable.f_static_012);
			maps.put("[抓狂]", R.drawable.f_static_013);
			maps.put("[委屈]", R.drawable.f_static_014);
			maps.put("[便便]", R.drawable.f_static_015);
			maps.put("[炸弹]", R.drawable.f_static_016);
			maps.put("[菜刀]", R.drawable.f_static_017);
			maps.put("[可爱]", R.drawable.f_static_018);
			maps.put("[色]", R.drawable.f_static_019);
			maps.put("[害羞]", R.drawable.f_static_020);
			maps.put("[得意]", R.drawable.f_static_021);
			maps.put("[吐]", R.drawable.f_static_022);
			maps.put("[微笑]", R.drawable.f_static_023);
			maps.put("[发怒]", R.drawable.f_static_024);
			maps.put("[尴尬]", R.drawable.f_static_025);
			maps.put("[惊恐]", R.drawable.f_static_026);
			maps.put("[冷汗]", R.drawable.f_static_027);
			maps.put("[爱心]", R.drawable.f_static_028);
			maps.put("[示爱]", R.drawable.f_static_029);
			// 第二屏
			maps.put("[白眼]", R.drawable.f_static_030);
			maps.put("[傲慢]", R.drawable.f_static_031);
			maps.put("[难过]", R.drawable.f_static_032);
			maps.put("[惊讶]", R.drawable.f_static_033);
			maps.put("[疑问]", R.drawable.f_static_034);
			maps.put("[睡]", R.drawable.f_static_035);
			maps.put("[亲亲]", R.drawable.f_static_036);
			maps.put("[憨笑]", R.drawable.f_static_037);
			maps.put("[爱情]", R.drawable.f_static_038);
			maps.put("[衰]", R.drawable.f_static_039);
			maps.put("[撇嘴]", R.drawable.f_static_040);
			maps.put("[阴险]", R.drawable.f_static_041);
			maps.put("[奋斗]", R.drawable.f_static_042);
			maps.put("[发呆]", R.drawable.f_static_043);
			maps.put("[右哼哼]", R.drawable.f_static_044);
			maps.put("[拥抱]", R.drawable.f_static_045);
			maps.put("[坏笑]", R.drawable.f_static_046);
			maps.put("[飞吻]", R.drawable.f_static_047);
			maps.put("[鄙视]", R.drawable.f_static_048);
			maps.put("[晕]", R.drawable.f_static_049);
			maps.put("[大兵]", R.drawable.f_static_050);
			maps.put("[可怜]", R.drawable.f_static_051);
			maps.put("[强]", R.drawable.f_static_052);
			maps.put("[弱]", R.drawable.f_static_053);
			maps.put("[握手]", R.drawable.f_static_054);
			maps.put("[胜利]", R.drawable.f_static_055);
			maps.put("[抱拳]", R.drawable.f_static_056);
			maps.put("[凋谢]", R.drawable.f_static_057);
			maps.put("[饭]", R.drawable.f_static_058);
			maps.put("[蛋糕]", R.drawable.f_static_059);
			// 第三屏
			maps.put("[西瓜]", R.drawable.f_static_060);
			maps.put("[啤酒]", R.drawable.f_static_061);
			maps.put("[飘虫]", R.drawable.f_static_062);
			maps.put("[勾引]", R.drawable.f_static_063);
			maps.put("[OK]", R.drawable.f_static_064);
			maps.put("[爱你]", R.drawable.f_static_065);
			maps.put("[咖啡]", R.drawable.f_static_066);
			maps.put("[钱]", R.drawable.f_static_067);
			maps.put("[月亮]", R.drawable.f_static_068);
			maps.put("[美女]", R.drawable.f_static_069);
			maps.put("[刀]", R.drawable.f_static_070);
			maps.put("[发抖]", R.drawable.f_static_071);
			maps.put("[差劲]", R.drawable.f_static_072);
			maps.put("[拳头]", R.drawable.f_static_073);
			maps.put("[心碎]", R.drawable.f_static_074);
			maps.put("[太阳]", R.drawable.f_static_075);
			maps.put("[礼物]", R.drawable.f_static_076);
			maps.put("[足球]", R.drawable.f_static_077);
			maps.put("[骷髅]", R.drawable.f_static_078);
			maps.put("[挥手]", R.drawable.f_static_079);
			maps.put("[闪电]", R.drawable.f_static_080);
			maps.put("[饥饿]", R.drawable.f_static_081);
			maps.put("[困]", R.drawable.f_static_082);
			maps.put("[咒骂]", R.drawable.f_static_083);
			maps.put("[折磨]", R.drawable.f_static_084);
			maps.put("[抠鼻]", R.drawable.f_static_085);
			maps.put("[鼓掌]", R.drawable.f_static_086);
			maps.put("[糗大了]", R.drawable.f_static_087);
			maps.put("[左哼哼]", R.drawable.f_static_088);
			maps.put("[哈欠]", R.drawable.f_static_089);
			// 第四屏
			maps.put("[快哭了]", R.drawable.f_static_090);
			maps.put("[吓]", R.drawable.f_static_091);
			maps.put("[篮球]", R.drawable.f_static_092);
			maps.put("[乒乓球]", R.drawable.f_static_093);
			maps.put("[NO]", R.drawable.f_static_094);
			maps.put("[跳跳]", R.drawable.f_static_095);
			maps.put("[怄火]", R.drawable.f_static_096);
			maps.put("[转圈]", R.drawable.f_static_097);
			maps.put("[磕头]", R.drawable.f_static_098);
			maps.put("[回头]", R.drawable.f_static_099);
			maps.put("[跳绳]", R.drawable.f_static_100);
			maps.put("[激动]", R.drawable.f_static_101);
			maps.put("[街舞]", R.drawable.f_static_102);
			maps.put("[献吻]", R.drawable.f_static_103);
			maps.put("[左太极]", R.drawable.f_static_104);
			maps.put("[右太极]", R.drawable.f_static_105);
			maps.put("[闭嘴]", R.drawable.f_static_106);

		}

		/**
		 * 获取表情数据，以分页的形式返回，list中的一项表示一页表情
		 * 
		 * @return
		 */
		public static List<Map<String, Integer>> getExpressionData() {
			List<Map<String, Integer>> pages = new ArrayList<Map<String, Integer>>();
			// page 表示一页表情
			Map<String, Integer> page = new LinkedHashMap<String, Integer>();
			int count = 0;
			for (Entry<String, Integer> entry : maps.entrySet()) {
				page.put(entry.getKey(), entry.getValue());
				count++;
				// 每页30个表情
				if (count % 30 == 0) {
					pages.add(page);
					page = new LinkedHashMap<String, Integer>();
				}
			}

			if (count % 30 != 0) {
				pages.add(page);
			}

			return pages;
		}

		public static Map<String, Integer> getMaps() {
			return maps;
		}

		public static int[][] getIds() {
			Log.d("New_Smailes", "ids的长度" + IDS.length);
			return IDS;
		}

//		public static String[][] getTexts() {
//			Log.d("New_Smailes", "texts的长度" + TEXTS.length);
//			return TEXTS;
//		}

		public static int textMapId(String text) {
			if (maps.containsKey(text)) {
				return maps.get(text);
			} else {
				return -1;
			}
		}

	}

	/**
	 * Builds the regular expression we use to find smileys in
	 * {@link #addSmileySpans}.
	 */
	private Pattern buildPattern() {
		// Set the StringBuilder capacity with the assumption that the average
		// smiley is 3 characters long.
		StringBuilder patternString = new StringBuilder();

		// Build a regex that looks like (:-)|:-(|...), but escaping the smilies
		// properly so they will be interpreted literally by the regex matcher.
		patternString.append('(');
		// for (int i = 0; i < New_Smiles.TEXTS.length; i++) {
		// for (int j = 0; j < New_Smiles.TEXTS[i].length; j++) {
		// patternString.append(Pattern.quote(New_Smiles.TEXTS[i][j]));
		// patternString.append('|');
		// }
		// }

		Set<String> keys = New_Smiles.maps.keySet();
		for (String key : keys) {
			patternString.append(Pattern.quote(key));
			patternString.append('|');
		}

		// Replace the extra '|' with a ')'
		patternString.replace(patternString.length() - 1, patternString.length(), ")");

		return Pattern.compile(patternString.toString());
	}

	private Pattern buildHtmlPattern() {
		// Set the StringBuilder capacity with the assumption that the average
		// smiley is 3 characters long.
		// StringBuilder patternString = new StringBuilder();

		// Build a regex that looks like (:-)|:-(|...), but escaping the smilies
		// properly so they will be interpreted literally by the regex matcher.
		// patternString.append('(');
		// patternString.append(Pattern.quote("(<a)(\\w)+(?=</a>)"));
		// // Replace the extra '|' with a ')'
		// patternString.replace(patternString.length() - 1,
		// patternString.length(), ")");

		return Pattern.compile("(http://(\\S+?)(\\s))|(www.(\\S+?)(\\s))");
	}

	/**
	 * Adds ImageSpans to a CharSequence that replace textual emoticons such as
	 * :-) with a graphical version.
	 * 
	 * @param text
	 *            A CharSequence possibly containing emoticons
	 * @return A CharSequence annotated with ImageSpans covering any recognized
	 *         emoticons.
	 */
	public CharSequence addSmileySpans(CharSequence text, boolean canClick) {

		SpannableStringBuilder builder = new SpannableStringBuilder(text);
		Matcher matcher = mPattern.matcher(text);
		while (matcher.find()) {
			int resId = New_Smiles.textMapId(matcher.group());
			if (resId != -1) {
				builder.setSpan(new MyImageSpan(mContext, resId), matcher.start(), matcher.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		if (canClick) {
			Matcher htmlmatcher = mHtmlPattern.matcher(text);
			while (htmlmatcher.find()) {
				builder.setSpan(new URLSpan(htmlmatcher.group()), htmlmatcher.start(), htmlmatcher.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		return builder;
	}

	ImageGetter imgGetter = new ImageGetter() {
		public Drawable getDrawable(String source) {
			Drawable drawable = null;
			drawable = Drawable.createFromPath(source); // 显示本地图片
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			return drawable;
		}
	};

}
