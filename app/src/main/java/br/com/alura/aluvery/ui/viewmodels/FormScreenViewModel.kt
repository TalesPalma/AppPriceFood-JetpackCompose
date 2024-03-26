package br.com.alura.aluvery.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import br.com.alura.aluvery.dao.ProductDao
import br.com.alura.aluvery.model.Product
import br.com.alura.aluvery.ui.screens.ProductFormUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal
import java.text.DecimalFormat

class FormScreenViewModel : ViewModel() {

    private val dao: ProductDao = ProductDao()
    private val _uiState = MutableStateFlow<ProductFormUiState>(ProductFormUiState())
    val uiState get() = _uiState.asStateFlow()
    val formatter = DecimalFormat("#.##")

    init {
        _uiState.update { currentState ->
            currentState.copy(
                onUrlChange = { _uiState.value = _uiState.value.copy(url = it) },
                onNameChange = { _uiState.value = _uiState.value.copy(name = it) },
                onDescriptionChange = { _uiState.value = _uiState.value.copy(description = it) },
                onPriceChange = {
                    val price = try {
                        formatter.format(BigDecimal(it))
                    } catch (e: IllegalArgumentException) {
                        if (it.isBlank()) {
                             it
                        }else null
                    }
                    price?.let {
                        _uiState.value = _uiState.value.copy(price = price)
                    }
                }
            )
        }
    }

    fun save() {
        _uiState.value.run {
            val convertedPrice = try {
                BigDecimal(price)
            } catch (e: NumberFormatException) {
                BigDecimal.ZERO
            }
            val product = Product(
                name = name,
                image = url,
                price = convertedPrice,
                description = description
            )
            dao.save(product = product)
        }
    }


}