package fm.jihua.kecheng.ui.adapter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fm.jihua.common.utils.TimeHelper;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.Note;
import fm.jihua.kecheng.ui.widget.CachedImageView;
import fm.jihua.kecheng.utils.Const;

public class NoteAdapter extends BaseAdapter {
	
List<Note> mNotes;
	
	public NoteAdapter(List<Note> notes){
		this.mNotes = notes;
	}

	@Override
	public int getCount() {
		return this.mNotes.size();
	}

	@Override
	public Object getItem(int position) {
		return this.mNotes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder viewHolder;
		Note note = (Note)getItem(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item_image, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.user_image = (CachedImageView)convertView.findViewById(R.id.user_image);
			viewHolder.title = (TextView)convertView.findViewById(R.id.noteTitle);
			viewHolder.time = (TextView)convertView.findViewById(R.id.noteTime);
			viewHolder.content = (TextView)convertView.findViewById(R.id.noteContent);
			if (note.creator.sex == Const.FEMALE) {
				viewHolder.user_image.setImageResource(R.drawable.avatar_default_female);
			}else {
				viewHolder.user_image.setImageResource(R.drawable.avatar_default_male);
			}
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		SimpleDateFormat sdf  =   new  SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ); 
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
		
		viewHolder.user_image.setImageURI(Uri.parse(note.creator.tiny_avatar_url));		
		viewHolder.title.setText(note.creator != null ? note.creator.name : "");
		viewHolder.time.setText(TimeHelper.getEarlyTime(note.created_at*1000));
		viewHolder.content.setText(note.content);
		if ((position+1) == getCount()) {
			convertView.findViewById(R.id.line).setVisibility(View.GONE);
		} else {
			convertView.findViewById(R.id.line).setVisibility(View.VISIBLE);
		}
			
		return convertView;
	}
	
	static class ViewHolder{
		CachedImageView user_image;
		TextView title;
		TextView time;
		TextView content;
	}
	
	public void addItem(Note note){
		mNotes.add(note);
	}
}
