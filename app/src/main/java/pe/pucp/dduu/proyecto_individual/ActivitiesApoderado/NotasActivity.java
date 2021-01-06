package pe.pucp.dduu.proyecto_individual.ActivitiesApoderado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pe.pucp.dduu.proyecto_individual.R;

public class NotasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas);

        //Enviar consulta
        Button enviarConsulta;
        enviarConsulta = (Button) findViewById(R.id.enviarConsulta);
        enviarConsulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), EnviarQuejaActivity.class);
                startActivity(i);
            }
        });
        //////////////

        final TextView notaTarea1Mate,notaTarea2Mate,notaTarea3Mate,notaTarea1Comu,notaTarea2Comu,notaTarea3Comu, promedioMate, promedioComu;
        notaTarea1Mate = (TextView) findViewById(R.id.notaTarea1Mate);
        notaTarea2Mate = (TextView) findViewById(R.id.notaTarea2Mate);
        notaTarea3Mate = (TextView) findViewById(R.id.notaTarea3Mate);
        notaTarea1Comu = (TextView) findViewById(R.id.notaTarea1Comu);
        notaTarea2Comu = (TextView) findViewById(R.id.notaTarea2Comu);
        notaTarea3Comu = (TextView) findViewById(R.id.notaTarea3Comu);
        promedioMate = (TextView) findViewById(R.id.notaPromedioMate);
        promedioComu = (TextView) findViewById(R.id.notaPromedioComu);

        //Obteniendo datos grado, seccion y codigo
        final String[] gradoEst = new String[1];
        final String[] seccionEst = new String[1];
        final String[] codigo = new String[1];
        final String[] nombreEst = new String[1];

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("usuarios");
        userDB.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    gradoEst[0] = snapshot.child("grado").getValue().toString();
                    seccionEst[0] = snapshot.child("seccion").getValue().toString();
                    nombreEst[0] = snapshot.child("nombre estudiante").getValue().toString();
                    codigo[0] = snapshot.child("codigo").getValue().toString();


                    //Obteniendo notas
                    DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("notas");
                    userDB.child(gradoEst[0] + seccionEst[0]).child(nombreEst[0] + " - " +codigo[0]).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                try {
                                    notaTarea1Mate.setText(snapshot.child("Tarea 1 - Bisutería").getValue().toString());
                                }catch (Exception e){
                                    notaTarea1Mate.setText("Aún no calificado");
                                }
                                try {
                                    notaTarea2Mate.setText(snapshot.child("Tarea 2 - Bisutería").getValue().toString());
                                }catch (Exception e){
                                    notaTarea2Mate.setText("Aún no calificado");
                                }
                                try {
                                    notaTarea3Mate.setText(snapshot.child("Tarea 3 - Bisutería").getValue().toString());
                                }catch (Exception e){
                                    notaTarea3Mate.setText("Aún no calificado");
                                }
                                try {
                                    notaTarea1Comu.setText(snapshot.child("Tarea 1 - Escultura").getValue().toString());
                                }catch (Exception e){
                                    notaTarea1Comu.setText("Aún no calificado");
                                }
                                try {
                                    notaTarea2Comu.setText(snapshot.child("Tarea 2 - Escultura").getValue().toString());
                                }catch (Exception e){
                                    notaTarea2Comu.setText("Aún no calificado");
                                }
                                try {
                                    notaTarea3Comu.setText(snapshot.child("Tarea 3 - Escultura").getValue().toString());
                                }catch (Exception e){
                                    notaTarea3Comu.setText("Aún no calificado");
                                }

                                try {
                                    double promMate = (Integer.parseInt(snapshot.child("Tarea 1 - Bisutería").getValue().toString()) + Integer.parseInt(snapshot.child("Tarea 2 - Bisutería").getValue().toString()) + Integer.parseInt(snapshot.child("Tarea 3 - Bisutería").getValue().toString()))/3;
                                    promedioMate.setText(String.format("%.0f", promMate));
                                }catch (Exception e){
                                    promedioMate.setText("-");
                                }

                                try {
                                    double promComu = (Integer.parseInt(snapshot.child("Tarea 1 - Escultura").getValue().toString()) + Integer.parseInt(snapshot.child("Tarea 2 - Escultura").getValue().toString()) + Integer.parseInt(snapshot.child("Tarea 3 - Escultura").getValue().toString()))/3;
                                    promedioComu.setText(String.format("%.0f", promComu));
                                }catch (Exception e){
                                    promedioComu.setText("-");
                                }


                            }else {
                                Toast.makeText(getApplicationContext(), "Aún no tiene calificaciones por revisar", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                    //////////////////////////////////////


                }else {
                    Toast.makeText(getApplicationContext(), "Aún no tiene calificaciones por revisar", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //////////////////////////////////////

    }
}