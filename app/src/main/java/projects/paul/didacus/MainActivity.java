package projects.paul.didacus;

import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import projects.paul.didacus.controller.AggiungiContattoActivity;
import projects.paul.didacus.controller.MyAdapter;
import projects.paul.didacus.database.ManagerDB;
import projects.paul.didacus.modello.Contact;


public class MainActivity extends AppCompatActivity {

    private EditText inputDialerTxt;
    private FloatingActionButton fabAddContact;
    private FloatingActionButton fabCall;
    private static final int REQUEST_CALL = 1;

    private ManagerDB managerdb;
    private List<Contact> listacontatti;

    private RecyclerView mRecyclerView;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ==== nuova istanza: costruisco il database === //
        managerdb = new ManagerDB(MainActivity.this);

        listacontatti = managerdb.getTuttiContatti();

        // ==== inizializza la variabile per la RecyclerView e sfrutta il LayoutManager (posiziona gli elementi del Recycler) === //
        mRecyclerView = findViewById(R.id.rv);
        mRecyclerView.setHasFixedSize(true);                                     /* se mantenere la posizione fissa o meno */
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // == richiama metodo per riempire il Recycler == //
        fillingRecycler();

        fabAddContact  = findViewById(R.id.fab_add_contact);
        fabCall  = findViewById(R.id.fab_call);
        inputDialerTxt = findViewById(R.id.inputDialerTxt);

        fabAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectAddContact();
            }
        });

        // == Se premo il pulsante per Chiamare == //
        fabCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!inputDialerTxt.getText().toString().trim().isEmpty()) {

                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel: " + inputDialerTxt.getText().toString().trim()));
                        MainActivity.this.startActivity(callIntent);
                        inputDialerTxt.getText().clear();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Non hai digitato nesssun numero!", Toast.LENGTH_SHORT).show();
                }
            }


        });

    }

    // ==== METODI AGGIUNTIVI ==== */

    // == Metodo che imposta il menu == //
    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.menu, menu );

        MenuItem search=menu.findItem(R.id.action_search);

        SearchView searchView=(SearchView)search.getActionView();
        searchView.setQueryHint("Ricerca un contatto");
        search(searchView);
        return super.onCreateOptionsMenu(menu);
    }

    // === Metodo per la Ricerca === //
    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText=newText.toLowerCase();
                ArrayList<Contact> nuovaLista=new ArrayList<>();
                listacontatti = managerdb.getTuttiContatti();

                for (Contact contactInfo : listacontatti){
                    String name=contactInfo.getName().toLowerCase();
                    String surname=contactInfo.getSurname().toLowerCase();
                    String phone=String.valueOf(contactInfo.getPhone()).toLowerCase();
                    if (name.contains(newText)||surname.contains(newText)||phone.contains(newText)){
                        nuovaLista.add(contactInfo);
                    }
                }
                adapter.setFilter(nuovaLista);

                return true;
            }
        });
    }

    // ==== Metodo che popola il Recycler View ==== //
    private void fillingRecycler(){
        adapter = new MyAdapter(this, managerdb.getTuttiContatti(), mRecyclerView);
        mRecyclerView.setAdapter(adapter);
    }

    // ==== Reindirizza all'activity per Aggiungere un contatto ==== //
    private void redirectAddContact(){
        Intent intent = new Intent(MainActivity.this, AggiungiContattoActivity.class);
        startActivity(intent);
    }
}