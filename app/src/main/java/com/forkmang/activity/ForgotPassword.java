package com.forkmang.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.forkmang.R;
import com.forkmang.helper.Constant;
import com.forkmang.helper.StorePrefrence;
import com.forkmang.network_call.Api;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity {
    TextInputEditText etv_newpas, etvcnf_pass;
    Context ctx = ForgotPassword.this;
    StorePrefrence storePrefrence;
    private ProgressDialog dialog;
    Button btn_reset;
    String idToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        storePrefrence=new StorePrefrence(ctx);
        btn_reset = findViewById(R.id.btn_reset);
        etv_newpas = findViewById(R.id.etv_newpas);
        etvcnf_pass = findViewById(R.id.etvcnf_pass);

        //etv_newpas.setText("123454");
        //etvcnf_pass.setText("123454");



        btn_reset.setOnClickListener(v -> {
           /* final Intent mainIntent = new Intent(ForgotPassword.this, FaceLoginPermission.class);
            startActivity(mainIntent);
            finish();*/

            if(etv_newpas.getText().length() > 0)
            {
                if(etvcnf_pass.getText().length() > 0)
                {
                    if(etv_newpas.getText().toString().equals(etvcnf_pass.getText().toString()))
                    {
                        //call api
                        callapi_forgot_passwprd(etv_newpas.getText().toString(), etvcnf_pass.getText().toString());

                        //Toast.makeText(ctx, "success", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(ctx, Constant.PASSWORD_MATCH, Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(ctx, Constant.CNFPASSWORD_MATCH, Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(ctx, Constant.PASSWORD, Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void callapi_forgot_passwprd(String new_password, String cnf_password)
    {
        showProgress();
        String contact = storePrefrence.getString(Constant.MOBILE);
        //String contact = "9829020400";
        getToken(contact);

    }

    private void callApi_forgetpassword(String contact) {
        Api.getInfo().forgot_pass(contact, idToken).
                enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try{
                            JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                            Log.d("Result", jsonObject.toString());
                            if(jsonObject.getString("status").equalsIgnoreCase("Success"))
                            {
                                Toast.makeText(ctx,jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                                storePrefrence.setString(Constant.MOBILE, jsonObject.getJSONObject("data").getString("contact"));
                                //storePrefrence.setString(Constant.TOKEN_FORGOTPASS, jsonObject.getJSONObject("data").getString("token"));
                                stopProgress();

                                storePrefrence.setBoolean("keeplogin", false);
                                final Intent mainIntent = new Intent(ForgotPassword.this, LoginFormActivity.class);
                                startActivity(mainIntent);
                                finish();
                            }
                            else{
                                stopProgress();
                                Toast.makeText(ctx, "Error occur please try again", Toast.LENGTH_LONG).show();
                            }

                        }
                        catch (JSONException ex)
                        {
                            ex.printStackTrace();
                            stopProgress();
                            Toast.makeText(ctx, "Error occur please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(ctx, "Error occur please try again", Toast.LENGTH_LONG).show();
                        stopProgress();
                    }
                });

    }

    public void showProgress() {
        dialog = new ProgressDialog(ctx);
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void stopProgress(){
        dialog.dismiss();
    }

    private void getToken(String contact) {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            idToken = task.getResult().getToken();
                            Log.d("token", idToken);
                            callApi_forgetpassword(contact);
                            // Send token to your backend via HTTPS
                            // ...
                        } else {
                            // Handle error -> task.getException();
                        }
                    }
                });
    }

}