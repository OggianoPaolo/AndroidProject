package projects.paul.didacus.controller;

/*
    Questa è la classe che gestisce in maniera ottimale la Recycler View
    evitando il dispendioso "findbyId"
*/

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import projects.paul.didacus.database.ManagerDB;
import projects.paul.didacus.modello.Contact;
import projects.paul.didacus.R;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewMyAdapter";

    private RecyclerView recview;
    private Context context;
    private List<Contact> listaContatti;
    private static final int REQUEST_CALL = 1;

    public MyAdapter(Context context, List<Contact> listaContatti, RecyclerView mrecyclerView) {
        this.context = context;
        this.listaContatti = listaContatti;
        this.recview = mrecyclerView;
    }

    // ==== Questo metodo servirà alla RecyclerView ogni volta che serve per il tipo adatto a come rappresentare l'elemento della lista === //
    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d(TAG, "onCreateViewHolder: chiamata");

        // prende come parametro il contesto del View "genitore" e poi il riferimento al layout activity
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_singolo_contatto, parent, false);
        return new ViewHolder(view);

    }

    // ===== Sfruttato per riempire l'elemento in questione, usando la posizione dell'elemento nella lista ==== //
    @Override
    public void onBindViewHolder(@NonNull final MyAdapter.ViewHolder holder, final int position) {

        Log.d(TAG, "onBindViewHolder: questa funziona");

        final Contact contatto = listaContatti.get(position);          // USARE GETPOSITION MI CREA PROBLEMI

        byte[] immagine = contatto.getImage();
        Bitmap bmp = BitmapFactory.decodeByteArray(immagine, 0 ,immagine.length);

        holder.nomeTx.setText(contatto.getName().concat(" ").concat(contatto.getSurname()));
        holder.numeroTx.setText(String.valueOf(contatto.getPhone()));
        holder.immagineVw.setImageBitmap(bmp);

        holder.cardVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Scegli l'opzione");
                builder.setItems(new CharSequence[]{"Chiama " + contatto.getName(), "Dettagli", "Annulla"},
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                switch (which) {

                                    case 0: ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);

                                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                                            callIntent.setData(Uri.parse("tel:" + contatto.getPhone()));
                                            context.startActivity(callIntent);
                                            }
                                            break;

                                    case 1: redirectFocusContatto(contatto.getIdContact());
                                            break;

                                    case 2: dialog.dismiss();
                                            break;

                                }
                            }
                        });
                builder.create().show();
            }});

        // Listener dell'elemento
        holder.cardVw.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final CharSequence[] items = {"Chiama " + contatto.getName() , "Modifica", "Elimina", "Annulla"};
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Scegli l'opzione:");
                builder.setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {

                            case 0: ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);
                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                                    callIntent.setData(Uri.parse("tel:" + contatto.getPhone()));
                                    context.startActivity(callIntent);
                                    }
                                    break;
                            case 1: redirectAggiornaContatto(contatto.getIdContact());
                                    break;

                            case 2: AlertDialog.Builder builderYN = new AlertDialog.Builder(context);
                                    builderYN.setTitle("Sei sicuro di voler eliminare il contatto?");

                                    builderYN.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                    });

                                    builderYN.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        removeContatto(contatto, position);
                                    }
                                    });
                                    builderYN.show();
                                    break;

                            case 3:
                                   dialog.dismiss();
                                   break;
                        }
                    }
                });
                builder.create().show();

                return true;
            }
        });
    }

    // ======== Per ottenere il numero totale di elementi ===== //
    @Override
    public int getItemCount() {
        return listaContatti.size();
    }

    // ====== Metodo per la Recycler view ==== //
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // ===== Metodo che rimuove un contatto dal database e dal recycler view === //
    private void removeContatto(Contact contatto, final int position) {
        ManagerDB manager = new ManagerDB(context);
        manager.eliminaContatto(contatto.getIdContact(), context);

        listaContatti.remove(position);
        recview.removeViewAt(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listaContatti.size());
        notifyDataSetChanged();
    }

    // ====== Metodo che reindirizza all'activity per visualizzare un contatto ==== //
    private void redirectFocusContatto(long contattoId){
        Intent redirect = new Intent(context, FocusContattoActivity.class);
        redirect.putExtra("contatto_id", contattoId);
        context.startActivity(redirect);
    }

    // ====== Metodo che reindirizza all'activity per aggiungere un contatto ==== //
    private void redirectAggiornaContatto(long contattoId){
        Intent redirect = new Intent(context, UpdateContattoActivity.class);
        redirect.putExtra("contatto_id", contattoId);
        context.startActivity(redirect);
    }

    // ====== Metodo per la ricerca ====== //
    public void setFilter(List<Contact> nuovalista){
        listaContatti = new ArrayList<>();
        listaContatti.addAll(nuovalista);
        notifyDataSetChanged();
    }



    // ====== INNER CLASS VIEW HOLDER che fornisce i riferimenti alle view vere e proprie ===== //
    public class ViewHolder extends RecyclerView.ViewHolder{

        private CardView       cardVw;
        private ImageView      immagineVw;
        private TextView       nomeTx;
        private TextView       numeroTx;

        private ViewHolder(View contattoView) {
            super(contattoView);

            // inizializza le variabili (le view)
            cardVw     = contattoView.findViewById(R.id.cardVw);
            immagineVw = contattoView.findViewById(R.id.titoloImmagine);
            nomeTx     = contattoView.findViewById(R.id.titoloNome);
            numeroTx   = contattoView.findViewById(R.id.titoloNumero);
        }
    }
}