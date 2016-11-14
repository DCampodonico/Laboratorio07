package dam.isi.frsf.utn.edu.ar.laboratorio07;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class ReclamoActivity extends AppCompatActivity implements OnMapReadyCallback {
	GoogleMap myMap;
	private boolean flagPermisoPedido;
	private static final int PERMISSION_REQUEST_ACCESS = 899;

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
		myMap = googleMap;
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
}
