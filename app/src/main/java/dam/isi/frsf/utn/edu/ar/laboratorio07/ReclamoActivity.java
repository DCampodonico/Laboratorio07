package dam.isi.frsf.utn.edu.ar.laboratorio07;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class ReclamoActivity extends AppCompatActivity implements OnMapReadyCallback {
	GoogleMap myMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reclamo);
		SupportMapFragment mapFragment = (SupportMapFragment)
				getSupportFragmentManager()
						.findFragmentById(R.id.map);
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
		myMap=googleMap;
	}
}
