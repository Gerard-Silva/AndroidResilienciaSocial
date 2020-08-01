package resiliencia.jc.androidresilienciasocial.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import resiliencia.jc.androidresilienciasocial.R
import resiliencia.jc.androidresilienciasocial.fragments.MapFragment
import resiliencia.jc.androidresilienciasocial.fragments.MatchesFragment
import resiliencia.jc.androidresilienciasocial.fragments.ProfileFragment
import resiliencia.jc.androidresilienciasocial.util.DATA_USERS
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectOutput


const val REQUEST_CODE_PHOTO = 1234
class MainActivity : AppCompatActivity(), Callback {

    //loca
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userId =firebaseAuth.currentUser?.uid

    //Database
    private  lateinit var userDatabase: DatabaseReference



    private var profileFragment: ProfileFragment? = null
    private var mapFragment: MapFragment? = null
    private var matchesFragment: MatchesFragment? = null

    private var profileTab: TabLayout.Tab? = null
    private var mapTab: TabLayout.Tab? = null
    private var matchesTab: TabLayout.Tab? = null


    private var resultImageUrl: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {


        //permiso



        //loca
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                // Got last known location. In some rare situations this can be null.
                if (location != null){
                    userDatabase.child(userId!!).child("latitud").setValue(location.latitude)
                    userDatabase.child(userId!!).child("longitud").setValue(location.longitude)

                }
            }







        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(userId.isNullOrEmpty()){
            onSignout()
        }

        //Database
        userDatabase = FirebaseDatabase.getInstance().reference.child(DATA_USERS)

        profileTab = navigationTabs.newTab()
        mapTab = navigationTabs.newTab()
        matchesTab = navigationTabs.newTab()

        profileTab?.icon =ContextCompat.getDrawable(this, R.drawable.tab_profile)
        mapTab?.icon = ContextCompat.getDrawable(this, R.drawable.tab_map)
        matchesTab?.icon = ContextCompat.getDrawable(this, R.drawable.tab_chat)

        navigationTabs.addTab(profileTab!!)
        navigationTabs.addTab(mapTab!!)
        navigationTabs.addTab(matchesTab!!)

        navigationTabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
                onTabSelected(tab)
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab) {
                    profileTab -> {
                        if(profileFragment == null){
                            profileFragment = ProfileFragment()
                            profileFragment!!.setCallback(this@MainActivity)
                        }
                        replaceFragment(profileFragment!!)
                    }
                    mapTab -> {
                        if(mapFragment == null){
                            mapFragment = MapFragment()
                            mapFragment!!.setCallback(this@MainActivity)
                        }
                        replaceFragment(mapFragment!!)
                    }
                    matchesTab -> {
                        if(matchesFragment == null){
                            matchesFragment = MatchesFragment()
                            matchesFragment!!.setCallback(this@MainActivity)
                        }
                        replaceFragment(matchesFragment!!)
                    }
                }
            }
        })

        profileTab?.select()
    }

    fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
        .replace(R.id.fragmentContainer, fragment)
        .commit()
    }

    companion object {
        fun newIntent(context: Context?) = Intent (context, MainActivity::class.java)
    }


    override fun onSigntest() {
        val intent = Intent(this, QuestionActivity::class.java)
        startActivity(intent)
    }

    override fun onSignout() {
        firebaseAuth.signOut()
        startActivity(StartupActivity.newIntent(this))
        finish()
    }

    override fun onGetUserId():String  = userId!!

    //Database
    override fun getUserDatabase(): DatabaseReference = userDatabase


    override fun profileComplete() {
        mapTab?.select()
    }

    override fun startActivityForPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type= "image/*"
        startActivityForResult(intent, REQUEST_CODE_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PHOTO){
            resultImageUrl = data?.data
            storeImage()
        }
    }

    fun storeImage(){

        //Database STORAGE
        if(resultImageUrl != null && userId != null ){
            val filePath = FirebaseStorage.getInstance().reference.child("profileImage").child(userId)
            var bitmap: Bitmap?  = null
            try {
                bitmap = MediaStore.Images.Media.getBitmap(application.contentResolver, resultImageUrl)
            }
            catch (e: IOException){
                e.printStackTrace()
            }

            val baos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 20, baos)
            val data = baos.toByteArray()

            val uploadTask = filePath.putBytes(data)
            uploadTask.addOnFailureListener{e -> e.printStackTrace()}
            uploadTask.addOnSuccessListener { taskSnapshot ->  }
            filePath.downloadUrl
                .addOnSuccessListener { uri ->
                    profileFragment?.updateImageUri(uri.toString())
                }
                .addOnFailureListener{e -> e.printStackTrace()}
        }

    }

}