package fm.jihua.kecheng.utils;


/**
 * @date 2013-7-20
 * @introduce 文件操作工具类
 */
public class AssetsFileUtils {

	private static AssetsFileUtils assetUtils;

	public static final AssetsFileUtils getInstance() {
		if (assetUtils == null) {
			assetUtils = new AssetsFileUtils();
		}
		return assetUtils;
	}

//	private List<StickersInfo> listPasterInfo;
//	public List<StickersInfo> getPasterInfoFromAssets(Context context,boolean cache) {
//
//		AssetManager assets = context.getAssets();
//		String pathString = Const.PASTER_STYLE_FOLDER_PATH;
//		if(listPasterInfo != null && cache){
//			return listPasterInfo;
//		}else{
//			listPasterInfo = new ArrayList<StickersInfo>();
//			try {
//				String[] list = assets.list(pathString);
//				for (String string : list) {
//					String skinJson = FileUtils.getInstance().readStringFromFile(assets.open(pathString + File.separator + string));
//					if (!TextUtils.isEmpty(skinJson)) {
//						Gson gson = new Gson();
//						StickersInfo pasterInfo = gson.fromJson(skinJson, StickersInfo.class);
//						initPasterArray(pasterInfo);
//						listPasterInfo.add(pasterInfo);
//					}
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		sortListPasterInfo();
//		return listPasterInfo;
//	}
	
//	private void sortListPasterInfo(){
//		ComparatorPasterInfo comparatorPasterInfo = new ComparatorPasterInfo();
//		Collections.sort(listPasterInfo, comparatorPasterInfo);
//	}

//	private class ComparatorPasterInfo implements Comparator<StickersInfo> {
//		@Override
//		public int compare(StickersInfo lhs, StickersInfo rhs) {
//			return String.valueOf(lhs.priority).compareTo(String.valueOf(rhs.priority));
//		}
//	}

//	private void initPasterArray(StickersInfo stickersInfo) {
//		if (stickersInfo.stickers != null) {
//			int length = stickersInfo.stickers.length;
//			for (int i = 0; i < length; i++) {
//				Paster paster = stickersInfo.stickers[i];
//				paster.parentPath = stickersInfo.stickerSetName;
//			}
//		}
//	}

//	private Map<String, Paster> mapChatCode2ImageName;
//
//	public Map<String, Paster> getPasterMap2KeyChatCode(Context context) {
//
//		if (mapChatCode2ImageName == null) {
//			mapChatCode2ImageName = new HashMap<String, Paster>();
//			
////			List<StickersInfo> pasterInfoFromAssets = getPasterInfoFromAssets(context, false);
//			List<StickersInfo> pasterInfoFromAssets = PasterUtils.getInstance().getPasterInfoFromSD(context, false);
//			for (StickersInfo stickersInfo : pasterInfoFromAssets) {
//				Paster[] stickers = stickersInfo.stickers;
//				for (Paster paster : stickers) {
//					mapChatCode2ImageName.put(paster.chatCode, paster);
//				}
//			}
//		}
//		return mapChatCode2ImageName;
//	}
//	
//	public Paster getPasterInfoByName(Context context, String name) {
//
////		List<StickersInfo> pasterInfoFromAssets = getPasterInfoFromAssets(context, true);
//		List<StickersInfo> pasterInfoFromAssets = PasterUtils.getInstance().getPasterInfoFromSD(context, false);
//		for (StickersInfo stickersInfo : pasterInfoFromAssets) {
//			Paster[] stickers = stickersInfo.stickers;
//			for (Paster paster : stickers) {
//				if (paster.name.equals(name))
//					return paster;
//			}
//		}
//		return null;
//	}

//	public BitmapDrawable getImageFromAsset(Context context, String filePath) {
//		InputStream input;
//		BitmapDrawable bitmapDrawable = null;
//		try {
//			input = context.getAssets().open(filePath + ".png");
//			bitmapDrawable = new BitmapDrawable(context.getResources(), input);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		return bitmapDrawable;
//	}

	// ----------------------------------------------------------

//	public List<WeekStyleBean> getWeekStyleListFromAssets(Context context) {
//
//		AssetManager assets = context.getAssets();
//		List<WeekStyleBean> listWeekStyle = new ArrayList<WeekStyleBean>();
//		try {
//			String[] list = assets.list(Const.SKIN_STYLE_FOLDER_PATH);
//			for (String string : list) {
//				String skinJson = FileUtils.getInstance().readStringFromFile(assets.open(Const.SKIN_STYLE_FOLDER_PATH + File.separator + string));
//				if (!TextUtils.isEmpty(skinJson)) {
//					Gson gson = new Gson();
//					WeekStyleBean styleBean = gson.fromJson(skinJson, WeekStyleBean.class);
//					if (Const.STR_CUSTOM_STRING.equals(styleBean.name)) {
//						styleBean.category = WeekStyleBean.CATEGORY_CUSTOM;
//					}
//					listWeekStyle.add(styleBean);
//				}
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return listWeekStyle;
//	}
//
//	public WeekStyleBean getWeekStyleBeanByName(Context context, String name) {
//		List<WeekStyleBean> weekStyleListFromAssets = getWeekStyleListFromAssets(context);
//		for (WeekStyleBean weekStyleBean : weekStyleListFromAssets) {
//			if (weekStyleBean.name.equals(name))
//				return weekStyleBean;
//		}
//		return null;
//	}

}
