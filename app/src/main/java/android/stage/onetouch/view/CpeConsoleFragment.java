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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;

public class CpeConsoleFragment extends Fragment {
    private static final String CTRL_B = "\u0002";
    private static final String CTRL_M = "\u0013";
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_DISCONNECTED:
                    Snackbar.make(getView(), "USB disconnected", Snackbar.LENGTH_LONG).show();
                    CpeWaitFragment nextFrag= new CpeWaitFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, nextFrag, "CpeConsoleFragment")
                            .addToBackStack(null)
                            .commit();
                    break;
            }
        }
    };

    private UsbService usbService;
    private TextView display;
    private EditText editText;
    private Button mButtonCtrlE;
    private Button mButtonCtrlM;
    private CpeConsoleFragment.MyHandler mHandler;


    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        getActivity().registerReceiver(mUsbReceiver, filter);
    }


    private static class MyHandler extends Handler {
        private final WeakReference<CpeConsoleFragment> mFragment;

        public MyHandler(Fragment fragment) {
            mFragment = new WeakReference<CpeConsoleFragment>((CpeConsoleFragment) fragment);
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
        setFilters();  // Start listening notifications from UsbService
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
        View v = inflater.inflate(R.layout.cpe_console_fragment, container, false);
        setHasOptionsMenu(true);
        mHandler = new MyHandler(CpeConsoleFragment.this);
        ((CpeActivity) getActivity()).setServiceHandler(mHandler);

        display = (TextView) v.findViewById(R.id.textView);
        editText = (EditText) v.findViewById(R.id.editText);
        mButtonCtrlE = v.findViewById(R.id.button2);
        mButtonCtrlM = v.findViewById(R.id.button3);
        ImageButton sendButton = (ImageButton) v.findViewById(R.id.button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = editText.getText().toString() + "\n";
                editText.setText("");
                if (usbService != null) {
                    usbService.write(data.getBytes());
                }
            }
        });
        mButtonCtrlE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.append(CTRL_B);
            }
        });
        mButtonCtrlM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.append(CTRL_M);
            }
        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
}
