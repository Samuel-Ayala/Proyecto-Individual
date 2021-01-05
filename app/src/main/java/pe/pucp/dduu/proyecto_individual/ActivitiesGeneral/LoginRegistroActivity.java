package pe.pucp.dduu.proyecto_individual.ActivitiesGeneral;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pe.pucp.dduu.proyecto_individual.ActivitiesApoderado.ApoderadoActivity;
import pe.pucp.dduu.proyecto_individual.ActivitiesProfesor.ProfesorActivity;
import pe.pucp.dduu.proyecto_individual.Entity.Usuario;
import pe.pucp.dduu.proyecto_individual.Fragments.LoginFragment;
import pe.pucp.dduu.proyecto_individual.R;

public class LoginRegistroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.contenedor_login_registro, new LoginFragment()).commit();

        sessionMantenerDatos();
    }

    ////////////////////////////////  INICIO DE SESION  //////////////////////////////////////////////

    public void iniciarSesion (final String email, String contra) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Ingresando ...");
        dialog.setCancelable(false);
        dialog.show();

        if (!email.equals("") && !contra.equals("")) {
            try {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, contra).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (task.isSuccessful()) {
                            if (user.isEmailVerified()) {
                                DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("usuarios");
                                DatabaseReference currentUserDB = userDatabase.child(user.getUid());
                                userDatabase.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            String rol = snapshot.child("rol").getValue().toString();

                                            if (rol.equals("apoderado")){
                                                dialog.dismiss();
                                                ingresoExitosoLoginUsuarioCliente(email,rol);
                                            }else if (rol.equals("profesor")){
                                                dialog.dismiss();
                                                ingresoExitosoLoginUsuarioTI(email,rol);
                                            }

                                        }else {
                                            dialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Error: la base de datos no responde", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        dialog.dismiss();
                                    }
                                });
                            }else {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Error: debe verificar su direccion de correo electrónico", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            dialog.dismiss();
                            mostrarError();
                        }
                    }
                });
            } catch (Error error) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error: surgio un problema con la autenticacion", Toast.LENGTH_SHORT).show();
            }
        } else {
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "Error: coloque su correo electronico y contrasenha", Toast.LENGTH_SHORT).show();
        }

    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void mostrarError(){
        Toast.makeText(getApplicationContext(),"Error: el usuario no se pudo autenticar correctamente",
                Toast.LENGTH_SHORT).show();
    }

    public void ingresoExitosoLoginUsuarioCliente(String inputEmail, String rol){
        Bundle params = new Bundle();
        params.putString("email",inputEmail);
        params.putString("rol",rol);
        Intent i = new Intent(getApplicationContext(), ApoderadoActivity.class);
        i.putExtras(params);
        startActivity(i);
        finish();
    }

    public void ingresoExitosoLoginUsuarioTI(String inputEmail, String rol){
        Bundle params = new Bundle();
        params.putString("email",inputEmail);
        params.putString("rol",rol);
        Intent i = new Intent(getApplicationContext(), ProfesorActivity.class);
        i.putExtras(params);
        startActivity(i);
        finish();
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////  REGISTRO  //////////////////////////////////////////////////

    public void registro (final Usuario usuario, String pwd){
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Registrando datos en el sistema ...");
        dialog.setCancelable(false);
        dialog.show();

        if (!usuario.getCorreo().equals("") && !pwd.equals("")){
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(usuario.getCorreo(), pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        dialog.dismiss();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("usuarios");
                        DatabaseReference currentUserDB = userDatabase.child(user.getUid());
                        currentUserDB.child("nombre apoderado").setValue(usuario.getNombre());
                        currentUserDB.child("codigo").setValue(usuario.getCodigo());
                        currentUserDB.child("rol").setValue(usuario.getRol());
                        currentUserDB.child("nombre estudiante").setValue(usuario.getNombreEstudiante());
                        currentUserDB.child("grado").setValue(usuario.getGradoEstudiante());
                        currentUserDB.child("seccion").setValue(usuario.getSeccionEstudiante());
                        currentUserDB.child("dni").setValue(usuario.getDni());
                        currentUserDB.child("celular").setValue(usuario.getNumeroCelular());

                        user.sendEmailVerification();
                        finish();
                        startActivity(getIntent());
                        Toast.makeText(getApplicationContext(),"Verifique su direccion de correo electrónico en el enlace enviado a " + usuario.getCorreo(), Toast.LENGTH_LONG).show();

                    }else {
                        dialog.dismiss();
                        mostrarError();
                    }
                }
            });
        }else {
            dialog.dismiss();
            Toast.makeText(getApplicationContext(),"Error: debe colocar sus datos", Toast.LENGTH_SHORT).show();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////// MANTENER DATOS AUN CUANDO EL USUARIO SALGA DE LA APLICACION (NO CIERRA SESION) ////////////////////

    public void sessionMantenerDatos(){
        SharedPreferences pref = getSharedPreferences("Datos", Context.MODE_PRIVATE);
        final String email = pref.getString("email",null);
        String rol = pref.getString("rol",null);

        if (email != null && rol != null){
            if (rol.equals("apoderado")){
                ingresoExitosoLoginUsuarioCliente(email,rol);
            }else if (rol.equals("profesor")){
                ingresoExitosoLoginUsuarioTI(email,rol);
            }

        }else {
            //Toast.makeText(getApplicationContext(),"Ingrese sus credenciales una vez haya verificado su dirección de correo electrónico a través del enlace enviado a " + email, Toast.LENGTH_LONG).show();
        }
    }

    public void errorDeRegistro() {
        Toast.makeText(getApplicationContext(),"Debe llenar todos los campos", Toast.LENGTH_LONG).show();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}