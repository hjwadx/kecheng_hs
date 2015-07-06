package fm.jihua.kecheng.ui.helper;

import java.io.File;

import android.app.Activity;
import android.os.Environment;
import fm.jihua.common.ui.helper.PhotoPicker;
import fm.jihua.kecheng.utils.Const;

public class FaceEditor extends PhotoPicker{
	public FaceEditor(Activity parent) {
		super(parent);
	}
	protected File getPhotoPath(){
		return new File(Environment.getExternalStorageDirectory() + Const.IMAGE_DIR + "myavatar.jpg");
	}
	protected int getWidth(){
		return Const.REGISTER_IMAGE_WIDTH;
	}
	protected int getHeight(){
		return Const.REGISTER_IMAGE_HEIGHT;
	}
}
