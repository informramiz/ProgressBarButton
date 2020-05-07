package github.informramiz.progressbarbutton.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import github.informramiz.progressbarbutton.R
import github.informramiz.progressbarbutton.databinding.MainFragmentBinding
import github.informramiz.progressbarbutton.model.DownloadStatus
import github.informramiz.progressbarbutton.model.exhaustive

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var viewBinding: MainFragmentBinding
    private var toast: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = MainFragmentBinding.inflate(layoutInflater, container, false)
        return viewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewBinding.lifecycleOwner = viewLifecycleOwner
        viewBinding.viewModel = viewModel
        viewModel.downloadStatus.observe(viewLifecycleOwner, Observer { status ->
            status ?: return@Observer
            onDownloadingStatusUpdate(status)
        })
    }

    private fun onDownloadingStatusUpdate(status: DownloadStatus) {
        when (status) {
            is DownloadStatus.Downloaded -> {
                toast?.cancel()
                toast = null
                viewModel.onDownloadComplete()
            }
            is DownloadStatus.Downloading -> {
                if (toast == null) {
                    toast = Toast.makeText(
                        requireActivity(),
                        R.string.msg_we_are_downloading,
                        Toast.LENGTH_SHORT
                    )
                }
                toast?.show()
            }
            is DownloadStatus.DownloadFailed -> {
                toast?.cancel()
                toast = null
                Toast.makeText(
                    requireContext(),
                    status.e?.localizedMessage
                        ?: getString(R.string.msg_default_for_downaloding_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
