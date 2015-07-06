package fm.jihua.kecheng.ui.activity.baoman;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import fm.jihua.kecheng_hs.R;

public class ArticleDetailActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

//        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            ArticleDetailFragment fragment = new ArticleDetailFragment();
            fragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.article_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            NavUtils.navigateUpTo(this, new Intent(this, ArticleListActivity.class));
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
