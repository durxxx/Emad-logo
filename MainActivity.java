package com.emad.hashemwatermark;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends Activity {
    private static final int PICK_IMAGE = 101;
    private ImageView preview;
    private Bitmap original;
    private Bitmap logo;
    private int logoPercent = 24;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(0xFF111111);
        getWindow().setNavigationBarColor(0xFF111111);
        logo = BitmapFactory.decodeResource(getResources(), R.drawable.emad_logo);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(20, 24, 20, 20);
        root.setBackgroundColor(0xFF111111);

        TextView title = new TextView(this);
        title.setText("EMAD AL HASHIM");
        title.setTextColor(0xFFFFD477);
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, 1);
        root.addView(title, new LinearLayout.LayoutParams(-1, -2));

        TextView subtitle = new TextView(this);
        subtitle.setText("أضف اللوغو الذهبي إلى صورك");
        subtitle.setTextColor(0xFFE6E6E6);
        subtitle.setTextSize(16);
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-1, -2);
        sp.setMargins(0, 8, 0, 16);
        root.addView(subtitle, sp);

        preview = new ImageView(this);
        preview.setBackgroundColor(0xFF242424);
        preview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        root.addView(preview, new LinearLayout.LayoutParams(-1, 0, 1));

        TextView sizeLabel = new TextView(this);
        sizeLabel.setText("حجم اللوغو");
        sizeLabel.setTextColor(0xFFFFD477);
        sizeLabel.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, 12, 0, 0);
        root.addView(sizeLabel, lp);

        SeekBar size = new SeekBar(this);
        size.setMax(45);
        size.setProgress(logoPercent - 10);
        root.addView(size, new LinearLayout.LayoutParams(-1, -2));
        size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar s, int p, boolean user) { logoPercent = p + 10; render(); }
            public void onStartTrackingTouch(SeekBar s) {}
            public void onStopTrackingTouch(SeekBar s) {}
        });

        Button choose = new Button(this);
        choose.setText("اختيار صورة من الاستوديو");
        root.addView(choose, new LinearLayout.LayoutParams(-1, -2));
        choose.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.setType("image/*");
            i.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(i, PICK_IMAGE);
        });

        Button save = new Button(this);
        save.setText("حفظ الصورة باللوغو");
        root.addView(save, new LinearLayout.LayoutParams(-1, -2));
        save.setOnClickListener(v -> saveImage());

        setContentView(root);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            try {
                Uri uri = data.getData();
                InputStream in = getContentResolver().openInputStream(uri);
                original = BitmapFactory.decodeStream(in);
                if (in != null) in.close();
                render();
            } catch (Exception e) {
                Toast.makeText(this, "تعذر فتح الصورة", Toast.LENGTH_LONG).show();
            }
        }
    }

    private Bitmap compose() {
        if (original == null) return null;
        Bitmap out = original.copy(Bitmap.Config.ARGB_8888, true);
        Canvas c = new Canvas(out);
        int maxW = Math.max(1, out.getWidth() * logoPercent / 100);
        int maxH = Math.max(1, (int)(logo.getHeight() * (maxW / (float)logo.getWidth())));
        Bitmap scaled = Bitmap.createScaledBitmap(logo, maxW, maxH, true);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        p.setAlpha(225);
        int margin = Math.max(12, out.getWidth() / 40);
        Rect dst = new Rect(out.getWidth() - maxW - margin, out.getHeight() - maxH - margin,
                            out.getWidth() - margin, out.getHeight() - margin);
        c.drawBitmap(scaled, null, dst, p);
        return out;
    }

    private void render() {
        if (original != null) preview.setImageBitmap(compose());
    }

    private void saveImage() {
        Bitmap result = compose();
        if (result == null) {
            Toast.makeText(this, "اختَر صورة أولاً", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "EmadLogo_" + System.currentTimeMillis() + ".jpg");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/EmadLogo");
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            OutputStream out = getContentResolver().openOutputStream(uri);
            result.compress(Bitmap.CompressFormat.JPEG, 95, out);
            out.flush(); out.close();
            Toast.makeText(this, "تم حفظ الصورة في Pictures/EmadLogo", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "تعذر حفظ الصورة", Toast.LENGTH_LONG).show();
        }
    }
}
