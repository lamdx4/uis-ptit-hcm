package lamdx4.uis.ptithcm.ui.more.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.data.repository.NotificationRepository
import javax.inject.Inject

// Constants for pagination
private const val PAGINATION_LIMIT = 10

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        loadNotifications(page = 1)
    }

    fun loadNotifications(page: Int) {
        if (_uiState.value.isLoading || (page > 1 && !_uiState.value.hasMorePages)) {
            return // Prevent multiple loads or loading beyond available pages
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val responseResult = notificationRepository.getNotification(
                    limit = PAGINATION_LIMIT,
                    page = page
                )

                responseResult.onSuccess { response ->
                    val newNotifications =
                        response.data.notificationList.map { it.toUiNotificationItem() }
                    val currentList = if (page == 1) emptyList() else _uiState.value.notifications

                    _uiState.value = _uiState.value.copy(
                        notifications = currentList + newNotifications,
                        totalItems = response.data.totalItems,
                        totalPages = response.data.totalPages,
                        currentPage = page,
                        isLoading = false,
                        hasMorePages = page < response.data.totalPages,
                        totalUnreadItems = response.data.notification // Assuming 'notification' field in data is unread count
                    )
                }.onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load notifications."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "An unexpected error occurred."
                )
            }
        }
    }

    fun refreshNotifications() {
        _uiState.value = NotificationUiState() // Reset state
        loadNotifications(page = 1)
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            // Simulate API call to mark all as read
            // In a real app, you'd call a repository function: notificationRepository.markAllAsRead()
            _uiState.value = _uiState.value.copy(
                notifications = _uiState.value.notifications.map { it.copy(isRead = true) },
                totalUnreadItems = 0
            )
            // After successful API call, you might want to refresh to get updated counts from server
            // refreshNotifications()
        }
    }

    fun markNotificationAsRead(id: String) {
        viewModelScope.launch {
            notificationRepository.readNotification(id).onSuccess {
                val updatedList = _uiState.value.notifications.map {
                    if (it.id == id) it.copy(isRead = true) else it
                }
                _uiState.value = _uiState.value.copy(
                    notifications = updatedList,
                    totalUnreadItems = updatedList.count { !it.isRead }
                )
            }
        }
    }
}

data class NotificationUiState(
    val notifications: List<NotificationItem> = emptyList(),
    val isRefreshing : Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val totalItems: Int = 0,
    val hasMorePages: Boolean = true, // Assume true until we know otherwise
    val totalUnreadItems: Int = 0
)

// UI Model for NotificationItem
data class NotificationItem(
    val id: String,
    val title: String,
    val content: String,
    val mustView: Boolean,
    val sentAt: String,
    val isRead: Boolean = false,
    val priority: NotificationPriority = NotificationPriority.NORMAL,
    val sender: String
)

enum class NotificationPriority {
    HIGH, NORMAL, LOW
}

// Mapper function from data model to UI model
fun lamdx4.uis.ptithcm.data.model.NotificationItem.toUiNotificationItem(): NotificationItem {
    return NotificationItem(
        id = this.id,
        mustView = this.mustView,
        title = this.title,
        content = this.content,
        sentAt = this.sentAt, // Assuming sentAt is in a displayable format
        isRead = this.isRead,
        sender = this.sender,
        priority =
            if (this.mustView) NotificationPriority.HIGH else NotificationPriority.NORMAL // Assuming mustView indicates high priority
    )
}
