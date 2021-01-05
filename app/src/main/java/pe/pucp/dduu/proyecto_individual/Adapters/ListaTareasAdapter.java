package pe.pucp.dduu.proyecto_individual.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import pe.pucp.dduu.proyecto_individual.Entity.Tareas;
import pe.pucp.dduu.proyecto_individual.R;

public class ListaTareasAdapter extends RecyclerView.Adapter<ListaTareasAdapter.TareasViewHolder> {

    private final List<Tareas> listaTareas;
    private Context context;

    public ListaTareasAdapter(List<Tareas> listaTareas, Context context) {
        this.listaTareas = listaTareas;
        this.context = context;
    }

    public static class TareasViewHolder extends RecyclerView.ViewHolder{

        public TextView curso, contenido, fechaLimite, materiales;
        public ImageView imageTarea;

        public TareasViewHolder(@NonNull View itemView) {
            super(itemView);
            this.curso = itemView.findViewById(R.id.cursoTarea);
            this.contenido = itemView.findViewById(R.id.contenidoTarea);
            this.fechaLimite = itemView.findViewById(R.id.fechaEntregaMaxima);
            this.materiales = itemView.findViewById(R.id.materialesAUsar);
            this.imageTarea = itemView.findViewById(R.id.imgModelo);
        }
    }

    @NonNull
    @Override
    public TareasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_tarea,parent,false);
        return new TareasViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TareasViewHolder holder, int position) {
        final Tareas tareas = listaTareas.get(position);

        holder.curso.setText(tareas.getCursoTarea());
        holder.contenido.setText("Premisa: " + tareas.getContenido());
        holder.fechaLimite.setText("Fecha límite de entrega: " + tareas.getFechaLimite());
        holder.materiales.setText("Materiales: " + tareas.getMateriales());

        Glide.with(context).load(tareas.getUrlFoto()).into(holder.imageTarea);
    }

    @Override
    public int getItemCount() {
        return listaTareas.size();
    }
}
