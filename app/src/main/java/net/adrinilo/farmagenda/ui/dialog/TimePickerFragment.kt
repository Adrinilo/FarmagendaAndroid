package net.adrinilo.farmagenda.ui.dialog

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import net.adrinilo.farmagenda.R
import java.time.LocalTime

class TimePickerFragment : DialogFragment() {
    companion object {
        fun newInstance(observer: TimePickerDialog.OnTimeSetListener): TimePickerFragment {

            return TimePickerFragment().apply {
                timeObserver = observer
            }
        }
    }

    private var timeObserver: TimePickerDialog.OnTimeSetListener? = null

    private var hour: Int
    private var minute: Int

    init {
        val now = LocalTime.now()
        hour = now.hour
        minute = now.minute
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return TimePickerDialog(requireActivity(), R.style.SpinnerTimePickerDialog, timeObserver, hour, minute, true)
    }
}