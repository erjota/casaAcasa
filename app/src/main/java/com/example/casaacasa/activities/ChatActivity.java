package com.example.casaacasa.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.casaacasa.R;
import com.example.casaacasa.modelo.ListAdaptorChat;
import com.example.casaacasa.modelo.ListAdaptorSolicitud;
import com.example.casaacasa.modelo.ListElement;
import com.example.casaacasa.modelo.Mensaje;
import com.example.casaacasa.modelo.Solicitud;
import com.example.casaacasa.modelo.Usuario;
import com.example.casaacasa.modelo.Vivienda;
import com.example.casaacasa.utils.Constantes;
import com.example.casaacasa.utils.Estado;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        inflater = LayoutInflater.from(ChatActivity.this);

        listadoDeConversaciones();
    }

    public void paginaSolicitudes(View v){
        Intent intent = new Intent(this, SolicitudActivity.class );
        startActivity(intent);
    }


    public void conversar(View v, Solicitud solicitud){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent=new Intent(ChatActivity.this, MensajeActivity.class);
               intent.putExtra("UsuarioContrario", solicitud.getEmisor());
               startActivity(intent);
            }
        });
    }

    public void eliminarChat(View v, Solicitud solicitud){
        ImageView bImagen = (ImageView) v.findViewById(R.id.iconEliminar);

        bImagen.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(ChatActivity.this);
                String mensaje = "¿Esta seguro que desea eliminar este mensaje?";
                dialog.setTitle(mensaje);
                View view = inflater.inflate(R.layout.popup_eliminar_chat, null);

                dialog.setView(view);
                dialog.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ChatActivity.this, "Has eliminado el chat", Toast.LENGTH_SHORT).show();
                        Constantes.db.child("Solicitud").child(solicitud.getUid()).child("estado").setValue(Estado.DENEGADA);
                        dialog.cancel();
                    }
                });
                dialog.setNeutralButton("CANCELAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int sumthin) {

                    }
                });
                dialog.show();
            }
        });
    }

    private void listadoDeConversaciones(){
        Query query = Constantes.db.child("Solicitud").orderByChild("receptor").equalTo("d5edaee4-9498-48c4-a4c4-baa3978adfeb"); //poner el id de la persona logeada
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.listaChats);
                linearLayout.removeAllViewsInLayout();
                linearLayout.removeAllViews();

                for(DataSnapshot s: snapshot.getChildren()){
                    Solicitud solicitud = s.getValue(Solicitud.class);
                    if(solicitud.getEstado().equals(Estado.ACEPTADA)){
                        rellenarSolicitud(solicitud, linearLayout);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void rellenarSolicitud(Solicitud solicitud, LinearLayout linearLayout) {
        View v = inflater.inflate(R.layout.usuario_mensaje, linearLayout, false);
        nombreUsuario(v, solicitud);
        recogerImagenYCiudad(v, solicitud);
        linearLayout.addView(v);
        conversar(v, solicitud);
        eliminarChat(v, solicitud);

    }

    private void recogerImagenYCiudad(View v, Solicitud solicitud) {
        ImageView imageView = v.findViewById(R.id.iconImagen);
        TextView poblacion= v.findViewById(R.id.nombrePoblacion);
        Constantes.db.child("Vivienda").orderByChild("user_id").equalTo(solicitud.getEmisor()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot v: snapshot.getChildren()){
                    Vivienda vi = v.getValue(Vivienda.class);

                    poblacion.setText(vi.getPoblacion());

                    StorageReference ruta = Constantes.storageRef.child(vi.getImagenes().get(0));
                    final long ONE_MEGABYTE = 1024 * 1024;
                    ruta.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {

                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imageView.setImageBitmap(bitmap);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void nombreUsuario(View v, Solicitud solicitud) {
        Query que = Constantes.db.child("Usuario").orderByChild("uid").equalTo(solicitud.getEmisor());
        que.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot us: snapshot.getChildren()){
                    Usuario u = us.getValue(Usuario.class);
                    TextView nombre = v.findViewById(R.id.nombreUsuario);
                    nombre.setText(u.getNombreUsuario());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
