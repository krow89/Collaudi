package kr89.samplebor.collaudi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import kr89.samplebor.collaudi.models.TestRecordFilter;
import kr89.samplebor.collaudi.views.TestSearchView;

class CustomPagerAdapter extends PagerAdapter{
    private TestSearchView.OnActionListener mActionListener;
    private TestSearchView mFirstChild;
    private DBShowerView    mSecondChild;

    public CustomPagerAdapter(TestSearchView.OnActionListener listener){
        mActionListener= listener;
    }

    public TestSearchView getTestSearchView(){
        return mFirstChild;
    }

    public DBShowerView getDBShowerView(){
        return mSecondChild;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, int position) {
        View childView= null;
        final Context ctx= container.getContext();
        if(position == 0){
            mFirstChild= new TestSearchView(container.getContext());
            mFirstChild.setOnActionListener(mActionListener);
            childView= mFirstChild;
        }else{
            mSecondChild= new DBShowerView(ctx);
            childView= mSecondChild;
        }
        container.addView(childView);
        return childView;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0) return "Ricerca";
        else return "Inserimento";
    }
}



public class MainActivity extends AppCompatActivity {
    private ViewPager   mViewPager;

    private CustomPagerAdapter mViewPagerAdapter= new CustomPagerAdapter(new TestSearchView.OnActionListener() {
        @Override
        public void onAction(TestRecordFilter searchFilter) {
            mViewPager.setCurrentItem(1);
            DBShowerView dbShower= mViewPagerAdapter.getDBShowerView();
            dbShower.fetchAndDisplay(searchFilter);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager= findViewById(R.id.viewPager);
        mViewPager.setAdapter(mViewPagerAdapter);
    }
}
