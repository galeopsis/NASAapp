package com.galeopsis.nasapictureofthedayapp.ui

import android.annotation.SuppressLint
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
import androidx.appcompat.widget.PopupMenu
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Typeface
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
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
        binding.menuButton.setOnClickListener {
            val popupMenu = context?.let { it1 -> PopupMenu(it1, it) }
            popupMenu?.menuInflater?.inflate(R.menu.main_menu, popupMenu.menu)
            popupMenu?.setOnMenuItemClickListener { menuItem ->

                // Обработка выбора пункта меню
                when (menuItem.itemId) {
                    R.id.mars_theme -> {
                        setMarsTheme()
                        true
                    }
                    R.id.moon_theme -> {
                        setMoonTheme()
                        true
                    }
                    R.id.default_theme -> {
                        setDefaultTheme()
                        true
                    }
                    else -> false
                }
            }
            popupMenu?.show()
        }
        initData()
        listeners()
    }

//*******Function section****************

    private fun setDefaultTheme() {
        with(binding) {
            cardViewContainer.setBackgroundResource(R.drawable.stars)
            datePickerTitle.typeface =
                context?.let { ResourcesCompat.getFont(it, R.font.ptserif_italic) }
        }

    }

    private fun setMoonTheme() {
        with(binding) {
            cardViewContainer.setBackgroundResource(R.drawable.moon_background)
            datePickerTitle.typeface =
                context?.let { ResourcesCompat.getFont(it, R.font.ptserif_bold) }
        }
    }

    private fun setMarsTheme() {
        with(binding) {
            cardViewContainer.setBackgroundResource(R.drawable.mars_background)
            datePickerTitle.typeface =
                context?.let { ResourcesCompat.getFont(it, R.font.ptserif_regular) }
        }
    }


    private fun setBottomSheetBehavior(bottomSheet: ConstraintLayout) {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        // BottomSheet скрыт
                        binding.fab.isEnabled = true
                        binding.inputLayout.isEnabled = true
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        // BottomSheet отображается полностью
                        binding.fab.isEnabled = false
                        binding.inputLayout.isEnabled = false
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        // BottomSheet скрыт полностью
                        binding.fab.isEnabled = true
                        binding.inputLayout.isEnabled = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Пользователь перемещает BottomSheet
            }
        })
    }

    private fun initData() {

        mainViewModel.data.observe(viewLifecycleOwner) {

            it?.forEach { nasaData ->

                view?.let { it1 -> setBottomSheetBehavior(it1.findViewById(R.id.bottom_sheet_container)) }
                val bottomSheetDescription =
                    requireActivity().findViewById<View>(R.id.bottom_sheet_description) as TextView
                val bottomSheetTitle =
                    requireActivity().findViewById<View>(R.id.bottom_sheet_description_header) as TextView
                val bottomSheetDate =
                    requireActivity().findViewById<View>(R.id.the_date) as TextView
                bottomSheetDate.text = nasaData.date
                bottomSheetTitle.text = nasaData.title
                bottomSheetDescription.text = nasaData.explanation

                val imageClick = binding.photoView
                val pictureUrl = nasaData.url
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
        }

        mainViewModel.loadingState.observe(viewLifecycleOwner) {
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
        }
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
                        binding.datePickerTitle.text = date
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
    }
}
