package com.example.epoxyrecyclerview

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.example.epoxyrecyclerview.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        setupExpoxy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupExpoxy() {
        binding.ervTest.buildModelsWith(object : EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {
                User.getSampleUsers()
                    .forEachIndexed { index, user ->
                        controller.userLayoutView {
                            id(index)
                            name(user.name)
                            age("${user.age}")
                            email(user.email)
                            itemClickListener { _ ->
                                Toast.makeText(requireActivity(),
                                    "You clicked on item ${user.name}",
                                    Toast.LENGTH_SHORT)
                                    .show()
                            }
                            if (index.rem(2) == 0) background(Color.GREEN)
                        }
                    }
            }
        })
    }
}
