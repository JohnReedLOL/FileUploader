package controllers

import java.io.ByteArrayOutputStream

import play.api.libs.iteratee.Iteratee
import play.api.mvc.BodyParsers.parse.multipartFormData
import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc.{BodyParser, MultipartFormData}
import play.core.parsers.Multipart.{FileInfo, PartHandler}
import play.core.parsers.Multipart.handleFilePart

import scala.concurrent.ExecutionContext.Implicits.global

import utilities.Print

trait BodyParsers {

  // hadle file part as Array[Byte]
  def handleFilePartAsByteArray: PartHandler[FilePart[Array[Byte]]] = {
    val os = new ByteArrayOutputStream()
    handleFilePart {
      case FileInfo(partName, filename, contentType) =>
        // simply write the data to the a ByteArrayOutputStream
        Iteratee.foreach[Array[Byte]] { data =>
          os.write(data)
        } map { _ =>
          os.close()
          os.toByteArray
        }
    }
  }

  // custom body parser to handle file part as Array[Byte]
  def multipartFormDataAsBytes: BodyParser[MultipartFormData[Array[Byte]]] =
    multipartFormData(handleFilePartAsByteArray)

}
