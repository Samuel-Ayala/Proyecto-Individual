package pe.pucp.dduu.proyecto_individual.ActivitiesProfesor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import pe.pucp.dduu.proyecto_individual.R;

public class CrearTareaActivity extends AppCompatActivity {

    private Uri rutaDeArchivo;
    byte[] imbytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tareas_crear);

        final EditText curso, premisa, fechaLimite, materiales;
        final Button agregarTarea, cargarFoto, tomarFoto;
        ImageView imagenDispositivo;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("tareas");

        agregarTarea = (Button) findViewById(R.id.agregarDispositivoAInventario);
        cargarFoto = (Button) findViewById(R.id.cargarFoto);
        tomarFoto = (Button) findViewById(R.id.tomarFoto);
        curso = (EditText) findViewById(R.id.cursoTareaCrear);
        premisa = (EditText) findViewById(R.id.premisaTareaCrear);
        fechaLimite = (EditText) findViewById(R.id.fechaLimiteTareaCrear);
        materiales = (EditText) findViewById(R.id.materialesTareaCrear);
        //imagenDispositivo = (ImageView) findViewById(R.id.imagenDeDispositivoAAgregar);

        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM MM dd, yyyy h:mm a");
        final String dateString = sdf.format(date);
        final String[] grado = new String[1];
        final String[] seccion = new String[1];

        agregarTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog dialog = new ProgressDialog(CrearTareaActivity.this);
                dialog.setMessage("Creando tarea ...");
                dialog.setCancelable(false);
                dialog.show();

                if (!curso.getText().toString().isEmpty() && !fechaLimite.getText().toString().isEmpty()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("usuarios");
                    userDB.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                grado[0] = snapshot.child("grado").getValue().toString();
                                seccion[0] = snapshot.child("seccion").getValue().toString();
                            }else {
                                Toast.makeText(getApplicationContext(), "Error: la base de datos no responde", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    final String nombreCarpetaDispositivo = curso.getText().toString() + "-" + fechaLimite.getText().toString() ;

                    if (rutaDeArchivo != null){
                        StorageReference stReference = FirebaseStorage.getInstance().getReference();
                        final StorageReference fotoRef = stReference.child("fotos").child(nombreCarpetaDispositivo);
                        fotoRef.putFile(rutaDeArchivo).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()){
                                    throw new Exception();
                                }
                                return fotoRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                Uri downloadLink = task.getResult();
                                final DatabaseReference currentUserDB = userDatabase.child(grado[0] + seccion[0]).child(nombreCarpetaDispositivo);

                                currentUserDB.child("curso y titulo de tarea").setValue(curso.getText().toString());
                                if (!premisa.getText().toString().isEmpty()){
                                    currentUserDB.child("premisa").setValue(premisa.getText().toString());
                                }else{
                                    currentUserDB.child("premisa").setValue("aplicar lo visto en clase, los niños ya saben cómo hacerlo. Cualquier consulta adicional, me escriben");
                                }

                                if (!materiales.getText().toString().isEmpty()){
                                    currentUserDB.child("materiales").setValue(materiales.getText().toString());
                                }else {
                                    currentUserDB.child("materiales").setValue("Esta tarea no necesita el empleo de materiales");
                                }

                                currentUserDB.child("fecha limite").setValue(fechaLimite.getText().toString());
                                Log.d("FOTOOOO",downloadLink.toString());

                                if (!downloadLink.toString().isEmpty()){
                                    currentUserDB.child("foto").setValue(downloadLink.toString());
                                }else {
                                    currentUserDB.child("foto").setValue("https://firebasestorage.googleapis.com/v0/b/proyecto-individual---ap-c1043.appspot.com/o/fotos%2Fimagen_no-disponible.jpg?alt=media&token=c03cb9a4-cd3e-4fe6-a163-e9fdf31970e7");
                                }
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Tarea asignada a los alumnos", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else if (imbytes != null){
                        StorageReference stReference = FirebaseStorage.getInstance().getReference();
                        final StorageReference fotoRef = stReference.child("fotos").child(nombreCarpetaDispositivo);
                        fotoRef.putBytes(imbytes).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()){
                                    throw new Exception();
                                }
                                return fotoRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                Uri downloadLink = task.getResult();
                                final DatabaseReference currentUserDB = userDatabase.child(grado[0] + seccion[0]).child(nombreCarpetaDispositivo);
                                    currentUserDB.child("curso y titulo de tarea").setValue(curso.getText().toString());

                                    if (!premisa.getText().toString().isEmpty()){
                                        currentUserDB.child("premisa").setValue(premisa.getText().toString());
                                    }else{
                                        currentUserDB.child("premisa").setValue("aplicar lo visto en clase, los niños ya saben cómo hacerlo. Cualquier consulta adicional, me escriben");
                                    }

                                    if (!materiales.getText().toString().isEmpty()){
                                        currentUserDB.child("materiales").setValue(materiales.getText().toString());
                                    }else {
                                        currentUserDB.child("materiales").setValue("Esta tarea no necesita el empleo de materiales");
                                    }

                                    currentUserDB.child("fecha limite").setValue(fechaLimite.getText().toString());
                                Log.d("FOTOOOO",downloadLink.toString());

                                    if (!downloadLink.toString().isEmpty()){
                                        currentUserDB.child("foto").setValue(downloadLink.toString());
                                    }else {
                                        currentUserDB.child("foto").setValue("https://firebasestorage.googleapis.com/v0/b/proyecto-individual---ap-c1043.appspot.com/o/fotos%2Fimagen_no-disponible.jpg?alt=media&token=c03cb9a4-cd3e-4fe6-a163-e9fdf31970e7");
                                    }
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Tarea asignada a los alumnos", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Debe ingresar al menos el curso, el titulo correspondiente de la tarea a asignar, la fecha límite de entrega y una imagen", Toast.LENGTH_LONG).show();
                }

            }
        });

        cargarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i,"Seleccione una imagen"),1);
            }
        });

        tomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CrearTareaActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    int permiso = ContextCompat.checkSelfPermission(CrearTareaActivity.this, Manifest.permission.CAMERA);

                    if (permiso == PackageManager.PERMISSION_GRANTED) {
                        tomarFoto();
                    } else {
                        ActivityCompat.requestPermissions(CrearTareaActivity.this, new String[]{Manifest.permission.CAMERA},3);
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "Error: este dispositivo no tiene camara", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void tomarFoto() {
        Intent tomarFotex = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(tomarFotex,2);
        }catch (ActivityNotFoundException e){
            Toast.makeText(getApplicationContext(), "Error: no es posible tomar una foto", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && data != null && data.getData() != null){
            rutaDeArchivo = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),rutaDeArchivo);
                ImageView imagenModelo = (ImageView) findViewById(R.id.imagenModeloTareaCrear);
                imagenModelo.setImageBitmap(bitmap);
                imbytes=null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == 2){
            try {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                ImageView imagenModelo = (ImageView) findViewById(R.id.imagenModeloTareaCrear);
                imagenModelo.setImageBitmap(imageBitmap);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                assert imageBitmap != null;
                imageBitmap.compress(Bitmap.CompressFormat.PNG,0,bos);
                imbytes = bos.toByteArray();
                rutaDeArchivo = null;
                imagenModelo.setVisibility(View.VISIBLE);

            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Error: debe seleccionar una imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }
}