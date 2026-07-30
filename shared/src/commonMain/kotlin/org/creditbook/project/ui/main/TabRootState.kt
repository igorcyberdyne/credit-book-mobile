package org.creditbook.project.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object TabRootState {
    var isHomeAtRoot by mutableStateOf(true)
    // Profil et Paramètres n'ont pas de navigation imbriquée pour l'instant,
    // donc toujours considérés "à la racine"
}