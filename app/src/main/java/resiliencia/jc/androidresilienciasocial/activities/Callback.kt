package resiliencia.jc.androidresilienciasocial.activities

import com.google.firebase.database.DatabaseReference

interface Callback {

    fun onSignout()
    fun onGetUserId(): String

    fun onSigntest()

    //Database
    fun getUserDatabase(): DatabaseReference

    fun profileComplete()

    fun startActivityForPhoto()
}