package projects.paul.didacus.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import projects.paul.didacus.R;

public class CircleImage {
    Context context;

    public CircleImage(Context context) {
        this.context = context;
    }

    public Bitmap transform(Bitmap source) {
        try {

            // == Restituisce il minimo tra altezza e larghezza dell'immagine
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            // == Crea un'immagine squadrata

            Bitmap squaredBitmap = Bitmap
                    .createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                //
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size,
                    squaredBitmap.getConfig());

            // == Prepara un oggetto Canvas (rendering dinamico immagini) per poter creare l'immagine tonda

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);                 // Prepara l'immagine tonda
            // squaredBitmap.recycle();
            return bitmap;
        } catch (Exception e) {
           e.printStackTrace();
        }
        return BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ico_android);
    }

}