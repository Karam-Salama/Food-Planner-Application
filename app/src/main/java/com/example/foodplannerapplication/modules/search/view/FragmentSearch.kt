package com.example.foodplannerapplication.modules.search.view
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentContainerView
import com.example.foodplannerapplication.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class FragmentSearch : Fragment() {
    private lateinit var fragmentContainer: FragmentContainerView
    private lateinit var chipGroup: ChipGroup
    private lateinit var chipCountries: Chip
    private lateinit var chipCategories: Chip
    private lateinit var chipIngredients: Chip

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        handleChipsLogic(view)

    }


    private fun initViews(view: View) {
        chipCountries = view.findViewById(R.id.fragmentCountriesSearch)
        chipCategories = view.findViewById(R.id.fragmentCategoriesSearch)
        chipIngredients = view.findViewById(R.id.fragmentIngredientsSearch)
        fragmentContainer = view.findViewById(R.id.fragmentContainerView)
    }

    private fun handleChipsLogic(view: View) {
        chipCountries.isChecked = true

        loadFragment(FragmentCountriesSearch())

        chipGroup = view.findViewById(R.id.chipGroup)
        if (chipGroup.checkedChipId == -1) {
            chipGroup.check(R.id.fragmentCountriesSearch)
            loadFragment(FragmentCountriesSearch())
        }
        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == -1) {
                group.check(R.id.fragmentCountriesSearch)
                loadFragment(FragmentCountriesSearch())
                return@setOnCheckedChangeListener
            }

            when (checkedId) {
                R.id.fragmentCountriesSearch -> loadFragment(FragmentCountriesSearch())
                R.id.fragmentCategoriesSearch -> loadFragment(FragmentCategoriesSearch())
                R.id.fragmentIngredientsSearch -> loadFragment(FragmentIngredientsSearch())
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }
}