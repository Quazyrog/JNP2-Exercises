package pl.edu.mimuw.students.wm382710.appa

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import pl.edu.mimuw.students.wm382710.appa.maps.*
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.random.Random.Default.nextDouble

class NavigationFragment : Fragment() {
    private val lock = ReentrantLock()
    private var target: TargetLocation? = null
    private var position: EarthPoint = EarthPoint(18.5000, 50.2314)
    private var callback: (() -> Unit)? = null
    private lateinit var locationClient: FusedLocationProviderClient

    private var hasView: Boolean = false
    private var targetNameText: TextView? = null
    private var targetCoordinatesText: TextView? = null
    private var currentCoordinatesText: TextView? = null
    private var azimuthText: TextView? = null
    private var distanceText: TextView? = null

    var targetLocation: TargetLocation?
        get() = target
        set(value: TargetLocation?) {
            target = value
            updateScreen()
        }

    var currentLocation: EarthPoint
        get() = position
        private set(value: EarthPoint) {
            position = value
            updateScreen()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationClient = LocationServices.getFusedLocationProviderClient(context!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val ui = inflater.inflate(R.layout.fragment_navigation, container, false)
        targetNameText = ui.findViewById(R.id.targetNameText)
        targetCoordinatesText = ui.findViewById(R.id.targetCoordinatesText)
        currentCoordinatesText = ui.findViewById(R.id.currentCoordinatesText)
        azimuthText = ui.findViewById(R.id.azimuthText)
        distanceText = ui.findViewById(R.id.distanceText)
        hasView = true
        return ui
    }

    override fun onDestroyView() {
        hasView = false
        targetNameText = null
        targetCoordinatesText = null
        currentCoordinatesText = null
        azimuthText = null
        distanceText = null
        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(context!!, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, ACCESS_FINE_LOCATION)) {
                val msg = "Without permission to access location, the app cannot get your current position to navigate you."
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            }

            val req = ActivityResultContracts.RequestPermission()
            val act = activity!!.registerForActivityResult(req, ActivityResultCallback { isGranted: Boolean ->
                if (isGranted) {
                    startUsingLocation()
                } else {
                    val msg = "Cannot retrieve current location."
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            })
            act.launch(ACCESS_FINE_LOCATION)
        } else {
            startUsingLocation()
        }
    }

    override fun onResume() {
        super.onResume()
        startUsingLocation()
        updateScreen()
    }

    fun onArrive(callback: () -> Unit) {
        this.callback = callback
    }

    private fun startUsingLocation() {
        if (ContextCompat.checkSelfPermission(context!!, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED){
            val msg = "Cannot retrieve current location."
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }

        locationClient.lastLocation.addOnSuccessListener {
            lock.withLock {
                currentLocation = EarthPoint(it.longitude, it.latitude)
            }
        }

        val request = LocationRequest.create()
        request.interval = 2000
        request.priority = PRIORITY_HIGH_ACCURACY
        locationClient.requestLocationUpdates(request, object: LocationCallback() {
            override fun onLocationResult(loc: LocationResult?) {
                loc ?: return
                currentLocation = averagePosition(loc)
                updateScreen()
            }
        }, Looper.getMainLooper())
    }

    private fun updateScreen() {
        if (!hasView)
            return

        lock.withLock {
            if (target === null)
                return
            targetNameText!!.text = target!!.name
            targetCoordinatesText!!.text = target.toString()
            currentCoordinatesText!!.text = position.toString()
            val navi = target!!.navigate(position)
            azimuthText!!.text = "%.2f°".format(navi.azimuth * DEG)
            distanceText!!.text = "%.2fm".format(navi.distance)

            if (navi.distance < 5 || nextDouble() < 0.1)
                callback?.invoke()
        }
    }

    private fun averagePosition(r: LocationResult): EarthPoint {
        var lon = 0.0
        var lat = 0.0
        var cnt = 0

        for (location in r.locations){
            val a = cnt / (cnt + 1.0)
            val b = 1.0 / (cnt + 1.0)
            lon = a * lon  + b * location.longitude
            lat = a * lat + b * location.latitude
            ++cnt
        }

        return EarthPoint(lon, lat)
    }
}