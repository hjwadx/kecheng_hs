package fm.jihua.kecheng.ui.activity.course;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.umeng.analytics.MobclickAgent;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.BaseResult;
import fm.jihua.kecheng.rest.entities.Note;
import fm.jihua.kecheng.rest.entities.NoteResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.rest.service.RestService;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.utils.Const;

public class NewNoteActivity extends BaseActivity implements DataCallback {

	private Note mNote;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			mNote = (Note) getIntent().getExtras().get(Const.BUNDLE_KEY_NOTE);
			setContentView(R.layout.new_note);
			getKechengActionBar().setLeftImage(R.drawable.menubar_btn_icon_cancel);
			if (mNote != null) {
				setTitle("修改笔记");
				getKechengActionBar().setRightImage(R.drawable.menubar_btn_icon_save);
				((EditText) findViewById(R.id.content)).setText(mNote.content);
				((EditText) findViewById(R.id.content)).setSelection(mNote.content.length());
			} else {
				MobclickAgent.onEvent(this, "enter_course_add_note_view");
				setTitle("添加笔记");
				getKechengActionBar().setRightImage(R.drawable.menubar_btn_icon_save);
				((EditText) findViewById(R.id.content)).requestFocus();
				((EditText) findViewById(R.id.content)).post(new Runnable() {
					@Override
					public void run() {
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.showSoftInput(((EditText) findViewById(R.id.content)), InputMethodManager.RESULT_SHOWN);
						// imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
						// InputMethodManager.HIDE_IMPLICIT_ONLY);
					}
				});
			}
			getKechengActionBar().getRightButton().setOnClickListener(clickListener);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(Const.TAG, "NewNoteActivity onCreate Exception:" + e.getMessage());
		}
	}

	/* 消息处理 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.action:
			case R.id.action_textview: {
				int courseId = getIntent().getIntExtra("course_id", 0);
				String content = ((EditText) findViewById(R.id.content)).getText().toString().trim();
				DataAdapter dataAdapter = new DataAdapter(getThis(), NewNoteActivity.this);
				UIUtil.block(NewNoteActivity.this);
				if (content != null && content.length() > 0) {
					if (mNote != null) {
						mNote.content = content;
						dataAdapter.request(RestService.get().saveNote(mNote), DataAdapter.MESSAGE_SAVE_NOTE);
					} else {
						dataAdapter.request(RestService.get().createNote(courseId, content), DataAdapter.MESSAGE_CREATE_NOTE);
					}
				} else if(mNote != null) {
					showDeleteDialog();
				}else {
					finish();
				}
			}
			default:
				break;
			}
		}
	};

	private void showDeleteDialog() {
		new AlertDialog.Builder(this).setTitle("提示").setMessage("空白笔记将会被删掉,是否继续？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i) {
				// 按钮事件
				new DataAdapter(NewNoteActivity.this, new DataCallback() {

					@Override
					public void callback(Message msg) {
						if (msg.what == DataAdapter.MESSAGE_REMOVE_NOTE) {
							BaseResult result = (BaseResult) msg.obj;
							if (result == null || !result.success) {
								// Hint.showTipsShort(NewNoteActivity.this,
								// "删除笔记失败");
								finish();
							} else {
								Intent intent = new Intent();
								intent.putExtra("NOTE", mNote);
								intent.putExtra("ISDELETE", true);
								setResult(RESULT_OK, intent);
								finish();
							}
						}
					}
				}).removeNote(mNote.id);
			}
		}).setNegativeButton("取消", null).show();
	}

	@Override
	public void callback(Message msg) {
		UIUtil.unblock(this);
		Intent intent = new Intent();
		Note note = null;
		switch (msg.what) {
		case DataAdapter.MESSAGE_SAVE_NOTE:
			NoteResult result = (NoteResult) msg.obj;
			if (result != null && result.success) {
				note = result.note;
			}
			break;
		case DataAdapter.MESSAGE_CREATE_NOTE:
			note = (Note) msg.obj;
			if (note != null) {
				MobclickAgent.onEvent(NewNoteActivity.this, "event_course_add_note_succeed");
			}
			break;
		default:
			break;
		}

		if (note != null) {
			intent.putExtra("NOTE", note);
			setResult(RESULT_OK, intent);
			finish();
		} else {
			Hint.showTipsShort(NewNoteActivity.this, "保存笔记失败");
		}

	}
}
