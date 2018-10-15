package projects.paul.didacus.controller;

/*
     Questa è la classe che controlla l'activity Aggiungi Contatto xml
 */


import android.Manifest;
import android.app.AlertDialog;
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

public class AggiungiContattoActivity extends AppCompatActivity {

    private ImageView imgView;

    private EditText  nomeTxt;
    private EditText  cognomeTxt;
    private EditText  numeroTxt;
    private EditText  indirizzoTxt;

    // == Eventuali nuovi campi == //
    private EditText  compagniaTxt;
    private EditText  mailTxt;
    private EditText  lavoroTxt;

    private ImageButton annullaButton;
    private ImageButton salvacontattoButton;
    private Button    nuovoCampoButton;

    private ManagerDB managerdb;

    int     REQ_CODE_GALLERIA = 999;
    Integer REQ_CODE_FOTOCAMERA = 1;

    static Map<String, Integer> mappaNuoviCampi;
    private String hint;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggiungi_contatto);

        managerdb = new ManagerDB(AggiungiContattoActivity.this);

        mappaNuoviCampi = new HashMap<>();

        // == Inizializza le variabili delle view normali == //
        imgView      = findViewById(R.id.casellaImmagine);
        nomeTxt      = findViewById(R.id.casellaNome);
        cognomeTxt   = findViewById(R.id.casellaCognome);
        numeroTxt    = findViewById(R.id.casellaNumero);
        indirizzoTxt = findViewById(R.id.casellaIndirizzo);

        // == Inizializza le variabili delle view button == //
        annullaButton = findViewById(R.id.annullaButton);
        salvacontattoButton = findViewById(R.id.salvaButton);
        nuovoCampoButton = findViewById(R.id.nuovoCampoButton);

        // == Se viene premuto Annulla == //
        annullaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!nomeTxt.getText().toString().trim().equals("") ||
                    !cognomeTxt.getText().toString().trim().equals("") ||
                    !numeroTxt.getText().toString().trim().equals("") ||
                    !indirizzoTxt.getText().toString().trim().equals(""))
                {
                    AlertDialog.Builder builderYN = new AlertDialog.Builder(AggiungiContattoActivity.this);
                    builderYN.setTitle("Sei sicuro di voler uscire? Perderai i dati");

                    builderYN.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builderYN.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            backHome();                        }
                    });
                    builderYN.show();
                }
                else { backHome();
                       finish();
                }
            }
        });

        // ======== Se viene premuto Salva ================== //
        salvacontattoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nuovoContatto()){
                backHome();
                finish();}
            }
        });

        // ======= Se viene premuto Aggiungi Campo ========= //
        nuovoCampoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(counter < 1) {
                    final CharSequence[] items = {"Compagnia", "Mail", "Lavoro", "Annulla"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(AggiungiContattoActivity.this);
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

                                        case 3:
                                            dialog.dismiss();
                                    }
                                }
                            });
                    builder.create().show();
                }else{
                    Toast.makeText(AggiungiContattoActivity.this, "Secondo campo: work in progress", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // ============= Se viene premuta l'Immagine ========= //
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] items = {"Fotocamera", "Galleria", "Annulla"};
                AlertDialog.Builder builder = new AlertDialog.Builder(AggiungiContattoActivity.this);
                builder.setTitle("Scegli l'opzione:");
                builder.setItems(items,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                switch (which) {

                                    case 0: ActivityCompat.requestPermissions
                                            (AggiungiContattoActivity.this,
                                                    new String[] {Manifest.permission.CAMERA},
                                                    REQ_CODE_FOTOCAMERA);
                                            break;


                                    case 1: ActivityCompat.requestPermissions
                                            (AggiungiContattoActivity.this,
                                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                                    REQ_CODE_GALLERIA);
                                            break;

                                    case 2: dialog.dismiss();
                                }
                            }
                        });
                builder.create().show();
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);  // Nasconde la tastiera all'apertura dell'activity
    }

    // =============== Metodo che consente l'aggiunta di un nuovo campo al Contatto ========== //

    public void createNewField(String hint){
        LinearLayout ll = findViewById(R.id.layoutCampi);
        // Aggiungi una nuova view: EdiText
        EditText nuovoCampoTxt = new EditText(this);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SPtoPX(50));
        p.setMargins(SPtoPX(10),SPtoPX(10),SPtoPX(10),0);
        nuovoCampoTxt.setLayoutParams(p);
        nuovoCampoTxt.setHint(hint);
        nuovoCampoTxt.setBackground(getResources().getDrawable(R.drawable.gradient_fields));
        nuovoCampoTxt.setTextColor(getResources().getColor(R.color.grey_700));
        nuovoCampoTxt.setPadding(SPtoPX(25),0,0,0);                       // uso la funzione per convertire SP in PIXEL
        nuovoCampoTxt.setGravity(Gravity.CENTER_VERTICAL);

        switch(hint){

            case "Compagnia": nuovoCampoTxt.setInputType(EditorInfo.TYPE_TEXT_FLAG_CAP_WORDS);
                              nuovoCampoTxt.setId(mappaNuoviCampi.get("Compagnia"));
                              break;

            case "Mail"     : nuovoCampoTxt.setInputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                              nuovoCampoTxt.setId(mappaNuoviCampi.get("Mail"));
                              break;


            case "Lavoro"   : nuovoCampoTxt.setInputType(EditorInfo.TYPE_TEXT_FLAG_CAP_WORDS);
                              nuovoCampoTxt.setId(mappaNuoviCampi.get("Lavoro"));
                              break;
        }
        ll.addView(nuovoCampoTxt);
        counter++; // mi serve per bloccare l'utente se aggiunge ancora
    }

    // ======== Metodo che crea e salva il nuovo contatto ========== //
    private boolean nuovoContatto() {

        Contact templatecontatto = new Contact();

        String name    = nomeTxt.getText().toString().trim();
        String surname = cognomeTxt.getText().toString().trim();
        String address = indirizzoTxt.getText().toString().trim();
        byte[] image   = immagineInByte(imgView);

        long phone = 0L;
        if(!numeroTxt.getText().toString().trim().equals("")){
            phone   = Long.valueOf(numeroTxt.getText().toString().trim());
        }

        if(!name.isEmpty() && !surname.isEmpty() && phone != 0L){

            templatecontatto.setName(name);
            templatecontatto.setSurname(surname);
            templatecontatto.setPhone(phone);
            templatecontatto.setAddress(address);
            templatecontatto.setImage(image);


            if(mappaNuoviCampi.get("Compagnia") != null){
                compagniaTxt = findViewById(mappaNuoviCampi.get("Compagnia"));
                templatecontatto.setCompany(compagniaTxt.getText().toString().trim());
            }
            if(mappaNuoviCampi.get("Mail") != null){
                mailTxt = findViewById(mappaNuoviCampi.get("Mail"));
                templatecontatto.setMail(mailTxt.getText().toString().trim());
            }
            if(mappaNuoviCampi.get("Lavoro") != null){
                lavoroTxt = findViewById(mappaNuoviCampi.get("Lavoro"));
                templatecontatto.setJob(lavoroTxt.getText().toString().trim());
            }

            managerdb.salvaDati(templatecontatto, AggiungiContattoActivity.this);
            finish();

        }else{
               Toast.makeText(this, "I primi tre campi sono obbligatori!", Toast.LENGTH_SHORT).show();
               return false; }

        return true;
    }

    // =========== Metodo che sistemerà immagine nella Image View =========== //
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

    // =========== Metodo che converte l'immagine Bitmap in array Byte per salvarla nel database ====== //
    private byte[] immagineInByte(ImageView imgview)
    {
        Bitmap bitmap = ((BitmapDrawable)imgview.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        return stream.toByteArray();
    }

    // =========== Metodo per gestire le permission per fotocamera/galleria ============= //
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQ_CODE_FOTOCAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQ_CODE_FOTOCAMERA);
            }
            else{
                Toast.makeText(getApplicationContext() ,"Non hai i permessi necessari",Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(AggiungiContattoActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQ_CODE_FOTOCAMERA);}
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
                ActivityCompat.requestPermissions(AggiungiContattoActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQ_CODE_GALLERIA);
            }

        }
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }


    // ======== Metodo che reindirizza alla Main Activity ======== //
    private void backHome() {
        startActivity(new Intent(AggiungiContattoActivity.this, MainActivity.class));
    }

    // ======== Metodo di conversione SP --> PX ================= //
    public static int SPtoPX(int sp) {
        return (int) (sp * Resources.getSystem().getDisplayMetrics().scaledDensity);
    }

    // ======== Se viene premuto il tasto fisico Back ========== //
    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }
}