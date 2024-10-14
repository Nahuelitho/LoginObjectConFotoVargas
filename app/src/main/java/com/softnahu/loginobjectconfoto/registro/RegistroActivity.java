package com.softnahu.loginobjectconfoto.registro;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.softnahu.loginobjectconfoto.modelo.Usuario;
import com.softnahu.loginobjectstreamvargas.R;
import com.softnahu.loginobjectstreamvargas.databinding.ActivityRegistroBinding;

public class RegistroActivity extends AppCompatActivity {
    private ActivityRegistroBinding binding;
    private RegistroActivityViewModel vm;
    private Intent intent;
    private ActivityResultLauncher<Intent> arl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding= ActivityRegistroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        vm= ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(RegistroActivityViewModel.class);

        abrirGaleria();
        binding.btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long dni = Long.parseLong(binding.etDni.getText().toString());
                String apellido = binding.etApellido.getText().toString();
                String nombre = binding.etNombre.getText().toString();
                String mail = binding.etCorreo.getText().toString();
                String pass = binding.etPass.getText().toString();

                Usuario user = new Usuario(dni,apellido,nombre,mail,pass);
                vm.guardar(user);
                binding.etDni.setText("");
                binding.etApellido.setText("");
                binding.etNombre.setText("");
                binding.etCorreo.setText("");
                binding.etPass.setText("");
            }
        });
        vm.getMUser().observe(this, new Observer<Usuario>() {
            @Override
            public void onChanged(Usuario usuario) {
                binding.etDni.setText(usuario.getDni()+"");
                binding.etApellido.setText(usuario.getApellido());
                binding.etNombre.setText(usuario.getNombre());
                binding.etCorreo.setText(usuario.getEmail());
                binding.etPass.setText(usuario.getPassword());
                String avatarString = usuario.getAvatar();
                if (avatarString != null && !avatarString.isEmpty()) {
                    vm.setAvatar(avatarString);
                    binding.ivFoto.setImageURI(Uri.parse(avatarString));
                } else {
                    // Manejar el caso donde avatarString es nulo o vacío
                    Log.e("RegistroActivity", "El avatarString es nulo o vacío.");
                    binding.ivFoto.setImageResource(R.drawable.defaultprofile); // Usar una imagen predeterminada
                }

            }
        });
        vm.getMAvatar().observe(this, new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                if (uri != null) { // Verificar que uri no sea nulo
                    binding.ivFoto.setImageURI(uri);
                } else {
                    // Manejar el caso donde uri es nulo
                    Log.e("RegistroActivity", "El URI del avatar es nulo.");
                }
            }
        });

        vm.leer(getIntent());

        binding.btCargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arl.launch(intent);
            }
        });
    }

    private void abrirGaleria() {
        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        arl = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                vm.recibirFoto(result);
            }
        });
    }
}