package com.example.onpus.weddingpanda.Scanner;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.hardware.Camera;

import com.example.onpus.weddingpanda.R;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Created by onpus on 2018/4/2.
 */

public class SimpleScannerFragment extends Fragment implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;
    int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mScannerView = new ZBarScannerView(getActivity());
        return mScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        if (ContextCompat.checkSelfPermission(getContext(), "android.permission.CAMERA") == PackageManager.PERMISSION_GRANTED) {
            mScannerView.startCamera();

        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.CAMERA"}, REQUEST_CODE_ASK_PERMISSIONS);


            mScannerView.startCamera();
        }
    }
    @Override
    public void handleResult(Result rawResult) {
        Toast.makeText(getActivity(), "Contents = " + rawResult.getContents() +
                ", Format = " + rawResult.getBarcodeFormat().getName(), Toast.LENGTH_SHORT).show();
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(SimpleScannerFragment.this);
            }
        }, 2000);
        Intent myIntent = new Intent(getActivity(), PhotoTakenActivity.class);
        myIntent.putExtra("key", rawResult.getContents()); //Optional parameters
        startActivity(myIntent);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }


}