$(function () {

  var resumable = new Resumable({
    target: '/resumable'
  });

  resumable.assignBrowse(document.getElementById('browseButton'));

  //resumable.on('fileAdded', function(file){
  //    resumable.upload();
  //});

  $("#button").click(function () {
      resumable.upload()
    }
  );

  resumable.on('fileSuccess', function (file) {
    console.debug(file);
    alert('fileSuccess: ' + file); // Appears when done.
  });

  resumable.on('fileProgress', function (file) {
    console.debug(file);
    alert('fileProgress: ' + file); // Corresponds to 'resumableInfo doesn't containsChunk #1. NotFound.'
    // Also corresponds to completing a post - Upload finished = true. Removed resumableInfo..
  });
// more events, look API docs
});