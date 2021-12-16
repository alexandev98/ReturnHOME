package com.returnhome.ui.activities.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.returnhome.R;
import com.returnhome.includes.Toolbar;
import com.returnhome.providers.ClientProvider;
import com.returnhome.providers.TokenProvider;
import com.returnhome.ui.activities.MainActivity;
import com.returnhome.utils.AppConfig;
import com.returnhome.models.RHResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectOptionProfileActivity extends AppCompatActivity {


    private LinearLayout mButtonDeleteProfile;
    private LinearLayout mButtonChangePassword;
    private LinearLayout mButtonGoToUpdateProfile;
    private LinearLayout mButtonLogOut;

    private AppConfig mAppConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option_profile);

        mButtonGoToUpdateProfile = findViewById(R.id.btnEditProfile);
        mButtonDeleteProfile = findViewById(R.id.btnDeleteProfile);
        mButtonChangePassword = findViewById(R.id.btnChangePassword);
        mButtonLogOut = findViewById(R.id.btnLogOut);

        mAppConfig = new AppConfig(this);
        Toolbar.show(this, "Seleccionar opción", true);

        mButtonGoToUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectOptionProfileActivity.this, UpdateProfileActivity.class);
                startActivity(intent);
            }
        });

        mButtonDeleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDelete();
            }
        });

        mButtonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectOptionProfileActivity.this, UpdatePasswordClientActivity.class);
                startActivity(intent);
            }
        });

        mButtonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialogLogOut();
            }
        });

    }

    private void clickDelete() {
        showAlertDialogDeleteAccount();
    }

    private void showAlertDialogDeleteAccount() {
        AlertDialog builder = new AlertDialog.Builder(this).create();
        builder.setTitle("ReturnHOME");
        builder.setMessage("Esta seguro que desea eliminar su cuenta?");
        builder.setButton(AlertDialog.BUTTON_POSITIVE, "SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TokenProvider.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            deleteAccount();
                        }
                        else{
                            Toast.makeText(SelectOptionProfileActivity.this,"No se pudo eliminar su cuenta", Toast.LENGTH_SHORT);
                        }
                    }
                });
            }
        });
        builder.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.cancel();
            }
        });

        builder.show();
    }

    private void showAlertDialogLogOut() {
        AlertDialog builder = new AlertDialog.Builder(this).create();
        builder.setTitle("ReturnHOME");
        builder.setMessage("Esta seguro que desea cerrar sesion?");
        builder.setButton(AlertDialog.BUTTON_POSITIVE, "SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TokenProvider.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Map<String, String> tokenInfo = new HashMap<>();
                            tokenInfo.put("idClient",String.valueOf(mAppConfig.getUserId()));
                            tokenInfo.put("token", "");
                            updateToken(tokenInfo);
                        }
                        else{
                            Toast.makeText(SelectOptionProfileActivity.this,"No se pudo cerrar sesion", Toast.LENGTH_SHORT);
                        }
                    }
                });
            }
        });
        builder.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.cancel();
            }
        });

        builder.show();
    }


    private void updateToken(Map<String, String> tokenInfo){

        ClientProvider.updateToken(tokenInfo).enqueue(new Callback<RHResponse>() {
            @Override
            public void onResponse(Call<RHResponse> call, Response<RHResponse> response) {
                if(response.isSuccessful()){
                   logout();
                }
                else{
                    Toast.makeText(SelectOptionProfileActivity.this, "No se pudo cerrar sesion", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RHResponse> call, Throwable t) {
                Toast.makeText(SelectOptionProfileActivity.this, "No se pudo cerrar sesion", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout(){
        mAppConfig.updateLoginStatus(false);
        mAppConfig.saveUserPhoneNumber(null);
        mAppConfig.saveUserName(null);
        mAppConfig.saveUserId(0);

        Intent intent = new Intent(SelectOptionProfileActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    private void deleteAccount(){

        ClientProvider.deleteAccount(mAppConfig.getUserId()).enqueue(new Callback<RHResponse>() {
            @Override
            public void onResponse(Call<RHResponse> call, Response<RHResponse> response) {
                if(response.isSuccessful()){
                    Toast.makeText(SelectOptionProfileActivity.this, "Cuenta eliminada", Toast.LENGTH_SHORT).show();
                    logout();
                }
            }

            @Override
            public void onFailure(Call<RHResponse> call, Throwable t) {
                Toast.makeText(SelectOptionProfileActivity.this, "No se pudo eliminar su cuenta", Toast.LENGTH_SHORT).show();
            }
        });
    }
}