package Com.Emad.hashemwatermark;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

    private ImageView imageView;
    private static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);
        layout.setBackgroundColor(0xFF101010);

        Button button = new Button(this);
        button.setText("اختيار صورة من المعرض");

        imageView = new ImageView(this);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        layout.addView(button);
        layout.addView(imageView,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0, 1));

        setContentView(layout);

        button.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(
                    Intent.createChooser(intent, "اختر صورة"),
                    PICK_IMAGE);
        });
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE
                && resultCode == RESULT_OK
                && data != null) {
            Uri uri = data.getData();

            if (uri != null) {
                imageView.setImageURI(uri);
                Toast.makeText(this,
                        "تم اختيار الصورة",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
