package com.iceberg.watchrrev1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;
import java.util.regex.Pattern;

public class ForgotPassword extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("[a-zA-Z0-9]{1,}"+
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{1,}" +
                    "("+"\\."+"[a-zA-Z0-9][a-zA-Z0-9\\-]{2,}"+
                    ")+");

    Button btnRequestPassword;
    TextView tvEmailErrorFP;
    TextInputEditText tiEmailFP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        String url = this.getApplicationContext().getString(R.string.my_url);
        sharedPreferences = this.getSharedPreferences("com.iceberg.watchrrev1", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        tiEmailFP = findViewById(R.id.tiEmailFP);
        btnRequestPassword = findViewById(R.id.btnRequestNewPassword);
        tvEmailErrorFP = findViewById(R.id.tvEmailErrorFP);
        tvEmailErrorFP.setVisibility(View.INVISIBLE);

        btnRequestPassword.setOnClickListener(v->{
            String email;
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            Boolean isValidEmail = validateEmail();
            if(isValidEmail){
                email = Objects.requireNonNull(tiEmailFP.getText()).toString().trim();
                String built = url+"fp.php?email="+email;
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                Log.i("PUT_DATA",built);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, built,
                        response ->{
                            if(response.equals("Check email")){
                                sharedPreferences.edit().putString("email",email).apply();
                                Intent i = new Intent(ForgotPassword.this,VerificationCode.class);
                                startActivity(i);
                                finish();
                            }else{
                                Toast.makeText(ForgotPassword.this,"Error.  That didn't work\n"+response,Toast.LENGTH_LONG).show();
                                Log.i("PUT_DATA",response);
                            }
                        },error ->{
                    error.printStackTrace();
                    Toast.makeText(ForgotPassword.this,"That didn't work",Toast.LENGTH_LONG).show();
                    Log.i("PUT_DATA",error.toString());
                });
                requestQueue.add(stringRequest);
            }
        });
    }

    private Boolean validateEmail() {
        String emailInput = Objects.requireNonNull(tiEmailFP.getText()).toString().trim();
        if(emailInput.isEmpty()){
            tvEmailErrorFP.setText(R.string.email_required);
            tvEmailErrorFP.setVisibility(View.VISIBLE);
            return false;
        }else if(!EMAIL_PATTERN.matcher(emailInput).matches()){
            tvEmailErrorFP.setText(R.string.invalid_email);
            tvEmailErrorFP.setVisibility(View.VISIBLE);
            return false;
        }else{
            tvEmailErrorFP.setVisibility(View.INVISIBLE);
            tvEmailErrorFP.setError(null);
            return true;
        }
    }
}