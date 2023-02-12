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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;
import java.util.regex.Pattern;

public class LogIn extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("[a-zA-Z0-9]{1,}"+
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{1,}" +
                    "("+"\\."+"[a-zA-Z0-9][a-zA-Z0-9\\-]{2,}"+
                    ")+");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!?@#$%^&+=])(?=\\S+$).{8,}$");

    TextView tvErrorEmail, tvErrorPass, tvNoAccount, tvForgotPassword;
    Button btnLogIn;
    TextInputEditText tiEmailLI,tiPassLI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        String url = this.getApplicationContext().getString(R.string.my_url);

        sharedPreferences = this.getSharedPreferences("com.iceberg.watchrrev1",Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        tvErrorEmail = findViewById(R.id.tvEmailErrorLI);
        tvErrorPass = findViewById(R.id.tvPassErrorLI);
        tvNoAccount = findViewById(R.id.dontHaveAccount);
        btnLogIn = findViewById(R.id.btnLogIn);
        tiEmailLI = findViewById(R.id.tiEmailLI);
        tiPassLI = findViewById(R.id.tiPassLI);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        tvErrorPass.setVisibility(View.INVISIBLE);
        tvErrorEmail.setVisibility(View.INVISIBLE);

        tvNoAccount.setOnClickListener(v -> {
            Intent i = new Intent(LogIn.this,SignUp.class);
            startActivity(i);
        });

        tvForgotPassword.setOnClickListener(view->{
            Intent i = new Intent(LogIn.this,ForgotPassword.class);
            startActivity(i);
        });

        btnLogIn.setOnClickListener(v ->{
            String email, password;
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            Boolean isValidEmail = validateEmail();
            Boolean isValidPassword = validPassword();

            if(isValidPassword && isValidEmail){
                email = Objects.requireNonNull(tiEmailLI.getText()).toString().trim();
                password = Objects.requireNonNull(tiPassLI.getText()).toString().trim();
                String built = url+"login.php?password="+password+"&email="+email;
                Log.i("PUT_DATA",built);
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, built,
                        response ->{
                            String user_id = null;
                            String user_name = "";
                            try{
                                String[] split = response.split("~~~");
                                Log.i("PUT_DATA",split[0]);
                                response = split[0];
                                user_id = split[1];
                                user_name = split[2];
                                Log.i("PUT_DATA",response);
                                Log.i("PUT_DATA",""+user_id);
                            }catch (Exception e) {
                                Log.i("PUT_DATA", e.toString());
                            }
                            if(response.equals("Success")){
                                sharedPreferences.edit().putBoolean("isRegistered",true).apply();
                                assert user_id != null;
                                sharedPreferences.edit().putInt("user_id",Integer.parseInt(user_id)).apply();
                                sharedPreferences.edit().putString("user_name",user_name).apply();
                                sharedPreferences.edit().putBoolean("isLoggedIn",true).apply();
                                sharedPreferences.edit().putString("email",email).apply();
                                Intent intent = new Intent(LogIn.this,Home.class);
                                startActivity(intent);
                                finish();
                            }else if(response.equals("Invalid email or password")){
                                Toast.makeText(LogIn.this,response,Toast.LENGTH_LONG).show();
                                Log.i("PUT_DATA",response);
                            }else{
                                Toast.makeText(LogIn.this,"Error.  That didn't work\n"+response,Toast.LENGTH_LONG).show();
                                Log.i("PUT_DATA",response);
                            }
                        },error ->{
                            Toast.makeText(LogIn.this,"That didn't work",Toast.LENGTH_LONG).show();
                            Log.i("PUT_DATA",error.toString());
                    });
                requestQueue.add(stringRequest);
            }
        });

    }

    private boolean validateEmail() {
        String emailInput = Objects.requireNonNull(tiEmailLI.getText()).toString().trim();
        if(emailInput.isEmpty()){
            tvErrorEmail.setText(R.string.email_required);
            tvErrorEmail.setVisibility(View.VISIBLE);
            return false;
        }else if(!EMAIL_PATTERN.matcher(emailInput).matches()){
            tvErrorEmail.setText(R.string.invalid_email);
            tvErrorEmail.setVisibility(View.VISIBLE);
            return false;
        }else{
            tvErrorEmail.setVisibility(View.INVISIBLE);
            tiEmailLI.setError(null);
            return true;
        }
    }

    private Boolean validPassword() {
        String passInput = Objects.requireNonNull(tiPassLI.getText()).toString().trim();
        if(passInput.isEmpty()){
            tvErrorPass.setText(R.string.pass_required);
            tvErrorPass.setVisibility(View.VISIBLE);
            return false;
        }else if(!PASSWORD_PATTERN.matcher(passInput).matches()){
            tvErrorPass.setText(R.string.invalid_pass);
            tvErrorPass.setVisibility(View.VISIBLE);
            return false;
        }else{
            tvErrorPass.setVisibility(View.INVISIBLE);
            tiPassLI.setError(null);
            return true;
        }
    }
}