package controllers

import play.api.mvc._

object UploaderApplication extends Controller {

  def index = Action {
    Ok(views.html.uploaderIndex())
  }
}
