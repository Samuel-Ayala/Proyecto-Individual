package pe.pucp.dduu.proyecto_individual.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import pe.pucp.dduu.proyecto_individual.Entity.Estudiante;
import pe.pucp.dduu.proyecto_individual.R;

public class ListaEstudiantesAdapter extends RecyclerView.Adapter<ListaEstudiantesAdapter.EstudiantesViewHolder>{

    private final List<Estudiante> listaEstudiantes;
    private Context context;

    public ListaEstudiantesAdapter(List<Estudiante> listaEstudiantes, Context context) {
        this.listaEstudiantes = listaEstudiantes;
        //Log.d("INFOADAPTER", listaEstudiantes.get(0).getNombreApoderado());
        this.context = context;
    }

    public static class EstudiantesViewHolder extends RecyclerView.ViewHolder{

        public TextView estudianteItem;

        public EstudiantesViewHolder(@NonNull View itemView) {
            super(itemView);
            this.estudianteItem = itemView.findViewById(R.id.estudianteItem);
        }
    }

    @NonNull
    @Override
    public EstudiantesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_info_estudiante,parent,false);
        return new EstudiantesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EstudiantesViewHolder holder, int position) {
        final Estudiante estudiante = listaEstudiantes.get(position);
        String data = estudiante.getNombreEstudiante() + " - " + estudiante.getNombreApoderado() + " - " + estudiante.getNumeroTelefonicoApoderado();
        Log.d("INFOAPP-ONbindVIEW", data);
        holder.estudianteItem.setText(data);
    }

    @Override
    public int getItemCount() {
        return listaEstudiantes.size();
    }
}
