package models

import java.io.File

import org.apache.commons.lang3.StringUtils

import scala.collection.mutable

case class ResumableInfo(resumableChunkSize: Int,
                         resumableTotalSize: Long,
                         resumableIdentifier: String,
                         resumableFilename: String,
                         resumableRelativePath: String,
                         resumableFilePath: String
                          ) {

  val uploadedChunks: mutable.MutableList[Int] = mutable.MutableList[Int]()

  def isValid: Boolean = {
    !(resumableChunkSize < 0 ||
      resumableTotalSize < 0 ||
      StringUtils.isEmpty(resumableIdentifier) ||
      StringUtils.isEmpty(resumableFilename) ||
      StringUtils.isEmpty(resumableRelativePath))
  }

  def checkIfUploadFinished: Boolean = {
    val count: Int = Math.ceil(resumableTotalSize.toDouble / resumableChunkSize.toDouble).toInt
    1 until count foreach { i =>
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
