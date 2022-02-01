package sn.ept.social;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import sn.ept.social.Config.ApiEndpoint;
import sn.ept.social.Models.APIResponse;
import sn.ept.social.Models.Person;


public class AddPerson extends AppCompatActivity {

    private static int EDIT_MODE = 0;
    private static int ADD_MODE = 1;
    private static String CLEF="mamadou@gmail.com";
    int MODE = 1;

    Person editPerson;

    @BindView(R.id.etNom)
    TextInputEditText etNom;
    @BindView(R.id.etPrenom)
    TextInputEditText etPrenom;
    @BindView(R.id.etEmail)
    TextInputEditText etEmail;

    OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(new StethoInterceptor())
            .build();

    Gson gson = new Gson();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);
        ButterKnife.bind(this); //Bind ButterKnife

        if(getIntent().getParcelableExtra("person") != null){
            editPerson = (Person) getIntent().getParcelableExtra("person");
            MODE = EDIT_MODE;

            etNom.setText(editPerson.getNom());
            etPrenom.setText(editPerson.getPrenom());
            etEmail.setText(editPerson.getEmail());

        }else{
            MODE = ADD_MODE;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.save:
                savePerson();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void savePerson(){
        String nom=etNom.getText().toString();
        String prenom=etPrenom.getText().toString();
        String email=etEmail.getText().toString();

        if (StringUtils.isEmpty(nom)) return;
        if (StringUtils.isEmpty(prenom)) return;
        if (StringUtils.isEmpty(email)) return;

        String URL = "";

        if(MODE == ADD_MODE){
            editPerson =new Person();
            URL=ApiEndpoint.PERSONNES;
        }else if(MODE == EDIT_MODE) {
            URL = ApiEndpoint.PERSONNES +"/"+email;
        }

        editPerson.setClef(CLEF);
        editPerson.setNom(nom);
        editPerson.setPrenom(prenom);
        editPerson.setEmail(email);

        System.out.println("L'URL de la requete :"+URL);
        MediaType JSON=MediaType.parse("application/json");
        Request request = new Request.Builder()
                .url(URL)
                .put(RequestBody.create(JSON,gson.toJson(editPerson)))
                .build();

        //Handle response from the request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                AddPerson.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Main Activity", e.getMessage());
                        Toast.makeText(AddPerson.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                System.out.println(response);
                if (response.isSuccessful()) {
                    try {
                        //Finish activity
                        AddPerson.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                APIResponse res =  gson.fromJson(response.body().charStream(), APIResponse.class);
                                //Jika response success, finish activity
                                if(StringUtils.equals(res.getStatus(), "success")){
                                    Toast.makeText(AddPerson.this, "Person saved!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{

                                    Toast.makeText(AddPerson.this, "Error: "+res.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (JsonSyntaxException e) {
                        Log.e("MainActivity", "JSON Errors:"+e.getMessage());
                    } finally {
                        response.body().close();
                    }

                } else {
                    AddPerson.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddPerson.this, "Server error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
