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

public class VerificationCode extends AppCompatActivity {

    TextInputEditText tiVC;
    Button btnConfirmCode;
    TextView tvErrorVC;

    private static final Pattern CODE_PATTERN =
            Pattern.compile("[0-9]{6}");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);
        String url = this.getApplicationContext().getString(R.string.my_url);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.iceberg.watchrrev1", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email","");
        tiVC = findViewById(R.id.tiVC);
        btnConfirmCode = findViewById(R.id.btnVerifyCode);
        tvErrorVC = findViewById(R.id.tvErrorVC);
        tvErrorVC.setVisibility(View.INVISIBLE);

        btnConfirmCode.setOnClickListener(v->{
            String code = null;
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            boolean isValidCode = validateCode();
            if(isValidCode){
                code = Objects.requireNonNull(tiVC.getText()).toString().trim();
                String built = url+"vc.php?vc="+code+"&e="+email;
                Log.i("PUT_DATA",built);
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, built,
                        response ->{
                            String[] split = response.split("~~~");
                            response = split[0];
                            String user_id = split[1];
                            String user_name = split[2];
                            if(response.equals("Match")){
                                sharedPreferences.edit().putString("user_name",user_name).apply();
                                sharedPreferences.edit().putBoolean("isRegistered",true).apply();
                                sharedPreferences.edit().putInt("user_id",Integer.parseInt(user_id)).apply();
                                sharedPreferences.edit().putBoolean("isLoggedIn",true).apply();
                                sharedPreferences.edit().putString("email",email).apply();
                                Intent i = new Intent(VerificationCode.this,Home.class);
                                startActivity(i);
                                finish();
                            }else{
                                Toast.makeText(VerificationCode.this,response,Toast.LENGTH_LONG).show();
                            }
                        },error ->{
                    Toast.makeText(VerificationCode.this,"That didn't work",Toast.LENGTH_LONG).show();
                    Log.i("PUT_DATA",error.toString());
                });
                requestQueue.add(stringRequest);
            }else{
                Toast.makeText(VerificationCode.this,R.string.invalid_code,Toast.LENGTH_LONG).show();
            }
        });

    }

    private boolean validateCode() {
        String codeInput = Objects.requireNonNull(tiVC.getText()).toString().trim();
        if(codeInput.isEmpty()){
            tvErrorVC.setText(getString(R.string.code_required));
            tvErrorVC.setVisibility(View.VISIBLE);
            return false;
        }else if(!CODE_PATTERN.matcher(codeInput).matches()){
            tvErrorVC.setText(getString(R.string.invalid_code));
            tvErrorVC.setVisibility(View.VISIBLE);
            return false;
        }else{
            tvErrorVC.setVisibility(View.INVISIBLE);
            tiVC.setError(null);
            return true;
        }
    }
}