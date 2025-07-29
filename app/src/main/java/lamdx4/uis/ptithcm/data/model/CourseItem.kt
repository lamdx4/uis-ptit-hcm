package lamdx4.uis.ptithcm.data.model

/**
 * Normalized course item for registration UI, combining RegisterGroup and Subject info.
 */
data class CourseItem(
    val group: RegisterGroup,
    val subject: Subject?
)
