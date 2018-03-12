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

public class MainActivity extends AppCompatActivity implements MainView{

    public static final int GET_IMG = 123;
    public static final String TAG = "TEST";
    protected Disposable selectButtonSubscription;
    protected Disposable saveButtonSubscription;
    private Bitmap loadedBitmap;

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
        saveButtonSubscription = RxView.clicks(saveButton).subscribe(o -> saveFile(loadedBitmap));
    }

    private void saveFile(Bitmap sourceBitmap) {
        Log.d(TAG, "SaveButtonClicked");
        MediaStore.Images.Media.insertImage(this.getContentResolver(),sourceBitmap,"title","descr");
    }

    private void onSelectButton() {
        selectButtonSubscription = RxView.clicks(selectButton).subscribe(o -> selectFile());
    }

    private void selectFile() {
        Log.d(TAG, "SelectButtonClicked");
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
                loadedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedfile);
                showImage(loadedBitmap);
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
