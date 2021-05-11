package kaita.stream_app_final.Activities.Modals

data class Post(
    var title: String? = null,
    var lastday: String? = null,
    var hostimage: String? = null,
    var hostname: String? = null,
    var contribution: String? = null,
    var remove: String? = null,
    var howtoshare: String? = null
)

data class EndBet(var manage: String? = null)