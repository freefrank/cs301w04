package testSkinApp;

import junit.framework.Assert;

import com.jayway.android.robotium.solo.Solo;
import android.test.ActivityInstrumentationTestCase2;

@SuppressWarnings({ "unchecked", "rawtypes" })

public class TestMoleFinderActivity extends ActivityInstrumentationTestCase2 {

	private static final String TARGET_PACKAGE_ID="skinConditionsTracker.Model";
	private static final String LAUNCHER_ACTIVITY_FULL_CLASSNAME="skinConditionsTracker.Model.MoleFinderActivity";
	private static Class launcherActivityClass;
	static{
		try{
			launcherActivityClass=Class.forName(LAUNCHER_ACTIVITY_FULL_CLASSNAME);
		}
		catch (ClassNotFoundException e){
			throw new RuntimeException(e);
		}
	}
	public TestMoleFinderActivity()throws ClassNotFoundException{
		super(TARGET_PACKAGE_ID,launcherActivityClass);
	}
	
	private Solo solo;
	
	@Override
	protected void setUp() throws Exception{
		solo = new Solo(getInstrumentation(),getActivity());
	}
	
//	public void testDisplayBlackBox(){
//		solo.clickOnButton("Add Folder");
//		solo.enterText(0, "Test Folder");
//		solo.clickOnButton(0);
//		solo.clickInList(2, 0);
//		solo.assertCurrentActivity("Error: Photo Layout not Open", "PhotoLayoutView" );
//		solo.goBack();
//		solo.clickLongInList(2,0);
//		solo.clickOnButton(0);
//		Assert.assertFalse(solo.searchText("Test Folder"));
//		solo.clickInList(1);
//		solo.assertCurrentActivity("Error: Photo Layout not Open", "PhotoLayoutView");
//		solo.clickOnScreen(50,250);
//		solo.assertCurrentActivity("Error: Photo Layout not Open", "DisplayPhotoView" );
//		
//	}
	
//	public void testAddFolderStressTest(){
//		int x = 0;
//		while (x < 1000){
//			solo.clickOnButton("Add Folder");
//			solo.enterText(0, "Test Folder"+x);
//			solo.clickOnButton(0);
//			x++;
//		}
//	}
	public void testFolderNames(){
		solo.clickOnButton("Add Folder");
		solo.enterText(0, "Test Folder");
		solo.clickOnButton(0);
		Assert.assertTrue(solo.searchText("Test Folder"));
		solo.clickOnButton("Add Folder");
		solo.enterText(0, "NEWLINE\n\n");
		solo.clickOnButton(0);
		Assert.assertFalse(solo.searchText("NEWLINE"));
		solo.clickOnButton("Add Folder");
		solo.enterText(0, "SINGLEQUOTE '' '' '''' '' '");
		solo.clickOnButton(0);
		Assert.assertFalse(solo.searchText("SINGLEQUOTE"));
		solo.clickOnButton("Add Folder");
		solo.enterText(0, "TOOLONG asjdhflakjsdhflkajshdlfkjahskjdhflkashjdflakjshdflkjahsdlfkjahsldkjfhalskdj");
		solo.clickOnButton(0);
		Assert.assertFalse(solo.searchText("TOOLONG"));
	}
	@Override
	public void tearDown() throws Exception{
		solo.finishOpenedActivities();
	}
}
