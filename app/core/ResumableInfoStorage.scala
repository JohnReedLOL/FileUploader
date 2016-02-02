package core

import java.io.File

import models.ResumableInfo
import play.api.Play
import utilities.Print

import scala.collection._
import scala.util.Try

object ResumableInfoStorage {

  val uploadDir: String = Try {
    Play.current.configuration.getString("local.upload.dir").get
  }.getOrElse("")

  private val mMap: mutable.Map[String, ResumableInfo] = {
    mutable.Map[String, ResumableInfo]()
  }

  def get(resumableInfo: ResumableInfo): ResumableInfo = {
    mMap.get(resumableInfo.resumableIdentifier) match {
      case Some(i) => i
      case None => mMap += (resumableInfo.resumableIdentifier ->(resumableInfo) )
        mMap(resumableInfo.resumableIdentifier)
    }
  }

  def remove(info: ResumableInfo) {
    Print.meh("Removed resumableInfo.resumableIdentifier from map")
    mMap.remove(info.resumableIdentifier)
  }

  def getResumableInfo(resumableParams: Map[String, String]): Option[ResumableInfo] = {
    new File(uploadDir).mkdir
    Print.meh("author Param: " + resumableParams("authorName")) // This works.

    val info = get(
      ResumableInfo(
        resumableAuthorName = resumableParams("authorName"),
        resumableChunkSize = resumableParams("resumableChunkSize").toInt,
        resumableTotalSize = resumableParams("resumableTotalSize").toLong,
        resumableIdentifier = resumableParams("resumableIdentifier"),
        resumableFilename = resumableParams("resumableFilename"),
        resumableRelativePath = resumableParams("resumableRelativePath"),
        resumableFilePath = new File(uploadDir, resumableParams("resumableFilename")).getAbsolutePath + ".temp"
      )
    )
    if (!info.isValid) {
      Print.bad("Invalid info")
      remove(info)
      None
    } else {
      Print.good("Valid info")
      Some(info)
    }
  }
}
