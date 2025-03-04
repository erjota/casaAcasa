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

public class SolicitudActivity extends AppCompatActivity {
    private LayoutInflater inflater;
    private String IDUsuarioLogueado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud);
        inflater = LayoutInflater.from(SolicitudActivity.this);

        IDUsuarioLogueado=Constantes.getIdUsuarioLogueado();

        solicitudesRecibidas();

        LinearLayout button=findViewById(R.id.paginaChat);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SolicitudActivity.this, ChatActivity.class );
                startActivity(intent);
            }
        });
    }

    public void verMensaje(View v, Solicitud solicitud){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(SolicitudActivity.this);
                String mensaje = solicitud.getMensaje();
                dialog.setTitle("Solicitud de intercambio");
                dialog.setMessage(mensaje);
                LayoutInflater layoutInflater = LayoutInflater.from(SolicitudActivity.this);
                View view = layoutInflater.inflate(R.layout.popup_mirar_solicitud, null);
                dialog.setView(view);

                dialog.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(SolicitudActivity.this, "Has aceptado la solicitud", Toast.LENGTH_SHORT).show();
                        Constantes.db.child("Solicitud").child(solicitud.getUid()).child("estado").setValue("ACEPTADA");
                        dialog.cancel();
                    }
                });

                dialog.setNegativeButton("RECHAZAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(SolicitudActivity.this, "Has rechazado la solicitud", Toast.LENGTH_SHORT).show();
                        Constantes.db.child("Solicitud").child(solicitud.getUid()).child("estado").setValue("DENEGADA");
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

    private void solicitudesRecibidas(){
        Query query = Constantes.db.child("Solicitud").orderByChild("receptor").equalTo(IDUsuarioLogueado);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.listaSolicitudes);
                linearLayout.removeAllViewsInLayout();
                linearLayout.removeAllViews();

                for(DataSnapshot s: snapshot.getChildren()){
                    Solicitud solicitud = s.getValue(Solicitud.class);
                    if(solicitud.getEstado().equals(Estado.PENDIENTE)){
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
        View v = inflater.inflate(R.layout.usuario_solicitud, linearLayout, false);
        nombreUsuario(v, solicitud);
        recogerImagenYCiudad(v, solicitud);

        linearLayout.addView(v);
        verMensaje(v, solicitud);

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

    public void irPerfil(View v){
        Intent intent=new Intent(SolicitudActivity.this, PerfilActivity.class);
        startActivity(intent);
    }

    public void irBusqueda (View v){
        Intent intent=new Intent(SolicitudActivity.this, BusquedaActivity.class);
        startActivity(intent);
    }

    public void irQuedadas (View v){
        AlertDialog.Builder dialog= new AlertDialog.Builder(SolicitudActivity.this);
        dialog.setTitle("Pagina en desarrollo.");
        dialog.setMessage("La página de chat grupal aun no está disponible");
        dialog.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.setNeutralButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int sumthin) {

            }
        });
        dialog.show();
    }

    public void irMap (View v){
        AlertDialog.Builder dialog= new AlertDialog.Builder(SolicitudActivity.this);
        dialog.setTitle("Pagina en desarrollo.");
        dialog.setMessage("La pagina puntos de interés aun no está disponible");
        dialog.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.setNeutralButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int sumthin) {

            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(SolicitudActivity.this, BusquedaActivity.class);
        startActivity(intent);
    }

}
