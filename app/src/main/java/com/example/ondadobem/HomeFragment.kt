package com.example.ondadobem

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialCardView>(R.id.cardDicas).setOnClickListener {
            startActivity(Intent(requireContext(), DicasActivity::class.java))
        }
        view.findViewById<MaterialCardView>(R.id.cardMetas).setOnClickListener {
            startActivity(Intent(requireContext(), MetasActivity::class.java))
        }
    }
}
