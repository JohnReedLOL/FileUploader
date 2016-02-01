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

  // handle file part as Array[Byte]
  def handle_File_Part_As_Byte_Array: PartHandler[FilePart[Array[Byte]]] = {
    //Print.meh("Starting handle_File_Part_As_Byte_Array.")
    val os = new ByteArrayOutputStream()

    handleFilePart {
      case FileInfo(partName, filename, contentType) =>
        // simply write the data to the a ByteArrayOutputStream
        Iteratee.foreach[Array[Byte]] { data: Array[Byte] =>
          os.write(data) // This runs in a loop.
          Print.meh("output stream wrote " + data.length + " bytes.")
        } map { _ =>
          os.close()
          val byteArray = os.toByteArray
          Print.meh("Finishing handle_File_Part_As_Byte_Array. Final length: " + byteArray.length)
          byteArray
        }
    }
  }

  // custom body parser to handle file part as Array[Byte]
  def multipart_Form_Data_As_Bytes: BodyParser[MultipartFormData[Array[Byte]]] = {
    Print.meh("Starting multipart_Form_Data_As_Bytes.")
    multipartFormData(handle_File_Part_As_Byte_Array)
  }

}
