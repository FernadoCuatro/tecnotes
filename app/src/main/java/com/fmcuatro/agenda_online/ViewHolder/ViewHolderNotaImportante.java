package com.fmcuatro.agenda_online.ViewHolder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fmcuatro.agenda_online.R;

public class ViewHolderNotaImportante extends RecyclerView.ViewHolder {
    // creamos nuestra propia vista
    View mView;

    private ViewHolderNotaImportante.ClickListener mClickListener;

    // creamos metodos al momento de interactuar
    public interface ClickListener {
        void onItemClick(View view, int posicion); // este metodo se va a ejecutar cuando presionenun item
        void onItemLongClick(View view, int posicion); // se ejecuta cuando se mantiene precionado por segundos el item
    }

    public void setOnclickListener(ViewHolderNotaImportante.ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public ViewHolderNotaImportante(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.onItemLongClick(view, getAdapterPosition());
                return false;
            }
        });
    }

    // metodo para setear la informacion desde la base de datos
    public void setearDatos(Context context, String idNota, String uidUsuario, String correoUsuario, String fechaHoraRegistro, String titulo, String descripcion, String fechaNota, String estado) {
        // identificadores de cada TextView
        TextView tvIdNota, tvUidUsuario, tvCorreoUsuario, tvFechaHoraRegistro, tvTitulo, tvDescripcion, tvFecha, tvEstado;
        ImageView ivTareaFinalizada, ivTareaNoFinalizada;

        // establecemos la conexion el item
        tvIdNota = mView.findViewById(R.id.tvIdNotaNi);
        tvUidUsuario = mView.findViewById(R.id.tvUidUsuarioNi);
        tvCorreoUsuario = mView.findViewById(R.id.tvCorreoUsuarioNi);
        tvFechaHoraRegistro = mView.findViewById(R.id.tvFechaHoraRegistroNi);
        tvTitulo = mView.findViewById(R.id.tvTituloNi);
        tvDescripcion = mView.findViewById(R.id.tvDescripcionNi);
        tvFecha = mView.findViewById(R.id.tvFechaNi);
        // para el manejo de los estados
        tvEstado = mView.findViewById(R.id.tvEstadoNi);
        ivTareaFinalizada = mView.findViewById(R.id.ivTareaFinalizadaNi);
        ivTareaNoFinalizada = mView.findViewById(R.id.ivTareaNoFinalizadaNi);

        // setiamos la informcion dentro del item
        tvIdNota.setText(idNota);
        tvUidUsuario.setText(uidUsuario);
        tvCorreoUsuario.setText(correoUsuario);
        tvFechaHoraRegistro.setText(fechaHoraRegistro);
        tvTitulo.setText(titulo);
        tvDescripcion.setText(descripcion);
        tvFecha.setText(fechaNota);
        tvEstado.setText(estado);

        // gestionamos el color del estado
        if (estado.equals("Finalizado")) {
            ivTareaFinalizada.setVisibility(View.VISIBLE);
        } else {
            ivTareaNoFinalizada.setVisibility(View.VISIBLE);
        }

    }
}
