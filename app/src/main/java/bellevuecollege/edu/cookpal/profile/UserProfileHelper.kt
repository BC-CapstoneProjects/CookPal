package bellevuecollege.edu.cookpal.profile

import android.content.ContentValues
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.lang.Exception

class UserProfileHelper {
    companion object {

        private fun getDb(): DatabaseReference? {
            var firebaseDatabase: FirebaseDatabase? = null

            // Reference for Firebase.
            var databaseReference: DatabaseReference? = null

            var fbu : FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()

            if (fbu == null)
            {
                throw Exception("Unable to get user profile, user is not logged in");
            }

            var uid : String = fbu.uid

            firebaseDatabase = FirebaseDatabase.getInstance();

            databaseReference = firebaseDatabase!!.getReference("users").child(uid);
            return databaseReference
        }
        fun loadProfile(myCallback: (result: Map<String, String>?) -> Unit){

            var databaseReference: DatabaseReference? = null

            databaseReference = getDb()

            databaseReference!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot){

                        val data= dataSnapshot.getValue() as Map<String, String>

                        myCallback.invoke(data)
                }
                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
                }
            })
        }

        fun saveProfile(data:UserProfile) {
            var databaseReference: DatabaseReference? = null

            databaseReference = getDb()

            databaseReference!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot){

                    databaseReference!!.setValue(data)
                }
                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
                }
            })
        }
    }
}