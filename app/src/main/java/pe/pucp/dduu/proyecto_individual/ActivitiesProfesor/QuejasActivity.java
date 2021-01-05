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

import pe.pucp.dduu.proyecto_individual.Adapters.ListaEstudiantesAdapter;
import pe.pucp.dduu.proyecto_individual.Adapters.ListaQuejasAdapter;
import pe.pucp.dduu.proyecto_individual.Entity.Estudiante;
import pe.pucp.dduu.proyecto_individual.Entity.Queja;
import pe.pucp.dduu.proyecto_individual.R;

public class QuejasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quejas);

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

                    //Obteniendo estudiantes con el respectivo identificador de grado y seccion
                    final List<Queja> listaQuejas = new ArrayList<>();
                    DatabaseReference quejasDatabase = FirebaseDatabase.getInstance().getReference();
                    Log.d("INFO APP GRADO SECC", gradoProfe[0] + seccionProfe[0]);
                    quejasDatabase.child("quejas sugerencias").child(gradoProfe[0] + seccionProfe[0]).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot data : snapshot.getChildren()) {

                                    Queja q = new Queja();

                                    String nombreEst = data.child("Estudiante").getValue().toString();
                                    String contenido = data.child("Contenido").getValue().toString();
                                    String codigo = data.child("Codigo").getValue().toString();

                                    q.setEstudiante(nombreEst);
                                    q.setCodigo(codigo);
                                    q.setContenido(contenido);

                                    listaQuejas.add(q);

                                }

                                //Poner los dispositivos en el recycler view
                                ListaQuejasAdapter listaQuejasAdapter = new ListaQuejasAdapter(listaQuejas, QuejasActivity.this);
                                listarEnRV(listaQuejasAdapter);

                            }else {
                                Toast.makeText(getApplicationContext(), "Aún no hay quejas registradas", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(), "Error inesperado", Toast.LENGTH_SHORT).show();
                        }
                    });
                    ////////////////////////////////////////////////////////////////////////////

                }else {
                    Toast.makeText(getApplicationContext(), "Error: la base de datos no responde", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //////////////////////////////////////



    }

    private void listarEnRV(ListaQuejasAdapter listaQuejasAdapter) {
        RecyclerView rv = findViewById(R.id.recyclerListaQuejas);
        rv.setAdapter(listaQuejasAdapter);
        rv.setLayoutManager(new LinearLayoutManager(QuejasActivity.this));
    }
}
