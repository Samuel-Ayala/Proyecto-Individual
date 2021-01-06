package pe.pucp.dduu.proyecto_individual.ActivitiesProfesor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import pe.pucp.dduu.proyecto_individual.Adapters.ListaEstudiantesAdapter;
import pe.pucp.dduu.proyecto_individual.Entity.Estudiante;
import pe.pucp.dduu.proyecto_individual.R;

public class GestionNotasActivity extends AppCompatActivity {

    Button publicarNota;
    EditText notaDelEstudiante;
    Spinner nombreEstudiante, tareaAsignada;
    String codigoEst, gradoEst, seccionEst;
    final String[] gradoProfe = new String[1];
    final String[] seccionProfe = new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_notas);

        //Obteniendo datos grado y seccion
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

        //Obteniendo estudiantes
        final Spinner listaEstudiantes;
        listaEstudiantes = (Spinner) findViewById(R.id.spinnerEstudiantes);
        final ArrayList<String> listaEstudiantesArray = new ArrayList<String>();

        DatabaseReference estudianteDatabase = FirebaseDatabase.getInstance().getReference();
        estudianteDatabase.child("usuarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String nombreEst = data.child("nombre estudiante").getValue().toString();
                        gradoEst = data.child("grado").getValue().toString();
                        seccionEst = data.child("seccion").getValue().toString();
                        String rol = data.child("rol").getValue().toString();
                        codigoEst = data.child("codigo").getValue().toString();


                        if (gradoEst.equals(gradoProfe[0]) && seccionEst.equals(seccionProfe[0]) && rol.equals("apoderado")){
                            listaEstudiantesArray.add(nombreEst + " - " + codigoEst);
                        }else {
                            Log.d("INFOAPP","No hay estudiantes");
                        }

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //seleccionando del spinner
        listaEstudiantesArray.add("--- Seleccione el nombre del estudiante ---");

        ArrayAdapter < CharSequence > adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listaEstudiantesArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listaEstudiantes.setAdapter(adapter);
        listaEstudiantes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String data2 = parent.getSelectedItem().toString();
                String data = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /////////////////
        publicarNota = (Button) findViewById(R.id.publicarNota);
        publicarNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publicarNotaFuncion(gradoEst, seccionEst, codigoEst);
            }
        });

    }

    private void publicarNotaFuncion(String grado, String seccion, String codigoEst) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Registrando nota en el sistema ...");
        dialog.setCancelable(false);
        dialog.show();

        notaDelEstudiante = (EditText) findViewById(R.id.notaAsignada);
        nombreEstudiante = (Spinner) findViewById(R.id.spinnerEstudiantes);
        tareaAsignada = (Spinner) findViewById(R.id.spinnerTareas);
        DatabaseReference notasDatabase = FirebaseDatabase.getInstance().getReference().child("notas").child(gradoProfe[0] + seccionProfe[0]).child(nombreEstudiante.getSelectedItem().toString());

        try {
            if (Double.parseDouble(notaDelEstudiante.getText().toString()) <= 20 && Double.parseDouble(notaDelEstudiante.getText().toString()) >= 0){
                Double nota = Double.parseDouble(notaDelEstudiante.getText().toString());
                String notaDefinitiva = String.format("%.0f", nota);

                notasDatabase.child(tareaAsignada.getSelectedItem().toString()).setValue(Integer.parseInt(notaDefinitiva));
                dialog.dismiss();
                notaDelEstudiante.setText("");
                Toast.makeText(getApplicationContext(),"Nota publicada exitosamente", Toast.LENGTH_LONG).show();
            }else {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(),"Debe ingresar una nota entre 0 - 20 y sin decimales", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            dialog.dismiss();
            Toast.makeText(getApplicationContext(),"La nota es en formato numérico, no se permiten letras", Toast.LENGTH_LONG).show();
        }





    }
}