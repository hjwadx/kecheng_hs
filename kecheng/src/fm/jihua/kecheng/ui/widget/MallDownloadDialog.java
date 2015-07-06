package fm.jihua.kecheng.ui.widget;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.mall.Product;
import fm.jihua.kecheng.utils.FileUtils;

/**
 * @date 2013-9-5
 * @introduce
 */
public class MallDownloadDialog extends LinearLayout {

	private CachedImageView dialogImage;
	private TextView tv_title;
	private TextView tv_progress;
	private ProgressBar dialog_progressBar;
	private ImageView dialog_cancleBtn;
	
	public static final int TYPE_PASTE = 1;
	public static final int TYPE_SKIN = 2;
	
	private int type;
	
	int layoutRes = 0;

	public MallDownloadDialog(Context context, int type) {
		this(context);
		this.type = type;
		init();
	}
	
	public MallDownloadDialog(Context context) {
		super(context);
	}

	private void init() {
		int layoutRes = 0;
		if (type == TYPE_PASTE) {
			layoutRes = R.layout.layout_download_dialog_paste;
		}else if(type == TYPE_SKIN){
			layoutRes = R.layout.layout_download_dialog_skin;
		}
		LayoutInflater.from(getContext()).inflate(layoutRes, this);
		if(layoutRes != 0){
			findViews();
			initViews();
		}
	}

	private void findViews() {
		dialogImage = (CachedImageView) findViewById(R.id.dialog_image);
		tv_title = (TextView) findViewById(R.id.dialog_text_title);
		tv_progress = (TextView) findViewById(R.id.dialog_text);
		dialog_progressBar = (ProgressBar) findViewById(R.id.dialog_progressBar);
		dialog_cancleBtn = (ImageView) findViewById(R.id.dialog_cancleBtn);
	}

	private void initViews() {
		dialog_cancleBtn.setOnClickListener(onClickCancleListener);
	}

	public void setData(Product product) {
		dialogImage.setImageURI(Uri.parse(product.banner_url));
		tv_title.setText(product.name);
		dialog_progressBar.setProgress(0);
		dialog_progressBar.setMax(100);
	}

	public void updateProgress(int progress, int current, int total) {
		tv_progress.setText("(正在下载 " + FileUtils.getInstance().byte2String(current) + "/" + FileUtils.getInstance().byte2String(total) + ")");
		dialog_progressBar.setProgress(progress);
	}

	OnClickListener onClickCancleListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			showAlertDialog();
		}
	};

	private void showAlertDialog() {
		Builder builder = new Builder(getContext());
		builder.setTitle("提示").setMessage("是否取消当前下载的内容").setNegativeButton("否", null).setPositiveButton("是", clickListener).create().show();
	}

	DialogInterface.OnClickListener clickListener;

	public void setDialogOnClickListener(DialogInterface.OnClickListener clickListener) {
		this.clickListener = clickListener;
	}

}
