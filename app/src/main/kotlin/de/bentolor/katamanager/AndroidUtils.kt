package de.bentolor.katamanager

import android.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

/**
 * Extension properties: Direct access to all views in a [ViewGroup].
 */
val ViewGroup.views: List<View>
    get() = (0..childCount - 1).map { getChildAt(it) }

/**
 * Return a flat map of all contained views (recursivly) in a [ViewGroup].
 */
val ViewGroup.viewsRecursive: List<View>
    get() = views.flatMap {
        when (it) {
            is ViewGroup -> it.viewsRecursive
            else -> listOf(it)
        }
    }

fun Fragment.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(getActivity(), message, duration).show()
}

fun ViewGroup.get(position: Int): View = getChildAt(position)