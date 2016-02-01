package core

import java.io.File

import models.ResumableInfo
import play.api.Play

import scala.collection._
import scala.util.Try

object ResumableInfoStorage {

  val uploadDir: String = Try {
    Play.current.configuration.getString("local.upload.dir").get
  }.getOrElse("")

  private val mMap: mutable.Map[String, ResumableInfo] = mutable.Map[String, ResumableInfo]()

  def get(resumableInfo: ResumableInfo): ResumableInfo = {
    mMap.get(resumableInfo.resumableIdentifier) match {
      case Some(i) => i
      case None => mMap += (resumableInfo.resumableIdentifier -> resumableInfo)
        mMap(resumableInfo.resumableIdentifier)
    }
  }

  def remove(info: ResumableInfo) {
    mMap.remove(info.resumableIdentifier)
  }

  def getResumableInfo(resumableParams: Map[String, String]): Option[ResumableInfo] = {
    new File(uploadDir).mkdir
    val info = get(
      ResumableInfo(
        resumableChunkSize = resumableParams("resumableChunkSize").toInt,
        resumableTotalSize = resumableParams("resumableTotalSize").toLong,
        resumableIdentifier = resumableParams("resumableIdentifier"),
        resumableFilename = resumableParams("resumableFilename"),
        resumableRelativePath = resumableParams("resumableRelativePath"),
        resumableFilePath = new File(uploadDir, resumableParams("resumableFilename")).getAbsolutePath + ".temp"
      )
    )
    if (!info.isValid) {
      remove(info)
      None
    } else Some(info)
  }
}
