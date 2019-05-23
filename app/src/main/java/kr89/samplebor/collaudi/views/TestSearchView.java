package kr89.samplebor.collaudi.views;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import com.rm.rmswitch.RMTristateSwitch;
import kr89.samplebor.collaudi.R;


public class TestSearchView extends FrameLayout {
    public static class Data{
        public String licensePlate;
    }

    public interface OnActionListener{
        void onAction(Data data);
    }

    private EditText            mUILicensePlateFilter;
    private RMTristateSwitch    mUITiresFilter;
    private RMTristateSwitch    mUIMechanicsFilter;
    private RMTristateSwitch    mUIBodyFilter;
    private FloatingActionButton   mUIDoSearch;

    private OnActionListener    mActionListener;

    private OnClickListener mOnClickListenerForAction= new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mActionListener != null)
                mActionListener.onAction(getData());
        }
    };



    public TestSearchView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_test_search, this);
        mUILicensePlateFilter= findViewById(R.id.licensePlate);
        mUITiresFilter= findViewById(R.id.tiresTestState);
        mUIMechanicsFilter= findViewById(R.id.mechanicsTestState);
        mUIBodyFilter= findViewById(R.id.bodyTestState);
        mUIDoSearch= findViewById(R.id.doSearch);

        mUIDoSearch.setOnClickListener(mOnClickListenerForAction);
    }

    public Data getData(){
        Data data= new Data();
        data.licensePlate= mUILicensePlateFilter.getText().toString();
        return data;
    }

    public void setOnActionListener(OnActionListener listener){
        mActionListener= listener;
    }
}
