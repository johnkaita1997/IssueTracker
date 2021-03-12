package smartherd.githubissuetracker.Adapteres

class Model {
    var created_at: String? = null
    var comments: String? = null
    var title: String? = null
    var developer: String? = null
    var commentload: String? = null
    var state: String? = null

    constructor() {}
    constructor(
        created_at: String?,
        comments: String?,
        title: String?,
        developer: String?,
        commentload: String?,
        state: String?
    ) {
        this.created_at = created_at
        this.comments = comments
        this.title = title
        this.developer = developer
        this.commentload = commentload
        this.state = commentload
    }

}


class Comments {
    var created_at: String? = null
    var body: String? = null
    var developer: String? = null

    constructor()
    constructor(body: String?, created_at: String?, developer: String?) {
        this.body = body
        this.created_at = created_at
        this.developer = developer
    }

}
