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
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import pe.pucp.dduu.proyecto_individual.ActivitiesApoderado.TareasActivity;
import pe.pucp.dduu.proyecto_individual.Adapters.ListaEditTareasAdapter;
import pe.pucp.dduu.proyecto_individual.Adapters.ListaTareasAdapter;
import pe.pucp.dduu.proyecto_individual.Entity.Tareas;
import pe.pucp.dduu.proyecto_individual.R;

public class ListaEditTareasActivity extends AppCompatActivity {

    ArrayList<Tareas> tareas = new ArrayList<>();
    private ArrayList<StorageReference> imgRefs=new ArrayList<>();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final String[] grado = new String[1];
    final String[] seccion = new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_edit_tareas);

        //obtener grado y seccion para que se listen las tareas correctas

        DatabaseReference referenciaGradoSeccion = reference.child("usuarios").child(user.getUid());
        referenciaGradoSeccion.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    grado[0] = dataSnapshot.child("grado").getValue().toString();
                    seccion[0] = dataSnapshot.child("seccion").getValue().toString();

                    DatabaseReference referenciaTareas = reference.child("tareas").child(grado[0] + seccion[0]);
                    referenciaTareas.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    Tareas t = new Tareas();
                                    if (t != null) {
                                        String urlFoto = ds.child("foto").getValue().toString();
                                        String cursoYTitulo = ds.child("curso y titulo de tarea").getValue().toString();
                                        String premisa = ds.child("premisa").getValue().toString();
                                        String materiales = ds.child("materiales").getValue().toString();
                                        String fecha_limite = ds.child("fecha limite").getValue().toString();

                                        t.setContenido(premisa);
                                        t.setCursoTarea(cursoYTitulo);
                                        t.setFechaLimite(fecha_limite);
                                        t.setMateriales(materiales);
                                        t.setUrlFoto(urlFoto);

                                        tareas.add(t);
                                    }
                                }
                                //Poner los dispositivos en el recycler view
                                ListaEditTareasAdapter listaEditTareasAdapter = new ListaEditTareasAdapter(tareas, ListaEditTareasActivity.this);
                                listarEnRV(listaEditTareasAdapter);
                            }else {
                                Toast.makeText(getApplicationContext(), "No hay tareas asignadas aún", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }else {
                    Toast.makeText(getApplicationContext(), "Error: la base de datos no responde", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Para listar en el RV
    public void listarEnRV(ListaEditTareasAdapter listaTareasAdapter)
    {
        RecyclerView rv = findViewById(R.id.recyclerTareasProfe);
        rv.setAdapter(listaTareasAdapter);
        rv.setLayoutManager(new LinearLayoutManager(ListaEditTareasActivity.this));
    }
}