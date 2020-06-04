package android.stage.onetouch;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Set;


public class CpeMenuFragment extends Fragment {
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_DISCONNECTED:
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    CpeWaitFragment nextFrag= new CpeWaitFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, nextFrag, "CpeMenuFragment")
                            .addToBackStack(null)
                            .commit();
                    break;
            }
        }
    };

    private UsbService usbService;

    Button mButtonApriConsole;
    CardView configurationCard;

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
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
        View v = inflater.inflate(R.layout.cpe_menu_fragment, container, false);

        mButtonApriConsole = (Button) v.findViewById(R.id.button8);
        mButtonApriConsole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CpeConsoleFragment nextFrag= new CpeConsoleFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, nextFrag, "CpeMenuFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
        configurationCard = (CardView) v.findViewById(R.id.cardView2);
        configurationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CpeConfigurationFragment nextFrag= new CpeConfigurationFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, nextFrag, "CpeMenuFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        return v;
    }

}
