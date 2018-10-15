package projects.paul.didacus.database;

/*

  Classe che implementa i metodi per interagire con il database

*/

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import projects.paul.didacus.modello.Contact;

import static projects.paul.didacus.database.ContactDB.COLUMN_ADDRESS;
import static projects.paul.didacus.database.ContactDB.COLUMN_COMPANY;
import static projects.paul.didacus.database.ContactDB.COLUMN_ID;
import static projects.paul.didacus.database.ContactDB.COLUMN_JOB;
import static projects.paul.didacus.database.ContactDB.COLUMN_MAIL;
import static projects.paul.didacus.database.ContactDB.COLUMN_NAME;
import static projects.paul.didacus.database.ContactDB.COLUMN_PHONE;
import static projects.paul.didacus.database.ContactDB.COLUMN_SURNAME;
import static projects.paul.didacus.database.ContactDB.COLUMN_IMAGE;
import static projects.paul.didacus.database.ContactDB.TABLE_CONTACTS;

public class ManagerDB {

    private ContactDB dbhelper;
    private SQLiteDatabase db;
    private ContentValues valori;

    // ===== Costruttore per il Database ==== //
    public ManagerDB(Context c) {
        dbhelper = new ContactDB(c);
    }


    // ===== Metodo per ottenere tutti i contatti ==== //
    public List<Contact> getTuttiContatti() {

        List<Contact> listacontatti = new ArrayList<>();
        db = dbhelper.getReadableDatabase();
        Cursor c = db.rawQuery(ContactDB.SELECT_ALL_CONTACTS, null);

        if(c.getCount() > 0) {
            if(c.moveToFirst()) {
                do {
                    long idContact = c.getLong(c.getColumnIndex(COLUMN_ID));
                    String nameContact = c.getString(c.getColumnIndex(COLUMN_NAME));
                    String surnameContact = c.getString(c.getColumnIndex(COLUMN_SURNAME));
                    long phoneContact = c.getLong(c.getColumnIndex(COLUMN_PHONE));
                    String addressContact = c.getString(c.getColumnIndex(COLUMN_ADDRESS));
                    byte[] imageContact = c.getBlob(c.getColumnIndex(COLUMN_IMAGE));
                    String companyContact = c.getString(c.getColumnIndex(COLUMN_COMPANY));
                    String mailContact = c.getString(c.getColumnIndex(COLUMN_MAIL));
                    String jobContact = c.getString(c.getColumnIndex(COLUMN_JOB));

                    Contact datiContatto;
                    datiContatto = creaSingoloContatto(idContact, nameContact, surnameContact, phoneContact, addressContact, imageContact, companyContact, mailContact, jobContact);

                    listacontatti.add(datiContatto);
                } while (c.moveToNext());

            }c.close();
             closeDBreadable();
        }

        return listacontatti;
    }

    // ====== Metodo per ottenere un contatto ===== //
    public Contact getContatto(long id){
            db = dbhelper.getReadableDatabase();

            Cursor cursor = db.rawQuery(ContactDB.SELECT_SINGLE_CONTACT + id, null);

            Contact contattorichiesto = new Contact();

        if(cursor.moveToFirst() && cursor.getCount() > 0) {
            do {
                contattorichiesto.setIdContact(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                contattorichiesto.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                contattorichiesto.setSurname(cursor.getString(cursor.getColumnIndex(COLUMN_SURNAME)));
                contattorichiesto.setPhone(cursor.getLong(cursor.getColumnIndex(COLUMN_PHONE)));
                contattorichiesto.setAddress(cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS)));
                contattorichiesto.setImage(cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE)));
                contattorichiesto.setCompany(cursor.getString(cursor.getColumnIndex(COLUMN_COMPANY)));
                contattorichiesto.setMail(cursor.getString(cursor.getColumnIndex(COLUMN_MAIL)));
                contattorichiesto.setJob(cursor.getString(cursor.getColumnIndex(COLUMN_JOB)));

            }while(cursor.moveToNext());
        }

        cursor.close();
        closeDBreadable();

        return contattorichiesto;
    }

    // ===== Metodo per la creazione di un Contatto (lettura) ==== //
    private Contact creaSingoloContatto(long id, String name, String surname, long phone, String address, byte[] image, String company, String mail, String job) {
        return new Contact(id, name, surname, phone, address, image, company, mail, job);
    }

    // ===== Metodo che PREPARA i dati del contatto ==== //
    private void setDati(Contact contatto) {
        valori = new ContentValues();

        valori.put(COLUMN_NAME, contatto.getName());
        valori.put(COLUMN_SURNAME, contatto.getSurname());
        valori.put(COLUMN_PHONE, contatto.getPhone());
        valori.put(COLUMN_ADDRESS, contatto.getAddress());
        valori.put(COLUMN_IMAGE, contatto.getImage());
        valori.put(COLUMN_COMPANY, contatto.getCompany());
        valori.put(COLUMN_MAIL, contatto.getMail());
        valori.put(COLUMN_JOB, contatto.getJob());
    }

    /* Metodo che SALVA il contatto nel Database */
    private void salvaContattoinDB(Contact datiContatto, SQLiteDatabase db, Context context) {

        setDati(datiContatto);
        db.insert(TABLE_CONTACTS, null, valori);

        Toast.makeText(context, "Contatto salvato correttamente", Toast.LENGTH_SHORT).show();
        closeDBwritable();
    }

    // ===== Metodo GENERICO che chiama i due precedenti metodi per impostare i dati e salvare il contatto nel DB  ==== //
    public void salvaDati(Contact datiContatto, Context context) {
        db = dbhelper.getWritableDatabase();

        setDati(datiContatto);
        salvaContattoinDB(datiContatto, db, context);
        closeDBwritable();
    }



    // ===== Metodo per ELIMINARE un contatto ===== //
    public void eliminaContatto(long id, Context context) {

        db = dbhelper.getWritableDatabase();
        db.delete(TABLE_CONTACTS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});

        Toast.makeText(context, "Contatto eliminato correttamente", Toast.LENGTH_SHORT).show();
        closeDBwritable();
    }

    /*  Metodo per aggiornare contatto */
    public void aggiornaContatto(long id, Context context, Contact updatedcontatto){

        setDati(updatedcontatto);
        db = dbhelper.getWritableDatabase();
        db.update(TABLE_CONTACTS, valori, COLUMN_ID + " = ?",  new String[]{String.valueOf(id)});

        Toast.makeText(context, "Contatto aggiornato correttamente", Toast.LENGTH_SHORT).show();
        closeDBwritable();
    }

    /* Chiusura del database */
    private void closeDBreadable() {
        db = dbhelper.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    /* Chiusura del database */
    private void closeDBwritable() {
        db = dbhelper.getWritableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}