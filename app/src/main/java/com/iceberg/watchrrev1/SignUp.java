package com.iceberg.watchrrev1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("[a-zA-Z0-9]{1,}"+
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{1,}" +
                    "("+"\\."+"[a-zA-Z0-9][a-zA-Z0-9\\-]{2,}"+
                    ")+");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!?@#$%^&+=])(?=\\S+$).{8,}$");
    TextInputEditText tiName,tiEmail,tiPass,tiConfPass;
    Button btnSignUp;
    TextView tvHaveAccount,tvNameErrorSU,tvEmailErrorSU,tvPassErrorSU,tvConfPassErrorSU;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        String url = this.getApplicationContext().getString(R.string.my_url);

        // Find all the views
        tiName = findViewById(R.id.tiName);
        tiEmail = findViewById(R.id.tiEmail);
        tiPass = findViewById(R.id.tiPass);
        tiConfPass = findViewById(R.id.tiConfPass);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvHaveAccount = findViewById(R.id.haveAccount);
        tvNameErrorSU = findViewById(R.id.tvNameErrorSU);
        tvEmailErrorSU = findViewById(R.id.tvEmailErrorSU);
        tvPassErrorSU = findViewById(R.id.tvPassErrorSU);
        tvConfPassErrorSU = findViewById(R.id.tvConfPassErrorSU);

        // Set all errors to invisible
        tvPassErrorSU.setVisibility(View.INVISIBLE);
        tvConfPassErrorSU.setVisibility(View.INVISIBLE);
        tvNameErrorSU.setVisibility(View.INVISIBLE);
        tvEmailErrorSU.setVisibility(View.INVISIBLE);

        // Set submit button for clicking
        btnSignUp.setOnClickListener(view -> {

            String my_name,email,password;
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            if(Objects.requireNonNull(tiPass.getText()).toString().trim().equals(
                    Objects.requireNonNull(tiConfPass.getText()).toString().trim())){
                tvConfPassErrorSU.setVisibility(View.INVISIBLE);
                tvPassErrorSU.setVisibility(View.INVISIBLE);
                Boolean isValidEmail = validateEmail();
                Boolean isValidPassword = validPassword();
                Boolean isValidConfPass = validateConfPass();
                Boolean isValidName = validateName();
                if(isValidConfPass && isValidName && isValidPassword && isValidEmail){
                    my_name = Objects.requireNonNull(tiName.getText()).toString().trim();
                    email = Objects.requireNonNull(tiEmail.getText()).toString().trim();
                    password = Objects.requireNonNull(tiPass.getText()).toString().trim();
                    String built = url+"signup.php?name="+my_name+"&password="+password+"&email="+email;
                    Log.i("PUT_DATA",built);
                    RequestQueue requestQueue = Volley.newRequestQueue(this);
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, built,
                            response -> {
                                Toast.makeText(SignUp.this, response,Toast.LENGTH_LONG).show();
                                Log.i("PUT_DATA",response);

                            }, error -> {
                                Toast.makeText(SignUp.this,"That didn't work",Toast.LENGTH_LONG).show();
                                Log.i("PUT_DATA",error.toString());
                            });
                    requestQueue.add(stringRequest);
                }
            }else{
                tvConfPassErrorSU.setText(R.string.passwordsMustMatch);
                tvPassErrorSU.setText(R.string.passwordsMustMatch);
                tvConfPassErrorSU.setVisibility(View.VISIBLE);
                tvPassErrorSU.setVisibility(View.VISIBLE);
            }
        });
    }

    private Boolean validateConfPass(){
        String confPassInput = Objects.requireNonNull(tiConfPass.getText()).toString().trim();
        if(confPassInput.isEmpty()){
            tvConfPassErrorSU.setText(R.string.conf_pass_required);
            tvConfPassErrorSU.setVisibility(View.VISIBLE);
            return false;
        }else if(!PASSWORD_PATTERN.matcher(confPassInput).matches()){
            tvConfPassErrorSU.setText(R.string.invalid_pass);
            tvConfPassErrorSU.setVisibility(View.VISIBLE);
            return false;
        }else{
            tvConfPassErrorSU.setVisibility(View.INVISIBLE);
            tiConfPass.setError(null);
            return true;
        }
    }

    private Boolean validPassword() {
        String passInput = Objects.requireNonNull(tiPass.getText()).toString().trim();
        if(passInput.isEmpty()){
            tvPassErrorSU.setText(R.string.pass_required);
            tvPassErrorSU.setVisibility(View.VISIBLE);
            return false;
        }else if(!PASSWORD_PATTERN.matcher(passInput).matches()){
            tvPassErrorSU.setText(R.string.invalid_pass);
            tvPassErrorSU.setVisibility(View.VISIBLE);
            return false;
        }else{
            tvPassErrorSU.setVisibility(View.INVISIBLE);
            tiPass.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        String emailInput = Objects.requireNonNull(tiEmail.getText()).toString().trim();
        if(emailInput.isEmpty()){
            tvEmailErrorSU.setText(R.string.email_required);
            tvEmailErrorSU.setVisibility(View.VISIBLE);
            return false;
        }else if(!EMAIL_PATTERN.matcher(emailInput).matches()){
            tvEmailErrorSU.setText(R.string.invalid_email);
            tvEmailErrorSU.setVisibility(View.VISIBLE);
            return false;
        }else{
            tvEmailErrorSU.setVisibility(View.INVISIBLE);
            tiEmail.setError(null);
            return true;
        }
    }

    private Boolean validateName(){
        String nameInput = Objects.requireNonNull(tiName.getText()).toString().trim();
        if(nameInput.isEmpty()){
            tvNameErrorSU.setText(R.string.name_required);
            tvNameErrorSU.setVisibility(View.VISIBLE);
            return false;
        }else{
            tvNameErrorSU.setVisibility(View.INVISIBLE);
            tiName.setError(null);
            return true;
        }
    }
}