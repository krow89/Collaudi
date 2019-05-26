package kr89.samplebor.collaudi.views;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.rm.rmswitch.RMAbstractSwitch;
import com.rm.rmswitch.RMTristateSwitch;
import kr89.samplebor.collaudi.R;
import kr89.samplebor.collaudi.models.TestRecordFilter;

import java.net.MalformedURLException;
import java.net.URL;


public class TestSearchView extends FrameLayout {

    public interface OnActionListener{
        void onAction(TestRecordFilter searchFilter);
    }

    private EditText                mUILicensePlateFilter;
    private RMTristateSwitch        mUITiresFilter;
    private RMTristateSwitch        mUIMechanicsFilter;
    private RMTristateSwitch        mUIBodyFilter;
    private RMTristateSwitch        mUIIsInsuranced;
    private FloatingActionButton    mUIDoSearch;
    private EditText                mUIDBUrl;

    private OnActionListener    mActionListener;

    private OnClickListener mOnClickListenerForAction= new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mActionListener != null){
                TestRecordFilter data= getData();
                if(data.dbServiceUrl != null && !data.dbServiceUrl.isEmpty()){
                    try {
                        URL u= new URL(data.dbServiceUrl);
                    } catch (MalformedURLException e) {
                        Toast.makeText(v.getContext(), "URL database non valido", Toast.LENGTH_LONG).show();
                        return;
                    }
                }else{
                    Toast.makeText(v.getContext(), "URL database non valido", Toast.LENGTH_LONG).show();
                    return;
                }
                mActionListener.onAction(data);
            }
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
        mUIIsInsuranced= findViewById(R.id.insuranceTestState);
        mUIDBUrl= findViewById(R.id.dbChoicer);

        mUIDoSearch.setOnClickListener(mOnClickListenerForAction);
    }

    public TestRecordFilter getData(){
        TestRecordFilter data= new TestRecordFilter();
        data.licensePlate= mUILicensePlateFilter.getText().toString();
        data.bodyTest= this._switchStateToFilter(mUIBodyFilter.getState());
        data.mechanicsTest= this._switchStateToFilter(mUIMechanicsFilter.getState());
        data.tiresTest= this._switchStateToFilter(mUITiresFilter.getState());
        data.isInsured= this._switchStateToFilter(mUIIsInsuranced.getState());
        data.dbServiceUrl= this.mUIDBUrl.getText().toString();
        return data;
    }

    private TestRecordFilter.Filter _switchStateToFilter(int state){
        switch (state){
            case RMAbstractSwitch.STATE_LEFT: return TestRecordFilter.Filter.ANY;
            case RMAbstractSwitch.STATE_MIDDLE: return TestRecordFilter.Filter.YES;
            case RMAbstractSwitch.STATE_RIGHT: return TestRecordFilter.Filter.NO;
        }
        return null;
    }


    public void setOnActionListener(OnActionListener listener){
        mActionListener= listener;
    }
}
