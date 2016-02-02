$(function () {

  var resumable = new Resumable({
    target: '/resumable',
    query: {authorName:'authorName'},
    simultaneousUploads: 1, // Number of simultaneous uploads
    testChunks: true, // Make a GET request to the server for each chunks to see if it already exists. If implemented on the server-side, this will allow for upload resumes even after a browser crash or even a computer restart.
    maxFiles: 1 // how many files can be uploaded in a single session.
  });

  if(!resumable.support) {
    alert("The uploader won't work on your web browser. Switch to Google Chrome or FireFox.")
  }

  resumable.assignBrowse(document.getElementById('fileSelector'));

  // r.assignBrowse(document.getElementById('browseButton'));

  //resumable.on('fileAdded', function(file){
  //    resumable.upload();
  //});

  $("#uploadButton").click(function () {
      resumable.upload() // Start or resume uploading.
    }
  );

  /**
   * $("#button2").click(function () {
      resumable.pause() // Pause uploading.
    }
   );
   */

  /**
   * $("#button2").click(function () {
      resumable.cancel() // Cancel upload of all ResumableFile objects
    }
   );
   */

  /**
   * $("#button2").click(function () {
      resumable.progress() // Returns a float between 0 and 1 indicating the current upload progress of all files.
    }
   );
   */

  /**
   * $("#button2").click(function () {
      resumable.isUploading() // Returns a boolean indicating whether or not the instance is currently uploading anything.
      }
   );
   */

  /**
   * $("#button2").click(function () {
      resumable.getSize() // Returns the total size of the upload in bytes.
      }
   );
   */

  resumable.on('fileSuccess', function (file) {
    console.debug(file);
    alert('fileSuccess: ' + file); // Appears when done.
  });

  resumable.on('fileProgress', function (file) {
    console.debug(file);
    alert('fileProgress: ' + file); // Corresponds to 'resumableInfo doesn't containsChunk #1. NotFound.'
    // Also corresponds to completing a post - Upload finished = true. Removed resumableInfo..
    alert("File progress is: " + file.progress(false) + " /1")
  });

   // A new file was added. Happens upon the selection of the file.
   resumable.on('fileAdded', function(file, message){
    alert("A new file was added.")
     const resumableFiles1 = resumable.files // An array of ResumableFile file objects added by the user (see full docs for this object type below).
    const fileName = file.fileName // The name of the file.
     const fileSize = file.size
     alert("File size is: " + fileSize + " bytes.")
     // file.cancel() // Abort uploading the file and delete it from the list of files to upload.
   });


   resumable.on('pause', function(file, message){
   alert("The upload was paused.")
   });

   resumable.on('cancel', function(file, message){
   alert("The upload was cancelled.")
   });

  resumable.on('fileError', function(file, message){
    alert("An error happened and your file could not be uploaded. Refresh and try again.")
  });

// more events, look API docs
});