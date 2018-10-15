package projects.paul.didacus.controller;

/*

Classe che controlla l'activity Update Contatto xml

 */

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import projects.paul.didacus.database.ManagerDB;
import projects.paul.didacus.MainActivity;
import projects.paul.didacus.modello.Contact;
import projects.paul.didacus.R;

public class UpdateContattoActivity extends AppCompatActivity {

    private ImageView imgView;
    private EditText  nomeTxt;
    private EditText  cognomeTxt;
    private EditText  numeroTxt;
    private EditText  indirizzoTxt;

    /* eventuali nuovi campi */
    private EditText  compagniaTxt;
    private EditText  mailTxt;
    private EditText  lavoroTxt;

    private ImageButton updateButton;
    private ImageButton annullaButton;
    private Button    nuovoCampoButton;

    private ManagerDB managerdb;
    private long      contattoIdOttenuto;
    private String    hint;
    static  Map<String, Integer> mappaNuoviCampi = new HashMap<>();

    int     REQ_CODE_GALLERIA = 999;
    Integer REQ_CODE_FOTOCAMERA = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_contatto);

        try {
            // == tramite Intent ottengo l'id del contatto == //
            contattoIdOttenuto = getIntent().getLongExtra("contatto_id", 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        managerdb = new ManagerDB(this);

        impostaDatiContatto(contattoIdOttenuto); // uso il manager e id del contatto

        imgView = findViewById(R.id.updateImmagine);
        updateButton = findViewById(R.id.updateButton);
        annullaButton = findViewById(R.id.annullaButton);
        nuovoCampoButton = findViewById(R.id.extraCampoButton);

        // == Se viene premuto Modifica == //
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(updateContatto())
                    backMain();
            }
        });

        // == Se viene premuto Annulla == //
        annullaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 backMain();
            }
        });

        // == Se viene premuta l'Immagine == //
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] items = {"Fotocamera", "Galleria", "Annulla"};
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(UpdateContattoActivity.this);
                builder.setTitle("Scegli l'opzione:");
                builder.setItems(items,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                switch (which) {

                                    case 0: ActivityCompat.requestPermissions(UpdateContattoActivity.this,
                                                new String[] {Manifest.permission.CAMERA},
                                                REQ_CODE_FOTOCAMERA);


                                    case 1: ActivityCompat.requestPermissions(
                                                UpdateContattoActivity.this,
                                                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                                REQ_CODE_GALLERIA);

                                    case 2: dialog.dismiss();
                                }
                            }
                        });
                builder.create().show();
            }
        });

        // == Se viene premuto Nuovo Campo == //
        nuovoCampoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] items = {"Compagnia", "Mail", "Lavoro", "Annulla"};
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(UpdateContattoActivity.this);
                builder.setTitle("Scegli il campo:");

                builder.setItems(items,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                switch (which) {

                                    case 0:
                                           hint = "Compagnia";
                                           mappaNuoviCampi.put(hint, 1);
                                           createNewField(hint);
                                           break;

                                    case 1:
                                           hint = "Mail";
                                           mappaNuoviCampi.put(hint, 2);
                                           createNewField(hint);
                                           break;

                                    case 2:
                                           hint = "Lavoro";
                                           mappaNuoviCampi.put(hint, 3);
                                           createNewField(hint);
                                           break;

                                    case 3: dialog.dismiss();
                                }
                            }
                        });
                builder.create().show();
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);  // Nasconde la tastiera all'apertura dell'activity
    }

    // ==== Metodo che imposta i dati ricevuti alle view dell'activity Update ==== //
    private void impostaDatiContatto(long id){

        Contact contattoRichiesto = managerdb.getContatto(id);

        String compagnia, mail, lavoro;
        compagnia = "Compagnia";
        mail      = "Mail";
        lavoro    = "Lavoro";

        mappaNuoviCampi.put(compagnia, 1);
        mappaNuoviCampi.put(mail, 2);
        mappaNuoviCampi.put(lavoro, 3);

        byte[] immagine = contattoRichiesto.getImage();
        Bitmap bm = BitmapFactory.decodeByteArray(immagine, 0 ,immagine.length);

        nomeTxt = findViewById(R.id.updateNome);
        cognomeTxt = findViewById(R.id.updateCognome);
        numeroTxt = findViewById(R.id.updateNumero);
        indirizzoTxt = findViewById(R.id.updateIndirizzo);
        imgView = findViewById(R.id.updateImmagine);

        nomeTxt.setText(contattoRichiesto.getName());
        cognomeTxt.setText(contattoRichiesto.getSurname());
        numeroTxt.setText(String.valueOf(contattoRichiesto.getPhone()));
        indirizzoTxt.setText(contattoRichiesto.getAddress());
        imgView.setImageBitmap(new CircleImage(this).transform(bm));

        /* == eventuali nuovi campi == */
        if(contattoRichiesto.getCompany() != null && !contattoRichiesto.getCompany().equals("")) {
            createNewField(compagnia);

            compagniaTxt = findViewById(mappaNuoviCampi.get(compagnia));
            compagniaTxt.setText(contattoRichiesto.getCompany());
        }
        else if (contattoRichiesto.getMail() != null && !contattoRichiesto.getMail().equals("")){
            createNewField(mail);

            mailTxt = findViewById(mappaNuoviCampi.get(mail));
            mailTxt.setText(contattoRichiesto.getMail());
        }
        else if (contattoRichiesto.getJob() != null && !contattoRichiesto.getJob().equals("")){
            createNewField(lavoro);

            lavoroTxt = findViewById(mappaNuoviCampi.get(lavoro));
            lavoroTxt.setText(contattoRichiesto.getJob());
        }
    }

    // ======= Metodo per la creazione di un nuovo campo ====== //
    public void createNewField(String name){
        LinearLayout ll = findViewById(R.id.layoutCampi);
        // aggiungi una nuova view
        EditText nuovoCampoTxt = new EditText(this);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SPtoPX(50));
        p.setMargins(SPtoPX(10),SPtoPX(10),SPtoPX(10),0);
        nuovoCampoTxt.setLayoutParams(p);
        nuovoCampoTxt.setBackground(getResources().getDrawable(R.drawable.gradient_fields));
        nuovoCampoTxt.setTextColor(getResources().getColor(R.color.grey_700));
        nuovoCampoTxt.setPadding(SPtoPX(35),0,0,0);
        nuovoCampoTxt.setGravity(Gravity.CENTER_VERTICAL);

        switch(name){

            case "Compagnia" : nuovoCampoTxt.setInputType(EditorInfo.TYPE_TEXT_FLAG_CAP_WORDS);
                               nuovoCampoTxt.setId(mappaNuoviCampi.get("Compagnia"));
                               break;

            case "Mail"      : nuovoCampoTxt.setInputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                               nuovoCampoTxt.setId(mappaNuoviCampi.get("Mail"));
                               break;


            case "Lavoro"    : nuovoCampoTxt.setInputType(EditorInfo.TYPE_TEXT_FLAG_CAP_WORDS);
                               nuovoCampoTxt.setId(mappaNuoviCampi.get("Lavoro"));
                               break;
        }
        ll.addView(nuovoCampoTxt);

    }

    // ======= Metodo di conversione SP --> PX ===== //
    public static int SPtoPX(int sp) {
        return (int) (sp * Resources.getSystem().getDisplayMetrics().scaledDensity);
    }

    // ======= Metodo che prende i nuovi dati del contatto e lo aggiorna ===== //
    private boolean updateContatto() {

        try {
            // == tramite Intent ottengo l'id del contatto == //
            contattoIdOttenuto = getIntent().getLongExtra("contatto_id", 1);

            Contact contattoRichiesto = managerdb.getContatto(contattoIdOttenuto);

            byte[] image = ImagetoByte(imgView);

            long phone = 0L;

            String name    = nomeTxt.getText().toString().trim();
            String surname = cognomeTxt.getText().toString().trim();
            if(!numeroTxt.getText().toString().trim().equals("")){
                phone   = Long.valueOf(numeroTxt.getText().toString().trim());
            }
            String address = indirizzoTxt.getText().toString().trim();

            if(!name.isEmpty() && !surname.isEmpty() && phone != 0L){
                contattoRichiesto.setName(name);
                contattoRichiesto.setSurname(surname);
                contattoRichiesto.setPhone(phone);
                contattoRichiesto.setAddress(address);
                contattoRichiesto.setImage(image);
            }else{
                Toast.makeText(this, "I primi tre campi sono obbligatori!", Toast.LENGTH_SHORT).show();
                return false;
            }

            compagniaTxt = findViewById(mappaNuoviCampi.get("Compagnia"));
            if(compagniaTxt != null){
                contattoRichiesto.setCompany(compagniaTxt.getText().toString().trim());
            }

            mailTxt = findViewById(mappaNuoviCampi.get("Mail"));
            if(mailTxt != null){
                contattoRichiesto.setMail(mailTxt.getText().toString().trim());
            }

            lavoroTxt = findViewById(mappaNuoviCampi.get("Lavoro"));
            if(lavoroTxt != null){
                contattoRichiesto.setJob(lavoroTxt.getText().toString().trim());
            }
            managerdb.aggiornaContatto(contattoIdOttenuto, UpdateContattoActivity.this, contattoRichiesto);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return  true;
    }
    
    // ====  Metodo chiamato da StartActivityForResult ==== //
    public void onActivityResult (int RQcode, int resultcode, Intent data){

        if(RQcode == REQ_CODE_FOTOCAMERA && resultcode == RESULT_OK  && data != null)
        {
            Bundle bundle = data.getExtras();
            Bitmap bitmap = null;
            if (bundle != null) {
                bitmap = (Bitmap)bundle.get("data");
            }
            imgView.setImageBitmap(new CircleImage(this).transform(bitmap));
        }
        if(RQcode ==  REQ_CODE_GALLERIA && resultcode == RESULT_OK  && data != null){
            Uri uri = data.getData();
            try {

                InputStream inputStream = null;
                if (uri != null) {
                    inputStream = getContentResolver().openInputStream(uri);
                }
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgView.setImageBitmap(new CircleImage(this).transform(bitmap));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(RQcode, resultcode, data);
    }

    // ======== Metodo per gestire le permission per fotocamera/galleria ==== //
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQ_CODE_FOTOCAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQ_CODE_FOTOCAMERA);
            }
            else
                Toast.makeText(getApplicationContext() ,"Non hai i permessi necessari",Toast.LENGTH_LONG).show();
        }
        if(requestCode == REQ_CODE_GALLERIA){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
            {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,REQ_CODE_GALLERIA);
            }
            else {
                Toast.makeText(getApplicationContext() ,"Non hai i permessi necessari",Toast.LENGTH_LONG).show();
            }

        }
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    // ======= Metodo per ottenere l'immagine dalla view per poi poterla salvare nel DB ===== //
    private byte[] ImagetoByte(ImageView imageView)
    {
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        return stream.toByteArray();
    }

    // ======= Metodo per tornare indietro nella main activity ======= //
    private void backMain(){
        startActivity(new Intent(this, MainActivity.class));
    }

    // ======= Se viene premuto il tasto fisico Back ===== //
    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }
}