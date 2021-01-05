package pe.pucp.dduu.proyecto_individual.ActivitiesProfesor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pe.pucp.dduu.proyecto_individual.ActivitiesApoderado.EnviarQuejaActivity;
import pe.pucp.dduu.proyecto_individual.ActivitiesApoderado.NotasActivity;
import pe.pucp.dduu.proyecto_individual.ActivitiesApoderado.TareasActivity;
import pe.pucp.dduu.proyecto_individual.R;

public class ProfesorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profesor);

        ///////////////////////Obtencion de datos de Database y guardado de datos/////////////////////////////////

        Bundle parametros = this.getIntent().getExtras();
        String email = parametros.getString("email");
        String rol = parametros.getString(("rol"));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            String uid = user.getUid();
            Log.d("uid",uid);
        }else {
            Log.d("Estado del usuario:","no logueado");

        }
        SharedPreferences.Editor pref = getSharedPreferences("Datos", Context.MODE_PRIVATE).edit();
        pref.putString("email",email);
        pref.putString("rol", rol);
        pref.apply();

        ////////////////////////////////////////////////////////////////////////////////////////////////////////

        ///////////////////////////////////////// Cerrar Sesion //////////////////////////////////////////////
        Button logout;
        logout = (Button) findViewById(R.id.cerrarSesionProfe);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /////////  Borrado de datos  ///////////////
                SharedPreferences.Editor pref = getSharedPreferences("Datos", Context.MODE_PRIVATE).edit();
                pref.clear();
                pref.apply();
                FirebaseAuth.getInstance().signOut();
                onBackPressed();
            }
        });
        //////////////////////////////////////////////////////////////////////////////////////////////////////

        Button notas,tareas, verListaEstudiantes;
        ImageView verQuejas;
        final TextView bienvenida;

        notas = (Button) findViewById(R.id.buttonGestionNotas);
        tareas = (Button) findViewById(R.id.buttonGestionTareas);
        verListaEstudiantes = (Button) findViewById(R.id.buttonListaEstudiantes);
        verQuejas = (ImageView) findViewById(R.id.verQuejas);
        bienvenida = (TextView) findViewById(R.id.bienvenidaProfesor);

        //bienvenida
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("usuarios");
        userDatabase.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String nombre = snapshot.child("nombre apoderado").getValue().toString();
                    bienvenida.setText("Bienvenido(a) auxiliar " + nombre);
                }else {
                    Toast.makeText(getApplicationContext(), "Error: la base de datos no responde", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: la base de datos no responde", Toast.LENGTH_SHORT).show();
            }
        });

        ///
        notas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), GestionNotasActivity.class);
                startActivity(i);
            }
        });
        ///
        tareas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CrearTareaActivity.class);
                startActivity(i);
            }
        });
        ///
        verQuejas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), QuejasActivity.class);
                startActivity(i);
            }
        });
        ///
        verListaEstudiantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ListaEstudiantesActivity.class);
                startActivity(i);
            }
        });

    }
}