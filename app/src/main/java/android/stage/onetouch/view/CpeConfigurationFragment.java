package android.stage.onetouch.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.stage.onetouch.R;
import android.stage.onetouch.service.UsbService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;

public class CpeConfigurationFragment extends Fragment {
    private static final String NEWLINE = "\n";

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_DISCONNECTED:
                    Snackbar.make(getView(), "USB disconnected", Snackbar.LENGTH_LONG).show();
                    CpeWaitFragment nextFrag= new CpeWaitFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_main, nextFrag, "CpeConfigurationFragment")
                            .addToBackStack(null)
                            .commit();
                    break;
            }
        }
    };

    private UsbService usbService;
    private TextView display;
    private CpeConfigurationFragment.MyHandler mHandler;

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        getActivity().registerReceiver(mUsbReceiver, filter);
    }


    private static class MyHandler extends Handler {
        private final WeakReference<CpeConfigurationFragment> mFragment;

        public MyHandler(Fragment fragment) {
            mFragment = new WeakReference<CpeConfigurationFragment>((CpeConfigurationFragment) fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = (String) msg.obj;
                    mFragment.get().display.append(data);
                    break;
                case UsbService.CTS_CHANGE:
                    Toast.makeText(mFragment.get().getContext(), "CTS_CHANGE",Toast.LENGTH_LONG).show();
                    break;
                case UsbService.DSR_CHANGE:
                    Toast.makeText(mFragment.get().getContext(), "DSR_CHANGE",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setFilters();
        usbService = ((CpeActivity) getActivity()).getUsbService();
        applicaConfigurazione();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mUsbReceiver);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cpe_configuration, container, false);
        mHandler = new CpeConfigurationFragment.MyHandler(CpeConfigurationFragment.this);
        ((CpeActivity) getActivity()).setServiceHandler(mHandler);

        display = (TextView) v.findViewById(R.id.textView);
        return v;
    }

    private void applicaConfigurazione(){
          usbService.write("system-view" + NEWLINE);
          usbService.write("user-interface aux 0" + NEWLINE);
          usbService.write("screen-length 0" + NEWLINE);
          usbService.write("display version" + NEWLINE);
          usbService.write("display cpu" + NEWLINE);
          usbService.write("display current-configuration" + NEWLINE);
    }
}
