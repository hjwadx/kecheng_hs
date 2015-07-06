package fm.jihua.kecheng.ui.activity.baoman;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.TextView;

import com.tinymission.rss.Item;

import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.ui.fragment.BaseFragment;

public class ArticleDetailFragment extends BaseFragment {

    public static final String ARG_ITEM_ID = "item_id";

    Item displayedArticle;
    public static final String KEY = "ITEM";

    public ArticleDetailFragment() {
    	setHasOptionsMenu(true);	//this enables us to set actionbar from fragment
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parent.setTitle("暴走漫画");
        if (getArguments().containsKey(KEY)) {
            displayedArticle = (Item) getArguments().getSerializable(KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        if (displayedArticle != null) {
        	String title = displayedArticle.getTitle();
        	Date pDate = displayedArticle.getPubDate();
        	String pubDate = "";
            SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss Z", Locale.ENGLISH);
            pubDate = "This post was published " + DateUtils.getDateDifference(pDate) + " by " + displayedArticle.getAuthor(); 
            
        	String content = displayedArticle.getDescription();
        	((TextView) rootView.findViewById(R.id.article_title)).setText(title);
        	((TextView) rootView.findViewById(R.id.article_author)).setText(pubDate);
        	WebView webView = ((WebView) rootView.findViewById(R.id.webView1));
        	webView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
        	WebSettings settings = webView.getSettings();  
        	settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);  
//        	TextView tView = ((TextView) rootView.findViewById(R.id.article_detail));
//            URLImageParser p = new URLImageParser(tView, rootView.getContext());
//            tView.setText(Html.fromHtml(content, p, null));
//            tView.setMovementMethod(LinkMovementMethod.getInstance());
//            Linkify.addLinks(tView, Linkify.WEB_URLS|Linkify.EMAIL_ADDRESSES);
        }
        return rootView;
    }
    
    public class URLDrawable extends Drawable {
        // the drawable that you need to set, you could set the initial drawing
        // with the loading image if you need to
        protected Drawable drawable;

        @Override
        public void draw(Canvas canvas) {
            // override the draw to facilitate refresh function later
            if(drawable != null) {
            	Log.d("BAOMAN",  "URLDrawable width = " + drawable.getIntrinsicWidth() + " height = " + drawable.getIntrinsicHeight());
                drawable.draw(canvas);
            }
        }

		@Override
		public void setAlpha(int alpha) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setColorFilter(ColorFilter cf) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int getOpacity() {
			// TODO Auto-generated method stub
			return 0;
		}
    }
    
    public class URLImageParser implements ImageGetter {
        Context c;
        View container;

        /***
         * Construct the URLImageParser which will execute AsyncTask and refresh the container
         * @param t
         * @param c
         */
        public URLImageParser(View t, Context c) {
            this.c = c;
            this.container = t;
        }

        public Drawable getDrawable(String source) {
            URLDrawable urlDrawable = new URLDrawable();

            // get the actual source
            ImageGetterAsyncTask asyncTask = 
                new ImageGetterAsyncTask( urlDrawable);

            asyncTask.execute(source);

            // return reference to URLDrawable where I will change with actual image from
            // the src tag
            return urlDrawable;
        }

        public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable>  {
            URLDrawable urlDrawable;

            public ImageGetterAsyncTask(URLDrawable d) {
                this.urlDrawable = d;
            }

            @Override
            protected Drawable doInBackground(String... params) {
                String source = params[0];
                return fetchDrawable(source);
            }

            @Override
            protected void onPostExecute(Drawable result) {
                // set the correct bound according to the result from HTTP call
            	if (result != null) {
            		Log.d("BAOMAN",  "width = " + result.getIntrinsicWidth() + " height = " + result.getIntrinsicHeight());
            		urlDrawable.setBounds(0, 0, 0 + result.getIntrinsicWidth(), 0 
                            + result.getIntrinsicHeight()); 

                    // change the reference of the current drawable to the result
                    // from the HTTP call
                    urlDrawable.drawable = result;

                    // redraw the image by invalidating the container
                    URLImageParser.this.container.invalidate();
				}
            }

            /***
             * Get the Drawable from URL
             * @param urlString
             * @return
             */
            public Drawable fetchDrawable(String urlString) {
                try {
                    InputStream is = fetch(urlString);
                    Drawable drawable = null;
                    if (!urlString.endsWith("gif")) {
                    	drawable = Drawable.createFromStream(is, "src");
                        Log.d("BAOMAN", "src = " + urlString + " width = " + drawable.getIntrinsicWidth() + " height = " + drawable.getIntrinsicHeight());
                        drawable.setBounds(0, 0, 0 + drawable.getIntrinsicWidth(), 0 
                                + drawable.getIntrinsicHeight());
					} 
                    return drawable;
                } catch (Exception e) {
                    return null;
                } 
            }

            private InputStream fetch(String urlString) throws MalformedURLException, IOException {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet request = new HttpGet(urlString);
                HttpResponse response = httpClient.execute(request);
                return response.getEntity().getContent();
            }
        }
    }
    
//    ImageGetter imageGetter = new Html.ImageGetter() {
//        @Override
//        public Drawable getDrawable(String source) {
//              Drawable drawable = null;
//              URL url;    
//              try {     
//                  url = new URL(source);    
//                  drawable = Drawable.createFromStream(url.openStream(), "");  //获取网路图片  
//              } catch (Exception e) {
//            	  e.printStackTrace();
//                  return null;    
//              }     
//              // Important
//              drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable
//                            .getIntrinsicHeight());
//              return drawable;
//        }
//    };
//    
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.detailmenu, menu);
    }
    
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        Log.d("item ID : ", "onOptionsItemSelected Item ID" + id);
//        if (id == R.id.actionbar_saveoffline) {
//        	Toast.makeText(getActivity().getApplicationContext(), "This article has been saved of offline reading.", Toast.LENGTH_LONG).show();
//        	return true;
//        } else if (id == R.id.actionbar_markunread) {
//            db.openToWrite();
//            db.markAsUnread(displayedArticle.getGuid());
//            db.close();
//            displayedArticle.setRead(false);
//            ArticleListAdapter adapter = (ArticleListAdapter) ((ArticleListFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.article_list)).getListAdapter();
//            adapter.notifyDataSetChanged();
//        	return true;
//        } else {
//        	return super.onOptionsItemSelected(item);
//        }
//    }
}