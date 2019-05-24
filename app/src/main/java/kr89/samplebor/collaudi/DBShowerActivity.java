package kr89.samplebor.collaudi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.adapter.AbstractTableAdapter;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import github.hotstu.sasuke.SasukeAdapter;
import kr89.samplebor.collaudi.models.TestRecord;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


class CellViewHolder extends AbstractViewHolder {
    public TextView textView;

    public CellViewHolder(View itemView, boolean isHeaderCell) {
        super(itemView);
        textView = itemView.findViewById(R.id.textView);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        if (isHeaderCell) {
            textView.setTypeface(null, Typeface.BOLD);
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
    private static List<String> colHeaderModel= Arrays.asList("Targa", "Test Meccanica", "Test Carrozzeria", "Test Pneumatici", "Assicurato ?");


    public TestRecordTableAdapter(Context context) {
        super(context);
    }

    public void setRecords(List<TestRecord> rec) {
        List<List<String>> ll = new ArrayList<List<String>>();
        for (TestRecord currRecord : rec) {
            List<String> cells = new ArrayList<>(5);
            cells.add(currRecord.licensePlate);
            cells.add(currRecord.bodyTestPassed ? "OK" : "No");
            cells.add(currRecord.mechanicsTestPassed ? "OK" : "No");
            cells.add(currRecord.tiresTestPassed ? "OK" : "No");
            cells.add(currRecord.isInsured ? "OK" : "No");
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
        ((CellViewHolder) holder).textView.setText((String) cellItemModel);
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
    }


}

public class DBShowerActivity extends AppCompatActivity {


    final public static String KEY_URL_EXTRA_NAME = "DB_URL";


    private TableView mRecordsView;
    private TestRecordTableAdapter mRecordTableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbshower);
        mRecordsView = findViewById(R.id.table);
        mRecordTableAdapter = new TestRecordTableAdapter(this);
        mRecordsView.setIgnoreSelectionColors(true);

        mRecordsView.setAdapter(mRecordTableAdapter);

        Intent intent = this.getIntent();
        final Context ctx = this;
        if (intent != null) {
            Bundle extraBundle = intent.getExtras();
            String url = extraBundle.getString(KEY_URL_EXTRA_NAME);
            if (url != null) {
                Volley.newRequestQueue(this).add(new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        TestRecord tmpRecord = new TestRecord();
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
                                    curr.bodyTestPassed = obj.getString("mechanicsTestResult").equals("1");
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
            }
        }
    }

    public static void startForFetchAndDisplay(){
        // TODO: add impl
    }
}
