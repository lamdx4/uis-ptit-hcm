package lamdx4.uis.ptithcm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request body for registering or unregistering a course
 * POST https://uis.ptithcm.edu.vn/api/dkmh/w-xulydkmhsinhvien
 */
@Serializable
data class RegisterActionRequest(
    @SerialName("filter")
    val filter: RegisterFilter
)

@Serializable
data class RegisterFilter(
    @SerialName("id_to_hoc")
    val groupId: String,
    
    @SerialName("is_checked")
    val isChecked: Boolean,  // true = register, false = unregister
    
    @SerialName("sv_nganh")
    val studentMajor: Int = 1
)
