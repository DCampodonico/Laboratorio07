package dam.isi.frsf.utn.edu.ar.laboratorio07;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

public class AltaReclamoActivity extends AppCompatActivity {

    private Button btnCancelar;
    private Button btnAgregar;
    private EditText txtDescripcion;
    private EditText txtMail;
    private EditText txtTelefono;
    private LatLng ubicacion;
	public static Integer CODIGO_RESULTADO_ALTA_RECLAMO = 1;
	public static Integer CODIGO_RESULTADO_ALTA_RECLAMO_OK = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        ubicacion = (LatLng) extras.get("coordenadas");
        setContentView(R.layout.activity_alta_reclamo);
        btnAgregar = (Button) findViewById(R.id.btnReclamar);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
        txtDescripcion = (EditText) findViewById(R.id.reclamoTexto);
        txtTelefono= (EditText) findViewById(R.id.reclamoTelefono);
        txtMail= (EditText) findViewById(R.id.reclamoMail);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Reclamo reclamo = new Reclamo(ubicacion.latitude, ubicacion.longitude,
		            txtDescripcion.getText().toString().trim(), txtTelefono.getText().toString().trim(),
		            txtMail.getText().toString().trim());
	            Intent intent = getIntent();
	            intent.putExtra("reclamo", reclamo);
	            setResult(CODIGO_RESULTADO_ALTA_RECLAMO_OK, intent);
	            finish();
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				setResult(2);
	            finish();
            }
        });
    }


}
