package controllers

import java.io.RandomAccessFile

import core.ResumableInfoStorage
import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc.{Action, Controller}

/**
 * Play controller for resumable.js
 * Inspired by: http://www.codedisqus.com/CyVjUkUWjq/how-to-upload-a-huge-file-in-play-framework.html
 * and https://github.com/23/resumable.js/tree/master/samples/java
 **/
object Resumable extends Controller with BodyParsers {

  def doPost() = { 
      
      System.err.println("DoPost")
      
      Action(multipartFormDataAsBytes) { request =>
    val resumableParams = request.body.dataParts.mapValues(_.head)
    val resumableChunkNumber = resumableParams("resumableChunkNumber").toInt
    ResumableInfoStorage.getResumableInfo(resumableParams) match {
      case Some(info) =>
        val raf: RandomAccessFile = new RandomAccessFile(info.resumableFilePath, "rw")
        raf.seek((resumableChunkNumber - 1) * info.resumableChunkSize.toLong)

        request.body.files foreach {
          case FilePart(key, filename, content, bytes) => raf.write(bytes)
        }
        raf.close()

        info.addUploadedChunk(resumableChunkNumber)
        if (info.checkIfUploadFinished) ResumableInfoStorage.remove(info)
        Ok
      case None => BadRequest
    }
  }

  }

  def doGet() = { 
      
      System.err.println("doGet")
      System.out.println("doGet")
      
      Action { request =>
    val resumableParams = request.queryString.mapValues(_.head)
    val resumableChunkNumber = resumableParams("resumableChunkNumber").toInt
    ResumableInfoStorage.getResumableInfo(resumableParams) match {
      case Some(info) =>
        if (info.containsChunk(resumableChunkNumber)) Ok
        else NotFound
      case None => BadRequest
    }
  }
  }

}
