package pe.pucp.dduu.proyecto_individual.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.List;

import pe.pucp.dduu.proyecto_individual.ActivitiesGeneral.LoginRegistroActivity;
import pe.pucp.dduu.proyecto_individual.ActivitiesProfesor.EditarTareaActivity;
import pe.pucp.dduu.proyecto_individual.ActivitiesProfesor.ProfesorActivity;
import pe.pucp.dduu.proyecto_individual.Entity.Tareas;
import pe.pucp.dduu.proyecto_individual.R;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class ListaEditTareasAdapter extends RecyclerView.Adapter<ListaEditTareasAdapter.TareasEditViewHolder> {
    private final List<Tareas> listaTareas;
    private Context context;

    public ListaEditTareasAdapter(List<Tareas> listaTareas, Context context) {
        this.listaTareas = listaTareas;
        this.context = context;
    }

    public static class TareasEditViewHolder extends RecyclerView.ViewHolder{

        public Button editarTarea, eliminarTarea;
        public TextView curso, contenido, fechaLimite, materiales;
        public ImageView imageTarea;

        public TareasEditViewHolder(@NonNull View itemView) {
            super(itemView);
            this.curso = itemView.findViewById(R.id.cursoTarea);
            this.contenido = itemView.findViewById(R.id.contenidoTarea);
            this.fechaLimite = itemView.findViewById(R.id.fechaEntregaMaxima);
            this.materiales = itemView.findViewById(R.id.materialesAUsar);
            this.imageTarea = itemView.findViewById(R.id.imgModelo);
            this.editarTarea = itemView.findViewById(R.id.editarTarea);
            this.eliminarTarea = itemView.findViewById(R.id.eliminarTarea);
        }
    }

    @NonNull
    @Override
    public TareasEditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_tarea_profe,parent,false);
        return new TareasEditViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TareasEditViewHolder holder, int position) {
        final Tareas tarea = (Tareas) listaTareas.get(position);

        holder.curso.setText(tarea.getCursoTarea());
        holder.contenido.setText("Premisa: " + tarea.getContenido());
        holder.fechaLimite.setText("Fecha límite de entrega: " + tarea.getFechaLimite());
        holder.materiales.setText("Materiales: " + tarea.getMateriales());
        Glide.with(context).load(tarea.getUrlFoto()).into(holder.imageTarea);

        holder.editarTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditarTareaActivity.class);
                Tareas t = new Tareas();
                t.setUrlFoto(tarea.getUrlFoto());
                t.setMateriales(tarea.getMateriales());
                t.setFechaLimite(tarea.getFechaLimite());
                t.setCursoTarea(tarea.getCursoTarea());
                t.setContenido(tarea.getContenido());

                intent.putExtra("Tarea", t);
                context.startActivity(intent);
            }
        });

        holder.eliminarTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] grado = new String[1];
                final String[] seccion = new String[1];
                final DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("tareas");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("usuarios");
                userDB.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            //Elimino referencia database
                            grado[0] = snapshot.child("grado").getValue().toString();
                            seccion[0] = snapshot.child("seccion").getValue().toString();
                            final String nombreCarpetaDispositivo = tarea.getCursoTarea() + "-" + tarea.getFechaLimite() ;
                            Log.d("INFO APPPPPPPPPPP", grado[0] + seccion[0] + nombreCarpetaDispositivo);
                            final DatabaseReference currentTareaDB = userDatabase.child(grado[0] + seccion[0]).child(nombreCarpetaDispositivo);

                            currentTareaDB.child(nombreCarpetaDispositivo).child("curso y titulo de tarea").removeValue();
                            currentTareaDB.child(nombreCarpetaDispositivo).child("fecha limite").removeValue();
                            currentTareaDB.child(nombreCarpetaDispositivo).child("foto").removeValue();
                            currentTareaDB.child(nombreCarpetaDispositivo).child("materiales").removeValue();
                            currentTareaDB.child(nombreCarpetaDispositivo).child("premisa").removeValue();

                            /*currentTareaDB.child(nombreCarpetaDispositivo).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("INFO APP","tarea eliminada");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("INFO APP","tarea no eliminada");
                                }
                            });

                             */

                            //Elimino referencia storage (foto)
                            /*
                            try{
                                StorageReference stReference = FirebaseStorage.getInstance().getReference().child("fotos");
                                stReference.child(nombreCarpetaDispositivo).delete();
                            }catch (Exception e){

                            }

                             */


                        }else {
                            //Toast.makeText(getApplicationContext(), "Error: la base de datos no responde", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });



            }
        });
    }

    @Override
    public int getItemCount() {
        return listaTareas.size();
    }
}
