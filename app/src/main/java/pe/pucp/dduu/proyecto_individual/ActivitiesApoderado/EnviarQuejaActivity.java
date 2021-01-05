package pe.pucp.dduu.proyecto_individual.ActivitiesApoderado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;

import pe.pucp.dduu.proyecto_individual.R;

public class EnviarQuejaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviar_queja);

        Button btnEnviarQueja;
        final EditText textoQueja;
        final String[] codigo = new String[1];
        final String[] nombreEst = new String[1];
        final String[] grado = new String[1];
        final String[] seccion = new String[1];

        textoQueja = (EditText) findViewById(R.id.textoQueja);
        btnEnviarQueja = (Button) findViewById(R.id.buttonEnviarQueja);
        btnEnviarQueja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("quejas sugerencias");

                long date = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("MMM MM dd, yyyy h:mm a");
                final String dateString = sdf.format(date);

                DatabaseReference estudianteDatabase = FirebaseDatabase.getInstance().getReference();
                estudianteDatabase.child("usuarios").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        codigo[0] = snapshot.child("codigo").getValue().toString();
                        nombreEst[0] = snapshot.child("nombre estudiante").getValue().toString();
                        grado[0] = snapshot.child("grado").getValue().toString();
                        seccion[0] = snapshot.child("seccion").getValue().toString();

                        DatabaseReference currentUserDB = userDatabase.child(grado[0] + seccion[0]).child(user.getUid() + "-" + dateString);
                        currentUserDB.child("Estudiante").setValue(nombreEst[0]);
                        currentUserDB.child("Contenido").setValue(textoQueja.getText().toString());
                        currentUserDB.child("Codigo").setValue(codigo[0]);
                        textoQueja.setText("");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                Toast.makeText(getApplicationContext(), "Mensaje enviado exitosamente: el auxiliar del salón se comunicará con usted a la brevedad", Toast.LENGTH_SHORT).show();
                //Intent i = new Intent(getApplicationContext(), ApoderadoActivity.class);
                //startActivity(i);
            }
        });

    }
}