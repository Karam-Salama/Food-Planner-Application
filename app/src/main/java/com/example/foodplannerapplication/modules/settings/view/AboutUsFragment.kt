package com.example.foodplannerapplication.modules.settings.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.foodplannerapplication.R

class AboutUsFragment : Fragment() {
    private lateinit var ivFacebookIcon:ImageView
    private lateinit var ivInstagramIcon:ImageView
    private lateinit var ivLinkedinIcon:ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about_us, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setListeners()
    }

    private fun initViews(view: View) {
        ivFacebookIcon = view.findViewById(R.id.iv_facebookIcon)
        ivInstagramIcon = view.findViewById(R.id.iv_instagramIcon)
        ivLinkedinIcon = view.findViewById(R.id.iv_linkedinIcon)
    }

    private fun setListeners() {
        ivFacebookIcon.setOnClickListener {
            // openLink("https://www.facebook.com/YOUR_USERNAME")
        }
        ivInstagramIcon.setOnClickListener {
            // openLink("https://www.instagram.com/YOUR_USERNAME")
        }
        ivLinkedinIcon.setOnClickListener {
            // openLink("https://www.linkedin.com/in/YOUR_USERNAME")
        }

    }

    private fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

}