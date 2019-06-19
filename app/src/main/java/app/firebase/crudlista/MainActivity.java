package app.firebase.crudlista;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private List<Articulo> listaArticulos=new ArrayList<Articulo>();
    ArrayAdapter<Articulo> arrayAdapterArticulo;
    EditText nombre, precio, descripcion;
    ListView lv_articulos;
    FirebaseDatabase database;
    DatabaseReference myReferencia;
    Articulo articuloSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nombre=findViewById(R.id.editTextNombre);
        precio=findViewById(R.id.editTextPrecio);
        descripcion=findViewById(R.id.editTextDesc);
        lv_articulos=findViewById(R.id.lv_Lista);
        inicioFirebase();
        listaDatos();
        lv_articulos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                articuloSelect= (Articulo) parent.getItemAtPosition(position);
                nombre.setText(articuloSelect.getNombre());
                precio.setText(articuloSelect.getPrecio());
                descripcion.setText(articuloSelect.getDescripcion());
            }
        });


    }

    private void listaDatos() {
        myReferencia.child("Articulo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaArticulos.clear();
                for(DataSnapshot objSnap : dataSnapshot.getChildren()){
                    Articulo articulo=objSnap.getValue(Articulo.class);
                    listaArticulos.add(articulo);
                    arrayAdapterArticulo=new ArrayAdapter<Articulo>(MainActivity.this, android.R.layout.simple_list_item_1,listaArticulos);
                    lv_articulos.setAdapter(arrayAdapterArticulo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicioFirebase() {
        FirebaseApp.initializeApp(this);
        database= FirebaseDatabase.getInstance();
        myReferencia= database.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String nomp=nombre.getText().toString();
        String precioP=precio.getText().toString();
        String des=descripcion.getText().toString();

        switch(item.getItemId()){

            case
                    R.id.icon_add: {
                if (nomp.equals("")){
                    validacion();
                }else {

                    Articulo articulo=new Articulo();
                    articulo.setUid(UUID.randomUUID().toString());
                    articulo.setNombre(nomp);
                    articulo.setPrecio(precioP);
                    articulo.setDescripcion(des);
                    myReferencia.child("Articulo").child(articulo.getUid()).setValue(articulo);
                    Toast.makeText(this, "Agregar", Toast.LENGTH_SHORT).show();
                    limpiarCajas();
                }
                break;
            }
            case
                R.id.icon_save: {
                Articulo a=new Articulo();
                a.setUid(articuloSelect.getUid());
                a.setNombre(nombre.getText().toString().trim());
                a.setPrecio(precio.getText().toString().trim());
                a.setDescripcion(descripcion.getText().toString().trim());
                myReferencia.child("Articulo").child(a.getUid()).setValue(a);
                Toast.makeText(this, "Actualizado", Toast.LENGTH_SHORT).show();
                limpiarCajas();
                break;
            }
            case
                R.id.icon_delete: {
                Articulo a=new Articulo();
                a.setUid(articuloSelect.getUid());
                myReferencia.child("Articulo").child(a.getUid()).removeValue();
                Toast.makeText(this, "Eliminar", Toast.LENGTH_SHORT).show();
                limpiarCajas();
                break;
            }
            default:break;
        }
        return true;
    }

    private void limpiarCajas() {
        nombre.setText("");
        precio.setText("");
        descripcion.setText("");
    }

    private void validacion() {
        String nomp=nombre.getText().toString();
        String preciop=precio.getText().toString();
        String des=descripcion.getText().toString();
        if(nomp.equals("")){
            nombre.setError("Esto es requerido");
        }
        if(preciop.equals("")){
            precio.setError("Esto es requerido");
        }
        if(des.equals("")){
            descripcion.setError("Esto es requerido");
        }
    }
}
