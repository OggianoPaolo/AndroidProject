package projects.paul.didacus.controller;

/*

    Classe che si occupa di controllare Focus Contatto xml

 */

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import projects.paul.didacus.database.ManagerDB;

import projects.paul.didacus.modello.Contact;
import projects.paul.didacus.R;

public class FocusContattoActivity extends AppCompatActivity {

    private static final String TAG = "Cfafs";
    private ImageView imgView;
    private TextView nomeTxt;
    private TextView cognomeTxt;
    private TextView numeroTxt;
    private TextView indirizzoTxt;

    /* eventuali nuovi campi */
    private TextView compagniaTxt;
    private TextView mailTxt;
    private TextView lavoroTxt;

    private ManagerDB managerdb;
    private long contattoIdOttenuto;
    private static final int REQUEST_CALL = 1;

    static Map<String, Integer> mappaNuoviCampi = new HashMap<>();

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_contatto);

        try {
            // == tramite Intent ottengo l'id del contatto == //
            contattoIdOttenuto = getIntent().getLongExtra("contatto_id", 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        managerdb = new ManagerDB(this);

        impostaDatiContatto(contattoIdOttenuto); // uso il manager e id contatto

        final Contact contattoRichiesto = managerdb.getContatto(contattoIdOttenuto);

        LinearLayout campoIndirizzo = findViewById(R.id.campoIndirizzo);
        LinearLayout campoNumero = findViewById(R.id.campoNumero);


        // ====== Se viene premuto l'Indirizzo ====== //

        campoIndirizzo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!contattoRichiesto.getAddress().equals("")) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(FocusContattoActivity.this);
                    builder.setTitle("Scegli l'opzione");
                    builder.setItems(new CharSequence[]{"Apri nella mappa", "Annulla"},
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    switch (which) {

                                        case 0: startActivity(MapsActivity.viewOnMapA(contattoRichiesto.getAddress()));

                                                // ALTERNATIVA 1
                                                /*String map = "http://maps.google.co.in/maps?q=" + contattoRichiesto.getAddress();
                                                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                                                startActivity(i);
                                                */

                                                // ALTERNATIVA 2
                                                // ricavare le coordinate e usare HELLOMAPS

                                        case 1: dialog.dismiss();
                                    }
                                }
                            });
                    builder.create().show();
                } else
                    Toast.makeText(FocusContattoActivity.this, "Nessun indirizzo da visualizzare!", Toast.LENGTH_SHORT).show();
            }
        });

        // ====== Se viene premuto il Numero ====== //
        campoNumero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(FocusContattoActivity.this);
                builder.setTitle("Scegli l'opzione");
                builder.setItems(new CharSequence[]{"Chiama " + contattoRichiesto.getName(), "Annulla"},
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                switch (which) {

                                    case 0: ActivityCompat.requestPermissions(FocusContattoActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);

                                    case 1: dialog.dismiss();

                                }
                            }
                        });
                builder.create().show();
            }
        });

    }

    /****** Metodi aggiuntivi *******/

    // ====== Metodo che imposta i dati ricevuti alle view dell'activity in questione ======= //

    private void impostaDatiContatto(long id) {

        Contact contattoRichiesto = managerdb.getContatto(id);

        String compagnia, mail, lavoro;
        compagnia = "Compagnia";
        mail      = "Mail";
        lavoro    = "Lavoro";

        mappaNuoviCampi.put(compagnia, 1);
        mappaNuoviCampi.put(mail, 2);
        mappaNuoviCampi.put(lavoro, 3);

        byte[] immagine = contattoRichiesto.getImage();
        Bitmap bmp = BitmapFactory.decodeByteArray(immagine, 0, immagine.length);

        nomeTxt = findViewById(R.id.detailsNome);
        cognomeTxt = findViewById(R.id.detailsCognome);
        numeroTxt = findViewById(R.id.detailsNumero);
        indirizzoTxt = findViewById(R.id.detailsIndirizzo);
        imgView = findViewById(R.id.detailsImmagine);

        nomeTxt.setText(contattoRichiesto.getName());

        if(!contattoRichiesto.getSurname().equals("")){
            cognomeTxt.setText(contattoRichiesto.getSurname());}

        else{
            cognomeTxt.setHint("Cognome non inserito");
        }

        if(!contattoRichiesto.getAddress().equals("")){
            indirizzoTxt.setText(contattoRichiesto.getAddress());}

            else{
            indirizzoTxt.setHint("Indirizzo non inserito");
        }

        numeroTxt.setText(String.valueOf(contattoRichiesto.getPhone()));
        indirizzoTxt.setText(contattoRichiesto.getAddress());
        imgView.setImageBitmap(new CircleImage(getApplicationContext()).transform(bmp));


        // == EVENTUALI NUOVI CAMPI == //
        if (contattoRichiesto.getCompany() != null && !contattoRichiesto.getCompany().equals("")) {
            createNewField(compagnia);
            compagniaTxt = findViewById(mappaNuoviCampi.get(compagnia));
            compagniaTxt.setText(contattoRichiesto.getCompany());

        } else if (contattoRichiesto.getMail() != null && !contattoRichiesto.getMail().equals("")) {
            createNewField(mail);
            mailTxt = findViewById(mappaNuoviCampi.get(mail));
            mailTxt.setText(contattoRichiesto.getMail());

        } else if (contattoRichiesto.getJob() != null && !contattoRichiesto.getJob().equals("")) {
            createNewField(lavoro);
            lavoroTxt = findViewById(mappaNuoviCampi.get(lavoro));
            lavoroTxt.setText(contattoRichiesto.getJob());
        }
    }

    // ======= Metodo per la creazione dinamica di un nuovo campo da riempire ===== //

    public void createNewField(String name) {
        LinearLayout ll = findViewById(R.id.layoutCampi);
        // aggiungi una nuova view
        TextView nuovoCampoTxt = new TextView(this);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SPtoPX(50));
        p.setMargins(SPtoPX(10), SPtoPX(10), SPtoPX(10), 0);
        nuovoCampoTxt.setLayoutParams(p);
        nuovoCampoTxt.setBackground(getResources().getDrawable(R.drawable.gradient_fields));
        nuovoCampoTxt.setTextColor(getResources().getColor(R.color.grey_700));
        nuovoCampoTxt.setPadding(SPtoPX(25), 0, 0, 0);
        nuovoCampoTxt.setGravity(Gravity.CENTER_VERTICAL);
        nuovoCampoTxt.setTextSize(SPtoPX(6));

        switch (name) {

            case "Compagnia":
                nuovoCampoTxt.setId(mappaNuoviCampi.get("Compagnia"));
                break;

            case "Mail":
                nuovoCampoTxt.setId(mappaNuoviCampi.get("Mail"));
                break;


            case "Lavoro":
                nuovoCampoTxt.setId(mappaNuoviCampi.get("Lavoro"));
                break;
        }
        ll.addView(nuovoCampoTxt);
    }

    // ======  Metodo per la conversione SP in PIXEL ====== //
    public static int SPtoPX(int sp) {
        return (int) (sp * Resources.getSystem().getDisplayMetrics().scaledDensity);
    }

    // ======= Risposta alle permission decise dall'utente ===== //
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CALL) {

            // Se ho ottenuto le permission
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                    Log.d(TAG, "Permissions ok");

                    long idOttenuto = getIntent().getLongExtra("contatto_id", 1);
                    Contact contattorichiesto = managerdb.getContatto(idOttenuto);

                    String numeroContatto = String.valueOf(contattorichiesto.getPhone());

                    Uri number = Uri.parse(numeroContatto);

                    Intent intent = new Intent(Intent.ACTION_CALL);

                    intent.setData(Uri.parse("tel:" + number));

                    startActivity(intent);
                }

                else {
                       Log.d(TAG, "Permissions NOT ok ");
                       ActivityCompat.requestPermissions(FocusContattoActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                }
            }
        }

        else {
               Toast.makeText(this, "Non hai i permessi necessari!", Toast.LENGTH_SHORT).show();
        }
    }

    // === Se viene premuto Back === //
    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }
}