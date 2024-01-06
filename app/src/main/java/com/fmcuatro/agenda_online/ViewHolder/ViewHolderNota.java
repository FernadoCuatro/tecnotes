package com.fmcuatro.agenda_online.ViewHolder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fmcuatro.agenda_online.R;

public class ViewHolderNota extends RecyclerView.ViewHolder {
    // creamos nuestra propia vista
    View mView;

    private ViewHolderNota.ClickListener mClickListener;

    // creamos metodos al momento de interactuar
    public interface ClickListener {
        void onItemClick(View view, int posicion); // este metodo se va a ejecutar cuando presionenun item
        void onItemLongClick(View view, int posicion); // se ejecuta cuando se mantiene precionado por segundos el item
    }

    public void setOnclickListener(ViewHolderNota.ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public ViewHolderNota(@NonNull View itemView) {
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
        tvIdNota = mView.findViewById(R.id.tvIdNota);
        tvUidUsuario = mView.findViewById(R.id.tvUidUsuario);
        tvCorreoUsuario = mView.findViewById(R.id.tvCorreoUsuario);
        tvFechaHoraRegistro = mView.findViewById(R.id.tvFechaHoraRegistro);
        tvTitulo = mView.findViewById(R.id.tvTitulo);
        tvDescripcion = mView.findViewById(R.id.tvDescripcion);
        tvFecha = mView.findViewById(R.id.tvFecha);
        // para el manejo de los estados
        tvEstado = mView.findViewById(R.id.tvEstado);
        ivTareaFinalizada = mView.findViewById(R.id.ivTareaFinalizadaIn);
        ivTareaNoFinalizada = mView.findViewById(R.id.ivTareaNoFinalizadaIn);

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
