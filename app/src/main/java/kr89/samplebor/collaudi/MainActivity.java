package kr89.samplebor.collaudi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import kr89.samplebor.collaudi.views.TestSearchView;

class CustomPagerAdapter extends PagerAdapter{
    public CustomPagerAdapter(){
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, int position) {
        View childView= null;
        if(position == 0){
            TestSearchView tsv= new TestSearchView(container.getContext());
            tsv.setOnActionListener(new TestSearchView.OnActionListener() {
                @Override
                public void onAction(TestSearchView.Data data) {
                    Toast.makeText(container.getContext(), data.licensePlate, Toast.LENGTH_LONG).show();
                }
            });
            childView= tsv;
        }else{
            childView= new Button(container.getContext());
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager= findViewById(R.id.viewPager);
        mViewPager.setAdapter(new CustomPagerAdapter());
    }
}
