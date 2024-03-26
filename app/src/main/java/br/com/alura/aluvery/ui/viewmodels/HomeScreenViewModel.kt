package br.com.alura.aluvery.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.alura.aluvery.dao.ProductDao
import br.com.alura.aluvery.model.Product
import br.com.alura.aluvery.sampledata.sampleCandies
import br.com.alura.aluvery.sampledata.sampleDrinks
import br.com.alura.aluvery.sampledata.sampleProducts
import br.com.alura.aluvery.ui.uistates.HomeScreenUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class HomeScreenViewModel : ViewModel() {

    private val dao = ProductDao()


    private val _uiState: MutableStateFlow<HomeScreenUiState> = MutableStateFlow(
        HomeScreenUiState()
    )

    val uiState get() = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(
                onSearchChange = { newText ->
                    _uiState.value = _uiState.value.copy(
                        searchText = newText,
                        searchedProducts = searchedProducts(newText)
                    )
                }
            )
        }


        viewModelScope.launch {
            dao.products().collect { products ->
                _uiState.value = _uiState.value.copy(
                    sections = mapOf(
                        "Novos" to products,
                        "Doces" to sampleCandies,
                        "Bebidas" to sampleDrinks
                    ),
                    searchedProducts = searchedProducts(_uiState.value.searchText)
                )
            }
        }

    }


    private fun containsInNameOrDescrioption(text: String) = { product: Product ->
        product.name.contains(
            text,
            ignoreCase = true,
        ) || product.description?.contains(
            text,
            ignoreCase = true,
        ) ?: false
    }

    private fun searchedProducts(text: String) =
        if (text.isNotBlank()) {
            sampleProducts.filter(containsInNameOrDescrioption(text)) +
                    dao.products().value.filter(containsInNameOrDescrioption(text))
        } else emptyList()


}