package ru.deltadelete.lab15

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import ru.deltadelete.lab15.databinding.FragmentAccountBinding
import ru.deltadelete.lab15.models.toUser

class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private val menu = Menu()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.addMenuProvider(menu, viewLifecycleOwner)

        val navController = findNavController()
        if (navController.graph.startDestinationId != R.id.SecondFragment) {
            navController.graph.setStartDestination(R.id.SecondFragment)
        }
        binding.buttonPosts.setOnClickListener {
            navController.navigate(R.id.postFragment)
        }


        val activity = requireActivity()
        if (activity is MainActivity) {
            activity.findViewById<View>(R.id.toolbar)?.visibility = View.VISIBLE
        }
        lifecycleScope.launch {
            val user = Firebase.auth.currentUser?.toUser()
            binding.currentUser.user = user
        }
        // TODO: Repurpose to Account fragment
        // TODO: Posts lazy loading / pagination
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    inner class Menu : MenuProvider {
        override fun onCreateMenu(menu: android.view.Menu, menuInflater: MenuInflater) {
            if (Firebase.auth.currentUser == null)
                return
            menuInflater.inflate(R.menu.menu_authenticated, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.logout -> {
                    Firebase.auth.signOut()
                    findNavController().navigate(R.id.action_global_AuthFragment)
                    activity?.removeMenuProvider(this)
                    true
                }
                else -> {
                    false
                }
            }
        }

    }
}