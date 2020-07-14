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
import java.util.ArrayList;
import java.util.List;

public class CpeConfigurationFragment extends Fragment {
    private static final String NEWLINE = "\n";

    private List<String> commands = new ArrayList<>();

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
        applyConfiguration();
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
        retriveCommands();
        display = (TextView) v.findViewById(R.id.textView);
        return v;
    }

    private void retriveCommands() {
        commands.add("system-view");
        commands.add("user-interface aux 0");
        commands.add("screen-length 0");
        commands.add("sysname tesiTriennale");
        commands.add("clock timezone 1 add 3:00:00");
        commands.add("header login information Tesi di laurea triennale i");
        commands.add("interface ethernet0/0");
        commands.add("port link-mode route");
        commands.add("description PROVA_TESI");
        commands.add("ip address 10.100.130.2 255.255.0.0");
        commands.add("dns server 140.3.255.254");
        commands.add("display version");
        commands.add("display cpu");
    }

    private void applyConfiguration(){
          for(String command: commands)
              usbService.write(command + NEWLINE);
    }
}
