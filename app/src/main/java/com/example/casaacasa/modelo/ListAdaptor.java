package com.example.casaacasa.modelo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.casaacasa.R;

import java.util.List;

public class ListAdaptor extends RecyclerView.Adapter<ListAdaptor.ViewHolder> {
    private List<ListElement> mData;
    private LayoutInflater mInflater;
    private Context context;

    public ListAdaptor(List<ListElement> itemList, Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = itemList;
    }

    @Override
    public int getItemCount() {return mData.size();}

    @Override
    public ListAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = mInflater.inflate(R.layout.usuariomensaje, null);
        return new ListAdaptor.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListAdaptor.ViewHolder holder, final int position){
        holder.binData(mData.get(position));
    }

    public void setItems(List<ListElement> items){ mData = items;}

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iconImage;
        TextView name, ciudad;

        ViewHolder(View itemView){
            super(itemView);
            iconImage = itemView.findViewById(R.id.iconImagen);
            name = itemView.findViewById(R.id.nombreUsuario);
            ciudad = itemView.findViewById(R.id.nombreCiudad);
        }

        void binData(final ListElement item) {
            iconImage.setColorFilter(Color.parseColor(item.getColor()), PorterDuff.Mode.SRC_IN);
            name.setText(item.getName());
            ciudad.setText(item.getCiudad());
        }

    }
}
