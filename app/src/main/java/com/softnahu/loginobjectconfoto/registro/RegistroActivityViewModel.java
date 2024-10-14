package com.softnahu.loginobjectconfoto.registro;

import static android.app.Activity.RESULT_OK;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.softnahu.loginobjectconfoto.login.ApiClient;
import com.softnahu.loginobjectconfoto.login.LoginActivity;
import com.softnahu.loginobjectconfoto.modelo.Usuario;

public class RegistroActivityViewModel extends AndroidViewModel {
    private Context context;
    private MutableLiveData<Usuario> mUser;
    private MutableLiveData<Uri> mAvatar;
    private String avatar;

    public RegistroActivityViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }

    public LiveData<Usuario> getMUser(){
        if (mUser==null){
            mUser=new MutableLiveData<>();
        }
        return mUser;
    }
    public LiveData<Uri> getMAvatar(){
        if(mAvatar == null){
            mAvatar = new MutableLiveData<>();
        }
        return mAvatar;
    }

    public void guardar(Usuario usuario){
        if(usuario.getNombre().isEmpty() || usuario.getApellido().isEmpty() || usuario.getEmail().isEmpty() || usuario.getPassword().isEmpty()){
            Toast.makeText(context, "Debe completar todos los campos", Toast.LENGTH_SHORT).show();
        }else{
            usuario.setAvatar(avatar);
            if(ApiClient.guardar(context, usuario)){
                Toast.makeText(context, "Guardado correctamente", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(context, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }else{
            Toast.makeText(context, "No se pudo registrar/editar al usuario", Toast.LENGTH_SHORT).show();
            }

        }
    }
    public void leer(Intent intent){
        boolean result = intent.getBooleanExtra("login",false);
        Usuario usuario=ApiClient.leer(context);

        if(usuario==null||!result) {
            mUser.setValue(new Usuario());
        }
        else {
            mUser.setValue(usuario);
            avatar = usuario.getAvatar();
        }
    }

    public void recibirFoto(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            if (data != null) { // Verificar que data no sea nulo
                Uri uri = data.getData();
                if (uri != null) { // Verificar que uri no sea nulo
                    avatar = uri.toString();
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        context.getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }
                    mAvatar.setValue(uri); // Esto notificará a los observadores
                } else {
                    // Manejar el caso donde uri es nulo
                    Log.e("RegistroActivity", "No se pudo obtener el URI de la imagen.");
                }
            } else {
                // Manejar el caso donde data es nulo
                Log.e("RegistroActivity", "El Intent de datos es nulo.");
            }
        }
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

}