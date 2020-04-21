package android.stage.onetouch;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CpeWaitFragment extends Fragment {
    private static final String NEWLINE = "\n";
    //TODO Estrarre il broadcastReceiver e gli altri metodi ripetuti
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED:
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    new Thread(){@Override public void run(){cpeFingerprint();}}.start();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED:
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB:
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED:
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED:
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private UsbService usbService;
    private CpeWaitFragment.MyHandler mHandler;

    private CpeInfo mCpeInfo; // Questo è il cpe atteso, si vuole confrontare quello atteso con quello attuale
    private String lastOutput;

    private void cpeFingerprint() {
        CpeInfo cpeInfo = new CpeInfo();
        for(int i = 0; i < 2; i++)
            usbService.write(NEWLINE);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lastOutput = "";
        usbService.write("display version\n" + NEWLINE);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(lastOutput.contains("Huawei")){
            cpeInfo.setVendorName("Huawei");
            Log.d("OneTouch", cpeInfo.getVendorName());
            cpeInfo.setModel(searchModel("Quidway (.*?)\\s", lastOutput));
            //searchSoftwareVersion("(.* Software, Version .*)", lastOutput);
            //searchOsFamily("(.*) Software, Version .*", lastOutput);
            //searchUpTime("uptime is (.*)",lastOutput);
            //searchLastReboot("Last reboot (.*)", lastOutput);
            //searchProcessor("CPU type: (.*)", lastOutput);
            //searchConfigurationMemory("(.*?) bytes .* SDRAM Memory", lastOutput);
            //searchFlashSize("(.*?) bytes Flash Memory", lastOutput);
            lastOutput = "";
        }
            /*send("display device manuinfo" + NEWLINE);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            searchDeviceSerialNumber("DEVICE_SERIAL_NUMBER\\s+:\\s+(\\w+)", lastOutput);*/
            mCpeInfo = cpeInfo;
    }

    private String searchModel(String s, String lastOutput) {
        String model = search(s, lastOutput);
        model = model.toUpperCase().replace("-", "");
        Log.d("OneTouch", model);
        return model;
    }

    private String searchRight(String s){
        return search(":.*", s).substring(1).trim();
    }

    private String search(String s, String lastOutput) {
        Pattern pattern = Pattern.compile(s);
        Matcher matcher = pattern.matcher(lastOutput);
        if(matcher.find())
            return matcher.group();
        return null;
    }
    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        getActivity().registerReceiver(mUsbReceiver, filter);
    }


    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(getActivity(), service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            getActivity().startService(startService);
        }
        Intent bindingIntent = new Intent(getActivity(), service);
        getActivity().bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mUsbReceiver);
        getActivity().unbindService(usbConnection);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.cpe_wait_fragment, container, false);
        mHandler = new CpeWaitFragment.MyHandler(CpeWaitFragment.this);
        return v;
    }

    private static class MyHandler extends Handler {
        private final WeakReference<CpeWaitFragment> mFragment;

        public MyHandler(Fragment fragment) {
            mFragment = new WeakReference<CpeWaitFragment>((CpeWaitFragment) fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = (String) msg.obj;
                    mFragment.get().lastOutput += data;
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
}
