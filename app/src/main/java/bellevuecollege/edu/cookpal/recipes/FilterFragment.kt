package bellevuecollege.edu.cookpal.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import bellevuecollege.edu.cookpal.databinding.FragmentFilterBinding
import bellevuecollege.edu.cookpal.databinding.FragmentProfileBinding

class FilterFragment: Fragment() {

    private lateinit var binding: FragmentFilterBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterBinding.inflate(inflater)

        binding.lifecycleOwner = this


        // Inflate the layout for this fragment
        return binding.root
    }
}