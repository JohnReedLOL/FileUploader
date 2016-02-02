package models

import java.io.File

import org.apache.commons.lang3.StringUtils

import scala.collection.mutable

case class ResumableInfo(resumableAuthorName: String,
                         resumableChunkSize: Int,
                         resumableTotalSize: Long,
                         resumableIdentifier: String,
                         resumableFilename: String,
                         resumableRelativePath: String,
                         resumableFilePath: String
                          ) {

  val uploadedChunks: mutable.MutableList[Int] = mutable.MutableList[Int]()

  def isValid: Boolean = {
    !(
      resumableIdentifier == null ||
      resumableFilename == null ||
      resumableRelativePath == null ||
      resumableAuthorName == null ||

      resumableChunkSize < 0 ||
      resumableTotalSize < 0 ||
      StringUtils.isEmpty(resumableIdentifier) ||
      StringUtils.isEmpty(resumableFilename) ||
      StringUtils.isEmpty(resumableRelativePath) ||
      StringUtils.isEmpty(resumableAuthorName) )
  }

  def checkIfUploadFinished: Boolean = {
    val count: Int = Math.ceil(resumableTotalSize.toDouble / resumableChunkSize.toDouble).toInt
    1.until(count) foreach { i: Int =>
      if (!uploadedChunks.contains(i)) return false
    }

    val file: File = new File(resumableFilePath)
    val newPath: String = file.getAbsolutePath.substring(0, file.getAbsolutePath.length - ".temp".length)
    file.renameTo(new File(newPath))
    true
  }

  def addUploadedChunk(resumableChunkNumber: Int) = {
    uploadedChunks += resumableChunkNumber
  }

  def containsChunk(resumableChunkNumber: Int) = {
    uploadedChunks.contains(resumableChunkNumber)
  }
}
