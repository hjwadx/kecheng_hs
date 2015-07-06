package fm.jihua.kecheng.ui.activity.course;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.BaseResult;
import fm.jihua.kecheng.rest.entities.Note;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.utils.Const;

public class NoteActivity extends BaseActivity {

	private Note mNote;
	DataAdapter mDataAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mNote = (Note) getIntent().getExtras().get(Const.BUNDLE_KEY_NOTE);
		setContentView(R.layout.note);
		// SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		setTitle(R.string.act_note_title);
		// ((TextView)findViewById(R.id.title)).setText(mNote.creator.name +
		// " 发表于 " + sdf.format(new Date(mNote.created_at*1000)));
		((TextView) findViewById(R.id.content)).setText(mNote.content);
		initTitlebar();
	}	
	
	void initTitlebar(){
		getKechengActionBar().getActionButton().setVisibility(View.VISIBLE);
		getKechengActionBar().setRightButtonGone();
		App app = (App)getApplication();
		if (mNote.creator.id == app.getMyUserId()) {
			mDataAdapter = new DataAdapter(this, dataCallback);
			getKechengActionBar().setRightText("删除");
			getKechengActionBar().getRightTextButton().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showDeleteDialog(mNote);
				}
			});
		}
	}
	
	private void showDeleteDialog(final Note note){
		String noteContent = "";
		if (note.content.length() > 10) {
			noteContent = note.content.substring(0, 10) + "...";
		}else {
			noteContent = note.content;
		}
		new AlertDialog.Builder(this)
		.setTitle("提示")
		.setMessage("是否删除笔记：" + noteContent)
		.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialoginterface,
							int i) {
						UIUtil.block(NoteActivity.this);
						mDataAdapter.removeNote(note.id);
					}
				})
		.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialoginterface,
							int i) {
						// 按钮事件
						dialoginterface.dismiss();
					}
				}).show();
	}
	
	DataCallback dataCallback = new DataCallback() {
		
		@Override
		public void callback(Message msg) {
			UIUtil.unblock(NoteActivity.this);
			if (msg.what == DataAdapter.MESSAGE_REMOVE_NOTE) {
				BaseResult result = (BaseResult)msg.obj;
				if (result != null && result.success) {
					Hint.showTipsShort(NoteActivity.this, "删除笔记成功");
					setResult(RESULT_OK);
					finish();
				}else {
					Hint.showTipsShort(NoteActivity.this, "删除笔记失败，请重试");
				}
			}
		}
	};
}
