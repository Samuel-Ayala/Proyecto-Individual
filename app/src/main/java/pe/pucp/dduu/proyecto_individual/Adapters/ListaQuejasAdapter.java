package pe.pucp.dduu.proyecto_individual.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import pe.pucp.dduu.proyecto_individual.Entity.Queja;
import pe.pucp.dduu.proyecto_individual.R;

public class ListaQuejasAdapter extends RecyclerView.Adapter<ListaQuejasAdapter.QuejasViewHolder>{
    private final List<Queja> listaQuejas;
    private Context context;

    public ListaQuejasAdapter(List<Queja> listaQuejas, Context context) {
        this.listaQuejas = listaQuejas;
        this.context = context;
    }

    public static class QuejasViewHolder extends RecyclerView.ViewHolder{

        public TextView nombreEst, codigoEst, contenidoQueja;

        public QuejasViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nombreEst = itemView.findViewById(R.id.nombreEst);
            this.codigoEst = itemView.findViewById(R.id.codigoEst);
            this.contenidoQueja = itemView.findViewById(R.id.contenidoQueja);
        }
    }

    @NonNull
    @Override
    public QuejasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_queja,parent,false);
        return new QuejasViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull QuejasViewHolder holder, int position) {
        final Queja q = listaQuejas.get(position);
        holder.nombreEst.setText(q.getEstudiante());
        holder.codigoEst.setText(q.getCodigo());
        holder.contenidoQueja.setText(q.getContenido());
    }

    @Override
    public int getItemCount() {
        return listaQuejas.size();
    }
}
