package com.ruparts.app.features.qrscan.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QrScanViewModel @Inject constructor() : ViewModel() {
    // This ViewModel can be expanded later to handle QR scan results,
    // store scan history, or process QR code data before passing it to other parts of the app
}
