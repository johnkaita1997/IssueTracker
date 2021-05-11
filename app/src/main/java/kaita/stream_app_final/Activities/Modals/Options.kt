package kaita.stream_app_final.Activities.Modals


data class Options(var name: String? = null)
data class BetsPlaced(var bettername: String? = null, var bettamount: String? = null, var streamoption: String? = null, var bettstate: String? = null)
data class BetsPlaced_Final(
    var bettstate: String? = null,
    var bettdate: String? = null,
    var bettamount: String? = null,
    var betttittle: String? = null,
    var streamoption: String? = null,
    var streamid: String? = null
)