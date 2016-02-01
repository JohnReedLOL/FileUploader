package controllers

import java.io.RandomAccessFile

import core.ResumableInfoStorage
import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc.{Action, Controller}

import utilities.Print

/**
  * Play controller for resumable.js
  * Inspired by: http://www.codedisqus.com/CyVjUkUWjq/how-to-upload-a-huge-file-in-play-framework.html
  * and https://github.com/23/resumable.js/tree/master/samples/java
  **/
object Resumable extends Controller with BodyParsers {

  def doPost() = {

    Print.meh("Starting doPost.")

    Action(multipart_Form_Data_As_Bytes) { request =>
      val resumableParams = request.body.dataParts.mapValues(_.head)
      Print.meh("resumableParams: " + resumableParams.toString)

      val resumableChunkNumber = resumableParams("resumableChunkNumber").toInt
      Print.meh("resumableChunkNumber: " + resumableChunkNumber.toString)

      ResumableInfoStorage.getResumableInfo(resumableParams) match {
        case Some(resumableInfo) =>

          Print.meh("Some(resumableInfo) from resumableParams =>: " + resumableInfo.toString)

          val randomAccessFile = new RandomAccessFile(resumableInfo.resumableFilePath, "rw")
          randomAccessFile.seek((resumableChunkNumber - 1) * resumableInfo.resumableChunkSize.toLong)

          request.body.files foreach {
            case FilePart(key, filename, content, bytes) =>
              randomAccessFile.write(bytes)
              Print.meh("Wrote bytes: " + bytes.length + " to fileName: " + filename)

          }
          randomAccessFile.close()

          resumableInfo.addUploadedChunk(resumableChunkNumber)
          if (resumableInfo.checkIfUploadFinished) {
            ResumableInfoStorage.remove(resumableInfo)
            Print.meh("Upload finished = true. Removed resumableInfo.")
          }
          Ok
        case None =>
          Print.bad("No resumableInfo in resumableParams. BadRequest")
          BadRequest

      }
    }

  }

  def doGet() = {

    Print.meh("Starting doGet.")

    Action { request =>
      val resumableParams = request.queryString.mapValues(_.head)
      Print.meh("resumableParams: " + resumableParams.toString)

      val resumableChunkNumber = resumableParams("resumableChunkNumber").toInt
      Print.meh("resumableChunkNumber: " + resumableChunkNumber.toString)

      ResumableInfoStorage.getResumableInfo(resumableParams) match {
        case Some(resumableInfo) =>
          Print.meh("resumableInfo from resumableParams: " + resumableInfo.toString)

          if (resumableInfo.containsChunk(resumableChunkNumber)) {
            Print.meh("resumableInfo containsChunk #" + resumableChunkNumber + ". Ok.")
            Ok
          }
          else {
            Print.meh("resumableInfo doesn't containsChunk #" + resumableChunkNumber + ". NotFound.")
            NotFound
          }
        case None => 
          Print.meh("No resumableInfo from resumableParams. Bad request.")
          BadRequest

      }
    }
  }

}
