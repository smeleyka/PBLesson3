package ru.smeleyka.pblesson3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import java.io.IOException;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    public static final int GET_IMG = 123;
    public static final String TAG = "TEST";
    protected Disposable selectButtonSubscription;
    protected Disposable saveButtonSubscription;

    @BindView(R.id.select_button)   Button selectButton;
    @BindView(R.id.save_button)     Button saveButton;
    @BindView(R.id.text_view)       TextView textView;
    @BindView(R.id.image_view)      ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        onSelectButton();
        onSaveButton();
    }

    private void onSaveButton() {
        Log.d(TAG, "SaveButtonClicked");
        saveButtonSubscription = RxView.clicks(selectButton).subscribe(o -> selectFile());
    }

    private void saveFile(Bitmap sourceBitmap,Uri sourceUri) {
        MediaStore.Images.Media.insertImage(this.getContentResolver(),sourceBitmap,sourceUri.getAuthority(),"");
    }

    private void onSelectButton() {
        Log.d(TAG, "SelectButtonClicked");
        selectButtonSubscription = RxView.clicks(selectButton).subscribe(o -> selectFile());
    }

    private void selectFile() {
        Intent intent = new Intent()
                .setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a file"), GET_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_IMG && resultCode == RESULT_OK) {
            try {
                Uri selectedfile = data.getData();
                Log.d(TAG, selectedfile.toString());
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedfile);
                showImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showImage(Bitmap bitmap) {
        Log.d(TAG, "Show Image"+bitmap.getConfig());
        textView.setText(bitmap.getWidth()+"x"+bitmap.getHeight());
        imageView.setImageBitmap(bitmap);
    }
}
