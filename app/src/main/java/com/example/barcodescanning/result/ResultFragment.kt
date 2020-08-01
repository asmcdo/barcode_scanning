package com.example.barcodescanning.result

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.example.barcodescanning.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ResultFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.fragment_result_bottom_sheet, container, false)

        view.findViewById<TextView>(R.id.resultTX).apply {
            arguments?.let {  barcodeValue ->
                this.text = barcodeValue.getString(BARCODE_RESULT)
            }
        }

        return view
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    companion object {
        private const val TAG = "ResultFragment"
        private const val BARCODE_RESULT = "barcode_result_argument"

        fun show(fragmentManager: FragmentManager, barcodeResult: String?) {
            val resultFragment = ResultFragment()
            resultFragment.arguments = Bundle().apply {
                putString(BARCODE_RESULT, barcodeResult ?: "")
            }
            resultFragment.show(fragmentManager, TAG)
        }

        fun dismiss(fragmentManager: FragmentManager) {
            (fragmentManager.findFragmentByTag(TAG) as ResultFragment?)?.dismiss()
        }
    }
}