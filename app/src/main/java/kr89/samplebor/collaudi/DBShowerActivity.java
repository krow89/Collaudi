package kr89.samplebor.collaudi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import github.hotstu.sasuke.SasukeAdapter;
import github.hotstu.sasuke.SasukeView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class DBShowerActivity extends AppCompatActivity {

    class TestRecord {
        public String licensePlate;
        public boolean mechanicsTestPassed;
        public boolean tiresTestPassed;
        public boolean bodyTestPassed;
        public boolean isInsured;

    }

    class RecordsAdapter extends SasukeAdapter{
        private List<TestRecord> mRecords;

        public RecordsAdapter(){
            mRecords= new ArrayList<>();
        }
        public void setRecords(List<TestRecord> records){
            mRecords.clear();
            mRecords.addAll(records);
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            public TextView labelView;
            public ViewHolder(TextView label){
                super(label);
                labelView= label;
            }
        }

        @Override
        public int getRowCount() {
            return mRecords.size();
        }

        @Override
        public int getColumnCount() {
            return 5;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int row, int column) {
            if(mRecords.size() < 1)
                return;
            TestRecord record= mRecords.get(row);

            ViewHolder vholder= (ViewHolder) holder;
            switch (column){
                case 0:
                    vholder.labelView.setText(record.licensePlate);
                    break;
                case 1:
                    vholder.labelView.setText(record.mechanicsTestPassed ? "OK":"Fallito");
                    break;
                case 2: vholder.labelView.setText(record.tiresTestPassed ? "OK":"Fallito"); break;
                case 3: vholder.labelView.setText(record.bodyTestPassed ? "OK":"Fallito");break;
                case 4: vholder.labelView.setText(record.isInsured ? "OK":"No"); break;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView tv = new TextView(parent.getContext());
            tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            return new ViewHolder(tv);
        }

        @Override
        public int getItemViewType(int row, int column) {
            return super.getItemViewType(row, column);
        }
    }

    final public static String KEY_URL_EXTRA_NAME= "DB_URL";


    private SasukeView mRecordView;
    private RecordsAdapter  mRecordsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbshower);
        mRecordView= findViewById(R.id.table);
        mRecordView.setStickColumnHead(true);
        mRecordView.setStickRowHead(true);
        mRecordsAdapter= new RecordsAdapter();
        mRecordView.setAdapter(mRecordsAdapter);

        Intent intent= this.getIntent();
        final Context ctx= this;
        if(intent != null){
            Bundle extraBundle= intent.getExtras();
            String url= extraBundle.getString(KEY_URL_EXTRA_NAME);
            if(url != null){
                Volley.newRequestQueue(this).add(new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Toast.makeText(ctx, "YAHAH", Toast.LENGTH_LONG).show();
                        TestRecord tmpRecord= new TestRecord();
                        if(response.has("data")){
                            try {
                                JSONArray dataJson= response.getJSONArray("data");
                                ArrayList<TestRecord> records= new ArrayList<>();
                                for(int i=0; i<dataJson.length(); ++i){
                                    TestRecord curr= new TestRecord();
                                    JSONObject obj= (JSONObject) dataJson.get(i);
                                    curr.licensePlate= obj.getString("licensePlate");
                                    curr.isInsured= obj.getString("insurance").equals("1") ? true : false;
                                    curr.mechanicsTestPassed= obj.getString("mechanicsTestResult").equals("1") ? true : false;
                                    curr.tiresTestPassed= obj.getString("tiresTestResult").equals("1") ? true : false;
                                    curr.bodyTestPassed= obj.getString("mechanicsTestResult").equals("1") ? true : false;
                                    records.add(curr);
                                }
                                mRecordsAdapter.setRecords(records);
                                mRecordView.setAdapter(mRecordsAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(ctx, "Errore: Risposta invalida dal servizio web", Toast.LENGTH_LONG);
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
}
