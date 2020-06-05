package android.stage.onetouch.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.stage.onetouch.model.Cliente;
import android.stage.onetouch.model.Sede;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.stage.onetouch.R;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class HomePageFragment extends Fragment {
    private Cliente mCliente;
    private Button mButtonCerca;
    private CardView mCardCliente;
    private EditText mEditText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Sede sede = new Sede("000001", "via esempio, 11");
        List<Sede> sedi = new ArrayList<>();
        sedi.add(sede);
        mCliente = new Cliente("000001", "Cybaze", sedi);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_page, container, false);
        mButtonCerca =  v.findViewById(R.id.cerca_cliente_button);
        mEditText = v.findViewById(R.id.codice_cliente);
        mCardCliente = v.findViewById(R.id.card_cliente);
        mCardCliente.setFocusable(true);
        mButtonCerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEditText.getText().toString().equals("0001")) {
                    mCardCliente.setVisibility(View.VISIBLE);
                    Snackbar.make(getView(), "Cliente trovato!", Snackbar.LENGTH_LONG).show();
                }else{
                    mCardCliente.setVisibility(View.INVISIBLE);
                    Snackbar.make(getView(), "Cliente non trovato, riprovare..", Snackbar.LENGTH_LONG).show();
                }

            }
        });
        mCardCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SediInPreattivazioneFragment nextFrag = new SediInPreattivazioneFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_main, nextFrag, "HomePageFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
        return v;
    }
}