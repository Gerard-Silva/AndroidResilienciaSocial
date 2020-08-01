package resiliencia.jc.androidresilienciasocial.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import resiliencia.jc.androidresilienciasocial.R
import resiliencia.jc.androidresilienciasocial.activities.Callback

class MatchesFragment : Fragment() {


    private var callback: Callback? = null
    fun setCallback(callback: Callback){
        this.callback = callback
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_matches, container, false)
    }

}