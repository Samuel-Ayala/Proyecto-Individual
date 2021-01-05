package pe.pucp.dduu.proyecto_individual.ActivitiesProfesor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pe.pucp.dduu.proyecto_individual.ActivitiesApoderado.TareasActivity;
import pe.pucp.dduu.proyecto_individual.Adapters.ListaEstudiantesAdapter;
import pe.pucp.dduu.proyecto_individual.Adapters.ListaTareasAdapter;
import pe.pucp.dduu.proyecto_individual.Entity.Estudiante;
import pe.pucp.dduu.proyecto_individual.R;

public class ListaEstudiantesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_estudiantes);

        //Obteniendo datos grado y seccion
        final String[] gradoProfe = new String[1];
        final String[] seccionProfe = new String[1];

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("usuarios");
        userDB.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    gradoProfe[0] = snapshot.child("grado").getValue().toString();
                    seccionProfe[0] = snapshot.child("seccion").getValue().toString();
                }else {
                    Toast.makeText(getApplicationContext(), "Error: la base de datos no responde", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //////////////////////////////////////

        //Obteniendo estudiantes con el respectivo identificador de grado y seccion
        final List<Estudiante> listaEstudiante = new ArrayList<>();
        Estudiante e2 = new Estudiante();
        e2.setNombreEstudiante("Estudiante - ");
        e2.setNombreApoderado("Apoderado - ");
        e2.setNumeroTelefonicoApoderado("Teléfono");
        listaEstudiante.add(e2);

        DatabaseReference estudianteDatabase = FirebaseDatabase.getInstance().getReference();
        estudianteDatabase.child("usuarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //Toast.makeText(getApplicationContext(), "se muestra la lista", Toast.LENGTH_SHORT).show();
                    for (DataSnapshot data : snapshot.getChildren()) {

                        Estudiante e = new Estudiante();

                        String nombreApoderado = data.child("nombre apoderado").getValue().toString();
                        String nombreEst = data.child("nombre estudiante").getValue().toString();
                        String telefono = data.child("celular").getValue().toString();
                        String gradoDB = data.child("grado").getValue().toString();
                        String seccionDB = data.child("seccion").getValue().toString();
                        String rol = data.child("rol").getValue().toString();
                        Log.d("INFOAPP",nombreEst);
                        Log.d("INFOAPP",nombreApoderado);

                        if (gradoDB.equals(gradoProfe[0]) && seccionDB.equals(seccionProfe[0]) && rol.equals("apoderado")){
                            e.setNombreApoderado(nombreApoderado);
                            e.setNombreEstudiante(nombreEst);
                            e.setNumeroTelefonicoApoderado(telefono);

                            listaEstudiante.add(e);
                        }
                    }

                    //Poner los dispositivos en el recycler view
                    ListaEstudiantesAdapter listaEstudiantesAdapter = new ListaEstudiantesAdapter(listaEstudiante, ListaEstudiantesActivity.this);
                    listarEnRV(listaEstudiantesAdapter);
                    
                }else {
                    Toast.makeText(getApplicationContext(), "Aún no hay estudiantes registrados", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error inesperado", Toast.LENGTH_SHORT).show();
            }
        });
        ////////////////////////////////////////////////////////////////////////////

    }

    private void listarEnRV(ListaEstudiantesAdapter listaEstudiantesAdapter) {
        RecyclerView rv = findViewById(R.id.recyclerListaEstudiante);
        rv.setAdapter(listaEstudiantesAdapter);
        rv.setLayoutManager(new LinearLayoutManager(ListaEstudiantesActivity.this));
    }
}