package dev.olog.presentation.dialog_delete

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseDialogFragment
import dev.olog.presentation.utils.makeDialog
import dev.olog.presentation.utils.withArguments

class DeleteDialog: BaseDialogFragment() {

    companion object {
        const val TAG = "DeleteDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        fun newInstance(mediaId: String): DeleteDialog {
            return DeleteDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId
            )
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
                .setTitle("delete?")
                .setNegativeButton(R.string.popup_negative_cancel, null)
                .setPositiveButton(R.string.popup_positive_ok, { dialog, button ->
                    // todo
                })


        return builder.makeDialog()
    }

}