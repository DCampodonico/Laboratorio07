package dam.isi.frsf.utn.edu.ar.laboratorio07;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import static dam.isi.frsf.utn.edu.ar.laboratorio07.AltaReclamoActivity.CODIGO_RESULTADO_ALTA_RECLAMO;
import static dam.isi.frsf.utn.edu.ar.laboratorio07.AltaReclamoActivity.CODIGO_RESULTADO_ALTA_RECLAMO_OK;

public class ReclamoActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {
	GoogleMap myMap;
	private boolean flagPermisoPedido;
	private static final int PERMISSION_REQUEST_ACCESS = 899;
	private ArrayList<Reclamo> reclamos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reclamo);
		SupportMapFragment mapFragment = (SupportMapFragment)
				getSupportFragmentManager()
						.findFragmentById(R.id.map);
		reclamos = new ArrayList<>();
		mapFragment.getMapAsync(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_reclamo, menu);
		return true;
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		myMap = googleMap;
		myMap.setOnMapLongClickListener(this);
		myMap.setOnMarkerClickListener(this);
		askForPermission();
	}

	public void askForPermission() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(ReclamoActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale(ReclamoActivity.this,
						android.Manifest.permission.ACCESS_FINE_LOCATION)) {
					AlertDialog.Builder builder = new AlertDialog.Builder(ReclamoActivity.this);
					builder.setTitle("Permisos Peligrosos!!!");
					builder.setPositiveButton(android.R.string.ok, null);
					builder.setMessage("Puedo acceder a un permiso peligroso???");
					builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
						@TargetApi(Build.VERSION_CODES.M)
						@Override
						public void onDismiss(DialogInterface dialog) {
							flagPermisoPedido = true;
							requestPermissions(
									new String[]
											{android.Manifest.permission.ACCESS_FINE_LOCATION}
									, PERMISSION_REQUEST_ACCESS);
						}
					});
					builder.show();
				} else {
					flagPermisoPedido = true;
					ActivityCompat.requestPermissions(ReclamoActivity.this,
							new String[]
									{android.Manifest.permission.ACCESS_FINE_LOCATION}
							, PERMISSION_REQUEST_ACCESS);
				}

			}
		}
		if (!flagPermisoPedido) setMyLocation();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
	                                       String permissions[], int[] grantResults) {
		switch (requestCode) {
			case ReclamoActivity.PERMISSION_REQUEST_ACCESS: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					setMyLocation();
				} else {
					finish();
					Toast.makeText(ReclamoActivity.this, "No permission for access fine location", Toast.LENGTH_SHORT).show();
				}
				return;
			}
		}
	}

	private void setMyLocation() {
		if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			return;
		}
		myMap.setMyLocationEnabled(true);
	}

	@Override
	public void onMapLongClick(LatLng latLng) {
		Intent i = new Intent(ReclamoActivity.this, AltaReclamoActivity.class);
		i.putExtra("coordenadas", latLng);
		startActivityForResult(i, CODIGO_RESULTADO_ALTA_RECLAMO);
	}

	protected void onActivityResult (int requestCode, int resultCode, Intent data){
		if(resultCode == CODIGO_RESULTADO_ALTA_RECLAMO_OK){
			Reclamo reclamo = (Reclamo) data.getSerializableExtra("reclamo");
			myMap.addMarker(new MarkerOptions()
					.position(reclamo.coordenadaUbicacion())
					.title(getString(R.string.Reclamo_title) + reclamo.getEmail())
					.snippet(reclamo.getTitulo()));
			reclamos.add(reclamo);
			Toast.makeText(getApplicationContext(), "Posición marcada", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onMarkerClick(final Marker marker) {
		final EditText edittext = new EditText(this);
		edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Marcar reclamos cercanos");
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Double km = 0.;
				try {
					km = Double.parseDouble(edittext.getText().toString().trim());
				}
				catch (Exception e){
					Toast.makeText(getApplicationContext(), "Distancia incorrecta", Toast.LENGTH_LONG).show();
				}
				trazarItinerario(marker.getPosition(), km);
			}
		});
		builder.setNegativeButton(android.R.string.cancel, null);
		builder.setMessage("Buscar reclamos a no mas de");
		builder.setView(edittext);
		builder.show();
		return false;
	}

	private void trazarItinerario(LatLng position, Double km) {
		ArrayList<LatLng> reclamosCercanos = new ArrayList<>();
		for(Reclamo reclamo : reclamos){
			float result[] = new float[1];
			Location.distanceBetween(position.latitude, position.longitude, reclamo.coordenadaUbicacion().latitude, reclamo.coordenadaUbicacion().longitude, result);
			Log.d("APP", "trazarItinerario: " + result[0]);
			if(result[0] <= 1000*km){
				reclamosCercanos.add(reclamo.coordenadaUbicacion());
			}
		}
		PolylineOptions polylineOptions = new PolylineOptions();
		polylineOptions.addAll(reclamosCercanos);
		myMap.addPolyline(polylineOptions);
	}
}
