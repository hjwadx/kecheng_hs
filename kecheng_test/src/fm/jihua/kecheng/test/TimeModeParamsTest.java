package fm.jihua.kecheng.test;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng.ui.activity.setting.TimeModeParams;

/**
 *	@date	2013-8-21
 *	@introduce	
 */
public class TimeModeParamsTest extends AndroidTestCase {

	TimeModeParams timeModeParams;
	protected void setUp() throws Exception {
		super.setUp();
		timeModeParams=new TimeModeParams(getContext());
	}
	
	public void testInitListItemData(){
		App.mTimeSlotLength = 4;
		timeModeParams.initListItemData("");
		List<String> stringList = new ArrayList<String>();
		stringList.add("8:00-8:45");
		stringList.add("8:55-9:40");
		stringList.add("9:50-10:35");
		stringList.add("10:45-11:30");
		assertEquals(stringList, timeModeParams.listString);
		
		timeModeParams=new TimeModeParams(getContext());
		timeModeParams.initListItemData("8:00-8:45@8:55-9:40");
		List<String> stringList1 = new ArrayList<String>();
		stringList1.add("8:00-8:45");
		stringList1.add("8:55-9:40");
		stringList1.add("9:50-10:35");
		stringList1.add("10:45-11:30");
		assertEquals(stringList1, timeModeParams.listString);
		
		String resultTimeStringByList = timeModeParams.getResultTimeStringByList();
		assertEquals("8:00-8:45@8:55-9:40@9:50-10:35@10:45-11:30", resultTimeStringByList);
		
		assertEquals("9:50-10:35", timeModeParams.getTimeStringByPosition(2));
	}
	
	public void testStringFromTime(){
		int totalTime = 9*60+10;//9:10
		assertEquals("9:10", timeModeParams.getStringFromTime(totalTime));
		
		int totalTime1 = 15*60 + 20;
		assertEquals("15:20", timeModeParams.getStringFromTime(totalTime1));
	}
	
	public void testInitBaseInfoByFirstSetting(){
		timeModeParams.initBaseInfoByFirstSetting("9:10-10:00");
		assertEquals(9, timeModeParams.startTimeHour);
		assertEquals(10, timeModeParams.startTimeMinute);
		assertEquals(50, timeModeParams.classLength);
		assertEquals(10, timeModeParams.playTime);
		
		timeModeParams.initBaseInfoByFirstSetting("8:30-8:00");
		assertEquals(8, timeModeParams.startTimeHour);
		assertEquals(30, timeModeParams.startTimeMinute);
		assertEquals(-30, timeModeParams.classLength);
		assertEquals(10, timeModeParams.playTime);
	}
	
	public void testCheckListString(){
		List<String> stringList1 = new ArrayList<String>();
		stringList1.add("8:00-8:45");
		stringList1.add("8:55-9:40");
		stringList1.add("9:50-10:35");
		stringList1.add("10:45-11:30");
		timeModeParams.listString = stringList1;
		assertEquals(true, timeModeParams.checkListString());
		
		timeModeParams.setWheelUtilString("8:55-9:50", 1);
		assertEquals("10:00-10:45", timeModeParams.listString.get(2));
		
		List<String> stringList2 = new ArrayList<String>();
		stringList2.add("8:00-8:45");
		stringList2.add("8:35-9:40");
		stringList2.add("9:50-10:35");
		stringList2.add("10:45-11:30");
		timeModeParams.listString = stringList2;
		assertEquals(false, timeModeParams.checkListString());
		
	}
	
	public void testGetStringFromTime(){
		
		assertEquals("23:59", timeModeParams.getStringFromTime(10000));
		assertEquals("9:00", timeModeParams.getStringFromTime(540));
		
	}
	

}
