package com.example.foodplannerapplication.modules.home.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import com.example.foodplannerapplication.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FragmentHome : Fragment() {
    private lateinit var addMealFab: FloatingActionButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as? AppCompatActivity
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val drawerLayout = activity?.findViewById<DrawerLayout>(R.id.main)
        addMealFab = view.findViewById(R.id.add_meal_fab)

        addMealFab.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentHome_to_addMealFragment)
        }

        view.findViewById<Toolbar>(R.id.tool_bar)?.setNavigationOnClickListener {
            drawerLayout?.openDrawer(GravityCompat.START)
        }
    }
}
