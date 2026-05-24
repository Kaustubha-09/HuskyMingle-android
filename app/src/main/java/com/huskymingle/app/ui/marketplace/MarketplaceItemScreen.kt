package com.huskymingle.app.ui.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.huskymingle.app.data.model.MarketplaceItem
import com.huskymingle.app.data.network.RetrofitClient
import com.huskymingle.app.ui.components.AvatarView
import com.huskymingle.app.ui.components.HMPrimaryButton
import com.huskymingle.app.ui.theme.HMTheme
import com.huskymingle.app.ui.theme.HuskyGold
import com.huskymingle.app.ui.theme.HuskyRed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MarketplaceItemState {
    object Loading : MarketplaceItemState()
    data class Error(val message: String) : MarketplaceItemState()
    data class Success(val item: MarketplaceItem) : MarketplaceItemState()
}

class MarketplaceItemViewModel : ViewModel() {
    private val api = RetrofitClient.apiService
    private val _state = MutableStateFlow<MarketplaceItemState>(MarketplaceItemState.Loading)
    val state: StateFlow<MarketplaceItemState> = _state.asStateFlow()

    fun load(id: String) {
        _state.value = MarketplaceItemState.Loading
        viewModelScope.launch {
            try {
                _state.value = MarketplaceItemState.Success(api.getMarketplaceItem(id))
            } catch (e: Exception) {
                _state.value = MarketplaceItemState.Error(e.message ?: "Couldn't load item")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceItemScreen(
    itemId: String,
    onBack: () -> Unit,
    onContactSeller: (String) -> Unit = {},
    onOpenSeller: (String) -> Unit = {},
    viewModel: MarketplaceItemViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(itemId) { viewModel.load(itemId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Listing", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
            )
        }
    ) { padding ->
        when (val s = state) {
            MarketplaceItemState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator(color = HuskyRed) }
            is MarketplaceItemState.Error -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) { Text(s.message, color = MaterialTheme.colorScheme.error) }
            is MarketplaceItemState.Success -> ItemBody(
                item = s.item,
                modifier = Modifier.padding(padding),
                onContactSeller = { onContactSeller(s.item.seller.username) },
                onOpenSeller = { onOpenSeller(s.item.seller.username) },
            )
        }
    }
}

@Composable
private fun ItemBody(
    item: MarketplaceItem,
    modifier: Modifier = Modifier,
    onContactSeller: () -> Unit,
    onOpenSeller: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(HMTheme.spacing.md),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            val hero = item.images.firstOrNull()
            if (hero != null) {
                AsyncImage(
                    model = hero,
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Image,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(64.dp),
                )
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = HMTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(HMTheme.spacing.sm),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "$${String.format("%.2f", item.price)}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = HuskyGold,
                    fontWeight = FontWeight.Bold,
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (item.condition.isNotBlank()) {
                    AssistChip(onClick = {}, label = { Text(item.condition) })
                }
                if (item.category.isNotBlank()) {
                    AssistChip(onClick = {}, label = { Text(item.category) })
                }
                if (item.isSold) {
                    AssistChip(
                        onClick = {},
                        label = { Text("SOLD") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            labelColor = MaterialTheme.colorScheme.error,
                        ),
                    )
                }
            }

            if (item.description.isNotBlank()) {
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(modifier = Modifier.height(HMTheme.spacing.sm))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(HMTheme.radius.lg))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onOpenSeller() }
                    .padding(HMTheme.spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HMTheme.spacing.sm),
            ) {
                AvatarView(
                    name = item.seller.displayName.ifEmpty { item.seller.username },
                    size = 44.dp,
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.seller.displayName.ifEmpty { item.seller.username },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Seller",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(HMTheme.spacing.xs))

            HMPrimaryButton(
                label = if (item.isSold) "Sold" else "Message seller",
                enabled = !item.isSold,
                onClick = onContactSeller,
            )

            Spacer(modifier = Modifier.height(HMTheme.spacing.lg))
        }
    }
}
