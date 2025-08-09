package lamdx4.uis.ptithcm.ui.nav

object TopAppBarConfig {
    data class TopBarConfig(
        val title: String,
        val showBack: Boolean = false,
        val showRefresh: Boolean = false
    )

    val routeTopBarConfig = mapOf(
        "curriculum" to TopBarConfig("Chương trình đào tạo", showBack = true, showRefresh = true),
        "prerequisites" to TopBarConfig("Môn học tiên quyết", showBack = true, showRefresh = true),
        "invoices" to TopBarConfig("Hóa đơn", showBack = true, showRefresh = true),
        "sync_calendar" to TopBarConfig("Đồng bộ Calendar", showBack = true, showRefresh = false),
        "notification_detail" to TopBarConfig("Thông báo", showBack = true, showRefresh = false),
        "notifications" to TopBarConfig("Thông báo", showBack = true, showRefresh = false),
        "detailed_info" to TopBarConfig("Thông tin chi tiết", showBack = true, showRefresh = false),
        "payment" to TopBarConfig("Thanh toán", showBack = true, showRefresh = false),
        "register" to TopBarConfig("Đăng ký môn học", showBack = true, showRefresh = true),
    )
}
