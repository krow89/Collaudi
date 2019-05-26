package kr89.samplebor.collaudi;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.adapter.AbstractTableAdapter;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import kr89.samplebor.collaudi.models.TestRecord;
import kr89.samplebor.collaudi.models.TestRecordFilter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

class TestFilterParcelable extends TestRecordFilter implements Parcelable{

    protected TestFilterParcelable(Parcel in) {
        this.licensePlate= in.readString();
        this.mechanicsTest= TestRecordFilter.Filter.valueOf(in.readString());
        this.bodyTest= TestRecordFilter.Filter.valueOf(in.readString());
        this.tiresTest= TestRecordFilter.Filter.valueOf(in.readString());
        this.isInsured= TestRecordFilter.Filter.valueOf(in.readString());
    }

    protected TestFilterParcelable(TestRecordFilter obj){
        this.licensePlate= obj.licensePlate;
        this.mechanicsTest= obj.mechanicsTest;
        this.bodyTest= obj.bodyTest;
        this.tiresTest= obj.tiresTest;
        this.isInsured= obj.isInsured;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.licensePlate);
        dest.writeString(this.mechanicsTest.name());
        dest.writeString(this.bodyTest.name());
        dest.writeString(this.tiresTest.name());
        dest.writeString(this.isInsured.name());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TestFilterParcelable> CREATOR = new Creator<TestFilterParcelable>() {
        @Override
        public TestFilterParcelable createFromParcel(Parcel in) {
            return new TestFilterParcelable(in);
        }

        @Override
        public TestFilterParcelable[] newArray(int size) {
            return new TestFilterParcelable[size];
        }
    };
}

class CellViewHolder extends AbstractViewHolder {
    public TextView textView;

    public CellViewHolder(View itemView, boolean isHeaderCell) {
        super(itemView);
        textView = itemView.findViewById(R.id.textView);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        if (isHeaderCell) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);
        }
    }
}

abstract class ColumnOnlyTableAdapter<VC, VR, CM> extends AbstractTableAdapter<VC, VR, CM> {

    public ColumnOnlyTableAdapter(Context context) {
        super(context);
    }

    @Override
    public int getColumnHeaderItemViewType(int position) {
        return 0;
    }

    @Override
    public int getRowHeaderItemViewType(int position) {
        return 0;
    }

    @Override
    public int getCellItemViewType(int position) {
        return 0;
    }

    @Override
    public AbstractViewHolder onCreateRowHeaderViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindRowHeaderViewHolder(AbstractViewHolder holder, Object rowHeaderItemModel, int rowPosition) {
    }

    @Override
    public View onCreateCornerView() {
        return null;

    }
}


class TestRecordTableAdapter extends ColumnOnlyTableAdapter<String, String, String> {
    private static List<String> colHeaderModel= Arrays.asList("Targa", "Test Meccanica", "Test Carrozzeria", "Test Pneumatici", "Assicurato ?", "Esito finale");


    public TestRecordTableAdapter(Context context) {
        super(context);
    }

    public void setRecords(List<TestRecord> rec) {
        List<List<String>> ll = new ArrayList<List<String>>();
        for (TestRecord currRecord : rec) {
            List<String> cells = new ArrayList<>(6);
            cells.add(currRecord.licensePlate);
            cells.add(currRecord.mechanicsTestPassed ? "OK" : "Non superato");
            cells.add(currRecord.bodyTestPassed ? "OK" : "Non superato");
            cells.add(currRecord.tiresTestPassed ? "OK" : "Non superato");
            cells.add(currRecord.isInsured ? "SI" : "NO");
            cells.add(currRecord.mechanicsTestPassed && currRecord.isInsured && currRecord.bodyTestPassed && currRecord.tiresTestPassed ? "OK":"Non superato");
            ll.add(cells);
        }
        setAllItems(colHeaderModel, null, ll);
    }

    private List<String> recordToList(TestRecord record){
        return null;
    }


    @Override
    public AbstractViewHolder onCreateCellViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_db_record_data, null);
        return new CellViewHolder(v, false);
    }

    @Override
    public void onBindCellViewHolder(AbstractViewHolder holder, Object cellItemModel, int columnPosition, int rowPosition) {
        CellViewHolder viewHolder= (CellViewHolder) holder;
        viewHolder.textView.setText((String) cellItemModel);
        if(columnPosition % 2 == 0)
            viewHolder.textView.setTypeface(null, Typeface.BOLD_ITALIC);
    }

    @Override
    public AbstractViewHolder onCreateColumnHeaderViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_db_record_data, null);
        return new CellViewHolder(v, true);
    }

    @Override
    public void onBindColumnHeaderViewHolder(AbstractViewHolder holder, Object columnHeaderItemModel, int columnPosition) {
        CellViewHolder cellViewHolder = (CellViewHolder) holder;
        cellViewHolder.textView.setText((String) columnHeaderItemModel);
        if(columnPosition % 2 == 0)
            cellViewHolder.textView.setTypeface(null, Typeface.BOLD_ITALIC);
    }
}

class CustomRequest extends StringRequest{
    private TestRecordFilter    mFilter;
    public CustomRequest(String url, TestRecordFilter filter, Response.Listener<String> listener, @Nullable Response.ErrorListener errorListener) {
        super(Method.POST, url, listener, errorListener);
        mFilter= filter;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> params= new HashMap<>();
        TestRecordFilter filter= mFilter;
        params.put("licensePlate", filter.licensePlate);
        if (filter.isInsured != TestRecordFilter.Filter.ANY) {
            params.put("insurance", filter.isInsured == TestRecordFilter.Filter.YES ? "yes" : "no");
        }
        if (filter.mechanicsTest != TestRecordFilter.Filter.ANY) {
            params.put("mechanics", filter.mechanicsTest == TestRecordFilter.Filter.YES ? "passed" : "failed");
        }
        if (filter.bodyTest != TestRecordFilter.Filter.ANY) {
            params.put("body", filter.bodyTest == TestRecordFilter.Filter.YES ? "passed" : "failed");
        }
        if (filter.tiresTest != TestRecordFilter.Filter.ANY) {
            params.put("tires", filter.tiresTest == TestRecordFilter.Filter.YES ? "passed" : "failed");
        }
        return params;
    }
}


public class DBShowerView extends FrameLayout {


    private TableView mRecordsView;
    private TestRecordTableAdapter mRecordTableAdapter;
    private ConstraintLayout mMessageView;
    private TextView    mMessage;
    private Button      mCancelRequest;
    private Request     mCurrRequest;
    private TestFilterParcelable      mLastFilter;


    private final String KEY_LAST_FILTER= "LAST_FILTER";

    public DBShowerView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_dbshower, this);
        mMessageView= findViewById(R.id.containerLoading);
        mMessage= mMessageView.findViewById(R.id.message);
        mRecordsView = findViewById(R.id.table);
        mRecordTableAdapter = new TestRecordTableAdapter(context);
        mCancelRequest= findViewById(R.id.stopBtn);
        mRecordsView.setIgnoreSelectionColors(true);
        mRecordsView.setAdapter(mRecordTableAdapter);
        mCancelRequest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrRequest != null)
                    mCurrRequest.cancel();
            }
        });
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable state= super.onSaveInstanceState();
        Bundle b= new Bundle();
        b.putParcelable(KEY_LAST_FILTER, mLastFilter);
        return b;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        if(state != null){
            Bundle b= (Bundle) state;
            TestFilterParcelable lastFilter= b.getParcelable(KEY_LAST_FILTER);
            if(lastFilter != null){
                fetchAndDisplay(lastFilter);
            }
        }
    }

    private void showLoadingView(String msg){
        mMessage.setText(msg);
        mRecordsView.setVisibility(GONE);
        mCancelRequest.setVisibility(VISIBLE);
        mMessageView.setVisibility(VISIBLE);
    }

    private void showLoadingView(boolean visible){
        mMessageView.setVisibility(visible ? VISIBLE : GONE);
        mCancelRequest.setVisibility(VISIBLE);
        mRecordsView.setVisibility(visible ? GONE : VISIBLE);
    }

    private void showMessage(String msg){
        mMessage.setText(msg);
        mRecordsView.setVisibility(GONE);
        mMessageView.setVisibility(VISIBLE);
        mCancelRequest.setVisibility(GONE);
    }

    public void fetchAndDisplay( TestRecordFilter filter){
        // TODO: add impl
        JSONObject postParams= new JSONObject();
        try {
            postParams.put("licensePlate", filter.licensePlate);
            if (filter.isInsured != TestRecordFilter.Filter.ANY) {
                postParams.put("insurance", filter.isInsured == TestRecordFilter.Filter.YES ? "yes" : "no");
            }
            if (filter.mechanicsTest != TestRecordFilter.Filter.ANY) {
                postParams.put("mechanics", filter.mechanicsTest == TestRecordFilter.Filter.YES ? "passed" : "failed");
            }
            if (filter.bodyTest != TestRecordFilter.Filter.ANY) {
                postParams.put("body", filter.bodyTest == TestRecordFilter.Filter.YES ? "passed" : "failed");
            }
            if (filter.tiresTest != TestRecordFilter.Filter.ANY) {
                postParams.put("tires", filter.tiresTest == TestRecordFilter.Filter.YES ? "passed" : "failed");
            }
        }catch (JSONException unhandled){
            unhandled.printStackTrace();
            showMessage("Nessun dato da mostrare");
        }
        final Context ctx= this.getContext();
        mCurrRequest= Volley.newRequestQueue(this.getContext()).add(new CustomRequest( filter.dbServiceUrl, filter, new Response.Listener<String>() {
            @Override
            public void onResponse(String data) {
                showLoadingView(false);
                TestRecord tmpRecord = new TestRecord();
                JSONObject response= null;
                try {
                    response = new JSONObject(data);
                } catch (JSONException e) {
                    // TODO: handle exception here
                    e.printStackTrace();
                    return;
                }
                if (response.has("data")) {
                    try {
                        JSONArray dataJson = response.getJSONArray("data");
                        ArrayList<TestRecord> records = new ArrayList<>();
                        for (int i = 0; i < dataJson.length(); ++i) {
                            TestRecord curr = new TestRecord();
                            JSONObject obj = (JSONObject) dataJson.get(i);
                            curr.licensePlate = obj.getString("licensePlate");
                            curr.isInsured = obj.getString("insurance").equals("1");
                            curr.mechanicsTestPassed = obj.getString("mechanicsTestResult").equals("1");
                            curr.tiresTestPassed = obj.getString("tiresTestResult").equals("1");
                            curr.bodyTestPassed = obj.getString("bodyTestResult").equals("1");
                            records.add(curr);
                        }
                        mRecordTableAdapter.setRecords(records);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ctx, "Errore: Risposta invalida dal servizio web", Toast.LENGTH_LONG).show();
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ctx, "Errore nella connessione al servizio web", Toast.LENGTH_LONG).show();
            }
        }));
        showLoadingView("Caricamento in corso");

    }


}
