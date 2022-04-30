package com.example.casaacasa.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.casaacasa.R;
import com.example.casaacasa.modelo.Mensaje;
import com.example.casaacasa.modelo.Usuario;
import com.example.casaacasa.modelo.Vivienda;
import com.example.casaacasa.utils.Constantes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.TreeSet;
import java.util.zip.Inflater;

public class MensajeActivity extends AppCompatActivity {

    private Intent startIntent;
    private TreeSet<Mensaje> mensajes;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensajeria);

        mensajes=new TreeSet<Mensaje>();
        inflater=LayoutInflater.from(MensajeActivity.this);
        startIntent=getIntent();

        setContentView(R.layout.activity_mensajeria);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        rellenarDatosUsuario();
        obtenerMensajesUsuarioLogueado();

    }

    private void rellenarDatosUsuario() {
        rellenarNombre();
        rellenarImagen();
    }

    private void rellenarImagen() {
        ImageView imageView = findViewById(R.id.iconImagen);
        Constantes.db.child("Vivienda").orderByChild("user_id").equalTo(startIntent.getStringExtra("UsuarioContrario"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot v: snapshot.getChildren()){
                    Vivienda vi = v.getValue(Vivienda.class);

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

    private void rellenarNombre() {
        TextView nombreUsuarioContrario= (TextView) findViewById(R.id.nombreReceptor);
        Constantes.db.child("Usuario").child(startIntent.getStringExtra("UsuarioContrario"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Usuario usuario=snapshot.getValue(Usuario.class);
                        nombreUsuarioContrario.setText(usuario.getNombreUsuario());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void obtenerMensajesUsuarioLogueado(){
        //TODO Añadir un atributo que sea una combinatoria del id del emisor y elklk receptor, así solo cojeré los mensajes que de los dos y no sobrecargo la apluicacióin
        // tambien tendré que hacer dos búscaquedas diferentes, una receptor emisor y otra emisor receptor, para coger los deo tipos de mensaje
        Query q=Constantes.db.child("Mensaje").orderByChild("emisorYReceptor").equalTo("26a08f75-5967-434d-a283-a8b60e70135a "+startIntent.getStringExtra("UsuarioContrario"));
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Añadirlos a un arraylist
                // Buscar los mensajes en orden receptor emisor
                for(DataSnapshot mensaje: snapshot.getChildren()){
                    Mensaje m=mensaje.getValue(Mensaje.class);
                    mensajes.add(m);
                }
                obtenerMensajesUsuarioContrario();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void obtenerMensajesUsuarioContrario() {
        Query q=Constantes.db.child("Mensaje").orderByChild("emisorYReceptor").equalTo(startIntent.getStringExtra("UsuarioContrario")+" 26a08f75-5967-434d-a283-a8b60e70135a");
        q.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot mensaje: snapshot.getChildren()){
                    Mensaje m=mensaje.getValue(Mensaje.class);
                    mensajes.add(m);
                }
                anadirMensajesAlLayout();

                ScrollView scrollView =(ScrollView) findViewById(R.id.scrollView);
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void anadirMensajesAlLayout() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.listaMensajes);
        linearLayout.removeAllViewsInLayout();
        linearLayout.removeAllViews();

        for(Mensaje m: mensajes){
            View v;
            if(m.getEmisor().equals("26a08f75-5967-434d-a283-a8b60e70135a")) v = inflater.inflate(R.layout.card_view_mensajes_emisor, linearLayout, false);
            else v = inflater.inflate(R.layout.card_view_mensajes_receptor, linearLayout, false);
            rellenarMensaje(linearLayout,v, m);

        }

    }

    private void rellenarMensaje(LinearLayout linearLayout, View v, Mensaje m) {
        nombreUsuario(v, m);
        TextView mensaje=v.findViewById(R.id.mensajeMensaje);
        mensaje.setText(m.getMensaje());
        TextView hora=v.findViewById(R.id.horaMensaje);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        hora.setText(formatter.format(m.getFechaCreacion()));

        linearLayout.addView(v);
    }

    private void nombreUsuario(View v, Mensaje m) {
        Query que = Constantes.db.child("Usuario").orderByChild("uid").equalTo(m.getEmisor());
        que.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot us: snapshot.getChildren()){
                    Usuario u = us.getValue(Usuario.class);
                    TextView nombre = v.findViewById(R.id.nombreMensaje);
                    nombre.setText(u.getNombreUsuario());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void enviarMensaje(View view) {
        EditText contenidoMensaje= (EditText) findViewById(R.id.txtMensaje);
        if(contenidoMensaje.getText().equals("")){
            Toast.makeText(MensajeActivity.this,"Debes escribir un mensaje para enviar",Toast.LENGTH_SHORT);
        } else{
            Mensaje mensaje=new Mensaje(contenidoMensaje.getText().toString(), "26a08f75-5967-434d-a283-a8b60e70135a", startIntent.getStringExtra("UsuarioContrario"));
            Constantes.db.child("Mensaje").child(mensaje.getUid()).setValue(mensaje);
        }
    }

}
