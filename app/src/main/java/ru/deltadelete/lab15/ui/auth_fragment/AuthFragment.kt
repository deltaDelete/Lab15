package ru.deltadelete.lab15.ui.auth_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.auth
import ru.deltadelete.lab15.MainActivity
import ru.deltadelete.lab15.R
import ru.deltadelete.lab15.databinding.FragmentAuthBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class AuthFragment : Fragment() {
    private lateinit var binding: FragmentAuthBinding
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var notificationHelper: NotificationHelper
    private var user: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthBinding.inflate(inflater, container, false)
        notificationHelper = NotificationHelper(
            NOTIFICATION_TAG,
            R.string.authentification,
            requireContext().applicationContext
        )
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        if (Firebase.auth.currentUser != null) {
            navController.navigate(R.id.action_global_HomeFragment)
            return
        }
        if (navController.graph.startDestinationId != R.id.auth_graph) {
            navController.graph.setStartDestination(R.id.auth_graph)
        }

        binding.loginButton.setOnClickListener {
            navController.navigate(R.id.action_AuthFragment_to_loginBottomSheet)
        }

        binding.registerButton.setOnClickListener {
            navController.navigate(R.id.action_AuthFragment_to_registerBottomSheet)
        }

        binding.googleAuthButton.setOnClickListener {
//            Firebase.auth.signInWithCredential()
        }


        val activity = requireActivity()
        if (activity is MainActivity) {
            activity.findViewById<View>(R.id.toolbar)?.visibility = View.INVISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        const val NOTIFICATION_TAG = "AuthFragment"
    }
}

