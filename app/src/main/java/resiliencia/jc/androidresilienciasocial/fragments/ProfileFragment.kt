package resiliencia.jc.androidresilienciasocial.fragments

import android.os.Bundle
import android.os.ProxyFileDescriptorCallback
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.common.data.DataHolder




import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_profile.*
import resiliencia.jc.androidresilienciasocial.R
import resiliencia.jc.androidresilienciasocial.User
import resiliencia.jc.androidresilienciasocial.activities.Callback
import resiliencia.jc.androidresilienciasocial.util.*


class ProfileFragment : Fragment() {

    private lateinit var userId:String



    //DataBase
    private  lateinit var userDatabase: DatabaseReference

    private var callback: Callback? = null
    fun setCallback(callback: Callback){
        this.callback = callback
        userId = callback.onGetUserId()

        //Database
        userDatabase = callback.getUserDatabase().child(userId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progessLayout.setOnTouchListener{view, event -> true}

        populateInfo()

        photoIV.setOnClickListener { callback?.startActivityForPhoto() }

        applyButton.setOnClickListener { onApply() }
        soButton.setOnClickListener { callback?.onSignout() }

    }

    fun populateInfo(){
        progessLayout.visibility = View.VISIBLE


        //Database
        userDatabase.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                progessLayout.visibility = View.GONE
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(isAdded){
                    val user = p0.getValue(User::class.java)
                    nameET.setText(user?.usuario, TextView.BufferType.EDITABLE)
                    emailET.setText(user?.email, TextView.BufferType.EDITABLE)
                    ageET.setText(user?.edad, TextView.BufferType.EDITABLE)

                    if(user?.rollId == ROLL_VOLUNTARIO){
                        radioRoll1.isChecked = true
                    }
                    if (user?.rollId == ROLL_PACIENTE){
                        radioRoll2.isChecked = true
                    }
                    if (user?.rollId == ROLL_MEDICO){
                        radioRoll3.isChecked = true
                    }

                    if(user?.preferedRoll == ROLL_VOLUNTARIO){
                        radioPRoll1.isChecked = true
                    }
                    if (user?.preferedRoll == ROLL_PACIENTE){
                        radioPRoll2.isChecked = true
                    }
                    if (user?.preferedRoll == ROLL_MEDICO){
                        radioPRoll3.isChecked = true
                    }
                    if(user?.imageUrl.isNullOrEmpty()){
                        populateImage(user?.imageUrl!!)
                    }
                    progessLayout.visibility = View.GONE

                }
            }

        })
    }

    fun onApply(){
        if(nameET.text.toString().isNullOrEmpty() || emailET.text.toString().isNullOrEmpty() || radioGroup1.checkedRadioButtonId == -1 || radioGroup2.checkedRadioButtonId == -1 || ageET.text.toString().isNullOrEmpty()){
            Toast.makeText(context, "Recuerda completar tu perfil", Toast.LENGTH_SHORT).show()
        }
        else{
            val name =nameET.text.toString()
            val age = ageET.text.toString()
            val email = emailET.text.toString()
            val roll =
                if (radioRoll1.isChecked) ROLL_VOLUNTARIO
                else if (radioRoll2.isChecked) ROLL_PACIENTE
                else ROLL_MEDICO
            val preferedRoll =
                if(radioPRoll1.isChecked) ROLL_VOLUNTARIO
                else if (radioPRoll2.isChecked) ROLL_PACIENTE
                else ROLL_MEDICO

            //DATABASE
            userDatabase.child(DATA_NAME).setValue(name)




            userDatabase.child(DATA_AGE).setValue(age)
            userDatabase.child(DATA_EMAIL).setValue(email)
            userDatabase.child(DATA_ROLL).setValue(roll)
            userDatabase.child(DATA_ROLL_PREFERENCE).setValue(preferedRoll)


            callback?.profileComplete()
        }
    }


    fun updateImageUri(uri: String){
        //DATABASE
        userDatabase.child(DATA_IMAGE_URL).setValue(uri)
        populateImage(uri)

    }

    fun populateImage(uri: String){
        Glide.with(this)
            .load(uri)
            .into(photoIV)
    }



}
