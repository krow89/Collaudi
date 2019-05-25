package kr89.samplebor.collaudi;

import android.content.Context;
import android.content.Intent;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import kr89.samplebor.collaudi.models.TestRecordFilter;
import kr89.samplebor.collaudi.views.TestSearchView;
import org.json.JSONObject;

class CustomPagerAdapter extends PagerAdapter{
    public CustomPagerAdapter(){

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, int position) {
        View childView= null;
        final Context ctx= container.getContext();
        if(position == 0){
            TestSearchView tsv= new TestSearchView(container.getContext());
            tsv.setOnActionListener(new TestSearchView.OnActionListener() {
                @Override
                public void onAction(TestRecordFilter data) {
                    String testUrl= "http://192.168.1.19/collaudi/get_data.php";
                    String realUrl= "http://blackkrow.altervista.org/get_data.php";
                    DBShowerActivity.startForFetchAndDisplay(ctx, data, realUrl);
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
