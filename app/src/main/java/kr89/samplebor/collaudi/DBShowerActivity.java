package kr89.samplebor.collaudi;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

public class DBShowerActivity extends AppCompatActivity {

    final public static String KEY_URL_EXTRA_NAME= "DB_URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbshower);

        Intent intent= this.getIntent();
        final Context ctx= this;
        if(intent != null){
            Bundle extraBundle= intent.getExtras();
            String url= extraBundle.getString(KEY_URL_EXTRA_NAME);
            if(url == null){
                Volley.newRequestQueue(this).add(new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(ctx, "YAHAH", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ctx, "Error", Toast.LENGTH_LONG).show();
                    }
                }));
            }
        }
    }
}
