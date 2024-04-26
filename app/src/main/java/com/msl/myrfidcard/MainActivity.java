package com.msl.myrfidcard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements NfcAdapter.ReaderCallback {
    private TextView mTextView;
    private NfcAdapter mNfcAdapter;
    private View rootView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        checkDeviceSupport();
        checkRFIDEnable();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mNfcAdapter != null) {
            Bundle option = new Bundle();
            option.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY,250);


            mNfcAdapter.enableReaderMode(this,
                    this, NfcAdapter.FLAG_READER_NFC_A |
                    NfcAdapter.FLAG_READER_NFC_B |
                    NfcAdapter.FLAG_READER_NFC_F |
                    NfcAdapter.FLAG_READER_NFC_V |
                    NfcAdapter.FLAG_READER_NFC_BARCODE |
                    NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                    option);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mNfcAdapter.disableReaderMode(this);
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }
    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            byte[] tagId = tag.getId();
            String tagIdHex = byteArrayToHexString(tagId);
            mTextView.setText("RFID Tag ID: " + tagIdHex);
        }
    }
    private String byteArrayToHexString(byte[] array) {
        StringBuilder sb = new StringBuilder();
        for (byte b : array) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
    private void init(){
        mTextView = (TextView) findViewById(R.id.textView);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        rootView = findViewById(android.R.id.content);
    }
    private void checkDeviceSupport(){
        if (mNfcAdapter == null) {
            // Device does not support NFC.
            snackBar("This device does not support NFC",rootView);
            finish();
            return;
        }
    }
    private void checkRFIDEnable(){
        if (!mNfcAdapter.isEnabled()) {
            // NFC is not enabled.
            snackBar("Please enable NFC",rootView);
        } else {
            handleIntent(getIntent());
        }
    }

    private void snackBar(String message, View view){
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);

        // Optionally, add an action to the Snackbar
        snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the undo action
            }
        });

        // Show the Snackbar
        snackbar.show();
    }

    @Override
    public void onTagDiscovered(Tag tag) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}