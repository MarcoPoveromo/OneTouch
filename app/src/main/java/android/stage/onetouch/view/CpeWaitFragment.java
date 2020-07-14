package android.stage.onetouch.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.stage.onetouch.R;
import android.stage.onetouch.service.UsbService;
import android.stage.onetouch.model.CpeInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CpeWaitFragment extends Fragment {

    private static final String NEWLINE = "\n";
    private UsbService usbService;
    private CpeWaitFragment.MyHandler mHandler;
    private String lastOutput;
    private CpeInfo expectedCpe;
    private CpeInfo connectedCpe;

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED:
                    Snackbar.make(getView(), "USB: Permessi concessi", Snackbar.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED:
                    Snackbar.make(getView(), "USB: Permessi non concessi", Snackbar.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_READY:
                    Snackbar.make(getView(), "USB: Connessione al dispositivo riuscita", Snackbar.LENGTH_SHORT).show();
                    new Thread(){@Override public void run(){
                        sendNewLine();
                        cpeFingerprint();
                        checkCpeRecognised();
                    }}.start();
                    break;
                case UsbService.ACTION_NO_USB:
                    Snackbar.make(getView(), "USB: Nessun dispositivo connesso", Snackbar.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED:
                    Snackbar.make(getView(), "USB: Disconnesso", Snackbar.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED:
                    Snackbar.make(getView(), "USB: Adattatore non supportato", Snackbar.LENGTH_SHORT).show();
                    break;
                case UsbService.FINGERPRINT_ACCEPT:
                    Snackbar.make(getView(), "CPE: Riconosciuto", Snackbar.LENGTH_SHORT).show();
                    CpeMenuFragment nextFrag= new CpeMenuFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_main, nextFrag, "CpeWaitFragment")
                            .addToBackStack(null)
                            .commit();
                    break;
                case UsbService.FINGERPRINT_REJECT:
                    Snackbar.make(getView(), "CPE: Cpe non Riconosciuto", Snackbar.LENGTH_SHORT).show();
            }
        }
    };

    private void checkCpeRecognised() {
        if (connectedCpe.equals(expectedCpe)) {
            Intent intent = new Intent(UsbService.FINGERPRINT_ACCEPT);
            getActivity().sendBroadcast(intent);
        } else {
            Intent intent = new Intent(UsbService.FINGERPRINT_REJECT);
            getActivity().sendBroadcast(intent);
        }
    }

    private void cpeFingerprint() {
        if (isHuawei()) {
            connectedCpe = getHuaweiInfo();
        }else if(isCisco()){
            // Retrive Cisco information
        }else if(isJuniper()){
            // Retrive Juniper information
        }
    }

    private boolean isJuniper() {
        return false;
    }

    private boolean isCisco() {
        return false;
    }

    private CpeInfo getHuaweiInfo() {
        CpeInfo cpeInfo = new CpeInfo();
        cpeInfo.setVendorName("Huawei");
        cpeInfo.setModel(searchModel("Quidway (.*?)\\s", lastOutput));
        //searchSoftwareVersion("(.* Software, Version .*)", lastOutput);
        //searchOsFamily("(.*) Software, Version .*", lastOutput);
        //searchUpTime("uptime is (.*)",lastOutput);
        //searchLastReboot("Last reboot (.*)", lastOutput);
        //searchProcessor("CPU type: (.*)", lastOutput);
        //searchConfigurationMemory("(.*?) bytes .* SDRAM Memory", lastOutput);
        //searchFlashSize("(.*?) bytes Flash Memory", lastOutput);
        /*send("display device manuinfo" + NEWLINE);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            searchDeviceSerialNumber("DEVICE_SERIAL_NUMBER\\s+:\\s+(\\w+)", lastOutput);*/
        lastOutput = "";
        return cpeInfo;
    }

    private boolean isHuawei() {
        usbService.write("display version\n" + NEWLINE);
        sleep();
        if(lastOutput.contains("Huawei"))
            return true;
        return false;
    }

    private void sendNewLine() {
        for(int i = 0; i < 2; i++)
            usbService.write(NEWLINE);
        sleep();
        lastOutput = "";
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

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        filter.addAction(UsbService.ACTION_USB_READY);
        filter.addAction(UsbService.FINGERPRINT_ACCEPT);
        filter.addAction(UsbService.FINGERPRINT_REJECT);
        getActivity().registerReceiver(mUsbReceiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        setFilters();
        usbService = ((CpeActivity) getActivity()).getUsbService();
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
        View v = inflater.inflate(R.layout.cpe_wait_fragment, container, false);
        mHandler = new CpeWaitFragment.MyHandler(CpeWaitFragment.this);
        ((CpeActivity) getActivity()).setServiceHandler(mHandler);
        expectedCpe = new CpeInfo();
        expectedCpe.setVendorName("Huawei");
        expectedCpe.setModel("QUIDWAY AR1915 ");
        return v;
    }

    private void sleep(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
