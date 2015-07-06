package fm.jihua.kecheng.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.widget.ListView;

import com.commonsware.cwac.endless.LoadMoreAdapter;

import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.Note;
import fm.jihua.kecheng.ui.activity.course.CourseActivity;

public class NoteLoadMoreAdapter extends LoadMoreAdapter{
	
	List<Note> notes ;
	int count;
	Context context;
	ListView listView;
	
	NoteAdapter adapter;
	public NoteLoadMoreAdapter(Activity context,List<Note> notes, ListView list) {
    	super(context, new NoteAdapter(new ArrayList<Note>(notes.subList(0, 3 < notes.size()?3:notes.size()))), R.layout.pending, 
    			R.layout.load_more);
    	this.notes = notes;
    	this.count = notes.size();
    	this.context = context;
    	this.listView = list;
        adapter = (NoteAdapter) getWrappedAdapter();
        if (getWrappedAdapter().getCount() == notes.size()) {
			stopAppending();
		}
    }
	@Override
	protected void appendCachedData() {
		if (getWrappedAdapter().getCount() < count) {
			NoteAdapter a = (NoteAdapter) getWrappedAdapter();
			if (getWrappedAdapter().getCount() + 5 <= notes.size()) {
				for (int i = 0; i < 5; i++) {
					a.addItem(notes.get(a.getCount()));
				}
			} else {
				for (int i = getWrappedAdapter().getCount(); i < count; i++) {
					a.addItem(notes.get(a.getCount()));
				}
			}
			if (getWrappedAdapter().getCount() == notes.size()) {
				stopAppending();
			}
			((CourseActivity)context).setListViewHeightBasedOnChildren(listView);
		}
	}
	
	@Override
	protected boolean cacheInBackground() throws Exception {
		SystemClock.sleep(3000); 
	    return(getWrappedAdapter().getCount() < notes.size());
	}

}
