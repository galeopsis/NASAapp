package com.galeopsis.nasapictureofthedayapp.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.galeopsis.nasapictureofthedayapp.R
import com.galeopsis.nasapictureofthedayapp.databinding.NasaSearchFragmentBinding
import com.galeopsis.nasapictureofthedayapp.model.entity.NasaData
import com.galeopsis.nasapictureofthedayapp.utils.DatePickerFragment
import com.galeopsis.nasapictureofthedayapp.utils.LoadingState
import com.galeopsis.nasapictureofthedayapp.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel

class NasaSearchFragment : Fragment() {

    companion object {
        fun newInstance() = NasaSearchFragment()
    }

    private val mainViewModel by viewModel<MainViewModel>()
    private var _binding: NasaSearchFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NasaSearchFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            activity?.finish()

        }

        initData()
        listeners()
    }

//*******Function section****************

    private fun setBottomSheetBehavior(bottomSheet: ConstraintLayout) {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

    }

    private fun initData() {

        mainViewModel.data.observe(viewLifecycleOwner, {

            it?.forEach { nasaData ->

                view?.let { it1 -> setBottomSheetBehavior(it1.findViewById(R.id.bottom_sheet_container)) }
                val bsTvDescription =
                    requireActivity().findViewById<View>(R.id.bottom_sheet_description) as TextView
                val bsTvTitle =
                    requireActivity().findViewById<View>(R.id.bottom_sheet_description_header) as TextView
                bsTvDescription.text = nasaData.explanation
                bsTvTitle.text = nasaData.title

                val date = nasaData.date
                binding.dateTv.text = date

                val imageClick = binding.photoView
                val pictureUrl = nasaData.url
//                val hdPicture = nasaData.hdurl
                val mediaType = nasaData.media_type

                if (mediaType.equals("video")) {
                    Glide.with(this)
                        .load(R.drawable.notapicture)
                        .into(binding.photoView)
                    imageClick.setOnClickListener {
                        appStarter(nasaData)
                    }
                } else {
                    Glide.with(this)
                        .load(pictureUrl)
                        .into(binding.photoView)

                    imageClick.setOnClickListener {
                        Glide.with(this)
                            .load(pictureUrl)
                            .into(binding.photoView)
                    }
                }
            }
        })

        mainViewModel.loadingState.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.FAILED -> {
                    Toast.makeText(context, "Something wrong!", Toast.LENGTH_SHORT).show()
                    binding.loadingLayout.visibility = View.GONE
                }
                LoadingState.Status.RUNNING ->
                    binding.loadingLayout.visibility = View.VISIBLE
                LoadingState.Status.SUCCESS ->
                    binding.loadingLayout.visibility = View.GONE
            }
        })
    }

    private fun listeners() {
        with(binding) {

            inputLayout
                .setEndIconOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW).apply {
                        data =
                            Uri.parse("https://en.wikipedia.org/wiki/${inputEditText.text.toString()}")
                        inputEditText.text = null
                    })
                }

            fab.setOnClickListener {
                // create new instance of DatePickerFragment
                val datePickerFragment = DatePickerFragment()
                val supportFragmentManager = requireActivity().supportFragmentManager

                // we have to implement setFragmentResultListener
                supportFragmentManager.setFragmentResultListener(
                    "REQUEST_KEY",
                    viewLifecycleOwner
                ) { resultKey, bundle ->
                    if (resultKey == "REQUEST_KEY") {

                        val date = myDate(bundle)
                        binding.dateTv.text = date
                        if (date != null) {
                            mainViewModel.fetchData(date)
                            initData()
                        }
                    }
                }

                // show
                datePickerFragment.show(supportFragmentManager, "DatePickerFragment")

            }
        }
    }

    private fun myDate(bundle: Bundle): String? {
        return bundle.getString("SELECTED_DATE")
    }

    private fun appStarter(data: NasaData) {
        val appLink = data.url
        val intentApp = Intent(Intent.ACTION_VIEW, Uri.parse(appLink))
        val intentBrowser = Intent(Intent.ACTION_VIEW, Uri.parse(appLink))
        try {
            this.startActivity(intentApp)
        } catch (ex: ActivityNotFoundException) {
            this.startActivity(intentBrowser)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        activity?.supportFragmentManager?.beginTransaction()
            ?.remove(newInstance())
            ?.commitNow()
    }
}
