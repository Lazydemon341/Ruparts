package com.ruparts.app.core.ui.viewmodel

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import dagger.hilt.android.lifecycle.withCreationCallback

inline fun <reified VM : ViewModel, reified VMF> Fragment.assistedViewModels(
    noinline ownerProducer: () -> ViewModelStoreOwner = { this },
    crossinline create: VMF.() -> VM,
): Lazy<VM> {
    return viewModels(
        ownerProducer = ownerProducer,
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<VMF> { factory ->
                factory.create()
            }
        }
    )
}