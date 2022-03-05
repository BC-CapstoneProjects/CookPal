package bellevuecollege.edu.cookpal.profile

import android.content.ContentValues
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class UserProfileHelper {
    companion object {

        private fun getDb(): DatabaseReference? {
            var firebaseDatabase: FirebaseDatabase? = null

            // Reference for Firebase.
            var databaseReference: DatabaseReference? = null

            var fbu : FirebaseUser? = FirebaseAuth.getInstance().currentUser
                ?: throw Exception("Unable to get user profile, user is not logged in")

            var uid : String = fbu!!.uid

            firebaseDatabase = FirebaseDatabase.getInstance();

            databaseReference = firebaseDatabase!!.getReference("users").child(uid);
            return databaseReference
        }

        private fun createDefaultProfile()
        {
            var firebaseDatabase: FirebaseDatabase?
            var databaseReference: DatabaseReference?
            firebaseDatabase = FirebaseDatabase.getInstance();
            var fbu : FirebaseUser? = FirebaseAuth.getInstance().currentUser

            databaseReference = firebaseDatabase.getReference("/users/" + fbu?.uid)
            databaseReference.setValue(fbu?.email?.let { UserProfile(it) })
        }

        fun loadProfile(myCallback: (result: Map<String, String>?) -> Unit) {

            var databaseReference: DatabaseReference? = getDb()

            databaseReference!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    try
                    {
                        val value = dataSnapshot.value

                        if (value == null) {
                            createDefaultProfile()
                            myCallback(null)
                        } else {
                            val data = value as Map<String, String>

                            myCallback.invoke(data)
                        }
                    } catch (ee: Exception) {
                        Log.d("", "mes");
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
                }
            })
        }

        fun saveProfile(data:UserProfile) {

            var databaseReference: DatabaseReference? = getDb()

            databaseReference!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    databaseReference.setValue(data)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
                }
            })
        }
    }
}