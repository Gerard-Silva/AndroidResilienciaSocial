package resiliencia.jc.androidresilienciasocial.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_profile.*
import resiliencia.jc.androidresilienciasocial.R
import resiliencia.jc.androidresilienciasocial.User
import resiliencia.jc.androidresilienciasocial.activities.Callback
import resiliencia.jc.androidresilienciasocial.activities.QuestionActivity

class MapFragment : Fragment() {

    private var callback: Callback? = null
    private lateinit var  userId: String
    private lateinit var userDatabase: DatabaseReference
    private var rowItems = ArrayList<User>()

    fun setCallback(callback: Callback){

        this.callback = callback
        userId = callback.onGetUserId()
        userDatabase = callback.getUserDatabase()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)

    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        conectButton.setOnClickListener { onCreate() }
        testButton.setOnClickListener { callback?.onSignout() }


    }

    fun onCreate(){

    }


}